package wuest.markus.vertretungsplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class TabbedPlanFragment extends Fragment implements PlanPagerAdapter.RefreshContentListener{

    private static HWGrade grade;
    private static final String GRADE = "grade";

    public static final String TAG = "TabbedPlanFragment";

    ViewPager pager;
    PlanPagerAdapter pagerAdapter;
    PagerTabStrip pagerTabStrip;

    private FloatingActionMenu fab;
    private FloatingActionButton shareFAB;
    private FloatingActionButton editFAB;
    private FloatingActionButton newFAB;

    EditInterface editInterface;
    private RefreshContentListener refreshListener;

    public static TabbedPlanFragment newInstance(HWGrade grade) {
        TabbedPlanFragment fragment = new TabbedPlanFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.getGradeName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            grade = new HWGrade(getArguments().getString(GRADE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {View view = inflater.inflate(R.layout.fragment_tabbed_time_table, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new PlanPagerAdapter(getChildFragmentManager(), grade, getActivity());
        pagerAdapter.setRefreshListener(this);
        pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tab_strip);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(2);
        //fab = (FloatingActionButton) view.findViewById(R.id.fab);


        fab = (FloatingActionMenu) view.findViewById(R.id.fab);
        shareFAB = (FloatingActionButton) view.findViewById(R.id.fab_share);
        editFAB = (FloatingActionButton) view.findViewById(R.id.fab_edit);
        newFAB = (FloatingActionButton) view.findViewById(R.id.fab_new);

        //mSwipeRefreshLayout.setOnRefreshListener();

        editFAB.setClickable(true);
        editFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                //pagerAdapter.setEdit(showCheckBoxes);
                editInterface.onEditLesson(pagerAdapter.getDay(pager.getCurrentItem()), grade);
            }
        });
        newFAB.setClickable(true);
        newFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                editInterface.onAddLesson(pagerAdapter.getDay(pager.getCurrentItem()), grade);
            }
        });
        shareFAB.setClickable(true);
        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(false);
                editInterface.onShareTimeTable();
            }
        });

        return view;
    }

    public void setEditInterface(EditInterface editInterface) {
        this.editInterface = editInterface;
    }

    @Override
    public void refreshedContent(SwipeRefreshLayout refreshLayout) {
        if (refreshListener == null) {
            refreshLayout.setRefreshing(false);
        } else {
            refreshListener.refreshedContent(refreshLayout);
        }
    }

    public interface EditInterface {
        void onEditLesson(int day, HWGrade grade);

        void onAddLesson(int day, HWGrade grade);

        void onShareTimeTable();
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
    }
}
