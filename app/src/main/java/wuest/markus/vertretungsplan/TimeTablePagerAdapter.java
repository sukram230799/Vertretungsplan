package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeTablePagerAdapter extends FragmentPagerAdapter implements TimeTableFragment.RefreshContentListener {

    private static final String TAG = "TimeTablePagerAdapter";
    private HWGrade grade;
    private RefreshContentListener refreshListener;
    private Context context;

    public TimeTablePagerAdapter(FragmentManager fm, HWGrade grade, Context context) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        /*for(HWTime time : getHWTimes()) { //PRE WORK
            fragments.add(TimeTableFragment.newInstance(grade, HWTime, false, false));
            dates.add(time);
        }*/
        for (int i = 0; i < getCount(); i++) {
            fragments.add(TimeTableFragment.newInstance(grade, i + 2, false, false));
        }
        this.context = context;
    }

    ArrayList<TimeTableFragment> fragments;
    ArrayList<HWTime> dates;

    @Override
    public Fragment getItem(int position) {
        TimeTableFragment timeTableFragment;
        if (fragments.size() == getCount()) {
            timeTableFragment = fragments.get(position);
        } else {
            timeTableFragment = TimeTableFragment.newInstance(grade, position + 2, false, false);
        }
        timeTableFragment.setRefreshListener(this);

        return timeTableFragment;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TimeTableHelper.getDayName(position + 2, context);
    }

    /*public void setEdit(boolean showCheckBoxes) {
        for (TimeTableFragment fragment : fragments) {
            fragment.setShowCheckBoxes(showCheckBoxes);
        }
    }*/

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

    private HWTime[] getHWTimes() {
        Calendar twoDaysAgo = GregorianCalendar.getInstance();
        twoDaysAgo.add(Calendar.DATE, -2);
        Log.d(TAG, DBHandler.dbDateFormat.format(twoDaysAgo.getTime()));
        Calendar oneDayAgo = GregorianCalendar.getInstance();
        oneDayAgo.add(Calendar.DATE, -1);
        Log.d(TAG, DBHandler.dbDateFormat.format(oneDayAgo.getTime()));
        Calendar today = GregorianCalendar.getInstance();
        Log.d(TAG, DBHandler.dbDateFormat.format(today.getTime()));
        Calendar plusOneDay = GregorianCalendar.getInstance();
        plusOneDay.add(Calendar.DATE, 1);
        Log.d(TAG, DBHandler.dbDateFormat.format(plusOneDay.getTime()));
        Calendar plusTwoDays = GregorianCalendar.getInstance();
        plusTwoDays.add(Calendar.DATE, 2);
        Log.d(TAG, DBHandler.dbDateFormat.format(plusTwoDays.getTime()));
        return new HWTime[]{
                new HWTime(twoDaysAgo),
                new HWTime(oneDayAgo),
                new HWTime(today),
                new HWTime(plusOneDay),
                new HWTime(plusTwoDays)
        };
    }
}
