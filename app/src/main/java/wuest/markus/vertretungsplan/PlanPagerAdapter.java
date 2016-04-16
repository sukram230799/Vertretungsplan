package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class PlanPagerAdapter extends FragmentPagerAdapter implements PlanFragment.RefreshContentListener {

    private static final String TAG = "PlanPagerAdapter";
    ArrayList<PlanFragment> fragments;
    ArrayList<HWTime> dates;
    Integer[] lessonDays;
    HWGrade grade;
    private RefreshContentListener refreshListener;
    private Context context;

    public PlanPagerAdapter(FragmentManager fm, HWGrade grade, Context context) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        lessonDays = new DBHandler(context, null, null, 0).getDaysWithLessons(grade);
        dates = new ArrayList<>(Arrays.asList(TimeTableHelper.getHWTimes(lessonDays)));
        for (HWTime time : dates) {
            fragments.add(PlanFragment.newInstance(grade, time, false));
        }
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        while (position >= dates.size()) {
            addNewDate();
        }
        PlanFragment planFragment = fragments.get(position);
        planFragment.setRefreshListener(this);

        return planFragment;
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
        fragments.add(PlanFragment.newInstance(grade, dates.get(dates.size() - 1), false));
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM");
        while (position >= dates.size()) {
            addNewDate();
        }
        Calendar calendar = new GregorianCalendar();
        Log.d(TAG, DBHandler.dbDateFormat.format(dates.get(position).toDate()));
        calendar.setTime(dates.get(position).toDate());
        Log.d(TAG, DBHandler.dbDateFormat.format(calendar.getTime()));
        return TimeTableHelper.getDayName(calendar.get(Calendar.DAY_OF_WEEK), context) +
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

    public int getDay(int itemNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dates.get(itemNumber).toDate());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
    }
}
