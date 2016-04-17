package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class HelpDialog extends DialogFragment {

    private static final String TYPE = "TYPE";
    private int type;

    public static HelpDialog newInstance(int type) {

        Bundle args = new Bundle();

        args.putInt(TYPE, type);

        HelpDialog fragment = new HelpDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            type = savedInstanceState.getInt(TYPE);
        } else if(getArguments() != null){
            type = getArguments().getInt(TYPE);
        } else {
            type = -1;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view;
        switch (type){
            case 1: //NCF Sare
                view = layoutInflater.inflate(R.layout.fragment_nfc_help, null);
                builder.setTitle("Per NFC Teilen");
                break;
            default:
                view = layoutInflater.inflate(R.layout.activity_settings, null);
                dismiss();
        }

        builder.setView(view);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        //builder.setCustomTitle(view);
        //return super.onCreateDialog(savedInstanceState);
        return builder.show();
    }
}
