package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.Inflater;

public class SelectLessonActivity extends AppCompatActivity {

    private HWGrade grade;
    private int day;

    private TimeTableFragment timeTableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            grade = new HWGrade(bundle.getString("GRADE"));
            day = bundle.getInt("DAY");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectLessonActivity.this, TableEditor.class);
                HWLesson lesson = timeTableFragment.getSelectedLesson();
                if (lesson != null) {
                    Bundle b = new Bundle();
                    ArrayList<Integer> hours = new ArrayList<>(Arrays.asList(lesson.getHours()));
                    Collections.sort(hours);
                    b.putString(TableEditor.GRADE, lesson.getGrade().getGradeName());
                    b.putInt(TableEditor.DAY, lesson.getDay());
                    b.putIntegerArrayList(TableEditor.HOURS, hours);
                    b.putString(TableEditor.TEACHER, lesson.getTeacher());
                    b.putString(TableEditor.SUBJECT, lesson.getSubject());
                    b.putString(TableEditor.ROOM, lesson.getRoom());
                    b.putString(TableEditor.REPEATTYPE, lesson.getRepeatType());
                    intent.putExtras(b);
                }
                startActivity(intent);
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        timeTableFragment = TimeTableFragment.newInstance(grade, day, true, true);
        //timeTableFragment.setRefreshListener(this);
        //timeTableFragment.setEditInterface(this);
        fragmentManager.beginTransaction()
                .replace(R.id.container, timeTableFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_remove) {
            new DBHandler(this, null, null, 0).removeLesson(timeTableFragment.getSelectedLesson());
            finish();
            return true;
        }
        return false;
    }
}
