package wuest.markus.vertretungsplan;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private static final String TAG = "DrawerAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<String> data = Collections.emptyList();
    private String chosenType = "NULL";
    private int chosenTypePosition = -1;
    private ClickListener clickListener;
    int selected = -1;
    //private Preferences preferences;


    public DrawerAdapter(Context context, List<String> data) {
        Log.v("DEBUG.GA", data.size() + "");
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public DrawerAdapter.DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_drawer_row, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.DrawerViewHolder holder, int position) {
        holder.position = position;
        if (chosenType.equals("NULL")) {
            chosenType = Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GRADE), "NULL");
            Log.v("DEBUG", chosenType);
        }
        Log.v("DEBUG.onBVH", data.get(position));
        holder.title.setText(data.get(position));
        if (chosenType.equals(data.get(position))) {
            Log.v("DEBUG.found", chosenType);
            chosenTypePosition = position;
            //holder.title.setBackgroundColor(Color.parseColor("#888888"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.title.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));//getColor(R.color.colorPrimary));
            } else {
                holder.title.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
            //holder.title.setBackgroundColor(Color.parseColor());
        } else {
            if (position == selected) {
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


    class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        int position;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            itemView.setLongClickable(true);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            title = (TextView) itemView.findViewById(R.id.drawerLabel);
        }

        private boolean longclick;

        @Override
        public void onClick(View v) {
            Log.d(TAG, "CLICK");
            if (clickListener != null) {
                notifyItemChanged(selected);
                notifyItemChanged(position);
                selected = position;
                //Log.v("DEBUG.gLP", String.valueOf(getLayoutPosition()));
                //Log.v("DEBUG.gAP", String.valueOf(getAdapterPosition()));
                clickListener.drawerItemClicked(v, getAdapterPosition(), longclick);
                longclick = false;
                v.isSelected();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Log.v(TAG, v.toString());
            Log.d(TAG, "LONGCLICK");
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            Snackbar.make(v, data.get(getAdapterPosition()) + " wurde als Standard festgelegt.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //Preferences.saveStringToPreferences(context, context.getString(R.string.SELECTED_GRADE), data.get(getAdapterPosition()).getGradeName());
            notifyItemChanged(chosenTypePosition);
            notifyItemChanged(position);
            chosenType = data.get(getAdapterPosition());
            //clickListener.gradeItemLongClicked(v, getAdapterPosition());
            longclick = true;
            onClick(v);
            return true;
            //return true;
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void drawerItemClicked(View view, int position, boolean longpress);

        //public void gradeItemLongClicked(View view, int position);
    }
}
