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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements /*Navigation*/DrawerFragment.ItemSelectedListener,
        VPFragment.RefreshContentListener, TimeTableFragment.RefreshContentListener,
        TabbedTimeTableFragment.EditInterface, TimeTableFragment.EditInterface, PlanFragment.EditInterface,
        SubjectChooserDialog.OnReloadData, TabbedPlanFragment.EditInterface, NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback, TabbedPlanFragment.RefreshContentListener, TabbedTimeTableFragment.RefreshContentListener,
        ShareDialog.QRRead {

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
                if (bundle.getBoolean(getString(R.string.update_available), false)) {
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
    private HWGrade grade;

    public static final String TAG = "MainActivity";
    /* Not used because defined in strings.xml
    public static final String PREF_FILE_NAME = "HWVPData";
    public static final String SELECTED_GRADE = "selectedGrade";
    public static final String REPEAT_TIME = "repeatTime";*/
    DBHandler dbHandler;

    static boolean foreground = false;
    static boolean manualupdate = false;

    private Toolbar toolbar;
    private /*Navigation*/ DrawerFragment drawerFragment;

    private int tableType; //Resembles the kind of Fragment for example TimeTableFragment;
    private boolean editTable;

    NfcAdapter mNfcAdapter;

    private boolean devMode = false;

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

        tableType = Preferences.readIntFromPreferences(this, getString(R.string.SELECTED_TYPE), 0);
        SetUp();

        String grade = Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), null);
        String group = Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GROUP), null);
        if (grade == null || group == null || grade.equals("") || group.equals("")) {
            ConfigureDialog configureDialog = new ConfigureDialog();
            configureDialog.show(getSupportFragmentManager(), "fragment_configure_dialog");
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        devMode = Preferences.readBooleanFromPreferences(this, getString(R.string.DEVELOPER_MODE), false);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Log.d(TAG, data.getHost());
            Log.d(TAG, data.getPath());
            Log.d(TAG, data.getQuery());
            String URL = "http://" + data.getHost() + data.getPath() + "?" + data.getQuery();
            Log.d(TAG, URL);
            importSP(TimeTableHelper.parseURL(URL, this), true);
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
        toolbar.setTitle("Klasse: " + grade.getGradeName());
        foreground = true;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
        boolean newSP = Preferences.readBooleanFromPreferences(this, getString(R.string.SP_CHANGED), false);
        if (newSP) {
            subjectChooser();
            Preferences.saveBooleanToPreferences(this, getString(R.string.SP_CHANGED), false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
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
            VPData[] vpData = {new VPData(-1, hwGrade, 1, "F", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 2, "F", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 3, "A", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 4, "A", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 5, "K", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 6, "K", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 8, "E", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 9, "E", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 10, "!", "A1337", "Freisetzung", "", new Date()),
                    new VPData(-1, hwGrade, 11, "!", "A1337", "Freisetzung", "", new Date())};
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
            readQR();
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
        } else if (id == R.id.changeGroupGrade) {
            ConfigureDialog configureDialog = new ConfigureDialog();
            configureDialog.show(getSupportFragmentManager(), "fragment_configure_dialog");
        } else if (id == R.id.selectSubjects) {
            subjectChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void readQR() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            try {
                startActivity(marketIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
                HWLesson[] lessons = TimeTableHelper.parseURL(contents, this);
                //HWLesson[] oldLessons = new HWLesson[0];
                HWGrade grade = null;
                if (lessons.length > 0) {
                    grade = lessons[0].getGrade();
                }
                importSP(lessons, false);
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

        try {
            grade = dbHandler.getGrade(position);
            String title = grade.getGradeName();
            toolbar.setTitle("Klasse: " + title);

            shown = false;
            Log.d(TAG, "TableType: " + tableType);
            Calendar calendar;
            switch (tableType) {
                case 0:
                    VPFragment vpFragment = VPFragment.newInstance(grade);
                    vpFragment.setRefreshListener(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, vpFragment)
                            .commit();
                    break;
                case 1:
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    TimeTableFragment timeTableFragment = TimeTableFragment.newInstance(grade,
                            TimeTableHelper.getNextHWTime(new HWTime(calendar), dbHandler.getDaysWithLessons(grade)), true);
                    timeTableFragment.setRefreshListener(this);
                    timeTableFragment.setEditInterface(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, timeTableFragment)
                            .commit();
                    break;
                case 2:
                    TabbedTimeTableFragment tabbedTimeTableFragment = TabbedTimeTableFragment.newInstance(grade);
                    tabbedTimeTableFragment.setEditInterface(this);
                    tabbedTimeTableFragment.setRefreshListener(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, tabbedTimeTableFragment)
                            .commit();
                    break;
                case 3:
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    PlanFragment planFragment = PlanFragment.newInstance(grade,
                            TimeTableHelper.getNextHWTime(new HWTime(calendar), dbHandler.getDaysWithLessons(grade)), true);
                    planFragment.setEditInterface(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, planFragment)
                            .commit();
                    break;
                case 4:
                    TabbedPlanFragment tabbedPlanFragment = TabbedPlanFragment.newInstance(grade);
                    tabbedPlanFragment.setEditInterface(this);
                    tabbedPlanFragment.setRefreshListener(this);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, tabbedPlanFragment)
                            .commit();
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
            String URL = TimeTableHelper.getURLForShare(dbHandler.getTimeTable(grade), grade, ";", "+");
            ShareDialog shareDialog = ShareDialog.newInstance(URL);
            shareDialog.setQrRead(this);
            shareDialog.show(getFragmentManager(), "TAG");
        } catch (DBError error) {
            error.printStackTrace();
        }

    }

    @Override
    public void reloadData() {
        loadVPFragment(getSupportFragmentManager(), position);
    }

    private void subjectChooser() {
        SubjectChooserDialog subjectChooserDialog = new SubjectChooserDialog();
        subjectChooserDialog.setOnReloadData(this);
        subjectChooserDialog.show(getSupportFragmentManager(), "fragment_subject_chooser_dialog");
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("No SP");
        try {
            HWLesson[] hwLessons = dbHandler.getTimeTable(grade);
            text = TimeTableHelper.getURLForShare(hwLessons, grade, ";", "+");
            if (devMode) Log.d(TAG, text);
        } catch (DBError error) {
            error.printStackTrace();
        }
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/wuest.markus.vertretungsplan", text.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the AAR
                 * is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this
                 * activity starts when receiving a beamed message. For now, this code
                 * uses the tag dispatch system.
                 */
                //,NdefRecord.createApplicationRecord("com.example.android.beam")
        );
        return msg;
    }

    private static final int MESSAGE_SENT = 1;

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /**
     * This handler receives a message from onNdefPushComplete
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        final String URL = new String(msg.getRecords()[0].getPayload());
        if (devMode) {
            Toast.makeText(this, URL, Toast.LENGTH_LONG).show();
        }
        HWLesson[] lessons = TimeTableHelper.parseURL(URL, mainActivity);
        HWGrade grade = null;
        if (lessons.length > 0) {
            grade = lessons[0].getGrade();
        }
        importSP(lessons, true);
        //mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
    }

    void importSP(final HWLesson[] lessons, final boolean finish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stundenplan");
        builder.setMessage("Alten Stundenplan löschen?");
        builder.setCancelable(false);
        builder.setNegativeButton("Behalten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (HWLesson lesson : lessons) {
                    dbHandler.addLesson(lesson);
                }
                dialog.dismiss();
                if (finish) {
                    Preferences.saveBooleanToPreferences(mainActivity, getString(R.string.SP_CHANGED), true);
                    finish();
                } else {
                    subjectChooser();
                }
            }
        });
        builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (lessons.length > 0) {
                    dbHandler.removeTimeTable(lessons[0].getGrade());
                }
                for (HWLesson lesson : lessons) {
                    dbHandler.addLesson(lesson);
                }
                dialog.dismiss();
                if (finish) {
                    Preferences.saveBooleanToPreferences(mainActivity, getString(R.string.SP_CHANGED), true);
                    finish();
                } else {
                    subjectChooser();
                }
            }
        });
        builder.show();
    }
}
