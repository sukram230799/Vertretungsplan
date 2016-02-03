package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

public class TabbedTimeTableFragment extends Fragment {

    private static HWGrade grade;
    private static final String GRADE = "grade";

    public static final String TAG = "TabbedTimeTableFragment";

    ViewPager pager;
    PagerAdapter pagerAdapter;
    FloatingActionButton fab;

    public TabbedTimeTableFragment() {
        // Required empty public constructor
    }

    public static TabbedTimeTableFragment newInstance(HWGrade grade) {
        Log.d(TAG, "newInstance");
        TabbedTimeTableFragment fragment = new TabbedTimeTableFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.get_GradeName());
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
        pagerAdapter = new TimeTablePager(getChildFragmentManager(), grade);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);


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
}
