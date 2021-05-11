package com.example.pedometer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private  static final int DATABASE_VERSION=1;
    private  static final String DATABASE_NAME= "details";
    private  static final String TABLE_NAME= "details";
    private  static final String  COLUMN_ID="id";
    private  static final String  COLUMN_NAME="gender";
   private static DatabaseHandler instance;

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static synchronized DatabaseHandler getInstance(final Context c) {
        if (instance == null) {
            instance = new DatabaseHandler(c.getApplicationContext());
        }
        return instance;
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Category table create query
        String CREATE_ITEM_TABLE = "CREATE TABLE    " + TABLE_NAME + "( "
                + COLUMN_ID + "   INTEGER PRIMARY KEY, " + COLUMN_NAME + "   TEXT)";
        db.execSQL(CREATE_ITEM_TABLE);
    }
//upgrading database
    @Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(db);

}
public void insertLabel(String label){
        SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values=new ContentValues();
    values.put(COLUMN_NAME,label);
    //Inserting Row
    db.insert(TABLE_NAME,null,values);
    db.close();
}
public String serachifnotnull(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query ="SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        String a,d;
        d="null";
        if(cursor.moveToFirst()){
            do{
                a=cursor.getString(0);
                if (a!=null){
                    d="not null";
                    break;
                }
            }
            while (cursor.moveToNext());
        }
        return d;



}
public List<String> getAllLables(){
        List<String> list = new ArrayList<>();
        String selectQuery="SELECT * FROM " + TABLE_NAME;
    SQLiteDatabase db=this.getReadableDatabase();
    Cursor cursor = db.rawQuery(selectQuery,null);
    if(cursor.moveToNext()){
        do {
            list.add(cursor.getString(1));

        }
        while (cursor.moveToNext());
    }
      cursor.close();
    db.close();
    return list;

}

}
