package wuest.markus.vertretungsplan;


import android.os.Bundle;
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
import java.util.Collections;
import java.util.List;

public class VPFragment extends Fragment implements VPAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "VPFragment";
    //private static int position;
    private static HWGrade grade;
    private static final String GRADE = "grade";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshContentListener refreshListener;

    private RecyclerView recyclerView;
    private VPAdapter VPAdapter;
    private List<VPData> data = Collections.emptyList();

    public static VPFragment newInstance(HWGrade grade) {
        //this.position = position;
        VPFragment fragment = new VPFragment();
        Bundle args = new Bundle();
        args.putString(GRADE, grade.getGradeName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "oC");
        if (savedInstanceState != null) {
            grade = new HWGrade(savedInstanceState.getString(GRADE));
            Log.v(TAG, "savedInstanceState not null");
        }
        grade = new HWGrade(getArguments().getString(GRADE));
        Log.v(TAG, "GRADE: " + grade.getGradeName());
        //if (position < 0) position = 0;
        this.data = new ArrayList<>();
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        try {
            VPData[] vpData = dbHandler.getVP(grade);
            vpData = CombineData.combineVP(vpData);
            this.data = Arrays.asList(vpData);
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
    }

    public VPFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "oCV");
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_vp, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.plan_recycler_view);
        VPAdapter = new VPAdapter(getActivity(), data);
        VPAdapter.setClickListener(this);
        Log.v(TAG, String.valueOf(VPAdapter));
        Log.v(TAG, String.valueOf(recyclerView));
        recyclerView.setAdapter(VPAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_to_reload_plan);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        /*if (data.isEmpty() && refreshListener != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshListener.refreshedContent(mSwipeRefreshLayout);
        }*/
//        TextView noVPText = (TextView) layout.findViewById(R.id.noVPText);
        final ListView noVP = (ListView) layout.findViewById(R.id.noVP);
        String[] i = {"Kein VP."};
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.no_vp, i);
        noVP.setAdapter(adapter);
        if (data.isEmpty()) {
//            noVPText.setVisibility(View.VISIBLE);
            noVP.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
//            noVPText.setVisibility(View.GONE);
            noVP.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        noVP.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (data.isEmpty()) {
                    int topRowVerticalPosition = (noVP == null || noVP.getChildCount() == 0) ? 0 : noVP.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                }
                else {
                    int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                }
            }
        });
        //mSwipeRefreshLayout.setOnRefreshListener();
        return layout;
        //return inflater.inflate(R.layout.fragment_vp, container, false);
    }

    public void setRefreshListener(RefreshContentListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public void planItemClicked(View view, int position, boolean longpress) {

    }

    @Override
    public void planItemLongClicked(View view, int position) {

    }

    @Override
    public void onRefresh() {
        Log.v(TAG, "reload");
        mSwipeRefreshLayout.setRefreshing(true);
        if (refreshListener != null) {
            refreshListener.refreshedContent(mSwipeRefreshLayout);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public interface RefreshContentListener {
        void refreshedContent(SwipeRefreshLayout refreshLayout);
    }

    /*public List<VPData> getData() {
        return this.data;
    }*/
}
