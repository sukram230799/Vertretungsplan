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

    private static final String TAG = "VPAdapter";
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
            holder.textHour.setText(String.valueOf(table.get(position).getHour()));

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
        if (selectedLessons.contains(position)) {
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(true);
            holder.checkBox.setAlpha(1.0f);
        } else {
            holder.checkBox.setChecked(false);
            if (selectedLesson != null && !(selectedLesson.getDay() == table.get(position).getDay() &&
                    selectedLesson.getTeacher().equals(table.get(position).getTeacher()) &&
                    selectedLesson.getSubject().equals(table.get(position).getSubject()) &&
                    selectedLesson.getRoom().equals(table.get(position).getRoom()) &&
                    selectedLesson.getRepeatType().equals(table.get(position).getRepeatType()))) {
                holder.checkBox.setEnabled(false);
                holder.checkBox.setAlpha(0.4f);
            } else {
                holder.checkBox.setEnabled(true);
                holder.checkBox.setAlpha(1.0f);
            }
        }
        holder.checkBoxNumber.setText(String.valueOf(position));
        holder.textTeacher.setText(table.get(position).getTeacher());
        holder.textSubject.setText(table.get(position).getSubject());
        holder.textRoom.setText(table.get(position).getRoom());
        //holder.spTextRoom.setText(TimeTableHelper.getRepeatTypeName(plan.get(position).getSpRepeatType()));
        holder.textRepeatType.setText(table.get(position).getRepeatType());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, String.valueOf(isChecked));
                int adapterPosition = Integer.parseInt(((TextView) ((View) buttonView.getParent()).findViewById(R.id.adapterPosition)).getText().toString());
                HWLesson selectedLesson;
                if (selectedLessons.isEmpty()) {
                    selectedLesson = null;
                } else {
                    selectedLesson = table.get(selectedLessons.get(0));
                }
                if (isChecked && buttonView.isShown()) {
                    selectedLessons.add(((Integer) adapterPosition));
                    if (selectedLesson == null) {
                        selectedLesson = table.get(adapterPosition);
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
                } else if (buttonView.isShown()) {
                    selectedLessons.remove(((Integer) adapterPosition));
                    if (selectedLessons.isEmpty()) {
                        for (int i = 0; i < getItemCount(); i++) {
                            notifyItemChanged(i);
                        }
                    }
                }
            }
        });
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
        TextView checkBoxNumber;

        public TableViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            textHour = (TextView) itemView.findViewById(R.id.spTextHour);
            textTeacher = (TextView) itemView.findViewById(R.id.spTextTeacher);
            textSubject = (TextView) itemView.findViewById(R.id.spTextSubject);
            textRoom = (TextView) itemView.findViewById(R.id.spTextRoom);
            textRepeatType = (TextView) itemView.findViewById(R.id.spTextRepeatType);
            ViewGroup viewGroup = (ViewGroup) itemView.findViewById(R.id.checkBoxContainer);
            checkBox = new CheckBox(context);
            checkBoxNumber = new TextView(context);
            checkBoxNumber.setId(R.id.adapterPosition);
            checkBoxNumber.setVisibility(View.GONE);
            viewGroup.addView(checkBox);
            viewGroup.addView(checkBoxNumber);
            textBreak = (TextView) itemView.findViewById(R.id.spTextBreak);
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

    public Integer[] getSelectedLessonIds() {
        if (selectedLessons.isEmpty()) {
            return new Integer[0];
        } else {
            /*for (HWLesson lesson : plan) {
                for (int hour : lesson.getHours()) {
                    for (int chour : selectedLessons) {
                        if (hour == chour) {
                            return new HWLesson(lesson.getGrade(), selectedLessons.toArray(new Integer[selectedLessons.size()]),
                                    lesson.getDay(), lesson.getSpTeacher(), lesson.getSubject(), lesson.getRoom(), lesson.getSpRepeatType());
                        }
                    }
                }
            }*/
            DBHandler dbHandler = new DBHandler(context, null, null, 0);
            ArrayList<Integer> ids = new ArrayList<>();
            for (int pos : selectedLessons) {
                ids.add(((Integer) dbHandler.getIdFromLesson(table.get(pos))));
            }
            HWLesson lesson = table.get(selectedLessons.get(0));
            return ids.toArray(new Integer[ids.size()]);
            //return null;
        }
    }

    public interface ClickListener {
        void tableItemClicked(View view, int position, boolean longpress);

        void tableItemLongClicked(View view, int position);
    }
}
