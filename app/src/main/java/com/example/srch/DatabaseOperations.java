package com.example.srch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.srch.TableData.TableInfo.DBname;

public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int Databaseversion = 1;
    public String CREATE_QUERY = "CREATE TABLE "+ TableData.TableInfo.TableName +"("+ TableData.TableInfo.Image + " TEXT," + TableData.TableInfo.Text + " TEXT);";
    public DatabaseOperations(Context context) {
        super(context, DBname, null, Databaseversion);
        Log.d("DATABASE OPERATION","DATABASE CREATED");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        Log.d("DATABASE OPERATION","TABLE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void  putInformation(DatabaseOperations dop,String name,String pass){
        SQLiteDatabase sq  = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.Image,name);
        cv.put(TableData.TableInfo.Text,pass);
        long k = sq.insert(TableData.TableInfo.TableName,null,cv);
        Log.d("DATABASE OPERATION","one raw inserted");
    }

    public Cursor getInformation(DatabaseOperations dop){
        SQLiteDatabase sq = dop.getReadableDatabase();
        String[] coloumns = {TableData.TableInfo.Image, TableData.TableInfo.Text};
        Cursor cr = sq.query(TableData.TableInfo.TableName,coloumns,null,null,null,null,null);
        return cr;
    }


}
