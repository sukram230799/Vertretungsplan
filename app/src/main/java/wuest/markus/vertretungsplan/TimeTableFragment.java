package wuest.markus.vertretungsplan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeTableFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TableAdapter.ClickListener {

    ProgressDialog progressDialog;

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
    private static int day;
    private static final String DAY = "day";
    private static boolean showFAB;
    private static final String FAB = "fab";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshContentListener refreshListener;


    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private FloatingActionButton editFAB;
    private List<HWLesson> data = Collections.emptyList();

    public static TimeTableFragment newInstance(HWGrade grade, int day, boolean showFAB) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.get_GradeName());
        args.putInt(DAY, day);
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
            day = savedInstanceState.getInt(DAY);
            showFAB = savedInstanceState.getBoolean(FAB);
            Log.v(TAG, "savedInstanceState not null");
        } else {
            grade = new HWGrade(getArguments().getString(GRADE));
            day = getArguments().getInt(DAY);
            showFAB = getArguments().getBoolean(FAB);
        }
        Log.v(TAG, "section_number: " + grade.get_GradeName());
        //if (position < 0) position = 0;
        this.data = new ArrayList<>();
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        try {
            HWLesson[] hwLessons = dbHandler.getTimeTable(grade, day /*Calendar.getInstance().get(Calendar.DAY_OF_WEEK)*/);
            int week = GregorianCalendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            hwLessons = CombineData.combineHWLessons(TimeTableHelper.selectLessonsFromRepeatType(hwLessons, week, getActivity()));
            this.data = Arrays.asList(hwLessons);
            /*for (VPData data : vpData) {
                this.data.add(data);
            }*/
        } catch (DBError dbError) {
            dbError.printStackTrace();
            if (dbError.getMessage().equals(DBError.TABLEEMPTY)) {
                Log.v(TAG, DBError.TABLEEMPTY);
                //dbHandler.addPlan(datas);
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

    public TimeTableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "oCV");
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_time_table, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.table_recycler_view);
        tableAdapter = new TableAdapter(getActivity(), data);
        tableAdapter.setClickListener(this);
        Log.v(TAG, String.valueOf(tableAdapter));
        Log.v(TAG, String.valueOf(recyclerView));
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_to_reload_plan);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        editFAB = (FloatingActionButton) layout.findViewById(R.id.fab);

        final ListView noTable = (ListView) layout.findViewById(R.id.noTable);
        String[] i = {"Kein Stundenplan."};
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.no_vp, i);
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

        if(!showFAB){
            editFAB.hide();
        }
        //mSwipeRefreshLayout.setOnRefreshListener();
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
}
