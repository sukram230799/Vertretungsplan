package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;

public class GetRepeatTypes implements Runnable {

    public static final String TAG = "GetRepeatTypes";

    private Context context;
    private Handler handler;

    public GetRepeatTypes(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            Document doc = Jsoup.connect("http://vertretungsplan.sukram230799.bplaced.net/getRepeatTypes/")
                    .parser(Parser.xmlParser())
                    .get();
            String year = doc.select("year").html();
            Log.d(TAG, year);
            Preferences.saveStringToPreferences(context, context.getString(R.string.QUARTER_LAST_UPDATE), year);

            int q1s = Integer.parseInt(doc.select("q1").select("start").html());
            int q1e = Integer.parseInt(doc.select("q1").select("end").html());
            int q2s = Integer.parseInt(doc.select("q2").select("start").html());
            int q2e = Integer.parseInt(doc.select("q2").select("end").html());
            int q3s = Integer.parseInt(doc.select("q3").select("start").html());
            int q3e = Integer.parseInt(doc.select("q3").select("end").html());
            int q4s = Integer.parseInt(doc.select("q4").select("start").html());
            int q4e = Integer.parseInt(doc.select("q4").select("end").html());

            Log.d(TAG, "" + q1s);
            Log.d(TAG, "" + q1e);
            Log.d(TAG, "" + q2s);
            Log.d(TAG, "" + q2e);
            Log.d(TAG, "" + q3s);
            Log.d(TAG, "" + q3e);
            Log.d(TAG, "" + q4s);
            Log.d(TAG, "" + q4e);

            Preferences.saveIntToPreferences(context, context.getString(R.string.Q1_S), q1s);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q1_E), q1e);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q2_S), q2s);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q2_E), q2e);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q3_S), q3s);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q3_E), q3e);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q4_S), q4s);
            Preferences.saveIntToPreferences(context, context.getString(R.string.Q4_E), q4e);

        } catch (IOException e) {
            Bundle bundle = new Bundle();
            bundle.putString("Error", e.getMessage());
            message.setData(bundle);
            e.printStackTrace();
        }
        if (handler != null) {
            message.obj = context;
            handler.sendMessage(message);
        }
    }
}
