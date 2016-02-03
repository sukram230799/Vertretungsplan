package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Handler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;

public class Initialize implements Runnable {

    private Context context;
    private Handler handler;

    public Initialize(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    @Override
    public void run() {
        int id = Preferences.readIntFromPreferences(context, context.getString(R.string.ID), -1);
        if(id==-1){
            Random random = new Random();
            id = random.nextInt();
            Preferences.saveIntToPreferences(context, context.getString(R.string.ID), id);
            try {
                Document doc = Jsoup.connect("http://vp.hw-schule.de/request.php")
                        .data("id", String.valueOf(id))
                        .data("comp", String.valueOf(Math.round(Math.sqrt(id))))
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Thread(new GetGradesFromVPGrades(context, handler)).start();
    }
}
