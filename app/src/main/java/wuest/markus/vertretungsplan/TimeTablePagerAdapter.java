package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeTablePagerAdapter extends FragmentPagerAdapter implements TimeTableFragment.RefreshContentListener {

    private HWGrade grade;
    private RefreshContentListener refreshListener;
    private Context context;

    public TimeTablePagerAdapter(FragmentManager fm, HWGrade grade, Context context) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            fragments.add(TimeTableFragment.newInstance(grade, i + 2, false, false));
        }
        this.context = context;
    }

    ArrayList<TimeTableFragment> fragments;

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
}
