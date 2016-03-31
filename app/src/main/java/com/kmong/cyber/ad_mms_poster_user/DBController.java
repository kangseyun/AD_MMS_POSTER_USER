package com.kmong.cyber.ad_mms_poster_user;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by seyun on 15. 5. 17..
 */
public class DBController extends SQLiteOpenHelper {
    private final Context myContext;
    public Cursor cursor;
    public ArrayList<String> result = new ArrayList<String>();
    public ArrayList<String> no = new ArrayList<String>();

    public DBController(Context context) {
        super(context, "MYINFO", null, 1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE content( num INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT);");
        db.execSQL("CREATE TABLE img( num INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");
        db.execSQL("CREATE TABLE block( num INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String num) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("delete from img where num = %s", num);
        db.execSQL(query);
        db.close();
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String text= null;
        cursor = db.rawQuery("select content from content", null);
        if(cursor.moveToFirst())
        {

            do {
                text = cursor.getString(0);
                Log.i("DB",text);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return text;
    }
    public ArrayList<String> PrintData2() {
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("select url from img", null);
        if(cursor.moveToFirst())
        {
            do {
                result.add(cursor.getString(0));
                Log.i("DB", cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return result;
    }

    public int CheckBlock(String number) {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        cursor = db.rawQuery("select count(*) from block where number = '" + number + "'", null);
        cursor.moveToFirst();
        result = cursor.getInt(0);
        cursor.close();

        return result;
    }

    public ArrayList<String> PrintData3() {
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("select num from img", null);
        if(cursor.moveToFirst())
        {
            do {
                no.add(cursor.getString(0));
                Log.i("DB", cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return no;
    }

}
