package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class VPWidgetAdapterPreview extends ArrayAdapter<VPData> {

    int type;

    public VPWidgetAdapterPreview(Context context, ArrayList<VPData> vpDataArrayList, int type) {
        super(context, 0, vpDataArrayList);
        this.type = type;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int design;
        switch (type) {
            case 0:
                design = R.layout.widget_full_row;
                break;
            case 1:
                design = R.layout.widget_minimal_row;
                break;
            default:
                design = R.layout.widget_full_row;
                break;
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(design, parent, false);
        }

        VPData vpData = getItem(position);
        String[] text = VPWidgetTextProcess.processData(getContext(), vpData);

        if (text.length < 6) {
            text = new String[] {"Something", "went", "horribly", "wrong!", "We're", "sorry"};
        }

        switch (type) {
            case 0:
                TextView textDate;
                TextView textHour;
                TextView textSubject;
                TextView textRoom;
                TextView textInfo1;
                TextView textInfo2;

                textDate = (TextView) convertView.findViewById(R.id.vpTextDate);
                textHour = (TextView) convertView.findViewById(R.id.vpTextHour);
                textSubject = (TextView) convertView.findViewById(R.id.vpTextSubject);
                textRoom = (TextView) convertView.findViewById(R.id.vpTextRoom);
                textInfo1 = (TextView) convertView.findViewById(R.id.vpTextInfo1);
                textInfo2 = (TextView) convertView.findViewById(R.id.vpTextInfo2);

                textDate.setText(text[0]);
                textHour.setText(text[1]);
                textSubject.setText(text[2]);
                textRoom.setText(text[3]);
                textInfo1.setText(text[4]);
                textInfo2.setText(text[5]);
                break;
            case 1:
                TextView textBrief;

                textBrief = (TextView) convertView.findViewById(R.id.textBrief);

                textBrief.setText(VPWidgetTextProcess.brief(text));
                break;
            default:
                textDate = (TextView) convertView.findViewById(R.id.vpTextDate);
                textHour = (TextView) convertView.findViewById(R.id.vpTextHour);
                textSubject = (TextView) convertView.findViewById(R.id.vpTextSubject);
                textRoom = (TextView) convertView.findViewById(R.id.vpTextRoom);
                textInfo1 = (TextView) convertView.findViewById(R.id.vpTextInfo1);
                textInfo2 = (TextView) convertView.findViewById(R.id.vpTextInfo2);

                textDate.setText(text[0]);
                textHour.setText(text[1]);
                textSubject.setText(text[2]);
                textRoom.setText(text[3]);
                textInfo1.setText(text[4]);
                textInfo2.setText(text[5]);
                break;
        }
        return convertView;
    }
}
