package wuest.markus.vertretungsplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;

/**
 * Created by Markus on 16.02.2016.
 */
public class TabbedPlanFragment extends Fragment{

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
        pagerAdapter = new PlanPagerAdapter(getChildFragmentManager(), grade);
        pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tab_strip);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2);
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
                editInterface.onEditLesson(pager.getCurrentItem() + 2, grade);
            }
        });
        newFAB.setClickable(true);
        newFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                editInterface.onAddLesson(pager.getCurrentItem() + 2, grade);
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

    public interface EditInterface {
        void onEditLesson(int day, HWGrade grade);

        void onAddLesson(int day, HWGrade grade);

        void onShareTimeTable();
    }
}
