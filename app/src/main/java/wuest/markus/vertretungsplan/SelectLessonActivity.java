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
                Integer[] lessonIds = timeTableFragment.getSelectedLesson();
                if (lessonIds.length != 0) {
                    Bundle b = new Bundle();
                    Arrays.sort(lessonIds);
                    ArrayList<Integer> hours = new ArrayList<>(Arrays.asList(lessonIds));
                    Collections.sort(hours);
                    b.putIntegerArrayList(TableEditor.IDS, hours);
                    intent.putExtras(b);
                }
                startActivity(intent);
            }
        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        timeTableFragment = TimeTableFragment.newEditInstance(grade, day);
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
            new DBHandler(this, null, null, 0).removeLessons(timeTableFragment.getSelectedLesson());
            finish();
            return true;
        }
        return false;
    }
}
