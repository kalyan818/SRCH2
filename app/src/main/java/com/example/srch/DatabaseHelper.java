package com.example.srch;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import org.w3c.dom.Text;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="Search.db";
    public static final String TABLE_NAME = "Txt";
    public static final String COL1 ="Images";
    public static final String COL2 ="Text";
    public DatabaseHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + "(" +COL1 + "TEXT" + COL2 + "TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    void DBSetter(DBSetter dbSetter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1 , dbSetter.getImages());
        contentValues.put(COL2 , dbSetter.getText());
        db.insert(TABLE_NAME,null,contentValues);
        db.close();
    }
}
