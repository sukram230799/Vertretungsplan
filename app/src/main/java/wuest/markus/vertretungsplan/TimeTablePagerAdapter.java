package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class TimeTablePagerAdapter extends FragmentPagerAdapter implements TimeTableFragment.RefreshContentListener {

    private static final String TAG = "TimeTablePagerAdapter";
    ArrayList<TimeTableFragment> fragments;
    ArrayList<HWTime> dates;
    Integer[] lessonDays;
    private HWGrade grade;
    private RefreshContentListener refreshListener;
    private Context context;
    private int pastDays;
    private int futureDays;

    public TimeTablePagerAdapter(FragmentManager fm, HWGrade grade, Context context, int pastDays, int futureDays) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        this.pastDays = pastDays;
        this.futureDays = futureDays;
        lessonDays = new DBHandler(context, null, null, 0).getDaysWithLessons(grade);
        dates = new ArrayList<>(Arrays.asList(TimeTableHelper.getHWTimes(lessonDays, pastDays, futureDays)));
        for (HWTime time : dates) { //PRE WORK
            fragments.add(TimeTableFragment.newInstance(grade, time, false));
        }
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        while (position >= dates.size()) {
            addFutureDate();
        }
        TimeTableFragment timeTableFragment = fragments.get(position);
        timeTableFragment.setRefreshListener(this);

        return timeTableFragment;
    }

    @Override
    public int getCount() {
        if (dates.size() < 20) {
            return dates.size() + 1;
        }
        return dates.size();
    }

    private void addFutureDate() {
        dates.add(TimeTableHelper.getNextHWTime(dates.get(dates.size() - 1), lessonDays));
        fragments.add(TimeTableFragment.newInstance(grade, dates.get(dates.size() - 1), false));
        notifyDataSetChanged();
    }

    /*private void addPastDate() {
        dates.add(0, TimeTableHelper.getNextHWTime(dates.get(dates.size() - 1), lessonDays));
        fragments.add(TimeTableFragment.newInstance(grade, dates.get(dates.size() - 1), false));
        notifyDataSetChanged();
    }*/

    @Override
    public CharSequence getPageTitle(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM");
        while (position >= dates.size()) {
            addFutureDate();
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

    private int getCenterDay() {
        return pastDays;
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
    }
}
