package wuest.markus.vertretungsplan;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CombineData {
    public static String TAG = "CombineData";

    public static VPData[] combine(VPData[] vpData) {
        ArrayList<VPData> combinedList = new ArrayList<>();
        ArrayList<VPData> participants = new ArrayList<>(vpData.length);
        for (VPData data : vpData) {
            participants.add(data);
        }
        while (!participants.isEmpty()) {
            ArrayList<Integer> hoursArrayList = new ArrayList<>();
            VPData data = participants.get(0);
            for (int hour : data.get_hours()) {
                hoursArrayList.add(hour);
            }
            participants.remove(0);
            for (VPData compare : (VPData[]) participants.toArray(new VPData[participants.size()])) {
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
                    (Integer[]) hoursArrayList.toArray(new Integer[hoursArrayList.size()]),
                    data.get_subject(),
                    data.get_room(),
                    data.get_info1(),
                    data.get_info2(),
                    data.get_date()
            ));
        }
        return (VPData[]) combinedList.toArray(new VPData[combinedList.size()]);
    }

    public VPData[] planChanges(VPData[] oldDataArray, VPData[] newDataArray) {
        List<VPData> bothData = new ArrayList<>(oldDataArray.length + newDataArray.length);
        for (VPData data : oldDataArray) {
            bothData.add(data);
        }
        for (VPData data : newDataArray) {
            bothData.add(data);
        }
        VPData[] combinedData = combine((VPData[]) bothData.toArray(new VPData[bothData.size()]));
        ArrayList<VPData> changesList = new ArrayList<>();
        for (VPData oldData : oldDataArray) {
            for (VPData data : combinedData) {
                if (!(oldData.get_hours().equals(data.get_hours())) &&
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
    public static String combineHours(Integer[] hoursArray) {
        ArrayList<Integer> hours = new ArrayList<>(hoursArray.length);
        for (Integer hour : hoursArray) {
            hours.add(hour);
        }
        Collections.sort(hours);
        String combined = String.valueOf(hours.get(0));
        Integer lastNumber = hours.get(0);
        Integer combineTimes = 0;
        for (Integer hour : hours) {
            if (lastNumber != hour) {
                if (lastNumber == hour - 1) {
                    lastNumber = hour;
                    combineTimes++;
                } else {
                    if (combineTimes > 0) {
                        combined += "-" + lastNumber + ", " + hour;
                    } else {
                        combined += ", " + hour;
                    }
                    combineTimes = 0;
                    lastNumber = hour;
                }
            }
        }
        if (combineTimes > 0) {
            combined += "-" + lastNumber;
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
                if(
                        oldData.get_hours()[0].equals(newData.get_hours()[0]) &&
                                oldData.get_grade().get_GradeName().equals(newData.get_grade().get_GradeName()) &&
                                oldData.get_date().equals(newData.get_date()) &&
                                oldData.get_subject().equals(newData.get_subject())&&
                                oldData.get_room().equals(newData.get_room()) &&
                                oldData.get_info1().equals(newData.get_info1()) &&
                                oldData.get_info2().equals(newData.get_info2())
                        ){
                    //if (oldData.equals(newData)) {
                    oldRemove.add(oldData);
                    newRemove.add(newData);
                    //oldDataList.remove(oldData);
                    //newDataList.remove(newData);
                }
            }
        }
        for(VPData id : oldRemove){
            oldDataList.remove(id);
        }
        for(VPData id : newRemove){
            newDataList.remove(id);
        }
        ArrayList<String> changes = new ArrayList<String>();
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
        ArrayList<ChangedHour> ChangedHour = new ArrayList<ChangedHour>();
        for (String value : changes) {
            String[] values = value.split("\\|");
            try {
                ChangedHour.add(new ChangedHour(Integer.parseInt(values[1]), GetVP.dateFormat.parse(values[0])));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(ChangedHour, ChangedHourComparator);
        return (ChangedHour[]) ChangedHour.toArray(new ChangedHour[ChangedHour.size()]);
    }

    public static String getChanges(ChangedHour[] changedHours) {
        ArrayList<Integer> hours = new ArrayList<>();
        Date date = new Date();
        String changesString = null;
        if (changedHours != null && changedHours.length > 0) {
            date = changedHours[0].getDate();
            changesString = GetVP.dateFormat.format(date) + " ";
            for (ChangedHour hour : changedHours) {
                if (hour.getDate().equals(date)) {
                    hours.add(hour.getHour());
                } else {
                    date = hour.getDate();
                    changesString += combineHours(hours.toArray(new Integer[hours.size()])) + "\n" + GetVP.dateFormat.format(date) + " ";
                    hours = new ArrayList<>();
                    hours.add(hour.getHour());
                }
            }
        }
        if (!hours.isEmpty()) {
            changesString += combineHours(hours.toArray(new Integer[hours.size()]));
        }
        return changesString;
    }

    /*public boolean isChanged(VPData[] oldData, VPData[] newData) {
        VPData[][] data = findChanges(oldData, newData);
        if (!(data.length == 0)) {
            if (data.length > 1) {
                if (data[0].length > 0 && data[1].length > 0) {
                    return true;
                }
            }
        }
        return false;
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

/*
    public CompareResult[] compareText(VPData[] oldDatas, VPData[] newDatas) {
        ArrayList<CompareResult> compareResults = new ArrayList<CompareResult>();
        if (newDatas.length > 0) {
            for (VPData newData : newDatas) {
                for (VPData oldData : oldDatas) {

                }
            }
        }
        else {
            for(VPData data : oldDatas){
                compareResults.add(new CompareResult("Am " + GetVP.dateFormat.format(data.get_date()) + " zur " + data.get_hours()[0] + ". Stunde",""))
            }
        }
        return null;
    }


    /*
        public class HourComparator implements Comparator<VPData> {

            @Override
            public int compare(VPData lhs, VPData rhs) {
                return lhs.get_hours()[0].compareTo(rhs.get_hours()[0]);
            }
        }
    public class CompareResult {
        private String oldString;
        private String newString;

        public String getOldString() {
            return oldString;
        }

        public void setOldString(String oldString) {
            this.oldString = oldString;
        }

        public String getNewString() {
            return newString;
        }

        public void setNewString(String newString) {
            this.newString = newString;
        }

        public CompareResult(String oldString, String newString) {
            this.oldString = oldString;
            this.newString = newString;

        }
    }*/
}
