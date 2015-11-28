package wuest.markus.vertretungsplan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "E-Mail", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "vp.school@outlook.com", null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "vp.school@outlook.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "VP Support");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Dem Entwickler eine E-Mail schreiben."));
            }
        });

        WebView aboutWebView = (WebView) findViewById(R.id.about_webview);
        aboutWebView.loadUrl("file:///android_asset/MITLicense.html");

        WebView josupLicense = (WebView) findViewById(R.id.jsoup_license);
        josupLicense.loadUrl("http://jsoup.org/license");
    }
}
