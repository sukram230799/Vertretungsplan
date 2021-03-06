package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private static final String TAG = "PlanAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<VPData> data = Collections.emptyList();
    private ClickListener clickListener;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public PlanAdapter(Context context, List<VPData> data) {
        Log.v(TAG, "@PlanAdapter");
        Log.v(TAG, "" + data.size());
        Log.v(TAG, String.valueOf(context));
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        Log.v(TAG, "-PlanAdapter");
    }

    public void deleteItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "oCVH");
        View view = inflater.inflate(R.layout.custom_plan_row, parent, false);
        PlanViewHolder holder = new PlanViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        Log.v(TAG, "oBVH");
        String Wochentag;
        Calendar c = new GregorianCalendar();
        c.setTime(data.get(position).get_date());
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                Wochentag = context.getString(R.string.dayMonday);
                break;
            case Calendar.TUESDAY:
                Wochentag = context.getString(R.string.dayTuesday);
                break;
            case Calendar.WEDNESDAY:
                Wochentag = context.getString(R.string.dayWednesday);
                break;
            case Calendar.THURSDAY:
                Wochentag = context.getString(R.string.dayThursday);
                break;
            case Calendar.FRIDAY:
                Wochentag = context.getString(R.string.dayFriday);
                break;
            case Calendar.SATURDAY:
                Wochentag = context.getString(R.string.daySaturday);
                break;
            case Calendar.SUNDAY:
                Wochentag = context.getString(R.string.daySunday);
                break;
            default:
                Wochentag = context.getString(R.string.dayInvalid);
                break;
        }
        holder.textDate.setText(context.getString(R.string.datebuilder, Wochentag, dateFormat.format(data.get(position).get_date())));
        holder.textHour.setText(CombineData.combineHours(data.get(position).get_hours()));
        holder.textSubject.setText(data.get(position).get_subject());
        holder.textRoom.setText(data.get(position).get_room());
        holder.textInfo1.setText(data.get(position).get_info1());
        holder.textInfo2.setText(data.get(position).get_info2());
    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "gIC");
        return data.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textDate;
        TextView textHour;
        TextView textSubject;
        TextView textRoom;
        TextView textInfo1;
        TextView textInfo2;

        public PlanViewHolder(View itemView) {
            super(itemView);
            Log.v(TAG + ".PVH", "PVH");
            itemView.setLongClickable(true);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textDate = (TextView) itemView.findViewById(R.id.textDate);
            textHour = (TextView) itemView.findViewById(R.id.textHour);
            textSubject = (TextView) itemView.findViewById(R.id.textSubject);
            textRoom = (TextView) itemView.findViewById(R.id.textRoom);
            textInfo1 = (TextView) itemView.findViewById(R.id.textInfo1);
            textInfo2 = (TextView) itemView.findViewById(R.id.textInfo2);
            Log.v(TAG, String.valueOf(textHour));
        }

        boolean longpress;

        @Override
        public void onClick(View v) {
            clickListener.planItemClicked(v, getAdapterPosition(), longpress);
            longpress = false;
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.planItemLongClicked(v, getAdapterPosition());
            longpress = true;
            return false;
        }
    }

    public interface ClickListener {
        public void planItemClicked(View view, int position, boolean longpress);

        public void planItemLongClicked(View view, int position);
    }
}
