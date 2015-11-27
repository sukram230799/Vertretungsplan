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
import java.util.ArrayList;

public class GetGradesFromGradeList implements Runnable {

    public static final String TAG = "GetGradesFromGradeList";

    private static Context context;
    private static Handler handler;

    public GetGradesFromGradeList(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = new Message();
        ArrayList<HWGrade> grades = new ArrayList<HWGrade>();
        try {
            Document doc = Jsoup.connect("http://intern.hw.pf.bw.schule.de/images/hws_info/stundenplaene/Kla_lang.htm")
                    .get();
            for (Element element : doc.select("table[cellspacing]")) {
                Document table = Jsoup.parse("<table>" + element.html() + "</table>");
                for (Element grade : table.select("a[href]")) {
                    Log.v(TAG, grade.text());
                    grades.add(new HWGrade(grade.text()));
                }
            }

        } catch (IOException e) {
            Bundle bundle = new Bundle();
            bundle.putString("Error", e.getMessage());
            message.setData(bundle);
        }
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        for (HWGrade grade : grades) {
            dbHandler.addGrade(grade);
        }
        dbHandler.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        if (handler != null) {
            message.obj = context;
            handler.sendMessage(message);
        }
    }
}
