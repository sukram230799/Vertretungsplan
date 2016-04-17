package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends DialogFragment {

    private static final String URL = "URL";
    private String link = "";

    private QRRead qrRead;

    public static ShareDialog newInstance(String link) {

        Bundle args = new Bundle();
        args.putString(URL, link);
        ShareDialog fragment = new ShareDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            link = savedInstanceState.getString(URL);
        } else if (getArguments() != null) {
            link = getArguments().getString(URL);
        } else {
            link = "";
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_share_dialog, null);

        LinearLayout linearLayoutNFC = (LinearLayout) view.findViewById(R.id.linearLayoutNFC);
        linearLayoutNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpDialog helpDialog = HelpDialog.newInstance(1);
                helpDialog.show(getFragmentManager(), "HELP");
            }
        });

        ImageButton imageButtonNFC = (ImageButton) view.findViewById(R.id.imageButtonNFC);
        imageButtonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                } else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                dismiss();
            }
        });

        ImageButton imageButtonQR = (ImageButton) view.findViewById(R.id.imageButtonQR);
        imageButtonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encodeBarcode("TEXT_TYPE", link);
                dismiss();
            }
        });

        ImageButton imageButtonQRScan = (ImageButton) view.findViewById(R.id.imageButtonQRScan);
        imageButtonQRScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrRead != null) {
                    qrRead.readQR();
                } else {
                    Toast.makeText(getActivity(), "QR Lesen ist momentan nicht möglich.", Toast.LENGTH_LONG).show();
                }
                dismiss();
            }
        });

        ImageButton imageButtonLink = (ImageButton) view.findViewById(R.id.imageButtonLink);
        imageButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Stundenplan:\n\n" + link);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Stundenplan versenden"));
                dismiss();
            }
        });

        ImageButton imageButtonWhatsApp = (ImageButton) view.findViewById(R.id.imageButtonWhatsApp);
        imageButtonWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");

                    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, "Stundenplan:\n" + link);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Teilen über");

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

    private void encodeBarcode(CharSequence type, CharSequence data) {
        try {
            /*IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.autoWide();
            integrator.addExtra("ENCODE_SHOW_CONTENTS", false);
            integrator.shareText(data, type);*/
            Intent intent = new Intent();
            intent.setAction("com.google.zxing.client.android.ENCODE");

            intent.putExtra("ENCODE_TYPE", type);
            intent.putExtra("ENCODE_DATA", data);
            intent.putExtra(Intent.EXTRA_TITLE, "Stundenplan");
            //intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.CODABAR.toString());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            intent.putExtra("ENCODE_SHOW_CONTENTS", false);
            //attachMoreExtras(intent); Same as above!
            startActivity(intent);
            //startActivityForResult(intent, 230799);
        } catch (Exception e) {
            try {
                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void setQrRead(QRRead qrRead) {
        this.qrRead = qrRead;
    }

    public interface QRRead {
        void readQR();
    }
}
