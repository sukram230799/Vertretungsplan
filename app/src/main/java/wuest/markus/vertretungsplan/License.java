package wuest.markus.vertretungsplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class License extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        String licenseURL;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                licenseURL = null;
            } else {
                licenseURL = extras.getString("URL");
            }
        } else {
            licenseURL = (String) savedInstanceState.getSerializable("URL");
        }
        Log.d("LICENSE", licenseURL);
        WebView licenseWebView = (WebView) findViewById(R.id.license_webview);
        licenseWebView.loadUrl(licenseURL);
    }
}
