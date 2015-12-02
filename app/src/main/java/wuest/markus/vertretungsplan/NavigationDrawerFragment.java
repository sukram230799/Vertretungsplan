package wuest.markus.vertretungsplan;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements GradeAdapter.ClickListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private View containerView;

    private RecyclerView recyclerView;
    private GradeAdapter gradeAdapter;
    private NavigationDrawerFragment navigationDrawerFragment = this;

    private ItemSelectedListener itemSelectedListener;
    public int selectedItem;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            gradeAdapter = new GradeAdapter(getActivity(), getData(getActivity()));
            gradeAdapter.setClickListener(navigationDrawerFragment);
            recyclerView.setAdapter(gradeAdapter);
        }
    };

    public NavigationDrawerFragment() {
        //preferences = new Preferences(getActivity());
        // Required empty public constructor
    }

    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(Preferences.readStringFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState == null) {
            mFromSavedInstanceState = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.grade_recycler_view);
        gradeAdapter = new GradeAdapter(getActivity(), getData(getActivity()));
        gradeAdapter.setClickListener(this);
        recyclerView.setAdapter(gradeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        gradeAdapter = new GradeAdapter(getActivity(), getData(getActivity()));
        gradeAdapter.setClickListener(this);
        recyclerView.setAdapter(gradeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.v("NavDrawFrag", "onResume");
    }

    public static List<HWGrade> getData(Context context) {
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        List<HWGrade> data;
        try {
            data = new ArrayList<>(Arrays.asList(dbHandler.getGrades()));
        } catch (DBError dbError) {
            dbError.printStackTrace();
            data = new ArrayList<>();
        }
        return data;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        selectedItem = -1;
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    Preferences.saveStringToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().supportInvalidateOptionsMenu();
                //super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().supportInvalidateOptionsMenu();
            }
        };
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void gradeItemClicked(View view, int position, boolean longclick) {
        if (selectedItem != position) {
            //Not used yet
        }
        if (!longclick) {
            mDrawerLayout.closeDrawers();
        }
        itemSelectedListener.gradeItemSelected(view, position, longclick);
    }

    public interface ItemSelectedListener {
        void gradeItemSelected(View view, int position, boolean longclick);
    }
}
