package wuest.markus.vertretungsplan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;

/**
 * Created by Markus on 16.02.2016.
 */
public class PlanPagerAdapter extends FragmentPagerAdapter implements PlanFragment.RefreshContentListener {

    ArrayList<PlanFragment> fragments;
    HWGrade grade;

    public PlanPagerAdapter(FragmentManager fm, HWGrade grade) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            fragments.add(PlanFragment.newInstance(grade, i + 2, false));
        }
    }

    @Override
    public Fragment getItem(int position) {
        PlanFragment planFragment;
        if (fragments.size() == getCount()) {
            planFragment = fragments.get(position);
        } else {
            planFragment = PlanFragment.newInstance(grade, position + 2, false);
        }
        planFragment.setRefreshListener(this);

        return planFragment;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TimeTableHelper.getDayName(position + 2);
    }

    @Override
    public void refreshedContent(SwipeRefreshLayout refreshLayout) {

    }
}
