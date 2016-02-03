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

        ArrayList<VPData> vpDataArrayList = new ArrayList<>(2);
        Integer[] hour0 = {0, 1};
        Integer[] hour1 = {2, 3};
        Integer[] hour2 = {4, 5};
        Integer[] hour3 = {6, 7};
        vpDataArrayList.add(new VPData(grade, hour0, "FCKW", "A1337", "Entfall", "Keine Lust", new Date()));
        vpDataArrayList.add(new VPData(grade, hour1, "CO", "A1337", "Entfall", "Keine Lust", new Date()));
        vpDataArrayList.add(new VPData(grade, hour2, "N2O", "A1337", "Entfall", "Keine Lust", new Date()));
        vpDataArrayList.add(new VPData(grade, hour3, "CH4", "A1337", "Entfall", "Keine Lust", new Date()));



        View layout = inflater.inflate(R.layout.vpwidget, container, false);
        ListView listView = (ListView) layout.findViewById(R.id.vpwidget);
        VPWidgetAdapterPreview widgetAdapterPreview = new VPWidgetAdapterPreview(getContext(), vpDataArrayList, type);
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
