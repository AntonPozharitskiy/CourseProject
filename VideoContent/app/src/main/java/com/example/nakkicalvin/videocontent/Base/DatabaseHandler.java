package com.example.asus.videocontent.Base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context){
        super(context, "videos.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMediaTableQuery =
                "CREATE TABLE Media (Path TEXT, Size REAL, Name TEXT, PRIMARY KEY(Path) )";
        db.execSQL(createMediaTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
