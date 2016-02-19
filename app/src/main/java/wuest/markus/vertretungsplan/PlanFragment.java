package wuest.markus.vertretungsplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

//import android.support.design.widget.FloatingActionButton;

public class PlanFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PlanAdapter.ClickListener {

    ProgressDialog progressDialog;
    public static boolean showCheckBoxes = false;

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                final Context context = (Context) msg.obj;
                final Bundle bundle = msg.getData();
                progressDialog.dismiss();
            }
        }
    };


    private static final String TAG = "TimeTableFragment";
    //private static int position;
    private static HWGrade grade;
    private static final String GRADE = "grade";
    private static int weekDay;
    private static final String WEEK_DAY = "weekDay";
    private static boolean showFAB;
    private static final String FAB = "fab";

    private static HWTime time;

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    //private static boolean editMode;
    //private static final String EDIT_MODE = "editMode";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshContentListener refreshListener;


    private RecyclerView recyclerView;
    //private TableAdapter tableAdapter;
    //private FloatingActionButton editFAB;

    private FloatingActionMenu fab;
    private FloatingActionButton shareFAB;
    private FloatingActionButton editFAB;
    private FloatingActionButton newFAB;

    private List<HWPlan> data = Collections.emptyList();
    private EditInterface editInterface;
    PlanAdapter planAdapter;

    public static PlanFragment newInstance(HWGrade grade, HWTime time, boolean showFab) {
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.getGradeName());
        args.putInt(YEAR, time.getYear());
        args.putInt(MONTH, time.getMonth());
        args.putInt(DAY, time.getDay());
        args.putBoolean(FAB, showFab);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlanFragment newInstance(HWGrade grade, int day, boolean showFAB) {
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.getGradeName());
        args.putInt(WEEK_DAY, day);
        args.putBoolean(FAB, showFAB);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "oC");
        if (savedInstanceState != null) {
            grade = new HWGrade(savedInstanceState.getString(GRADE));
            weekDay = savedInstanceState.getInt(WEEK_DAY, -1);
            showFAB = savedInstanceState.getBoolean(FAB);
            Log.v(TAG, "savedInstanceState not null");
            if(weekDay == -1){
                time = new HWTime(0,0,savedInstanceState.getInt(YEAR), savedInstanceState.getInt(MONTH), savedInstanceState.getInt(DAY));
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(time.toDate());
                weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            }
        } else {
            grade = new HWGrade(getArguments().getString(GRADE));
            weekDay = getArguments().getInt(WEEK_DAY, -1);
            showFAB = getArguments().getBoolean(FAB);
            if(weekDay == -1){
                time = new HWTime(0,0,getArguments().getInt(YEAR), getArguments().getInt(MONTH), getArguments().getInt(DAY));
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(time.toDate());
                weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            }
        }
        Log.v(TAG, "section_number: " + grade.getGradeName());
        //if (position < 0) position = 0;
        this.data = new ArrayList<>();
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        try {
            HWLesson[] hwLessons = dbHandler.getTimeTable(grade, weekDay /*Calendar.getInstance().get(Calendar.DAY_OF_WEEK)*/);
            HWPlan[] plan;
            String[] subscribedSubjects = dbHandler.getSubscribedSubjects();
            int week = GregorianCalendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            hwLessons = TimeTableHelper.selectLessonsFromRepeatType(hwLessons, week, subscribedSubjects, getActivity()); //No CombineData, because of better layout;
            hwLessons = TimeTableHelper.fillGabs(hwLessons, week, weekDay, getActivity());
            try {
                VPData[] vpData = dbHandler.getVP(grade);
                vpData = TimeTableHelper.selectVPDataFromWeekDay(vpData, weekDay);
                plan = TimeTableHelper.combineVPSP(hwLessons, vpData, false, false);
            } catch (DBError e) {
                e.printStackTrace();
                plan = TimeTableHelper.combineVPSP(hwLessons, new VPData[0], false, false);
            }
            this.data = Arrays.asList(plan);
        } catch (DBError dbError) {
            dbError.printStackTrace();
            if (dbError.getMessage().equals(DBError.TABLEEMPTY)) {
                Log.v(TAG, DBError.TABLEEMPTY);
            }
        }
        dbHandler.close();

        if (TimeTableHelper.updateQuarters(getContext(), new HWTime(Calendar.getInstance()))) {
            new Thread(new GetRepeatTypes(getContext(), updateHandler)).start();
            progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Initializing");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

    }

    public PlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "oCV");
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_time_table, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.table_recycler_view);

        planAdapter = new PlanAdapter(getActivity(), data);
        planAdapter.setClickListener(this);
        recyclerView.setAdapter(planAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_to_reload_plan);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        fab = (FloatingActionMenu) layout.findViewById(R.id.fab);
        shareFAB = (FloatingActionButton) layout.findViewById(R.id.fab_share);
        editFAB = (FloatingActionButton) layout.findViewById(R.id.fab_edit);
        newFAB = (FloatingActionButton) layout.findViewById(R.id.fab_new);

        final ListView noTable = (ListView) layout.findViewById(R.id.noTable);
        String[] i = {"Kein Stundenplan."};
        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.no_vp, i);
        noTable.setAdapter(adapter);
        if (data.isEmpty()) {
            noTable.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noTable.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        noTable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (data.isEmpty()) {
                    int topRowVerticalPosition = (noTable == null || noTable.getChildCount() == 0) ? 0 : noTable.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                } else {
                    int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                }
            }
        });

        if (!showFAB) {
            //fab.hide(false);
            fab.hideMenu(false);
        }
        //mSwipeRefreshLayout.setOnRefreshListener();
        editFAB.setClickable(true);
        editFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick");
                showCheckBoxes = !showCheckBoxes;
                //tableAdapter.notifyDataSetChanged();
                //setShowCheckBoxes(showCheckBoxes);
                if (editInterface != null) {
                    editInterface.onEditLesson(weekDay, grade);
                }
            }
        });
        newFAB.setClickable(true);
        newFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                editInterface.onAddLesson(weekDay, grade);
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

        return layout;
        //return inflater.inflate(R.layout.fragment_vp, container, false);
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (refreshListener != null) {
            refreshListener.refreshedContent(mSwipeRefreshLayout);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void tableItemClicked(View view, int position, boolean longpress) {

    }

    @Override
    public void tableItemLongClicked(View view, int position) {

    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
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
