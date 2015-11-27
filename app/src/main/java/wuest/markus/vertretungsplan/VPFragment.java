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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class VPFragment extends Fragment implements PlanAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "VPFragment";
    private static int position;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshContentListener refreshListener;

    public static VPFragment newInstance(int position) {
        //this.position = position;
        VPFragment fragment = new VPFragment();
        Bundle args = new Bundle();
        args.putInt("section_number", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "oC");
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("section_number");
            Log.v(TAG, "savedInstanceState not null");
        }
        position = getArguments().getInt("section_number");
        Log.v(TAG, "section_number: " + position);
        //if (position < 0) position = 0;
        this.data = new ArrayList<VPData>();
        DBHandler dbHandler = new DBHandler(getContext(), null, null, 1);
        try {
            VPData[] vpData = dbHandler.getVP(dbHandler.getGrade(position));
            vpData = CombineData.combine(vpData);
            for (VPData data : vpData) {
                this.data.add(data);
            }
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

    private RecyclerView recyclerView;
    private PlanAdapter planAdapter;
    private List<VPData> data = Collections.emptyList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "oCV");
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_vp, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.plan_recycler_view);
        planAdapter = new PlanAdapter(getActivity(), data);
        planAdapter.setClickListener(this);
        Log.v(TAG, String.valueOf(planAdapter));
        Log.v(TAG, String.valueOf(recyclerView));
        recyclerView.setAdapter(planAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_to_reload_plan);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        /*if (data.isEmpty() && refreshListener != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshListener.refreshedContent(mSwipeRefreshLayout);
        }*/
        TextView noVPText = (TextView) layout.findViewById(R.id.noVPText);
        if(data.isEmpty()){
            noVPText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noVPText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
        public void refreshedContent(SwipeRefreshLayout refreshLayout);
    }

    /*public List<VPData> getData() {
        return this.data;
    }*/
}
