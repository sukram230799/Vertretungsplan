package wuest.markus.vertretungsplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CustomPlanAdapter extends ArrayAdapter<VPData>{

    public CustomPlanAdapter(Context context, VPData[] resource) {
        super(context, R.layout.custom_plan_row, (VPData[]) resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_plan_row, parent, false);
        VPData vpData = getItem(position);
        TextView textHour = (TextView) view.findViewById(R.id.textHour);
        TextView textSubject = (TextView) view.findViewById(R.id.textSubject);
        TextView textRoom = (TextView) view.findViewById(R.id.textRoom);
        TextView textInfo1 = (TextView) view.findViewById(R.id.textInfo1);
        TextView textInfo2 = (TextView) view.findViewById(R.id.textInfo2);

        //textHour.setText(String.valueOf(vpData.get_hour()));
        textSubject.setText(vpData.get_subject());
        textRoom.setText(vpData.get_room());
        textInfo1.setText(vpData.get_info1());
        textInfo2.setText(vpData.get_info2());

        return view;
    }
}
