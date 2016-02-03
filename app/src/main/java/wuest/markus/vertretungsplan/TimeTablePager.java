package wuest.markus.vertretungsplan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeTablePager extends FragmentPagerAdapter implements TimeTableFragment.RefreshContentListener {

    private HWGrade grade;
    private RefreshContentListener refreshListener;

    public TimeTablePager(FragmentManager fm, HWGrade grade) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            fragments.add(TimeTableFragment.newInstance(grade, i + 2, false));
        }
    }

    ArrayList<TimeTableFragment> fragments;

    @Override
    public Fragment getItem(int position) {
        TimeTableFragment timeTableFragment;
        if (fragments.size() == getCount()) {
            timeTableFragment = fragments.get(position);
        } else {
            timeTableFragment = TimeTableFragment.newInstance(grade, position + 2, false);
        }
        timeTableFragment.setRefreshListener(this
        );
        return timeTableFragment;
        //Fragment fragment = null;
        //return TimeTableFragment.newInstance(grade, position + 2); //1 = Sunday;
        /*
        switch (position){
            case 0: TimeTableFragment.newInstance(new HWGrade("TG11-2"), position + 2); //1 = Sunday;
        }
        return null;*/
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Montag";
            case 1:
                return "Dienstag";
            case 2:
                return "Mittwoch";
            case 3:
                return "Donnerstag";
            case 4:
                return "Freitag";
            case 5:
                return "Samstag";
            default:
                return "Sonntag";
        }
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
