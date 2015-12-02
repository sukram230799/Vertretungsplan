package wuest.markus.vertretungsplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Vertretungsplan.db";

    public static final String TABLE_GRADES = "grades";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GRADE = "grade";
    public static final String TABLE_VP = "vp";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_INFO1 = "infoone";
    public static final String COLUMN_INFO2 = "infotwo";
    public static final String COLUMN_DATE = "date";

    public static final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static final String TAG = "SQLiteOpenHelper";
    public static final String TAGQUERY = "SQLQuery";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Log.d(TAG, "@DBHandler()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "@onCreate()");
        //Grade Table
        String query = "CREATE TABLE " + TABLE_GRADES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GRADE + " TEXT " +
                ");";
        db.execSQL(query);
        //Data Table
        query = "CREATE TABLE " + TABLE_VP + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GRADE + " TEXT, " +
                COLUMN_HOUR + " INTEGER, " +
                COLUMN_SUBJECT + " TEXT, " +
                COLUMN_ROOM + " TEXT, " +
                COLUMN_INFO1 + " TEXT, " +
                COLUMN_INFO2 + " TEXT, " +
                COLUMN_DATE + " DATE " +
                ");";
        db.execSQL(query);

        Log.d(TAG, "-onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "@onUpgrade()");
        //No Migration!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VP);
        onCreate(db);
        Log.d(TAG, "-onUpgrade()");
    }

    //Add ROW
    public void addGrade(HWGrade hwGrade) {
        Log.d(TAG, "@addGrade()");
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE, hwGrade.get_GradeName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GRADES, null, values);
        //db.close(); //Never CLOSE DB!
        Log.d(TAG, "-addGrade()");
    }

    public void saveAddGrades(HWGrade[] newGrades) {
        ArrayList<HWGrade> hwGradeArrayList = new ArrayList<>(Arrays.asList(newGrades));
        HWGrade[] oldGrades;
        try {
            oldGrades = getGrades();
            int outer = 0;
            int inner = 0;
            int innerif = 0;
            for (HWGrade oldGrade : oldGrades) {
                Log.d("saveAddGrade", "outer: " + outer++);
                for (HWGrade newGrade : newGrades) {
                    Log.d("saveAddGrade", "inner: " + inner++);
                    if (oldGrade.get_GradeName().equals(newGrade.get_GradeName())) {
                        hwGradeArrayList.remove(newGrade);
                        Log.d("saveAddGrade", "if: " + innerif++);
                    }
                }
            }
            for (HWGrade grade : hwGradeArrayList) {
                addGrade(grade);
            }
        } catch (DBError dbError) {
            dbError.printStackTrace();
            for (HWGrade hwGrade : newGrades) {
                addGrade(hwGrade);
            }
        }
    }

    public void delGrade(String Grade) {
        Log.d(TAG, "@delGrade()");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GRADES + " WHERE " + COLUMN_GRADE + "=\"" + Grade + "\";");
        //db.close(); //Never CLOSE DB!
        Log.d(TAG, "-delGrade()");
    }

    public HWGrade getGrade(int position) throws DBError {
        Log.d(TAG, "@getGrade:" + position);
        HWGrade hwGrade;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GRADES + " WHERE " + COLUMN_ID + "=\"" + (position + 1) + "\";";
        Log.d(TAGQUERY, query);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            hwGrade = new HWGrade(c.getString(c.getColumnIndex("grade")));
        } else {
            throw new DBError(DBError.NOTFOUND);
        }
        c.close();
        return hwGrade;
    }

    public HWGrade[] getGrades() throws DBError {
        Log.d(TAG, "@getGrades()");
        ArrayList<HWGrade> GradeList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GRADES + " WHERE 1"/* ORDER BY " + COLUMN_GRADE*/;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d(TAG, "$getGrades:Before while");
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_GRADE)) != null) {
                Log.d(TAG, "$getGrades:WHILE");
                GradeList.add(new HWGrade(c.getString(c.getColumnIndex(COLUMN_GRADE))));
            }
            c.moveToNext();
        }
        if (GradeList.isEmpty()) {
            throw new DBError(DBError.TABLEEMPTY);
        }
        db.close();
        Log.d(TAG, "-getGrades():db.close();");
        c.close();
        return GradeList.toArray(new HWGrade[GradeList.size()]);
    }

    public int getGradePosition(HWGrade grade) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GRADES + " WHERE " + COLUMN_GRADE + "=\"" + grade.get_GradeName() + "\";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            return c.getInt(c.getColumnIndex(COLUMN_ID)) - 1;
        }
        c.close();
        return -1;
    }

    public void sortGrades() {
        ArrayList<HWGrade> gradeList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GRADES + " WHERE 1 ORDER BY " + COLUMN_GRADE;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d(TAG, "$getGrades:Before while");
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_GRADE)) != null) {
                Log.d(TAG, "$getGrades:WHILE");
                gradeList.add(new HWGrade(c.getString(c.getColumnIndex(COLUMN_GRADE))));
            }
            c.moveToNext();
        }
        if (gradeList.isEmpty()) {
            return;
        }
        c.close();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
        query = "CREATE TABLE " + TABLE_GRADES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GRADE + " TEXT " +
                ");";
        db.execSQL(query);
        for (HWGrade hwGrade: gradeList){
            addGrade(hwGrade);
        }
    }

    public VPData[] getVP(HWGrade grade) throws DBError {
        trimPlans();
        Log.d(TAG, "@getVP");
        ArrayList<VPData> vpDatas = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query;
        //String query = "SELECT * FROM " + TABLE_VP + " WHERE " + COLUMN_DATE + ">= date(" + sdf.format(new Date()) + ");"; //Not used because of sorting with deleting of old entrys!
        query = "SELECT * FROM " + TABLE_VP + " WHERE " + COLUMN_GRADE + "=\"" + grade.get_GradeName() + "\" ORDER BY " + COLUMN_DATE + ", " + COLUMN_HOUR + " ASC";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d(TAG, "$getVP@while");
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_GRADE)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_SUBJECT)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_ROOM)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_INFO1)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_INFO2)) != null &&
                    c.getString(c.getColumnIndex(COLUMN_DATE)) != null
                    ) {
                Log.d(TAG, "2");
                try {
                    Log.d(TAG, "3");
                    vpDatas.add(new VPData(grade,
                            new Integer[]{c.getInt(c.getColumnIndex(COLUMN_HOUR))},
                            c.getString(c.getColumnIndex(COLUMN_SUBJECT)),
                            c.getString(c.getColumnIndex(COLUMN_ROOM)),
                            c.getString(c.getColumnIndex(COLUMN_INFO1)),
                            c.getString(c.getColumnIndex(COLUMN_INFO2)),
                            dbDateFormat.parse(c.getString(c.getColumnIndex(COLUMN_DATE)))
                    ));
                } catch (ParseException e) {
                    e.printStackTrace();
                    onUpgrade(db, 0, DATABASE_VERSION);
                    throw new DBError(DBError.INVALIDDATEFORMAT);
                }
                Log.d(TAG, "4");
            }
            c.moveToNext();
        }
        c.close();
        if (vpDatas.isEmpty()) {
            throw new DBError(DBError.TABLEEMPTY);
        } else {
            return vpDatas.toArray(new VPData[vpDatas.size()]);
        }
    }

    public void addPlan(VPData[] vpDatas) {
        Log.d(TAG, "@addDayPlan");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d(TAG, "$addDayPlan:before for");
        for (VPData data : vpDatas) {
            for (Integer hour : data.get_hours()) {
                Log.d(TAG, "$addDayPlan:for");
                values.put(COLUMN_DATE, dbDateFormat.format(data.get_date()));
                values.put(COLUMN_GRADE, data.get_grade().get_GradeName());
                values.put(COLUMN_HOUR, hour);
                values.put(COLUMN_SUBJECT, data.get_subject());
                values.put(COLUMN_ROOM, data.get_room());
                values.put(COLUMN_INFO1, data.get_info1());
                values.put(COLUMN_INFO2, data.get_info2());
                db.insert(TABLE_VP, null, values);
                values = new ContentValues();
            }
        }
        Log.d(TAG, "-addDayPlan");
    }

    void removeVP(HWGrade grade) {
        String query = "DELETE FROM " + TABLE_VP + " WHERE " + COLUMN_GRADE + "=\"" + grade.get_GradeName() + "\";";
        getWritableDatabase().execSQL(query);
    }

    public boolean isVP(HWGrade grade) {
        String query = "SELECT 1 FROM " + TABLE_VP + " WHERE " + COLUMN_GRADE + "=\"" + grade.get_GradeName() + "\";";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            Log.d(TAG, "false");
            return false;
        }
        Log.d(TAG, "true");
        cursor.close();
        return true;
    }

    public void trimPlans() {
        SQLiteDatabase db = getWritableDatabase();
        String query;
        query = "DELETE FROM " + TABLE_VP + " WHERE " + COLUMN_DATE + " < '"/*date("*/ + dbDateFormat.format(new Date()) + /*")*/"';";
        Log.d(TAG, query);
        db.execSQL(query); //Delete old VP
        /*
        SQLiteDatabase db = getWritableDatabase();
        String query = "delete from dist where rowid not in (select max(rowid) from dist group by hash);";
        //query = "SELECT * FROM " + TABLE_VP + "WHERE 1;";
        //Cursor cursor = db.rawQuery(query, null);
        query = "DELETE FROM " + TABLE_VP + " WHERE " + COLUMN_ID + " NOT IN (SELECT min(" + COLUMN_ID + " FROM " + TABLE_VP + " GROUP BY " + COLUMN_HOUR + ");";
        db.execSQL(query);
        */
    }
}
