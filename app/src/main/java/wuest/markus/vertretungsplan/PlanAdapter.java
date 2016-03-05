package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private static final String TAG = "PlanAdapter";
    private LayoutInflater inflater;
    private Context context;
    List<HWPlan> plan = Collections.emptyList();
    List<Integer> selectedLessons = new ArrayList<>();
    //private HWPlan selectedLesson = null;
    private ClickListener clickListener;
    //int expandedPosition;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public PlanAdapter(Context context, List<HWPlan> plan) {
        this.context = context;
        this.plan = plan;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_plan_row, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        HWPlan plan = this.plan.get(position);

        final int redColor = Color.parseColor("#8aFF0000");
        final int textColor = Color.parseColor("#8a000000");

        boolean onlyVP = (plan.getSpRoom() == null);
        boolean onlySP = (plan.getVpRoom() == null);

        collapseCard(holder.spLayout, holder.vpLayout, holder.cardLayout);

        if (!onlyVP) {
            holder.spLayout.setVisibility(View.VISIBLE);
            if (onlySP) {
                holder.imageViewQuestionMark.setVisibility(View.GONE);
            } else {
                holder.imageViewQuestionMark.setVisibility(View.VISIBLE);
            }
            if (plan.getSpSubject().equals("PAUSE")) {
                //holder.spTextHour.setVisibility(View.GONE); //Hour is shown always
                holder.spTextTeacher.setVisibility(View.GONE);
                holder.spTextSubject.setVisibility(View.GONE);
                holder.spTextRoom.setVisibility(View.GONE);
                holder.spTextRepeatType.setVisibility(View.GONE);

                holder.spTextBreak.setVisibility(View.VISIBLE);
            } else {

                //holder.spTextHour.setVisibility(View.VISIBLE);
                holder.spTextTeacher.setVisibility(View.VISIBLE);
                holder.spTextSubject.setVisibility(View.VISIBLE);
                holder.spTextRoom.setVisibility(View.VISIBLE);
                holder.spTextRepeatType.setVisibility(View.VISIBLE);

                holder.spTextBreak.setVisibility(View.GONE);
            }

            holder.spTextHour.setText(plan.getHourString());
            holder.spTextTeacher.setText(plan.getTeacher());
            holder.spTextTeacher.setTextColor(textColor);
            holder.spTextSubject.setText(plan.getSpSubject());
            holder.spTextRoom.setText(plan.getSpRoom());
            //holder.spTextRoom.setText(TimeTableHelper.getRepeatTypeName(plan.getRepeatType()));
            holder.spTextRepeatType.setText(plan.getRepeatType());

            if (plan.getSpRoom().equals(plan.getVpRoom())) {
                holder.spTextRoom.setTextColor(redColor);
            } else {
                holder.spTextRoom.setTextColor(textColor);
            }
            if (plan.getSpSubject().equals(plan.getVpSubject())) {
                holder.spTextSubject.setTextColor(redColor);
            } else {
                holder.spTextSubject.setTextColor(textColor);
            }
        } else {
            //holder.spLayout.setVisibility(View.GONE);
        }
        if (!onlySP) {
            holder.imageViewQuestionMark.setVisibility(View.VISIBLE);
            //holder.vpLayout.setVisibility(View.VISIBLE);
            String Wochentag;
            Calendar c = new GregorianCalendar();
            c.setTime(plan.getDate());
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
            holder.vpTextDate.setText(context.getString(R.string.datebuilder, Wochentag, dateFormat.format(plan.getDate())));
            holder.vpTextHour.setText(plan.getHourString());
            holder.vpTextSubject.setText(plan.getVpSubject());
            holder.vpTextRoom.setText(plan.getVpRoom());
            holder.vpTextInfo1.setText(plan.getInfo1());
            holder.vpTextInfo2.setText(plan.getInfo2());
            if (onlyVP) {
                holder.spTextHour.setText(plan.getHourString());
                holder.spTextSubject.setText(plan.getVpSubject());
                holder.spTextRoom.setText(plan.getVpRoom());
                holder.spTextTeacher.setText("VP!");
                holder.spTextTeacher.setTextColor(redColor);
                holder.spTextRepeatType.setText("");

                holder.spTextRoom.setTextColor(redColor);
                holder.spTextSubject.setTextColor(redColor);

                holder.spTextBreak.setVisibility(View.GONE);
            } else {
                if (plan.getSpSubject().equals(plan.getVpSubject())) {
                    holder.spTextSubject.setTextColor(textColor);
                } else {
                    holder.spTextSubject.setTextColor(redColor);
                }
                if (plan.getSpRoom().equals(plan.getVpRoom())) {
                    holder.spTextRoom.setTextColor(textColor);
                } else {
                    holder.spTextRoom.setTextColor(redColor);
                }
            }
        } else {
            holder.vpLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        /*int size = 0;
        for(HWPlan lesson : plan){
            for(int hour : lesson.getHours()){
                size++;
            }
        }*/
        return plan.size();
        //return size;
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView spTextHour;
        TextView spTextTeacher;
        TextView spTextSubject;
        TextView spTextRoom;
        TextView spTextRepeatType;
        TextView spTextBreak;
        TextView vpTextDate;
        TextView vpTextHour;
        TextView vpTextSubject;
        TextView vpTextRoom;
        TextView vpTextInfo1;
        TextView vpTextInfo2;

        RelativeLayout spLayout;
        RelativeLayout vpLayout;
        CardView cardLayout;

        ImageView imageViewQuestionMark;

        public PlanViewHolder(View itemView) {
            super(itemView);
            cardLayout = (CardView) itemView.findViewById(R.id.cardLayout);
            itemView.setClickable(true);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            spLayout = (RelativeLayout) itemView.findViewById(R.id.spLayout);

            spTextHour = (TextView) itemView.findViewById(R.id.spTextHour);
            spTextTeacher = (TextView) itemView.findViewById(R.id.spTextTeacher);
            spTextSubject = (TextView) itemView.findViewById(R.id.spTextSubject);
            spTextRoom = (TextView) itemView.findViewById(R.id.spTextRoom);
            spTextRepeatType = (TextView) itemView.findViewById(R.id.spTextRepeatType);
            spTextBreak = (TextView) itemView.findViewById(R.id.spTextBreak);


            vpLayout = (RelativeLayout) itemView.findViewById(R.id.vpLayout);

            vpTextDate = (TextView) itemView.findViewById(R.id.vpTextDate);
            vpTextHour = (TextView) itemView.findViewById(R.id.vpTextHour);
            vpTextSubject = (TextView) itemView.findViewById(R.id.vpTextSubject);
            vpTextRoom = (TextView) itemView.findViewById(R.id.vpTextRoom);
            vpTextInfo1 = (TextView) itemView.findViewById(R.id.vpTextInfo1);
            vpTextInfo2 = (TextView) itemView.findViewById(R.id.vpTextInfo2);

            imageViewQuestionMark = (ImageView) itemView.findViewById(R.id.imageView);
        }

        boolean longpress;

        @Override
        public void onClick(View v) {
            clickListener.tableItemClicked(v, getAdapterPosition(), longpress);
            computeExpansion(v, getAdapterPosition());
            longpress = false;
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.tableItemLongClicked(v, getAdapterPosition());
            longpress = true;
            return false;
        }
    }

    private void computeExpansion(View view, int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        RelativeLayout spLayout = (RelativeLayout) view.findViewById(R.id.spLayout);
        RelativeLayout vpLayout = (RelativeLayout) view.findViewById(R.id.vpLayout);
        CardView cardLayout = (CardView) view.findViewById(R.id.cardLayout);
        if (imageView.getVisibility() == View.VISIBLE) {
            if (vpLayout.getVisibility() == View.VISIBLE) {
                collapseCard(spLayout, vpLayout, cardLayout);
            } else {
                expandCard(spLayout, vpLayout, cardLayout);
            }
        } else {
            collapseCard(spLayout, vpLayout, cardLayout);
        }
    }

    public interface ClickListener {
        void tableItemClicked(View view, int position, boolean longpress);

        void tableItemLongClicked(View view, int position);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private void collapseCard(RelativeLayout spLayout, RelativeLayout vpLayout, CardView cardLayout) {
        ViewGroup.LayoutParams spParams = spLayout.getLayoutParams();
        spParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        spParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        spLayout.requestLayout();
        vpLayout.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = cardLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, context.getResources().getDisplayMetrics());
        cardLayout.requestLayout();
    }

    private void expandCard(RelativeLayout spLayout, RelativeLayout vpLayout, CardView cardLayout) {
        ViewGroup.LayoutParams spParams = spLayout.getLayoutParams();
        spParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        spParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        spLayout.requestLayout();
        vpLayout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = cardLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.requestLayout();
    }
}
