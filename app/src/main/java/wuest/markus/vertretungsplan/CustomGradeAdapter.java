package wuest.markus.vertretungsplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CustomGradeAdapter extends ArrayAdapter<HWGrade>{

    public CustomGradeAdapter(Context context, HWGrade[] resource) {
        super(context, R.layout.custom_plan_row, resource);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_grade_row_listview, parent, false);
        HWGrade hwGrade = getItem(position);

        TextView textGrade = (TextView) view.findViewById(R.id.textGrade);

        textGrade.setText(hwGrade.getGradeName());

        return view;
    }
}
