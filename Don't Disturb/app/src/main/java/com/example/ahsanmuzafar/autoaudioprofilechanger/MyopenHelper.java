package com.example.ahsanmuzafar.autoaudioprofilechanger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ahsan.muzafar on 6/14/2015.
 */
    public class MyopenHelper extends SQLiteOpenHelper {

    Context con;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dbapp.db";
    public MyopenHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLContract.commands.CreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

