package wuest.markus.vertretungsplan;

//import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import java.util.Collections;
import java.util.List;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {
    private static final String TAG = "GradeAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<HWGrade> data = Collections.emptyList();
    private String chosenGrade = "NULL";
    private int chosenGradePosition = -1;
    private ClickListener clickListener;
    int selected = -1;
    //private Preferences preferences;

    public GradeAdapter(Context context, List<HWGrade> data) {
        Log.v("DEBUG.GA", data.size() + "");
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void deleteItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    /*
    public void selectItem(int position) {

    }

    public void deselectItem(int position) {

    }*/

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.custom_grade_row, parent, false);
        //GradeViewHolder holder = new GradeViewHolder(view);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int i) {
        holder.position = i;
        if (chosenGrade.equals("NULL")) {
            chosenGrade = Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GRADE), "NULL");
            Log.v("DEBUG", chosenGrade);
        }
        Log.v("DEBUG.onBVH", data.get(i).get_GradeName());
        holder.title.setText(data.get(i).get_GradeName());
        if (chosenGrade.equals(data.get(i).get_GradeName())) {
            Log.v("DEBUG.found", chosenGrade);
            chosenGradePosition = i;
            //holder.title.setBackgroundColor(Color.parseColor("#888888"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.title.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));//getColor(R.color.colorPrimary));
            } else {
                holder.title.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
            //holder.title.setBackgroundColor(Color.parseColor());
        } else {
            if (i == selected) {
                holder.title.setBackgroundColor(Color.parseColor("#cccccc"));
            } else {
                holder.title.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class GradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        int position;

        public GradeViewHolder(View itemView) {
            super(itemView);
            itemView.setLongClickable(true);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            title = (TextView) itemView.findViewById(R.id.gradeLabel);
        }

        private boolean longclick;

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                notifyItemChanged(selected);
                notifyItemChanged(position);
                selected = position;
                //Log.v("DEBUG.gLP", String.valueOf(getLayoutPosition()));
                //Log.v("DEBUG.gAP", String.valueOf(getAdapterPosition()));
                clickListener.gradeItemClicked(v, getAdapterPosition(), longclick);
                longclick = false;
                v.isSelected();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Log.v(TAG, v.toString());
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            Snackbar.make(v, "Klasse " + data.get(getAdapterPosition()).get_GradeName() + " wurde als Standard festgelegt.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Preferences.saveStringToPreferences(context, context.getString(R.string.SELECTED_GRADE), data.get(getAdapterPosition()).get_GradeName());
            notifyItemChanged(chosenGradePosition);
            notifyItemChanged(position);
            chosenGrade = data.get(getAdapterPosition()).get_GradeName();
            //clickListener.gradeItemLongClicked(v, getAdapterPosition());
            longclick = true;
            return false;
            //return true;
        }
    }

    public interface ClickListener {
        void gradeItemClicked(View view, int position, boolean longpress);

        //public void gradeItemLongClicked(View view, int position);
    }
}
