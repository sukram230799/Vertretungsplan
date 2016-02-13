package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawerFragment extends Fragment implements DrawerAdapter.ClickListener, AdapterView.OnItemSelectedListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ItemSelectedListener itemSelectedListener;

    private RecyclerView recyclerView;
    private DrawerAdapter drawerAdapter;
    private Spinner gradeSpinner;
    private View containerView;

    public int selectedItem;


    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer_v2";
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    public DrawerFragment() {
        // Required empty public constructor
    }

    public static DrawerFragment newInstance(String param1, String param2) {
        DrawerFragment fragment = new DrawerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Preferences.readBooleanFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, false);
        if (savedInstanceState == null) {
            mFromSavedInstanceState = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        drawerAdapter = new DrawerAdapter(getActivity(), getData(getActivity()));
        drawerAdapter.setClickListener(this);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        gradeSpinner = (Spinner) layout.findViewById(R.id.spinner);
        String selectedGrade = Preferences.readStringFromPreferences(getActivity(), getString(R.string.SELECTED_GRADE), "");
        try{
            DBHandler handler = new DBHandler(getActivity(), null, null, 0);
            HWGrade[] grades = handler.getGrades();
            ArrayList<String> gradeNameArrayList = new ArrayList<>(grades.length);
            for(HWGrade grade : grades){
                gradeNameArrayList.add(grade.getGradeName());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.spinner_layout, gradeNameArrayList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            gradeSpinner.setAdapter(arrayAdapter);
            gradeSpinner.setSelection(arrayAdapter.getPosition(selectedGrade), true);
        } catch (DBError error) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.spinner_layout, new String[] {selectedGrade});
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gradeSpinner.setAdapter(arrayAdapter);
            gradeSpinner.setSelection(0, true);
            error.printStackTrace();
        }
        gradeSpinner.setOnItemSelectedListener(this);
        //return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return layout;
    }


    public static List<String> getData(Context context) {
        List<String> data = new ArrayList<>(Arrays.asList(new String[] {"Vertretungsplan", "Stundenplan", "Wochenplan", "Stunden- & Vertretunsplan", "Wochen- & Vertretunsplan"}));
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
                    Preferences.saveBooleanToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer);
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

    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public void drawerItemClicked(View view, int position, boolean longpress) {
        if (selectedItem != position) {
            //Not used yet
        }
        if (!longpress) {
            mDrawerLayout.closeDrawer(containerView);
        }
        itemSelectedListener.drawerItemSelected(view, position, longpress);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemSelectedListener.gradeItemSelected(view, position, false);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface ItemSelectedListener {
        void drawerItemSelected(View view, int position, boolean longclick);
        void gradeItemSelected(View view, int position, boolean longclick);
    }
}
