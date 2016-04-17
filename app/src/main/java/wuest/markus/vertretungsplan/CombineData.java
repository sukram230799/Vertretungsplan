package wuest.markus.vertretungsplan;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CombineData {
    public static String TAG = "CombineData";

    public static VPData[] getSimilarVP(VPData compare, VPData[] rest) {
        ArrayList<VPData> dataArrayList = new ArrayList<>();
        for (VPData data : rest) {
            if (data.getDate().equals(compare.getDate()) &&
                    data.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                    data.getSubject().equals(compare.getSubject()) &&
                    data.getRoom().equals(compare.getRoom()) &&
                    data.getInfo1().equals(compare.getInfo1()) &&
                    data.getInfo2().equals(compare.getInfo2())) {
                dataArrayList.add(data);
            }
        }
        return dataArrayList.toArray(new VPData[dataArrayList.size()]);
    }

    public static HWLesson[] getSimilarLessons(HWLesson compare, HWLesson[] rest) {
        ArrayList<HWLesson> lessonArrayList = new ArrayList<>();
        for (HWLesson hwLesson : rest) {
            if (hwLesson.getDay() == compare.getDay() &&
                    hwLesson.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                    hwLesson.getTeacher().equals(compare.getTeacher()) &&
                    hwLesson.getSubject().equals(compare.getSubject()) &&
                    hwLesson.getRoom().equals(compare.getRoom()) &&
                    hwLesson.getRepeatType().equals(compare.getRepeatType())) {
                lessonArrayList.add(hwLesson);
            }
        }
        return lessonArrayList.toArray(new HWLesson[lessonArrayList.size()]);
    }

    public static Integer[] getSimilarHours(VPData compare, VPData[] rest) {
        ArrayList<Integer> hoursArrayList = new ArrayList<>();
        for (VPData data : rest) {
            if (data.getDate().equals(compare.getDate()) &&
                    data.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                    data.getSubject().equals(compare.getSubject()) &&
                    data.getRoom().equals(compare.getRoom()) &&
                    data.getInfo1().equals(compare.getInfo1()) &&
                    data.getInfo2().equals(compare.getInfo2())) {
                hoursArrayList.add(data.getHour());
            }
        }
        return hoursArrayList.toArray(new Integer[hoursArrayList.size()]);
    }

    public static Integer[] getSimilarHours(HWLesson compare, HWLesson[] rest) {
        ArrayList<Integer> hoursArrayList = new ArrayList<>();
        for (HWLesson hwLesson : rest) {
            if (hwLesson.getDay() == compare.getDay() &&
                    hwLesson.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                    hwLesson.getTeacher().equals(compare.getTeacher()) &&
                    hwLesson.getSubject().equals(compare.getSubject()) &&
                    hwLesson.getRoom().equals(compare.getRoom()) &&
                    hwLesson.getRepeatType().equals(compare.getRepeatType())) {
                hoursArrayList.add(hwLesson.getHour());
            }
        }
        return hoursArrayList.toArray(new Integer[hoursArrayList.size()]);
    }

    public static Integer[] getSimilarHours(HWPlan compare, HWPlan[] rest) {
        ArrayList<Integer> hoursArrayList = new ArrayList<>();
        for (HWPlan hwPlan : rest) {
            if (hwPlan.getDay() == compare.getDay() &&
                    hwPlan.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                    hwPlan.getSpTeacher().equals(compare.getSpTeacher()) &&
                    hwPlan.getSpSubject().equals(compare.getSpSubject()) &&
                    hwPlan.getSpRoom().equals(compare.getSpRoom()) &&
                    hwPlan.getSpRepeatType().equals(compare.getSpRepeatType()) &&
                    hwPlan.getVpDate().equals(compare.getVpDate()) &&
                    hwPlan.getVpInfo1().equals(compare.getVpInfo1()) &&
                    hwPlan.getVpInfo2().equals(compare.getVpInfo2()) &&
                    hwPlan.getVpRoom().equals(compare.getVpRoom())) {
                hoursArrayList.add(hwPlan.getHour());
            }
        }
        return hoursArrayList.toArray(new Integer[hoursArrayList.size()]);
    }

    /*public static VPData[] combineVP(VPData[] vpData) {
        ArrayList<VPData> combinedList = new ArrayList<>();
        ArrayList<VPData> participants = new ArrayList<>(Arrays.asList(vpData));
        while (!participants.isEmpty()) {
            ArrayList<Integer> hoursArrayList = new ArrayList<>();
            VPData data = participants.get(0);
            for (int hour : data.getHours()) {
                hoursArrayList.add(hour);
            }
            participants.remove(0);
            for (VPData compare : participants.toArray(new VPData[participants.size()])) {
                Log.v(TAG, String.valueOf(data.getVpDate().equals(compare.getVpDate())));
                Log.v(TAG, String.valueOf(data.getGrade().getGradeName().equals(compare.getGrade().getGradeName())));
                Log.v(TAG, String.valueOf(data.getSubject().equals(compare.getSubject())));
                Log.v(TAG, String.valueOf(data.getRoom().equals(compare.getRoom())));
                Log.v(TAG, String.valueOf(data.getVpInfo1().equals(compare.getVpInfo1())));
                Log.v(TAG, String.valueOf(data.getVpInfo2().equals(compare.getVpInfo2())));
                if (data.getVpDate().equals(compare.getVpDate()) &&
                        data.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                        data.getSubject().equals(compare.getSubject()) &&
                        data.getRoom().equals(compare.getRoom()) &&
                        data.getVpInfo1().equals(compare.getVpInfo1()) &&
                        data.getVpInfo2().equals(compare.getVpInfo2())
                        ) {
                    for (int hour : compare.getHours()) {
                        if (!hoursArrayList.contains(hour)) {
                            hoursArrayList.add(hour);
                        }
                    }
                    participants.remove(compare);
                }
            }
            Collections.sort(hoursArrayList);
            combinedList.add(new VPData(data.getGrade(),
                    hoursArrayList.toArray(new Integer[hoursArrayList.size()]),
                    data.getSubject(),
                    data.getRoom(),
                    data.getVpInfo1(),
                    data.getVpInfo2(),
                    data.getVpDate()
            ));
        }
        return combinedList.toArray(new VPData[combinedList.size()]);
    }*/

    /*public VPData[] planChanges(VPData[] oldDataArray, VPData[] newDataArray) {
        List<VPData> bothData = new ArrayList<>(Arrays.asList(oldDataArray));
        bothData.addAll(Arrays.asList(newDataArray));
        /*for (VPData data : oldDataArray) {
            bothData.add(data);
        }
        for (VPData data : newDataArray) {
            bothData.add(data);
        }
        VPData[] combinedData = combineVP(bothData.toArray(new VPData[bothData.size()]));
        ArrayList<VPData> changesList = new ArrayList<>();
        for (VPData oldData : oldDataArray) {
            for (VPData data : combinedData) {
                if (!(Arrays.equals(oldData.getHours(), data.getHours())) &&
                        oldData.getVpDate().equals(data.getVpDate()) &&
                        oldData.getGrade().getGradeName().equals(data.getGrade().getGradeName()) &&
                        oldData.getSubject().equals(data.getSubject()) &&
                        oldData.getRoom().equals(data.getRoom()) &&
                        oldData.getVpInfo1().equals(data.getVpInfo1()) &&
                        oldData.getVpInfo2().equals(data.getVpInfo2())
                        ) {
                }
            }
        }

        return null;
    }*/

    /*
    public class HourComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a < b ? -1 : a == b ? 0 : 1;
        }
    }*/
    public static String hoursString(Integer[] hoursArray, boolean linebreak) {
        final String MINUS;
        final String COMMA = ", ";
        if (linebreak) {
            MINUS = "\n-\n";
        } else {
            MINUS = "-";
        }
        ArrayList<Integer> hours = new ArrayList<>(Arrays.asList(hoursArray));
        Collections.sort(hours);
        String combined = String.valueOf(hours.get(0));
        Integer lastNumber = hours.get(0);
        Integer combineTimes = 0;
        for (Integer hour : hours) {
            if (!lastNumber.equals(hour)) {
                if (lastNumber == hour - 1) {
                    lastNumber = hour;
                    combineTimes++;
                } else {
                    if (combineTimes > 0) {
                        combined += MINUS + lastNumber + COMMA + hour;
                    } else {
                        combined += COMMA + hour;
                    }
                    combineTimes = 0;
                    lastNumber = hour;
                }
            }
        }
        if (combineTimes > 0) {
            combined += MINUS + lastNumber;
        }
        /*
        if(lastNumber != hours.get(hours.size()-1)) {

        }*/
        return combined;
    }

    public static ChangedHour[] findChanges(VPData[] oldDataArray, VPData[] newDataArray) {
        ArrayList<VPData> oldDataList = new ArrayList<>(Arrays.asList(oldDataArray));
        ArrayList<VPData> newDataList = new ArrayList<>(Arrays.asList(newDataArray));
        /*if (oldDataArray != null) {
            oldDataList = new ArrayList<>(oldDataArray.length);
            for (VPData data : oldDataArray) {
                for (Integer hour : data.getHours()) {
                    Integer[] hours = {hour};
                    oldDataList.add(new VPData(data.getGrade(), hours, data.getSubject(), data.getRoom(), data.getVpInfo1(), data.getVpInfo2(), data.getVpDate()));
                }
            }
        } else {
            oldDataList = new ArrayList<>(0);
        }
        if (newDataArray != null) {
            newDataList = new ArrayList<>(newDataArray.length);
            for (VPData data : newDataArray) {
                for (Integer hour : data.getHours()) {
                    Integer[] hours = {hour};
                    newDataList.add(new VPData(data.getGrade(), hours, data.getSubject(), data.getRoom(), data.getVpInfo1(), data.getVpInfo2(), data.getVpDate()));
                }
            }
        } else {
            newDataList = new ArrayList<>(0);
        }*/
        ArrayList<VPData> oldRemove = new ArrayList<>(oldDataList.size());
        ArrayList<VPData> newRemove = new ArrayList<>(newDataList.size());
        for (VPData oldData : /**/oldDataList/*/oldDataArray*/) {
            for (VPData newData : /**/newDataList/*/newDataArray*/) {
                if (
                    //oldData.getHour().equals(newData.getHour()) &&
                        oldData.getGrade().getGradeName().equals(newData.getGrade().getGradeName()) &&
                                oldData.getDate().equals(newData.getDate()) &&
                                oldData.getSubject().equals(newData.getSubject()) &&
                                oldData.getRoom().equals(newData.getRoom()) &&
                                oldData.getInfo1().equals(newData.getInfo1()) &&
                                oldData.getInfo2().equals(newData.getInfo2())
                        ) {
                    //if (oldData.equals(newData)) {
                    oldRemove.add(oldData);
                    newRemove.add(newData);
                    //oldDataList.remove(oldData);
                    //newDataList.remove(newData);
                }
            }
        }
        for (VPData id : oldRemove) {
            oldDataList.remove(id);
        }
        for (VPData id : newRemove) {
            newDataList.remove(id);
        }
        ArrayList<String> changes = new ArrayList<>();
        for (VPData data : oldDataList) {
            if (!changes.contains(GetVP.dateFormat.format(data.getDate()) + "|" + data.getHour())) {
                changes.add(GetVP.dateFormat.format(data.getDate()) + "|" + data.getHour());
            }
        }
        for (VPData data : newDataList) {
            if (!changes.contains(GetVP.dateFormat.format(data.getDate()) + "|" + data.getHour())) {
                changes.add(GetVP.dateFormat.format(data.getDate()) + "|" + data.getHour());
            }
        }
        ArrayList<ChangedHour> ChangedHour = new ArrayList<>();
        for (String value : changes) {
            String[] values = value.split("\\|");
            try {
                ChangedHour.add(new ChangedHour(Integer.parseInt(values[1]), GetVP.dateFormat.parse(values[0])));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(ChangedHour, ChangedHourComparator);
        return ChangedHour.toArray(new ChangedHour[ChangedHour.size()]);
    }

    public static String getChanges(ChangedHour[] changedHours) {
        ArrayList<Integer> hours = new ArrayList<>();
        Date date;
        String changesString = null;
        if (changedHours != null && changedHours.length > 0) {
            date = changedHours[0].getDate();
            changesString = GetVP.dateFormat.format(date) + " ";
            for (ChangedHour hour : changedHours) {
                if (hour.getDate().equals(date)) {
                    hours.add(hour.getHour());
                } else {
                    date = hour.getDate();
                    changesString += hoursString(hours.toArray(new Integer[hours.size()]), false) + "\n" + GetVP.dateFormat.format(date) + " ";
                    hours = new ArrayList<>();
                    hours.add(hour.getHour());
                }
            }
        }
        if (!hours.isEmpty()) {
            changesString += hoursString(hours.toArray(new Integer[hours.size()]), false);
        }
        return changesString;
    }

    /*public static HWLesson[] combineHWLessons(HWLesson[] hwLessons) {
        ArrayList<HWLesson> combinedList = new ArrayList<>();
        ArrayList<HWLesson> participants = new ArrayList<>(Arrays.asList(hwLessons));


        while (!participants.isEmpty()) {
            ArrayList<Integer> hoursArrayList = new ArrayList<>();
            HWLesson hwLesson = participants.get(0);
            for (int hour : hwLesson.getHours()) {
                hoursArrayList.add(hour);
            }
            participants.remove(0);
            for (HWLesson compare : participants.toArray(new HWLesson[participants.size()])) {
                /*Log.v(TAG, String.valueOf(hwLesson.getVpDate().equals(compare.getVpDate())));
                Log.v(TAG, String.valueOf(hwLesson.getGrade().getGradeName().equals(compare.getGrade().getGradeName())));
                Log.v(TAG, String.valueOf(hwLesson.getSubject().equals(compare.getSubject())));
                Log.v(TAG, String.valueOf(hwLesson.getRoom().equals(compare.getRoom())));
                Log.v(TAG, String.valueOf(hwLesson.getVpInfo1().equals(compare.getVpInfo1())));
                Log.v(TAG, String.valueOf(hwLesson.getVpInfo2().equals(compare.getVpInfo2())));
                if (hwLesson.getDay() == compare.getDay() &&
                        hwLesson.getGrade().getGradeName().equals(compare.getGrade().getGradeName()) &&
                        hwLesson.getSpTeacher().equals(compare.getSpTeacher()) &&
                        hwLesson.getSubject().equals(compare.getSubject()) &&
                        hwLesson.getRoom().equals(compare.getRoom()) &&
                        hwLesson.getSpRepeatType().equals(compare.getSpRepeatType())
                        ) {
                    for (int hour : compare.getHours()) {
                        if (!hoursArrayList.contains(hour)) {
                            hoursArrayList.add(hour);
                        }
                    }
                    participants.remove(compare);
                }
            }
            Collections.sort(hoursArrayList);
            combinedList.add(new HWLesson(hwLesson.getGrade(),
                    hoursArrayList.toArray(new Integer[hoursArrayList.size()]),
                    hwLesson.getDay(),
                    hwLesson.getSpTeacher(),
                    hwLesson.getSubject(),
                    hwLesson.getRoom(),
                    hwLesson.getSpRepeatType()
            ));
        }
        return combinedList.toArray(new HWLesson[combinedList.size()]);
    }*/


    public static class ChangedHour {
        private Integer hour;
        private Date date;

        public ChangedHour(Integer hour, Date date) {
            this.hour = hour;
            this.date = date;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static Comparator<ChangedHour> ChangedHourComparator = new Comparator<ChangedHour>() {
        @Override
        public int compare(ChangedHour lhs, ChangedHour rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    };
}
