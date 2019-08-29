package com.fivespecial.ploking.AdapterEtc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fivespecial.ploking.MemberClass.Album;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    public static DbHelper mInstance = null;
    public static String DATABASE= "photo.db";
    public static String PHOTO = "Photo";
    private static final int DATABASE_VERSION = 2;
    String sql;

    public static DbHelper getInstance(Context ctx){
        if(mInstance == null){
            mInstance = new DbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    public DbHelper(Context ctx ) {
        super(ctx, DATABASE, null,DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sql = "CREATE TABLE IF NOT EXISTS Photo ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "path TEXT, file_name TEXT, longitude REAL, latitude REAL);";
        db.execSQL(sql);
    }



    public void insertData(String path, String file_name, Double longitude, Double latitude){
        SQLiteDatabase db= getWritableDatabase();
        db.execSQL("INSERT INTO Photo VALUES(null,'"+path+"','"+file_name+"',"+longitude+","+latitude+")");
        db.close();
    }


    public void deleteData(String file){
        SQLiteDatabase db= getWritableDatabase();
        db.execSQL("DELETE FROM Photo WHERE file_name = '" + file + "'");
        db.close();
    }

/*    public Cursor getData(String sql){
        SQLiteDatabase database= getReadableDatabase();
        return database.rawQuery(sql,null);
    }*/

    public List<Album> getdata(){
        List<Album> data = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+PHOTO+" ; ",null);
        StringBuffer stringBuffer= new StringBuffer();
        Album album = null;
        while(cursor.moveToNext()){
            album= new Album();
            String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
            String file_name= cursor.getString(cursor.getColumnIndexOrThrow("file_name"));
            album.setFile_name(file_name);
            album.setPath(path);
            stringBuffer.append(album);
            album.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
            album.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
            data.add(album);
        }
        return data;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
