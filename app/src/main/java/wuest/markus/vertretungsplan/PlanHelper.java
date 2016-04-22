package wuest.markus.vertretungsplan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class PlanHelper {
    private static final String TAG = "PlanHelper";
    //public static HWPlan[] combineVPSP(HWLesson[] sp, VPData[] vp, boolean combinedSP, boolean combinedVP) {
    //    return combineVPSP(sp, vp, combinedSP, combinedVP, -1);
    //}

    /**
     * @param sp "HWLesson[] pre selected"
     * @param vp "VPData[] pre selected"
     * @return "Combined SP and VP"
     */
    public static HWPlan[] combineVPSP(HWLesson[] sp, VPData[] vp) {
        ArrayList<HWPlan> hwPlanArrayList = new ArrayList<>();
        ArrayList<VPData> vpArrayList;
        ArrayList<HWLesson> spArrayList;
        vpArrayList = new ArrayList<>(Arrays.asList(vp));

        spArrayList = new ArrayList<>(Arrays.asList(sp));

        for (int i = 0; i < spArrayList.size(); i++) {
            HWLesson lesson = spArrayList.get(i);
            boolean add = false;
            for (VPData vpData : vpArrayList) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(vpData.getDate());
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (lesson.getDay() == day &&
                        lesson.getHour() == vpData.getHour()) {
                    hwPlanArrayList.add(new HWPlan(lesson.getGrade(), lesson.getHour(), lesson.getDay(), lesson.getId(), lesson.getTeacher(),
                            lesson.getSubject(), lesson.getRoom(), lesson.getRepeatType(), vpData.getId(), vpData.getSubject(), vpData.getRoom(),
                            vpData.getInfo1(), vpData.getInfo2(), vpData.getDate()));
                    vpArrayList.remove(vpData);
                    add = true;
                    break; //No double add!
                }
            }
            if (!add) { //No double add!
                hwPlanArrayList.add(new HWPlan(lesson.getGrade(), lesson.getHour(), lesson.getDay(), lesson.getId(), lesson.getTeacher(),
                        lesson.getSubject(), lesson.getRoom(), lesson.getRepeatType(), -1, null, null,
                        null, null, null));
            }
        }
        for (VPData vpData : vpArrayList) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(vpData.getDate());
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            hwPlanArrayList.add(new HWPlan(vpData.getGrade(), vpData.getHour(), day, -1, null,
                    null, null, null, vpData.getId(), vpData.getSubject(), vpData.getRoom(),
                    vpData.getInfo1(), vpData.getInfo2(), vpData.getDate()));
        }
        Collections.sort(hwPlanArrayList, new Comparator<HWPlan>() {
            @Override
            public int compare(HWPlan plan1, HWPlan plan2) {

                return ((Integer) plan1.getHour()).compareTo(plan2.getHour());
            }
        });
        return hwPlanArrayList.toArray(new HWPlan[hwPlanArrayList.size()]);
    }

    public static HWPlan[] fillPlanGabs(HWPlan[] plans, int day) {
        HWGrade grade = new HWGrade("");
        if (plans.length > 0) {
            grade = plans[0].getGrade();
        }
        ArrayList<HWPlan> planArrayList = new ArrayList(Arrays.asList(plans));
        ArrayList<Integer> placedHours = new ArrayList<>();
        for (HWPlan plan : plans) {
            placedHours.add(plan.getHour());
        }
        Collections.sort(placedHours);
        int lastHour = 14;
        for (int hour : placedHours) {
            if (hour > (lastHour + 1)) {
                Integer[] breakHours = new Integer[hour - lastHour - 1];
                for (int breakHour = lastHour + 1; breakHour < hour; breakHour++) {
                    Log.d(TAG, breakHour + "_" + lastHour + "_" + hour);
                    breakHours[hour - breakHour - 1] = breakHour;
                }
                Arrays.sort(breakHours);
                HWPlan plan = new HWPlan(grade, hour - 1, day, -1, "", "PAUSE", "", "", -1, "", null, null, null, null);
                plan.setHourString(CombineData.hoursString(breakHours, true));
                planArrayList.add(placedHours.indexOf(hour), plan);
            }
            lastHour = hour;
        }
        return planArrayList.toArray(new HWPlan[planArrayList.size()]);
    }

    public static ArrayList<HWPlan[]> combinePlans(HWPlan[] plans) {

        ArrayList<HWPlan[]> combinedPlan = new ArrayList<>();
        for(HWPlan hwPlan : plans){
            boolean found = false;
            for(HWPlan[] usedPlanArray : combinedPlan) {
                for (HWPlan usedPlan: usedPlanArray){
                    if(usedPlan == hwPlan){
                        found = true;
                        break;
                    }
                }
            }
            if(!found){
                combinedPlan.add(getSimilarPlans(hwPlan, plans));
            }
        }
        return combinedPlan;
    }


    public static HWPlan[] getSimilarPlans(HWPlan compare, HWPlan[] rest) {
        ArrayList<HWPlan> planArrayList = new ArrayList<>();
        for (HWPlan plan : rest) {
            if (plan.getGrade().equals(compare.getGrade()) &&
                    plan.getSpTeacher().equals(compare.getSpTeacher()) &&
                    plan.getSpSubject().equals(compare.getSpSubject()) &&
                    plan.getSpRoom().equals(compare.getSpRoom()) &&
                    plan.getSpRepeatType().equals(compare.getSpRepeatType()) &&
                    plan.getVpSubject().equals(compare.getVpSubject()) &&
                    plan.getVpRoom().equals(compare.getVpRoom()) &&
                    plan.getVpInfo1().equals(compare.getVpInfo1()) &&
                    plan.getVpInfo2().equals(compare.getVpInfo2())) {
                planArrayList.add(plan);
            }
        }
        return planArrayList.toArray(new HWPlan[planArrayList.size()]);
    }
}
