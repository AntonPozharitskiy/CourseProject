package com.example.asus.videocontent.Base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Querrys {

    private static SQLiteDatabase db;
    private static ArrayList<com.example.asus.videocontent.Base.VideoModel> data;

    public Querrys(SQLiteDatabase db){
        this.db = db;
    }

    private com.example.asus.videocontent.Base.VideoModel readCursor(Cursor cursor) {
        String path = cursor.getString(0);
        float size = cursor.getFloat(1);
        String name = cursor.getString(2);

        return new com.example.asus.videocontent.Base.VideoModel(path, size, name);
    }

    public ArrayList<com.example.asus.videocontent.Base.VideoModel> getVideoName(){
        data = new ArrayList<>();
        String query =
                "SELECT Path, Size, Name from Media";

        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            data.add(readCursor(cursor));
            while (cursor.moveToNext()) {
                data.add(readCursor(cursor));
            }
        }
        return data;
    }

    public void DeleteVideo(String pathId) {
        String string = String.valueOf(pathId);
        db.execSQL("DELETE FROM Media WHERE Path = '" + string + "'");
    }

}
