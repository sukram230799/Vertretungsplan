package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class VPWidgetAdapterPreview extends ArrayAdapter<Object> {

    int type;

    public VPWidgetAdapterPreview(Context context, ArrayList<Object> vpDataArrayList, int type) {
        super(context, 0, vpDataArrayList);
        this.type = type;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView spTextHour;
        TextView spTextTeacher;
        TextView spTextSubject;
        TextView spTextRoom;
        TextView spTextRepeatType;
        TextView spTextBreak;

        TextView vpTextDate;
        TextView vpTextHour;
        TextView vpTextSubject;
        TextView vpTextRoom;
        TextView vpTextInfo1;
        TextView vpTextInfo2;
        int design;
        switch (type) {
            case 0:
                design = R.layout.widget_full_row;
                break;
            case 1:
                design = R.layout.widget_minimal_row;
                break;
            case 2:
                design = R.layout.widget_table_row;
                break;
            case 3:
                design = R.layout.widget_plan_row;
                break;
            default:
                design = R.layout.widget_full_row;
                break;
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(design, parent, false);
        }

        String[] text;
        VPData vpData;
        HWLesson lesson;
        HWPlan plan;
        if (type <= 1) {
            vpData = ((VPData[]) getItem(position))[0];
            Integer[] hours = new Integer[((VPData[]) getItem(position)).length];
            for (int i = 0; i < hours.length; i++) {
                hours[i] = ((VPData[]) getItem(position))[i].getHour();
            }
            text = VPWidgetTextProcess.processVPData(getContext(), vpData, hours);
            if (text.length < 6) {
                text = new String[]{"Something", "went", "horribly", "wrong!", "We're", "sorry"};
            }
        } else if (type <= 2) {
            lesson = (HWLesson) getItem(position);
            text = VPWidgetTextProcess.processSPData(getContext(), lesson);
        } else {
            plan = (HWPlan) getItem(position);
            text = VPWidgetTextProcess.processPlan(getContext(), plan);
        }


        switch (type) {
            case 0:
                vpTextDate = (TextView) convertView.findViewById(R.id.vpTextDate);
                vpTextHour = (TextView) convertView.findViewById(R.id.vpTextHour);
                vpTextSubject = (TextView) convertView.findViewById(R.id.vpTextSubject);
                vpTextRoom = (TextView) convertView.findViewById(R.id.vpTextRoom);
                vpTextInfo1 = (TextView) convertView.findViewById(R.id.vpTextInfo1);
                vpTextInfo2 = (TextView) convertView.findViewById(R.id.vpTextInfo2);

                vpTextDate.setText(text[0]);
                vpTextHour.setText(text[1]);
                vpTextSubject.setText(text[2]);
                vpTextRoom.setText(text[3]);
                vpTextInfo1.setText(text[4]);
                vpTextInfo2.setText(text[5]);
                break;
            case 1:
                TextView textBrief;

                textBrief = (TextView) convertView.findViewById(R.id.textBrief);

                textBrief.setText(VPWidgetTextProcess.brief(text));
                break;
            case 2:

                spTextHour = (TextView) convertView.findViewById(R.id.spTextHour);
                spTextTeacher = (TextView) convertView.findViewById(R.id.spTextTeacher);
                spTextSubject = (TextView) convertView.findViewById(R.id.spTextSubject);
                spTextRoom = (TextView) convertView.findViewById(R.id.spTextRoom);
                spTextRepeatType = (TextView) convertView.findViewById(R.id.spTextRepeatType);
                spTextBreak = (TextView) convertView.findViewById(R.id.spTextBreak);

                if (text[0].contains("\n")) {
                    spTextHour.setGravity(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    spTextHour.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
                }

                if (text.length <= 2) {
                    spTextHour.setVisibility(View.GONE);
                    spTextTeacher.setVisibility(View.GONE);
                    spTextSubject.setVisibility(View.GONE);
                    spTextRoom.setVisibility(View.GONE);
                    spTextRepeatType.setVisibility(View.GONE);

                    spTextBreak.setVisibility(View.VISIBLE);


                    spTextHour.setText(text[0]);
                    spTextBreak.setText(text[1]);
                } else {
                    spTextHour.setText(text[0]);

                    spTextHour.setVisibility(View.VISIBLE);
                    spTextTeacher.setVisibility(View.VISIBLE);
                    spTextSubject.setVisibility(View.VISIBLE);
                    spTextRoom.setVisibility(View.VISIBLE);
                    spTextRepeatType.setVisibility(View.VISIBLE);

                    spTextBreak.setVisibility(View.GONE);


                    spTextTeacher.setText(text[1]);
                    spTextSubject.setText(text[2]);
                    spTextRoom.setText(text[3]);
                    spTextRepeatType.setText(text[4]);

                }
                break;
            case 3:
                vpTextDate = (TextView) convertView.findViewById(R.id.vpTextDate);
                vpTextHour = (TextView) convertView.findViewById(R.id.vpTextHour);
                vpTextSubject = (TextView) convertView.findViewById(R.id.vpTextSubject);
                vpTextRoom = (TextView) convertView.findViewById(R.id.vpTextRoom);
                vpTextInfo1 = (TextView) convertView.findViewById(R.id.vpTextInfo1);
                vpTextInfo2 = (TextView) convertView.findViewById(R.id.vpTextInfo2);


                spTextHour = (TextView) convertView.findViewById(R.id.spTextHour);
                spTextTeacher = (TextView) convertView.findViewById(R.id.spTextTeacher);
                spTextSubject = (TextView) convertView.findViewById(R.id.spTextSubject);
                spTextRoom = (TextView) convertView.findViewById(R.id.spTextRoom);
                spTextRepeatType = (TextView) convertView.findViewById(R.id.spTextRepeatType);
                spTextBreak = (TextView) convertView.findViewById(R.id.spTextBreak);

                if (text.length <= 2) {
                    spTextTeacher.setVisibility(View.GONE);
                    spTextSubject.setVisibility(View.GONE);
                    spTextRoom.setVisibility(View.GONE);
                    spTextRepeatType.setVisibility(View.GONE);

                    spTextBreak.setVisibility(View.VISIBLE);
                    spTextHour.setText(text[0]);
                    spTextBreak.setText(text[1]);
                } else {

                }
                break;
            default:
                vpTextDate = (TextView) convertView.findViewById(R.id.vpTextDate);
                vpTextHour = (TextView) convertView.findViewById(R.id.vpTextHour);
                vpTextSubject = (TextView) convertView.findViewById(R.id.vpTextSubject);
                vpTextRoom = (TextView) convertView.findViewById(R.id.vpTextRoom);
                vpTextInfo1 = (TextView) convertView.findViewById(R.id.vpTextInfo1);
                vpTextInfo2 = (TextView) convertView.findViewById(R.id.vpTextInfo2);

                vpTextDate.setText(text[0]);
                vpTextHour.setText(text[1]);
                vpTextSubject.setText(text[2]);
                vpTextRoom.setText(text[3]);
                vpTextInfo1.setText(text[4]);
                vpTextInfo2.setText(text[5]);
                break;
        }
        return convertView;
    }
}
