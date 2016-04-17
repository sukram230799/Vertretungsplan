package wuest.markus.vertretungsplan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VPWidgetPreviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VPWidgetPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VPWidgetPreviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_DESIGN = "design";

    //private String mParam1;
    //private String mParam2;
    private int type;

    private OnFragmentInteractionListener mListener;

    public VPWidgetPreviewFragment() {
        // Required empty public constructor
    }

    public static VPWidgetPreviewFragment newInstance(int design) {
        VPWidgetPreviewFragment fragment = new VPWidgetPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DESIGN, design);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VPWgPrFrg", "@onCreate");
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_DESIGN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HWGrade grade = new HWGrade(Preferences.readStringFromPreferences(getContext(), getString(R.string.SELECTED_GRADE), "KLASSE"));

        View layout = inflater.inflate(R.layout.vpwidget, container, false);
        ListView listView = (ListView) layout.findViewById(R.id.vpwidget);
        VPWidgetAdapterPreview widgetAdapterPreview;
        if (type <= 1) {
            ArrayList<VPData[]> vpDataArrayList = new ArrayList<>(4);
            vpDataArrayList.add(new VPData[]{
                    new VPData(-1, grade, 1, "FCKW", "A1337", "Entfall", "Keine Lust", new Date()),
                    new VPData(-1, grade, 2, "FCKW", "A1337", "Entfall", "Keine Lust", new Date())
            });
            vpDataArrayList.add(new VPData[]{
                    new VPData(-1, grade, 3, "CO", "A1337", "Entfall", "Keine Lust", new Date()),
                    new VPData(-1, grade, 4, "CO", "A1337", "Entfall", "Keine Lust", new Date())
            });
            vpDataArrayList.add(new VPData[]{
                    new VPData(-1, grade, 5, "N2O", "A1337", "Entfall", "Keine Lust", new Date()),
                    new VPData(-1, grade, 6, "N2O", "A1337", "Entfall", "Keine Lust", new Date())
            });
            vpDataArrayList.add(new VPData[]{
                    new VPData(-1, grade, 8, "CH4", "A1337", "Entfall", "Keine Lust", new Date()),
                    new VPData(-1, grade, 9, "CH4", "A1337", "Entfall", "Keine Lust", new Date())
            });
            widgetAdapterPreview = new VPWidgetAdapterPreview(getContext(), (ArrayList) vpDataArrayList, type);
        } else if (type <= 2) {
            ArrayList<HWLesson> hwLessonArrayList = new ArrayList<>(4);
            hwLessonArrayList.add(new HWLesson(-1, grade, 1, 2, "TEA", "FCKW", "A314", "W"));
            hwLessonArrayList.add(new HWLesson(-1, grade, 3, 2, "TEA", "CO", "A314", "W"));
            hwLessonArrayList.add(new HWLesson(-1, grade, 5, 2, "TEA", "N2O", "A314", "W"));
            hwLessonArrayList.add(new HWLesson(-1, grade, 8, 2, "TEA", "CH4", "A314", "W"));
            widgetAdapterPreview = new VPWidgetAdapterPreview(getContext(), (ArrayList) hwLessonArrayList, type);
        } else {
            ArrayList<HWPlan> hwPlanArrayList = new ArrayList<>(4);
            hwPlanArrayList.add(new HWPlan(grade, 1, 2, -1, "TEA", "FCKW", "A314", "W", -1, null, null, null, null, null));
            hwPlanArrayList.add(new HWPlan(grade, 2, 2, -1, "TEA", "CO", "A314", "W", -1, "N2O", "---", "Entfall", "Keine Lust", new Date()));
            hwPlanArrayList.add(new HWPlan(grade, 3, 2, -1, null, null, null, null, -1, "CH4", "---", "Entfall", "Keine Lust", new Date()));
            widgetAdapterPreview = new VPWidgetAdapterPreview(getContext(), (ArrayList) hwPlanArrayList, type);
        }
        listView.setAdapter(widgetAdapterPreview);
        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
