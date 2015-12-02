package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.ItemSelectedListener, VPFragment.RefreshContentListener {

    Handler vpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            final Context context = (Context) msg.obj;
            final Bundle bundle = msg.getData();
            if (bundle.getString("Error") == null) {
                loadVPFragment(getSupportFragmentManager(), position);
                if (refreshLayout != null) {
                    Log.d(TAG, "setRefreshing(false)");
                    refreshLayout.setRefreshing(false);
                    /*refreshLayout.destroyDrawingCache();
                    refreshLayout.clearAnimation();*/
                }
            } else {
                if (refreshLayout != null) {
                    refreshLayout.setColorSchemeColors(Color.RED);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Keine Verbindung");
                builder.setMessage("Die App konnte aufgrund eines Netzwerkfehlers die Daten nicht laden.");
                builder.setCancelable(false);
                builder.setPositiveButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bundle.getString("HWGrade") != null) {
                            if (refreshLayout != null) {
                                refreshLayout.setColorSchemeColors(Color.BLACK);
                            }
                            Log.d(GetVP.TAG, "From 1");
                            new Thread(new GetVP(context, new HWGrade(bundle.getString("HWGrade")), vpHandler)).start();
                        }
                    }
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        VPFragment vpFragment = VPFragment.newInstance(position);
                        vpFragment.setRefreshListener(mainActivity);
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, vpFragment)
                                .commit();
                        if (refreshLayout != null) {
                            Log.d(TAG, "setRefreshing(false)");
                            refreshLayout.setRefreshing(false);
                            /*refreshLayout.destroyDrawingCache();
                            refreshLayout.clearAnimation();*/
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                if (!isFinishing()) {
                    alertDialog.show();
                }
            }

            Log.d(TAG, "reloaded");
        }
    };

    private MainActivity mainActivity = this;
    Handler gradesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Context context = (Context) msg.obj;
            final Bundle bundle = msg.getData();
            progressDialog.dismiss();
            if (bundle.get("Error") == null) {
                drawerFragment.onResume();
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Neustarten");
                builder.setMessage("Die App muss nun neugestartet werden.");
                builder.setPositiveButton("Neustarten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startActivity = new Intent(context, MainActivity.class);
                        int pendingIntentId = 230799;
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingIntentId, startActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                        System.exit(0);
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                if (!isFinishing()) {
                    alertDialog.show();
                }*/
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Keine Verbindung");
                builder.setCancelable(false);
                builder.setMessage("Die App konnte aufgrund eines Netzwerkfehlers die Daten nicht laden.");
                builder.setPositiveButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bundle.getString("HWGrade") != null) {
                            new Thread(new GetGradesFromVPGrades(context, gradesHandler)).start();
                        }
                    }
                });
                builder.setNegativeButton("Beenden", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                if (!isFinishing()) {
                    alertDialog.show();
                }
            }
        }
    };

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout refreshLayout = null;

    public static final String TAG = "MainActivity";
    /* Not used because defined in strings.xml
    public static final String PREF_FILE_NAME = "HWVPData";
    public static final String SELECTED_GRADE = "selectedGrade";
    public static final String REPEAT_TIME = "repeatTime";*/
    DBHandler dbHandler;

    static boolean foreground = false;
    static boolean manualupdate = false;

    private Toolbar toolbar;
    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this, null, null, 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
        }
        //getSupportActionBar().setHomeButtonEnabled(true);

        try {
            dbHandler.getGrades();
        } catch (DBError dbError) {
            new Thread(new GetGradesFromVPGrades(this, gradesHandler)).start();
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Initializing");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            if (!isFinishing()) {
                progressDialog.show();
            }
        }

        SetUp();
    }

    public void SetUp() {
        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setItemSelectedListener(this);
        position = dbHandler.getGradePosition(new HWGrade(Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "NULL")));
        if (position < 0) {
            position = 0;
        }
        loadVPFragment(getSupportFragmentManager(), position);
        Log.d(TAG, Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "NULL"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!Preferences.readBooleanFromPreferences(this, getString(R.string.DEVELOPER_MODE), false)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_dev, menu);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                startActivity(new Intent(this, Settings.class));
            } else {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            return true;
        } else if (id == R.id.about) {
            startActivity(new Intent(this, About.class));
        } else if (id == R.id.sort) {
            dbHandler.sortGrades();
            drawerFragment.onResume();
        } else if (id == R.id.updatedataset){
            manualupdate = true;
            startService(new Intent(this, UpdateDataSet.class));
        } else if (id == R.id.fakedata){
            HWGrade hwGrade;
            try {
                hwGrade = dbHandler.getGrade(position);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                hwGrade = new HWGrade("TG11-2");
            }
            Integer[] hour0 = {0,1};
            Integer[] hour1 = {2,3};
            Integer[] hour2 = {4,5};
            Integer[] hour3 = {6,7};
            Integer[] hour4 = {8,9};
            VPData[] vpData = {new VPData(hwGrade, hour0, "S", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour1, "C", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour2, "H", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour3, "U", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour4, "L", "A1337", "Freisetzung", "", new Date())};
            dbHandler.addPlan(vpData);
            loadVPFragment(getSupportFragmentManager(), position);
        }

        return super.onOptionsItemSelected(item);
    }

    private int position = -1;
    private boolean shown = false;

    @Override
    public void gradeItemSelected(View view, int position, boolean longclick) {
        this.position = position;
        Log.d("DEBUG.Item", String.valueOf(position));
        FragmentManager fragmentManager = getSupportFragmentManager();
        loadVPFragment(fragmentManager, position);
    }

    private void loadVPFragment(FragmentManager fragmentManager, int position) {
        this.position = position;
        //HWGrade grade = null;
        //boolean isVP;
        /*try {
            grade = dbHandler.getGrade(position);
            isVP = dbHandler.isVP(grade);
        } catch (DBError dbError) {
            isVP = false;
        }*/
        /*if (isVP) {*/
        shown = false;
        VPFragment vpFragment = VPFragment.newInstance(position);
        vpFragment.setRefreshListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.container, vpFragment)
                .commit();
        try {
            String title = dbHandler.getGrade(position).get_GradeName();
            toolbar.setTitle("Klasse: " + title);
        } catch (DBError dbError) {
            dbError.printStackTrace();
            toolbar.setTitle("Vertretungsplan");
        }
        /*} else {
            if (!shown) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new LoadingFragment())
                        .commit();
            }
            shown = true;
            new Thread(new GetVP(this, grade, vpHandler)).start();
        }*/
    }

    @Override
    public void refreshedContent(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        HWGrade hwGrade;
        try {
            hwGrade = dbHandler.getGrade(position);
        } catch (DBError dbError) {
            dbError.printStackTrace();
            hwGrade = new HWGrade(Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "NULL"));
        }
        Log.d(GetVP.TAG, "From 2");
        new Thread(new GetVP(this, hwGrade, vpHandler)).start();

        Log.d(TAG, "reload");
    }
}
