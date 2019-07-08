package com.example.srch;

import android.provider.BaseColumns;

public class TableData {
    public TableData(){

    }
    public static abstract class TableInfo implements BaseColumns
    {

        public static final String Image = "Image";
        public static final String Text = "Text";
        public static final String DBname = "DBname";
        public static final String TableName = "TableName";
    }

}
