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

public class VPAdapter extends RecyclerView.Adapter<VPAdapter.PlanViewHolder> {
    private static final String TAG = "VPAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<VPData> data = Collections.emptyList();
    private ClickListener clickListener;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public VPAdapter(Context context, List<VPData> data) {
        Log.v(TAG, "@VPAdapter");
        Log.v(TAG, "" + data.size());
        Log.v(TAG, String.valueOf(context));
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        Log.v(TAG, "-VPAdapter");
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
        View view = inflater.inflate(R.layout.custom_vp_row, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        Log.v(TAG, "oBVH");
        VPData vpData = data.get(position);
        String Wochentag;
        Calendar c = new GregorianCalendar();
        c.setTime(vpData.getDate());
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
        holder.textDate.setText(context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpData.getDate())));
        holder.textHour.setText(CombineData.hoursString(vpData.getHours(), false));
        holder.textSubject.setText(vpData.getSubject());
        holder.textRoom.setText(vpData.getRoom());
        holder.textInfo1.setText(vpData.getInfo1());
        holder.textInfo2.setText(vpData.getInfo2());
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
            itemView.setClickable(true);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textDate = (TextView) itemView.findViewById(R.id.vpTextDate);
            textHour = (TextView) itemView.findViewById(R.id.vpTextHour);
            textSubject = (TextView) itemView.findViewById(R.id.vpTextSubject);
            textRoom = (TextView) itemView.findViewById(R.id.vpTextRoom);
            textInfo1 = (TextView) itemView.findViewById(R.id.vpTextInfo1);
            textInfo2 = (TextView) itemView.findViewById(R.id.vpTextInfo2);
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
        void planItemClicked(View view, int position, boolean longpress);

        void planItemLongClicked(View view, int position);
    }
}
