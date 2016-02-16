package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private static final String TAG = "TableAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<HWLesson> table = Collections.emptyList();
    List<Integer> selectedLessons = new ArrayList<>();
    //private HWLesson selectedLesson = null;
    private ClickListener clickListener;

    public TableAdapter(Context context, List<HWLesson> table) {
        this.context = context;
        this.table = table;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_table_row, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, final int position) {
        if (table.get(position).getSubject().equals("PAUSE")) {
            holder.textHour.setVisibility(View.GONE);
            holder.textTeacher.setVisibility(View.GONE);
            holder.textSubject.setVisibility(View.GONE);
            holder.textRoom.setVisibility(View.GONE);
            holder.textRepeatType.setVisibility(View.GONE);

            holder.textBreak.setVisibility(View.VISIBLE);
        } else {
            holder.textHour.setText(CombineData.hoursString(table.get(position).getHours(), true));

            holder.textHour.setVisibility(View.VISIBLE);
            holder.textTeacher.setVisibility(View.VISIBLE);
            holder.textSubject.setVisibility(View.VISIBLE);
            holder.textRoom.setVisibility(View.VISIBLE);
            holder.textRepeatType.setVisibility(View.VISIBLE);

            holder.textBreak.setVisibility(View.GONE);
        }

        holder.textTeacher.setText(table.get(position).getTeacher());
        holder.textSubject.setText(table.get(position).getSubject());
        holder.textRoom.setText(table.get(position).getRoom());
        //holder.spTextRoom.setText(TimeTableHelper.getRepeatTypeName(plan.get(position).getRepeatType()));
        holder.textRepeatType.setText(table.get(position).getRepeatType());

        if (table.get(position).changedRoom) {
            holder.textRoom.setTextColor(Color.parseColor("#8aFF0000"));
        } else {
            holder.textRoom.setTextColor(Color.parseColor("#8a000000"));
        }
        if (table.get(position).changedSubject) {
            holder.textSubject.setTextColor(Color.parseColor("#8aFF0000"));
        } else {
            holder.textSubject.setTextColor(Color.parseColor("#8a000000"));
        }
    }

    @Override
    public int getItemCount() {
        /*int size = 0;
        for(HWLesson lesson : plan){
            for(int hour : lesson.getHours()){
                size++;
            }
        }*/
        return table.size();
        //return size;
    }

    public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textHour;
        TextView textTeacher;
        TextView textSubject;
        TextView textRoom;
        TextView textRepeatType;
        CheckBox checkBox;
        TextView textBreak;

        public TableViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            textHour = (TextView) itemView.findViewById(R.id.textHour);
            textTeacher = (TextView) itemView.findViewById(R.id.textTeacher);
            textSubject = (TextView) itemView.findViewById(R.id.textSubject);
            textRoom = (TextView) itemView.findViewById(R.id.textRoom);
            textRepeatType = (TextView) itemView.findViewById(R.id.textRepeatType);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            textBreak = (TextView) itemView.findViewById(R.id.textBreak);

            checkBox.setVisibility(View.GONE);
        }

        boolean longpress;

        @Override
        public void onClick(View v) {
            clickListener.tableItemClicked(v, getAdapterPosition(), longpress);
            longpress = false;
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.tableItemLongClicked(v, getAdapterPosition());
            longpress = true;
            return false;
        }
    }

    public interface ClickListener {
        void tableItemClicked(View view, int position, boolean longpress);

        void tableItemLongClicked(View view, int position);
    }
}
