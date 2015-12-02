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

public class GetGradesFromVPGrades implements Runnable {

    private static Context context;
    private static Handler handler;

    public GetGradesFromVPGrades(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = new Message();
        ArrayList<HWGrade> grades = new ArrayList<>();
        String grade_number;
        for (int times = 1; times <= 7; times++) {
            try {
                grade_number = String.valueOf(times);
                Document doc = Jsoup.connect("http://vp.hw-schule.de/request.php")
                        .data("type", "getKlassen")
                        .data("id", grade_number)
                        .post();
                //Log.d("GRADES.TEXT", doc.text());
                if (!doc.text().equals("")) {
                    Log.d("GRADES", doc.text());
                    for (Element grade : doc.select("a")) {
                        Log.d("GRADES.GRADE", grade.html());
                        grades.add(new HWGrade(grade.html()));
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                Bundle bundle = new Bundle();
                bundle.putString("Error", e.getMessage());
                message.setData(bundle);
            }
        }
        DBHandler dbHandler = new DBHandler(context, null, null, 1);
        /*for (HWGrade grade : grades) {
            dbHandler.addGrade(grade);
        }*/
        dbHandler.saveAddGrades(grades.toArray(new HWGrade[grades.size()]));
        dbHandler.sortGrades();
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
