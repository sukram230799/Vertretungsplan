package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeTablePagerAdapter extends FragmentPagerAdapter implements TimeTableFragment.RefreshContentListener {

    private static final String TAG = "TimeTablePagerAdapter";
    ArrayList<TimeTableFragment> fragments;
    ArrayList<HWTime> dates;
    Integer[] lessonDays;
    private HWGrade grade;
    private RefreshContentListener refreshListener;
    private Context context;

    public TimeTablePagerAdapter(FragmentManager fm, HWGrade grade, Context context) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        lessonDays = new DBHandler(context, null, null, 0).getDaysWithLessons(grade);
        dates = new ArrayList<>(Arrays.asList(TimeTableHelper.getHWTimes(lessonDays)));
        for (HWTime time : dates) { //PRE WORK
            fragments.add(TimeTableFragment.newInstance(grade, time, false));
        }
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        while (position >= dates.size()) {
            addNewDate();
        }
        TimeTableFragment timeTableFragment = fragments.get(position);
        timeTableFragment.setRefreshListener(this);

        return timeTableFragment;
    }

    @Override
    public int getCount() {
        if(dates.size() < 20) {
            return dates.size() + 1;
        }
        return dates.size();
    }

    private void addNewDate() {
        dates.add(
                TimeTableHelper.getNextHWTime(dates.get(dates.size() - 1), lessonDays));
        fragments.add(TimeTableFragment.newInstance(grade, dates.get(dates.size() - 1), false));
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM");
        while (position >= dates.size()) {
            addNewDate();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dates.get(position).toDate());
        return TimeTableHelper.getDayName(c.get(Calendar.DAY_OF_WEEK), context) +
                " " + sdf.format(dates.get(position).toDate());
    }

    @Override
    public void refreshedContent(SwipeRefreshLayout refreshLayout) {
        if (refreshListener == null) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshListener.refreshedContent(refreshLayout);
        }
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
    }
}
