package wuest.markus.vertretungsplan;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TableEditor extends AppCompatActivity implements HourPickerDialog.NumberDialogInterface {


    final TableEditor tableEditor = this;
    public final static String TAG = "TableEditor";

    public static final String GRADE = "grade";
    public static final String DAY = "day";
    public static final String HOURS = "hours";
    public static final String TEACHER = "teacher";
    public static final String SUBJECT = "subject";
    public static final String ROOM = "room";
    public static final String REPEATTYPE = "repeatType";


    private AutoCompleteTextView textTeacher;
    private AutoCompleteTextView textDay;
    private AutoCompleteTextView textHour;
    private AutoCompleteTextView textGrade;
    private AutoCompleteTextView textSubject;
    private AutoCompleteTextView textRoom;
    private AutoCompleteTextView textRepeatType;

    private TextInputLayout textLayoutTeacher;
    private TextInputLayout textLayoutDay;
    private TextInputLayout textLayoutHour;
    private TextInputLayout textLayoutGrade;
    private TextInputLayout textLayoutSubject;
    private TextInputLayout textLayoutRoom;
    private TextInputLayout textLayoutRepeatType;

    //private ScrollView scrollView;

    private InputMethodManager imm;

    private DBHandler dbHandler;

    private int startHour = 1;
    private int endHour = 1;

    private HWLesson lesson = null;
    private int day = 0;


    /*public TableEditor(HWLesson lesson) {
        this.lesson = lesson;
    }

    public TableEditor(int day) {
        this.day = day;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                lesson = new HWLesson(new HWGrade(bundle.getString(GRADE)),
                        bundle.getIntegerArrayList(HOURS).toArray(new Integer[bundle.getIntegerArrayList(HOURS).size()]),
                        bundle.getInt(DAY),
                        bundle.getString(TEACHER),
                        bundle.getString(SUBJECT),
                        bundle.getString(ROOM),
                        bundle.getString(REPEATTYPE));
                startHour = lesson.getHours()[0];
                endHour = lesson.getHours()[lesson.getHours().length - 1];
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }
            Log.d(TAG, String.valueOf(startHour));
            Log.d(TAG, String.valueOf(endHour));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
        }
        //getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_36dp);

        manageInputs();

        setHour(textHour);

        textHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HourPickerDialog hourPickerDialog = HourPickerDialog.newInstance(startHour, endHour);
                hourPickerDialog.setNumberDialogInterface(tableEditor);
                hourPickerDialog.show(getSupportFragmentManager(), "fragment_hour_picker_dialog");
            }
        });
        textHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    HourPickerDialog hourPickerDialog = HourPickerDialog.newInstance(startHour, endHour);
                    hourPickerDialog.setNumberDialogInterface(tableEditor);
                    hourPickerDialog.show(getSupportFragmentManager(), "fragment_hour_picker_dialog");
                }
            }
        });

        dbHandler = new DBHandler(this, null, null, 0);

        ArrayList<String> teachers = new ArrayList<>(20);
        ArrayList<String> subjects = new ArrayList<>(30);
        ArrayList<String> rooms = new ArrayList<>(50);
        //lesson;
        HWLesson[] hwLessons;

        try {
            hwLessons = dbHandler.getTimeTable(new HWGrade(Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), "")));
            hwLessons = CombineData.combineHWLessons(hwLessons);
            HWLesson[] selectedLessons = TimeTableHelper.selectLessonsFromRepeatType(hwLessons, GregorianCalendar.getInstance().get(Calendar.WEEK_OF_YEAR), null, this);
            for (HWLesson lesson : selectedLessons) {
                if (lesson.getDay() == day && lesson.getHours()[0] >= startHour && lesson.getHours()[0] <= endHour) {
                    //TODO investigate why this function is used.
                }
            }
            for (HWLesson lesson : hwLessons) {
                if (!teachers.contains(lesson.getTeacher())) {
                    teachers.add(lesson.getTeacher());
                }
                if (!subjects.contains(lesson.getSubject())) {
                    subjects.add(lesson.getSubject());
                }
                if (!rooms.contains(lesson.getRoom())) {
                    rooms.add(lesson.getRoom());
                }

            }
        } catch (DBError error) {
            error.printStackTrace();
        }


        ArrayList<String> grades = new ArrayList<>();
        try {
            for (HWGrade grade : dbHandler.getGrades()) {
                grades.add(grade.getGradeName());
            }
        } catch (DBError error) {
            error.printStackTrace();
            grades.add("TG11-2");
        }
        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, grades);
        textGrade.setAdapter(gradesAdapter);

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, teachers);
        textTeacher.setAdapter(teacherAdapter);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, subjects);
        textSubject.setAdapter(subjectAdapter);

        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, rooms);
        textRoom.setAdapter(roomAdapter);
        textRoom.setAdapter(roomAdapter);

        String[] days = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, days);
        textDay.setAdapter(dayAdapter);

        new Thread(new GetRepeatTypes(this, new Handler())).start();
    }

    private void setHour(AutoCompleteTextView textHour) {
        if (startHour == endHour)
            textHour.setText(String.valueOf(startHour));
        else
            textHour.setText(startHour + " - " + endHour);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_table_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {

            ArrayList<Integer> hours = new ArrayList<>(endHour - startHour + 1);

            for (int counter = startHour; counter <= endHour; counter++) {
                hours.add(counter);
                Log.d(TAG, "" + counter);
            }

            /*if (textGrade.getText().toString().trim().length() <= 0) {
                //textGrade.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                textLayoutGrade.setError("Bitte Klasse eingeben.");
                textLayoutGrade.setErrorEnabled(true);
            } else {
                textLayoutGrade.setErrorEnabled(false);
            }*/

            int day;
            if (textDay.getText().toString().trim().length() > 0 && getDay() != Calendar.SUNDAY) {
                day = getDay();
            } else {
                return true;
            }


            if (textTeacher.getText().toString().trim().length() > 0 &&
                    textDay.getText().toString().trim().length() > 0 &&
                    textHour.getText().toString().trim().length() > 0 &&
                    textGrade.getText().toString().trim().length() > 0 &&
                    textSubject.getText().toString().trim().length() > 0 &&
                    textRoom.getText().toString().trim().length() > 0 &&
                    textRepeatType.getText().toString().trim().length() > 0) {
                if (lesson == null) {
                    dbHandler.addLesson(new HWLesson(new HWGrade(textGrade.getText().toString().trim()),
                            hours.toArray(new Integer[hours.size()]),
                            day,
                            textTeacher.getText().toString().trim(),
                            textSubject.getText().toString().trim(),
                            textRoom.getText().toString().trim(),
                            textRepeatType.getText().toString().trim()));
                } else {
                    dbHandler.updateLesson(lesson, new HWLesson(new HWGrade(textGrade.getText().toString().trim()),
                                    hours.toArray(new Integer[hours.size()]),
                                    day,
                                    textTeacher.getText().toString().trim(),
                                    textSubject.getText().toString().trim(),
                                    textRoom.getText().toString().trim(),
                                    textRepeatType.getText().toString().trim())
                    );
                }
                finish();
            }

            Log.d(TAG, startHour + " - " + endHour);
            Log.d(TAG, textRepeatType.getText().toString().trim());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueSaved(int startHour, int endHour) {
        textTeacher.requestFocus();
        this.startHour = startHour;
        this.endHour = endHour;
        setHour(textHour);
    }

    private int getDay() {
        switch (textDay.getText().toString().trim().toLowerCase()) {
            case "montag":
                return Calendar.MONDAY;
            case "dienstag":
                return Calendar.TUESDAY;
            case "mittwoch":
                return Calendar.WEDNESDAY;
            case "donnerstag":
                return Calendar.THURSDAY;
            case "freitag":
                return Calendar.FRIDAY;
            case "samstag":
                return Calendar.SATURDAY;
            default:
                return Calendar.SUNDAY;
        }
    }

    private void manageInputs() {
        //scrollView = (ScrollView) findViewById(R.id.scrollView);

        textTeacher = (AutoCompleteTextView) findViewById(R.id.input_teacher);
        textDay = (AutoCompleteTextView) findViewById(R.id.input_day);
        textHour = (AutoCompleteTextView) findViewById(R.id.input_hour);
        textGrade = (AutoCompleteTextView) findViewById(R.id.input_grade);
        textSubject = (AutoCompleteTextView) findViewById(R.id.input_subject);
        textRoom = (AutoCompleteTextView) findViewById(R.id.input_room);
        textRepeatType = (AutoCompleteTextView) findViewById(R.id.input_repeattype);

        textLayoutTeacher = (TextInputLayout) findViewById(R.id.input_layout_teacher);
        textLayoutDay = (TextInputLayout) findViewById(R.id.input_layout_day);
        textLayoutHour = (TextInputLayout) findViewById(R.id.input_layout_hour);
        textLayoutGrade = (TextInputLayout) findViewById(R.id.input_layout_grade);
        textLayoutSubject = (TextInputLayout) findViewById(R.id.input_layout_subject);
        textLayoutRoom = (TextInputLayout) findViewById(R.id.input_layout_room);
        textLayoutRepeatType = (TextInputLayout) findViewById(R.id.input_layout_repeattype);

        textDay.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, String.valueOf(keyCode));
                Log.d(TAG, String.valueOf(event.getCharacters()));
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT)) {
                    if (getDay() == Calendar.SUNDAY) {
                        textLayoutDay.setErrorEnabled(true);
                        textLayoutDay.setError("Bitte Tag eingeben.");
                    } else {
                        textLayoutDay.setErrorEnabled(false);
                    }
                    return true;
                }
                return false;
            }
        });

        textDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && getDay() == Calendar.SUNDAY) {
                    textLayoutDay.setErrorEnabled(true);
                    textLayoutDay.setError("Bitte gültigen Tag eingeben.");
                } else {
                    textLayoutDay.setErrorEnabled(false);
                }
            }
        });

        textTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textTeacher.getText().toString().trim().length() <= 0) {
                    textLayoutTeacher.setErrorEnabled(true);
                    textLayoutTeacher.setError("Bitte Lehrer eingeben.");
                } else {
                    textLayoutTeacher.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textDay.getText().toString().trim().length() <= 0) {
                    textLayoutDay.setErrorEnabled(true);
                    textLayoutDay.setError("Bitte Tag eingeben.");
                } else {
                    textLayoutDay.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textHour.getText().toString().trim().length() <= 0) {
                    textLayoutHour.setErrorEnabled(true);
                    textLayoutHour.setError("Bitte Stunde eingeben.");
                } else {
                    textLayoutHour.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textGrade.getText().toString().trim().length() <= 0) {
                    textLayoutGrade.setErrorEnabled(true);
                    textLayoutGrade.setError("Bitte Klasse eingeben.");
                } else {
                    textLayoutGrade.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textSubject.getText().toString().trim().length() <= 0) {
                    textLayoutSubject.setErrorEnabled(true);
                    textLayoutSubject.setError("Bitte Fach eingeben.");
                } else {
                    textLayoutSubject.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textRoom.getText().toString().trim().length() <= 0) {
                    textLayoutRoom.setErrorEnabled(true);
                    textLayoutRoom.setError("Bitte Raum eingeben.");
                } else {
                    textLayoutRoom.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textRepeatType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textRepeatType.getText().toString().trim().length() <= 0) {
                    textLayoutRepeatType.setErrorEnabled(true);
                    textLayoutRepeatType.setError("Bitte Wiederholunstyp eingeben.");
                } else {
                    textLayoutRepeatType.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        textGrade.setText(Preferences.readStringFromPreferences(this, getString(R.string.SELECTED_GRADE), ""));
        if (lesson != null) {
            textTeacher.setText(lesson.getTeacher());
            textDay.setText(TimeTableHelper.getDayName(lesson.getDay(), this));
            startHour = lesson.getHours()[0];
            endHour = lesson.getHours()[lesson.getHours().length - 1];
            setHour(textHour);
            textGrade.setText(lesson.getGrade().getGradeName());
            textSubject.setText(lesson.getSubject());
            textRoom.setText(lesson.getRoom());
            textRepeatType.setText(lesson.getRepeatType());
        } else if (day != 0) {
            textDay.setText("Montag");
        }
    }
}
