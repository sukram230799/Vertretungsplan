package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private static final String TAG = "PlanAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<HWLesson> table = Collections.emptyList();
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
    public void onBindViewHolder(TableViewHolder holder, int position) {
        holder.textHour.setText(CombineData.hoursString(table.get(position).getHours(), true));
        holder.textTeacher.setText(table.get(position).getTeacher());
        holder.textSubject.setText(table.get(position).getSubject());
        holder.textRoom.setText(table.get(position).getRoom());
        //holder.textRoom.setText(TimeTableHelper.getRepeatTypeName(table.get(position).getRepeatType()));
        holder.textRepeatType.setText(table.get(position).getRepeatType());
    }

    @Override
    public int getItemCount() {
        return table.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textHour;
        TextView textTeacher;
        TextView textSubject;
        TextView textRoom;
        TextView textRepeatType;

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
