package wuest.markus.vertretungsplan;

import android.content.Context;
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

public class TableEditAdapter extends RecyclerView.Adapter<TableEditAdapter.TableViewHolder> {

    private static final String TAG = "PlanAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<HWLesson> table = Collections.emptyList();
    List<Integer> selectedLessons = new ArrayList<>();
    //private HWLesson selectedLesson = null;
    private ClickListener clickListener;

    public TableEditAdapter(Context context, List<HWLesson> table) {
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
            holder.checkBox.setVisibility(View.GONE);
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
            //EditMode
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setPadding(25, 0, 0, 0);
            holder.textHour.setPadding(0, 0, 0, 0);
        }
        HWLesson selectedLesson;
        if (selectedLessons.isEmpty()) {
            selectedLesson = null;
        } else {
            selectedLesson = table.get(selectedLessons.get(0));
        }
        if (selectedLesson != null && !(selectedLesson.getDay() == table.get(position).getDay() &&
                selectedLesson.getTeacher().equals(table.get(position).getTeacher()) &&
                selectedLesson.getSubject().equals(table.get(position).getSubject()) &&
                selectedLesson.getRoom().equals(table.get(position).getRoom()) &&
                selectedLesson.getRepeatType().equals(table.get(position).getRepeatType()))) {
            holder.checkBox.setEnabled(false);
        } else {
            holder.checkBox.setEnabled(true);
        }

        holder.textTeacher.setText(table.get(position).getTeacher());
        holder.textSubject.setText(table.get(position).getSubject());
        holder.textRoom.setText(table.get(position).getRoom());
        //holder.textRoom.setText(TimeTableHelper.getRepeatTypeName(table.get(position).getRepeatType()));
        holder.textRepeatType.setText(table.get(position).getRepeatType());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, String.valueOf(isChecked));
                HWLesson selectedLesson;
                if (selectedLessons.isEmpty()) {
                    selectedLesson = null;
                } else {
                    selectedLesson = table.get(selectedLessons.get(0));
                }
                if (isChecked) {
                    selectedLessons.add(((Integer) position));
                    if (selectedLesson == null) {
                        selectedLesson = table.get(position);
                        for (int i = 0; i < table.size(); i++) {
                            HWLesson lesson = table.get(i);
                            if (!(selectedLesson.getDay() == lesson.getDay() &&
                                    selectedLesson.getTeacher().equals(lesson.getTeacher()) &&
                                    selectedLesson.getSubject().equals(lesson.getSubject()) &&
                                    selectedLesson.getRoom().equals(lesson.getRoom()) &&
                                    selectedLesson.getRepeatType().equals(lesson.getRepeatType()))) {
                                notifyItemChanged(i);
                            }
                        }
                    }
                } else {
                    selectedLessons.remove(((Integer) position));
                    if (selectedLessons.isEmpty()) {
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        /*int size = 0;
        for(HWLesson lesson : table){
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

    public HWLesson getSelectedLesson() {
        if (selectedLessons.isEmpty()) {
            return null;
        } else {
            /*for (HWLesson lesson : table) {
                for (int hour : lesson.getHours()) {
                    for (int chour : selectedLessons) {
                        if (hour == chour) {
                            return new HWLesson(lesson.getGrade(), selectedLessons.toArray(new Integer[selectedLessons.size()]),
                                    lesson.getDay(), lesson.getTeacher(), lesson.getSubject(), lesson.getRoom(), lesson.getRepeatType());
                        }
                    }
                }
            }*/
            ArrayList<Integer> hours = new ArrayList<>();
            for (int pos : selectedLessons) {
                for (int hour : table.get(pos).getHours()) {
                    hours.add(((Integer) hour));
                }
            }
            HWLesson lesson = table.get(selectedLessons.get(0));
            return new HWLesson(lesson.getGrade(), hours.toArray(new Integer[hours.size()]),
                    lesson.getDay(), lesson.getTeacher(), lesson.getSubject(), lesson.getRoom(), lesson.getRepeatType());
            //return null;
        }
    }

    public interface ClickListener {
        void tableItemClicked(View view, int position, boolean longpress);

        void tableItemLongClicked(View view, int position);
    }
}