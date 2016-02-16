package wuest.markus.vertretungsplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus on 16.02.2016.
 */
public class SubjectChooserAdapter extends ArrayAdapter<String> {

    private OnCheckedChangeListener onCheckedChangeListener;
    private Boolean[] selected;

    private LayoutInflater inflater;
    private Context context;

    private List<String> subjects;
    private List<String> selectedSubjects;

    public SubjectChooserAdapter(Context context, int resource, List<String> subjects) {
        super(context, resource, subjects);
        this.subjects = subjects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_subject_row, null);
        }
        CheckBox checkBoxSubject = (CheckBox) itemView.findViewById(R.id.checkBoxSubject);
        String subject = subjects.get(position);
        for (String selectedSubject : selectedSubjects) {
            if (selectedSubject.equals(subject)) {
                checkBoxSubject.setChecked(true);
                break;
            }
        }
        checkBoxSubject.setText(subject);
        checkBoxSubject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChanged(position, isChecked);
                }
            }
        });

        return itemView;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setSelectedSubjects(List<String> selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
    }

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(int position, boolean isChecked);
    }

/*public SubjectChooserAdapter(String[] subjects, Context context) {
        this.subjects = subjects;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.selected = new Boolean[subjects.length];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
    }*/

    /*@Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_subject_row, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        holder.checkBoxSubject.setText(subjects[position]);
    }

    @Override
    public int getItemCount() {
        return subjects.length;
    }*/

    /*public String[] getSelectedSubjects() {
        if(subjects.length == selected.length){
            ArrayList<String> selectedSubjects = new ArrayList<>(subjects.length);
            for (int i = 0; i < subjects.length; i++){
                if(selected[i]){
                    selectedSubjects.add(subjects[i]);
                }
            }
            return selectedSubjects.toArray(new String[selectedSubjects.size()]);
        } else {
            return new String[0];
        }
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSubject;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            checkBoxSubject = (CheckBox) itemView.findViewById(R.id.checkBoxSubject);

            checkBoxSubject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected[getAdapterPosition()] = isChecked;
                }
            });
        }
    }*/
}
