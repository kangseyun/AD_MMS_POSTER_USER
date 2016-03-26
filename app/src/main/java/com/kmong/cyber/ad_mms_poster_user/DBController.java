package com.kmong.cyber.ad_mms_poster_user;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Created by seyun on 15. 5. 17..
 */
public class DBController extends SQLiteOpenHelper {
    private final Context myContext;
    public Cursor cursor;


    public DBController(Context context) {
        super(context, "MYINFO", null, 1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE content( num INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT);");
        db.execSQL("CREATE TABLE img( num INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public int selectProfile()
    {
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("select count(*) from content",null);
        if(cursor.moveToFirst())
        {
            if(cursor.getInt(0) == 0)
            {
                Log.i("mytable", "s");
                return 1;
            }
        }
        return 0;

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

    public void insertContent(String content){
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("select content from content", null);
    }


    public String printName()
    {
        String name;
        SQLiteDatabase db = getReadableDatabase();
        cursor = db.rawQuery("select name from my;",null);
        cursor.moveToFirst();
        name = cursor.getString(0);
        return name;
    }

}
