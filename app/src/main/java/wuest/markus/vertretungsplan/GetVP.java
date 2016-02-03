package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetVP implements Runnable {

    public static final String TAG = "GetVP";

    private Context context;
    private HWGrade grade;
    private Handler handler;

    public GetVP(Context context, HWGrade grade, Handler handler) {
        this.context = context;
        this.grade = grade;
        this.handler = handler;
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void run() {
        Log.d(TAG, handler.toString());
        Log.v(TAG, "@getVP");
        if (!(grade == null)) {
            Message message = new Message();
            try {
                DBHandler dbHandler = new DBHandler(context, null, null, 1);
                Document document = Jsoup.connect("http://vp.hw-schule.de/request.php")
                        .userAgent("wuest.markus.vertretungsplan")
                        .data("type", "getVertretungsplan")
                        .data("id", grade.get_GradeName())
                        .post();
                VPData[] vpData = parseData(document.html());
                dbHandler.removeVP(grade);
                dbHandler.addPlan(vpData);
                dbHandler.close();
                Log.v(TAG, String.valueOf(vpData.length));
            } catch (IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("Error", e.getMessage());
                bundle.putString("HWGrade", grade.get_GradeName());
                message.setData(bundle);
                e.printStackTrace();
            }
            if (handler != null) {
                message.obj = context;
                handler.sendMessage(message);
            }
        }
        Log.v(TAG, "-getVP");
    }

    private VPData[] parseData(String webpage) {
        ArrayList<VPData> vpDataArrayList = new ArrayList<>();
        Document document = Jsoup.parseBodyFragment(webpage);
        for (Element day : document.select("div.tagesansicht")) {
            HWGrade grade;
            String dayName;
            Date date;
            //Selecting every div with each day
            for (Element spandate : day.select("span.tagesdatum")) {
                //Parsing the grade, dayName and date
                //Example input: <span class='tagesdatum'>&Auml;nderungen f&uuml;r die Klasse 6TG8/2 f&uuml;r Dienstag, den 28.07.2015</span>
                String text = spandate.html().replaceFirst("Änderungen für die Klasse ", "");
                text = text.replace(" für ", "|");
                text = text.replace(", den ", "|");
                String[] parts = text.split("\\|");
                grade = new HWGrade(parts[0]);
                dayName = parts[1];
                try {
                    date = dateFormat.parse(parts[2]);
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = new Date();
                }
                //After getting the general information (which are the same for every following object)
                //Selecting the Table
                for (Element row : day.select("tr.reihe")) {
                    //In this section the rows with their hours are seperated!
                    //v This step is made because Jsoup expects a whole table
                    Document rowDoc = Jsoup.parseBodyFragment("<table><tr>" + row.html() + "</tr></table>");
                    int hour;
                    for (Element hourEl : rowDoc.select("td.spalte1")) {
                        //Every hour object is seperated in the following step! This is needed because sometime the website combines the hours
                        try {
                            //Now we try to parse the hour as int but if it uses multiple hours the function generate a "NumberFormatException"
                            hour = Integer.parseInt(hourEl.html());
                            vpDataArrayList.add(parseRow(hour, rowDoc, grade, date));
                        } catch (NumberFormatException e) {
                            String[] hours = hourEl.html().split(" - ");
                            Integer firsthour = Integer.parseInt(hours[0]);
                            Integer secondhour = Integer.parseInt(hours[1]);
                            for (hour = firsthour; hour <= secondhour; hour++) {
                                vpDataArrayList.add(parseRow(hour, rowDoc, grade, date));
                            }
                        }
                    }
                }
            }
        }
        return vpDataArrayList.toArray(new VPData[vpDataArrayList.size()]);
    }

    private VPData parseRow(int hour, Element element, HWGrade grade, Date date) {
        ArrayList<String> stringArrayList = new ArrayList<>(2);
        for (Element row : element.select("td.spalte4")) {
            if (row.html().contains("[")) {
                stringArrayList.add("");
            } else {
                stringArrayList.add(row.html());
            }
        }
        if (stringArrayList.size() < 2) {
            stringArrayList.add("");
        }
        return new VPData(grade, new Integer[]{hour},
                element.select("td.spalte2").html(),
                element.select("td.spalte3").html(),
                stringArrayList.get(0),
                stringArrayList.get(1),
                date);
    }
}
