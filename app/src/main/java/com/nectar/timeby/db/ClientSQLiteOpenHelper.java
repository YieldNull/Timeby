package com.nectar.timeby.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wsw on 2015/7/20.
 */
public class ClientSQLiteOpenHelper extends SQLiteOpenHelper {

    public ClientSQLiteOpenHelper(Context context) {
        super(context, "client.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS User(phoneNumber VARCHAR(20) PRIMARY KEY," +
                "userName VARCHAR(20),nickName VARCHAR(20),sex VARCHAR(3) DEFAULT '男'," +
                "headImage VARCHAR(40),password VARCHAR(20),age INTEGER DEFAULT 0," +
                "numberOfShell INTEGER,numberOfHammer INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Friendship(phoneNumberB VARCHAR(20),phoneNumberA VARCHAR(20),remark VARCHAR(20)," +
                "PRIMARY KEY(phoneNumberB,phoneNumberA),FOREIGN KEY (phoneNumberB) REFERENCES User(phoneNumber),FOREIGN KEY (phoneNumberA) REFERENCES User(phoneNumber))");
        db.execSQL("CREATE TABLE IF NOT EXISTS Task(startTime INTEGER,endTime INTEGER,phoneNumberA VARCHAR(20)," +
                "taskContent VARCHAR(20),foucusDegree INTEGER,Effiency INTEGER,SuccessOrNot INTEGER,PRIMARY KEY(phoneNumberA,startTime))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
