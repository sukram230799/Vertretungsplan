package wuest.markus.vertretungsplan;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CombineData {
    public static String TAG = "CombineData";

    public static VPData[] combineVP(VPData[] vpData) {
        ArrayList<VPData> combinedList = new ArrayList<>();
        ArrayList<VPData> participants = new ArrayList<>(Arrays.asList(vpData));
        while (!participants.isEmpty()) {
            ArrayList<Integer> hoursArrayList = new ArrayList<>();
            VPData data = participants.get(0);
            for (int hour : data.get_hours()) {
                hoursArrayList.add(hour);
            }
            participants.remove(0);
            for (VPData compare : participants.toArray(new VPData[participants.size()])) {
                Log.v(TAG, String.valueOf(data.get_date().equals(compare.get_date())));
                Log.v(TAG, String.valueOf(data.get_grade().get_GradeName().equals(compare.get_grade().get_GradeName())));
                Log.v(TAG, String.valueOf(data.get_subject().equals(compare.get_subject())));
                Log.v(TAG, String.valueOf(data.get_room().equals(compare.get_room())));
                Log.v(TAG, String.valueOf(data.get_info1().equals(compare.get_info1())));
                Log.v(TAG, String.valueOf(data.get_info2().equals(compare.get_info2())));
                if (data.get_date().equals(compare.get_date()) &&
                        data.get_grade().get_GradeName().equals(compare.get_grade().get_GradeName()) &&
                        data.get_subject().equals(compare.get_subject()) &&
                        data.get_room().equals(compare.get_room()) &&
                        data.get_info1().equals(compare.get_info1()) &&
                        data.get_info2().equals(compare.get_info2())
                        ) {
                    for (int hour : compare.get_hours()) {
                        if (!hoursArrayList.contains(hour)) {
                            hoursArrayList.add(hour);
                        }
                    }
                    participants.remove(compare);
                }
            }
            Collections.sort(hoursArrayList);
            combinedList.add(new VPData(data.get_grade(),
                    hoursArrayList.toArray(new Integer[hoursArrayList.size()]),
                    data.get_subject(),
                    data.get_room(),
                    data.get_info1(),
                    data.get_info2(),
                    data.get_date()
            ));
        }
        return combinedList.toArray(new VPData[combinedList.size()]);
    }

    public VPData[] planChanges(VPData[] oldDataArray, VPData[] newDataArray) {
        List<VPData> bothData = new ArrayList<>(Arrays.asList(oldDataArray));
        bothData.addAll(Arrays.asList(newDataArray));
        /*for (VPData data : oldDataArray) {
            bothData.add(data);
        }
        for (VPData data : newDataArray) {
            bothData.add(data);
        }*/
        VPData[] combinedData = combineVP(bothData.toArray(new VPData[bothData.size()]));
        ArrayList<VPData> changesList = new ArrayList<>();
        for (VPData oldData : oldDataArray) {
            for (VPData data : combinedData) {
                if (!(Arrays.equals(oldData.get_hours(), data.get_hours())) &&
                        oldData.get_date().equals(data.get_date()) &&
                        oldData.get_grade().get_GradeName().equals(data.get_grade().get_GradeName()) &&
                        oldData.get_subject().equals(data.get_subject()) &&
                        oldData.get_room().equals(data.get_room()) &&
                        oldData.get_info1().equals(data.get_info1()) &&
                        oldData.get_info2().equals(data.get_info2())
                ) {
                }
            }
        }

        return null;
    }

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
        ArrayList<VPData> oldDataList;
        ArrayList<VPData> newDataList;
        if (oldDataArray != null) {
            oldDataList = new ArrayList<>(oldDataArray.length);
            for (VPData data : oldDataArray) {
                for (Integer hour : data.get_hours()) {
                    Integer[] hours = {hour};
                    oldDataList.add(new VPData(data.get_grade(), hours, data.get_subject(), data.get_room(), data.get_info1(), data.get_info2(), data.get_date()));
                }
            }
        } else {
            oldDataList = new ArrayList<>(0);
        }
        if (newDataArray != null) {
            newDataList = new ArrayList<>(newDataArray.length);
            for (VPData data : newDataArray) {
                for (Integer hour : data.get_hours()) {
                    Integer[] hours = {hour};
                    newDataList.add(new VPData(data.get_grade(), hours, data.get_subject(), data.get_room(), data.get_info1(), data.get_info2(), data.get_date()));
                }
            }
        } else {
            newDataList = new ArrayList<>(0);
        }
        ArrayList<VPData> oldRemove = new ArrayList<>(oldDataList.size());
        ArrayList<VPData> newRemove = new ArrayList<>(newDataList.size());
        for (VPData oldData : /**/oldDataList/*/oldDataArray*/) {
            for (VPData newData : /**/newDataList/*/newDataArray*/) {
                if (
                        oldData.get_hours()[0].equals(newData.get_hours()[0]) &&
                                oldData.get_grade().get_GradeName().equals(newData.get_grade().get_GradeName()) &&
                                oldData.get_date().equals(newData.get_date()) &&
                                oldData.get_subject().equals(newData.get_subject()) &&
                                oldData.get_room().equals(newData.get_room()) &&
                                oldData.get_info1().equals(newData.get_info1()) &&
                                oldData.get_info2().equals(newData.get_info2())
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
            if (!changes.contains(GetVP.dateFormat.format(data.get_date()) + "|" + data.get_hours()[0])) {
                changes.add(GetVP.dateFormat.format(data.get_date()) + "|" + data.get_hours()[0]);
            }
        }
        for (VPData data : newDataList) {
            if (!changes.contains(GetVP.dateFormat.format(data.get_date()) + "|" + data.get_hours()[0])) {
                changes.add(GetVP.dateFormat.format(data.get_date()) + "|" + data.get_hours()[0]);
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

    public static HWLesson[] combineHWLessons(HWLesson[] hwLessons) {
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
                /*Log.v(TAG, String.valueOf(hwLesson.get_date().equals(compare.get_date())));
                Log.v(TAG, String.valueOf(hwLesson.get_grade().get_GradeName().equals(compare.get_grade().get_GradeName())));
                Log.v(TAG, String.valueOf(hwLesson.get_subject().equals(compare.get_subject())));
                Log.v(TAG, String.valueOf(hwLesson.get_room().equals(compare.get_room())));
                Log.v(TAG, String.valueOf(hwLesson.get_info1().equals(compare.get_info1())));
                Log.v(TAG, String.valueOf(hwLesson.get_info2().equals(compare.get_info2())));*/
                if (hwLesson.getDay() == (compare.getDay()) &&
                        hwLesson.getGrade().get_GradeName().equals(compare.getGrade().get_GradeName()) &&
                        hwLesson.getTeacher().equals(compare.getTeacher()) &&
                        hwLesson.getSubject().equals(compare.getSubject()) &&
                        hwLesson.getRoom().equals(compare.getRoom()) &&
                        hwLesson.getRepeatType().equals(compare.getRepeatType())
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
                    hwLesson.getTeacher(),
                    hwLesson.getSubject(),
                    hwLesson.getRoom(),
                    hwLesson.getRepeatType()
            ));
        }
        return combinedList.toArray(new HWLesson[combinedList.size()]);
    }

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
