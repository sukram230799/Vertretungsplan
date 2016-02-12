package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;

public class TabbedTimeTableFragment extends Fragment {

    private static HWGrade grade;
    private static final String GRADE = "grade";

    public static final String TAG = "TabbedTimeTableFragment";

    ViewPager pager;
    TimeTablePagerAdapter pagerAdapter;
    PagerTabStrip pagerTabStrip;

    private FloatingActionMenu fab;
    private FloatingActionButton shareFAB;
    private FloatingActionButton editFAB;
    private FloatingActionButton newFAB;

    EditInterface editInterface;

    boolean showCheckBoxes = false;

    public TabbedTimeTableFragment() {
        // Required empty public constructor
    }

    public static TabbedTimeTableFragment newInstance(HWGrade grade) {
        Log.d(TAG, "newInstance");
        TabbedTimeTableFragment fragment = new TabbedTimeTableFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed_time_table, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new TimeTablePagerAdapter(getChildFragmentManager(), grade);
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
                showCheckBoxes = !showCheckBoxes;
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
                fab.close(true);
                editInterface.onShareTimeTable();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
