package wuest.markus.vertretungsplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Markus on 12.09.2015.
 */
public class LoadingFragment extends Fragment implements View.OnClickListener {

/*
    public static LoadingFragment newInstance(int times){
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putInt("times", times);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_loading, container, false);
        return layout;
    }

    @Override
    public void onClick(View v) {
        System.exit(0);
    }
}
