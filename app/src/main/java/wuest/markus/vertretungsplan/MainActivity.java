package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements /*Navigation*/DrawerFragment.ItemSelectedListener, VPFragment.RefreshContentListener, TimeTableFragment.RefreshContentListener, TabbedTimeTableFragment.EditInterface, TimeTableFragment.EditInterface {

    Handler vpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
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
                            try {
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                VPFragment vpFragment = VPFragment.newInstance(dbHandler.getGrade(position));
                                vpFragment.setRefreshListener(mainActivity);
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, vpFragment)
                                        .commit();
                            } catch (DBError e) {
                                e.printStackTrace();
                            }
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
        }
    };

    private MainActivity mainActivity = this;
    Handler gradesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
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
                                progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
                                progressDialog.setMessage("Initializing");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                if (!isFinishing()) {
                                    progressDialog.show();
                                }
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
        }
    };

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                final Context context = (Context) msg.obj;
                final Bundle bundle = msg.getData();
                if (bundle.getBoolean(getString(R.string.update_avaliable), false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Update");
                    builder.setCancelable(false);
                    builder.setMessage("Es gibt eine neue Version!");
                    builder.setPositiveButton("Herunterladen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String url = "http://1drv.ms/1OiRDtE";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
    private /*Navigation*/DrawerFragment drawerFragment;

    private int tableType; //Resembles the kind of Fragment for example TimeTableFragment;
    private boolean editTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
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
            //new Thread(new Initialize(this, gradesHandler)).start();
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Initializing");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            if (!isFinishing()) {
                progressDialog.show();
            }
        }

        new Thread(new CallHome(this, updateHandler)).start();
        tableType = -1;
        if (Preferences.readBooleanFromPreferences(this, getString(R.string.SHOW_VP), false)) {
            tableType = 0;
        }

        if (Preferences.readBooleanFromPreferences(this, getString(R.string.SHOW_TABLE), false)) {
            tableType = 1;
        }

        if (Preferences.readBooleanFromPreferences(this, getString(R.string.SHOW_TABBEDTABLE), false)) {
            tableType = 2;
        }
        SetUp();

        String grade = Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), null);
        String group = Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GROUP), null);
        if (grade == null || group == null || grade.equals("") || group.equals("")) {
            ConfigureDialog configureDialog = new ConfigureDialog();
            configureDialog.show(getSupportFragmentManager(), "fragment_configure_dialog");
        }
    }

    public void SetUp() {
        drawerFragment = (/*Navigation*/DrawerFragment)
                //getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        //drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setItemSelectedListener(this);
        position = dbHandler.getGradePosition(new HWGrade(Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "NULL")));
        if (position < 0) {
            position = 0;
        }
        loadVPFragment(getSupportFragmentManager(), position);
        Log.d(TAG, Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "NULL"));

        //startActivity(new Intent(this, TableEditor.class));
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
        } else if (id == R.id.updatedataset) {
            manualupdate = true;
            startService(new Intent(this, UpdateDataSet.class));
        } else if (id == R.id.fakedata) {
            HWGrade hwGrade;
            try {
                hwGrade = dbHandler.getGrade(position);
            } catch (DBError dbError) {
                dbError.printStackTrace();
                hwGrade = new HWGrade("TG11-2");
            }
            Integer[] hour0 = {0, 1};
            Integer[] hour1 = {2, 3};
            Integer[] hour2 = {4, 5};
            Integer[] hour3 = {6, 7};
            Integer[] hour4 = {8, 9};
            VPData[] vpData = {new VPData(hwGrade, hour0, "S", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour1, "C", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour2, "H", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour3, "U", "A1337", "Freisetzung", "", new Date()),
                    new VPData(hwGrade, hour4, "L", "A1337", "Freisetzung", "", new Date())};
            dbHandler.addPlan(vpData);
            loadVPFragment(getSupportFragmentManager(), position);
        } else if (id == R.id.showid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ID");
            builder.setCancelable(false);
            builder.setMessage(String.valueOf(Preferences.readIntFromPreferences(this, getString(R.string.ID), -1)));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            if (!isFinishing()) {
                alertDialog.show();
            }
        } else if (id == R.id.showTableEditor) {
            startActivity(new Intent(this, TableEditor.class));
        } else if (id == R.id.deleteTimeTable) {
            dbHandler.dropTimeTable();
        } else if (id == R.id.getTimeTableCSV) {
            try {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Stundenplan", TimeTableHelper.getCSV(dbHandler.getTimeTable(new HWGrade("TG11-2")), ",", "\n"));
                clipboard.setPrimaryClip(clip);
            } catch (DBError error) {
                error.printStackTrace();
            }
        } else if (id == R.id.getQR) {
            try {

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                startActivityForResult(intent, 0);

            } catch (Exception e) {

                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);

            }
        } else if (id == R.id.addTimeTableCSV) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CSV Import");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HWLesson[] lessons = TimeTableHelper.parseCSV(input.getText().toString(), ",", "\\r?\\n");
                    for (HWLesson lesson : lessons) {
                        dbHandler.addLesson(lesson);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else if (id == R.id.AlarmSP) {
            AlarmSP alarmSP = new AlarmSP();
            alarmSP.SetAlarm(this);
            //startService(new Intent(this, TimeTableService.class));
        }

        /*else if (id == R.id.shareTimeTableCSV) {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, "test.csv");
                sendIntent.putExtra(Intent.EXTRA_TEXT, TimeTableHelper.getCSV(dbHandler.getTimeTable(new HWGrade("TG11-2")), ",", "\n"));
                sendIntent.setType("text/csv");
                startActivity(Intent.createChooser(sendIntent, "CSV"));
            } catch (DBError error) {
                error.printStackTrace();
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    private int position = -1;
    private boolean shown = false;

    @Override
    public void drawerItemSelected(View view, int position, boolean longclick) {
        Log.d(TAG, "TableType: " + position);
        tableType = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        loadVPFragment(fragmentManager, this.position);
    }

    @Override
    public void gradeItemSelected(View view, int position, boolean longclick) {
        this.position = position;
        Log.d("DEBUG.Item", String.valueOf(position));
        FragmentManager fragmentManager = getSupportFragmentManager();
        loadVPFragment(fragmentManager, position);
    }

    //Receive of QR-Code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Log.d(TAG, result.toString());
            } else {
                //showDialog(R.string.result_failed, getString(R.string.result_failed_why));
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
                HWLesson[] newLessons = TimeTableHelper.parseURL(contents, ";", "\\+");
                //HWLesson[] oldLessons = new HWLesson[0];
                for (HWLesson lesson : newLessons) {
                    dbHandler.addLesson(lesson);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
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
        HWGrade grade;

        try {
            grade = dbHandler.getGrade(position);
            String title = grade.getGradeName();
            toolbar.setTitle("Klasse: " + title);

            shown = false;
            Log.d(TAG, "TableType: " + tableType);
            switch (tableType) {
                case 0:
                    VPFragment vpFragment = VPFragment.newInstance(grade);
                    vpFragment.setRefreshListener(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, vpFragment)
                            .commit();
                    break;
                case 1:
                    TimeTableFragment timeTableFragment = TimeTableFragment.newInstance(grade, Calendar.getInstance().get(Calendar.DAY_OF_WEEK), true, false);
                    timeTableFragment.setRefreshListener(this);
                    timeTableFragment.setEditInterface(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, timeTableFragment)
                            .commit();
                    break;
                case 2:
                    TabbedTimeTableFragment tabbedTimeTableFragment = TabbedTimeTableFragment.newInstance(grade);
                    tabbedTimeTableFragment.setEditInterface(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, tabbedTimeTableFragment)
                            .commit();
                    break;
                default:
                    Log.d(TAG, "No TableType");
            }
        } catch (DBError e) {
            e.printStackTrace();
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

    @Override
    public void onEditLesson(int day, HWGrade grade) {
        Intent intent = new Intent(this, SelectLessonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("DAY", day);
        bundle.putString("GRADE", grade.getGradeName());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onAddLesson(int day, HWGrade grade) {
        startActivity(new Intent(this, TableEditor.class));
    }

    @Override
    public void onShareTimeTable() {
        try {
            encodeBarcode("TEXT_TYPE", TimeTableHelper.getURLForShare(dbHandler.getTimeTable(new HWGrade("TG11-2")), ";", "+"));
        } catch (DBError error) {
            error.printStackTrace();
        }

    }

    private void encodeBarcode(CharSequence type, CharSequence data) {
        try {
            /*IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.autoWide();
            integrator.addExtra("ENCODE_SHOW_CONTENTS", false);
            integrator.shareText(data, type);*/
            Intent intent = new Intent();
            intent.setAction("com.google.zxing.client.android.ENCODE");

            intent.putExtra("ENCODE_TYPE", type);
            intent.putExtra("ENCODE_DATA", data);
            intent.putExtra(Intent.EXTRA_TITLE, "Stundenplan");
            //intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.CODABAR.toString());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            intent.putExtra("ENCODE_SHOW_CONTENTS", false);
            //attachMoreExtras(intent); Same as above!
            startActivity(intent);
            //startActivityForResult(intent, 230799);
        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
    }
}
