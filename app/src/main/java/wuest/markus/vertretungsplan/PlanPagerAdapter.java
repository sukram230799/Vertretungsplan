package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by Markus on 16.02.2016.
 */
public class PlanPagerAdapter extends FragmentPagerAdapter implements PlanFragment.RefreshContentListener {

    private static final String TAG = "PlanPagerAdapter";
    //ArrayList<HWTime> registeredDates;
    ArrayList<PlanFragment> fragments;
    HWGrade grade;
    private Context context;

    public PlanPagerAdapter(FragmentManager fm, HWGrade grade, Context context) {
        super(fm);
        this.grade = grade;
        fragments = new ArrayList<>();
        //registeredDates = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            //fragments.add(PlanFragment.newInstance(grade, i + 2, false));
            fragments.add(PlanFragment.newInstance(grade, getHWTimeFromWeekDay(i + 2), false));
        }
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        PlanFragment planFragment;
        if (fragments.size() == getCount()) {
            planFragment = fragments.get(position);
        } else {
            planFragment = PlanFragment.newInstance(grade, getHWTimeFromWeekDay(position + 2), false);
        }
        planFragment.setRefreshListener(this);

        return planFragment;
        //return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 6;
        //return registeredDates.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*Calendar calendar = new GregorianCalendar();
        calendar.setTime(registeredDates.get(position).toDate());*/
        return TimeTableHelper.getDayName(position + 2, context);
        //return TimeTableHelper.getDayName(calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Override
    public void refreshedContent(SwipeRefreshLayout refreshLayout) {
        refreshLayout.setRefreshing(false);
    }

    private HWTime getHWTimeFromWeekDay(int weekDay) {
        Calendar calendar = GregorianCalendar.getInstance();
        int WEEKDAY = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, - (WEEKDAY - weekDay));
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DAY = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "" + DAY + " " + WEEKDAY + " " + weekDay);
        return new HWTime(0, 0, YEAR, MONTH, DAY);
    }
/*
    public void registerNewDate(HWTime time, boolean background) {
        if (!registeredDates.contains(time)) {
            if (background) {
                new Thread(new BackgroundNewDate(grade, time, handler)).start();
            }
            registeredDates.add(time);
            fragments.add(PlanFragment.newInstance(grade, time, false));
            this.notifyDataSetChanged();
        }
    }

    private void newDate(HWTime time, PlanFragment fragment) {
        int position = registeredDates.size();
        registeredDates.add(position, time);
        fragments.add(position, fragment);
        this.notifyDataSetChanged();
    }

    class BackgroundNewDate implements Runnable {

        HWGrade grade;
        HWTime time;
        Handler handler;

        public BackgroundNewDate(HWGrade grade, HWTime time, Handler handler) {
            this.grade = grade;
            this.time = time;
            this.handler = handler;
        }

        @Override
        public void run() {
            Message message = new Message();
            PlanFragment fragment = PlanFragment.newInstance(grade, time, false);
            message.obj = new ArrayList<Object>(Arrays.asList(new Object[]{time, fragment}));
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                if (msg.obj != null) {
                    ArrayList list = (ArrayList) msg.obj;
                    newDate((HWTime) list.get(0), (PlanFragment) list.get(1));
                }
            }
        }
    };*/
}
