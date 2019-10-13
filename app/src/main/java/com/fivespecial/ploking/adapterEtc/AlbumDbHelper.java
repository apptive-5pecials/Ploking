package com.fivespecial.ploking.adapterEtc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fivespecial.ploking.model.Album;

import java.util.ArrayList;
import java.util.List;


public class AlbumDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE= "photo.db";
    private static final String PHOTO = "Photo";
    private static final int DATABASE_VERSION = 2;

    private String sql;

    public AlbumDbHelper(Context context ) {
        super(context, DATABASE, null, DATABASE_VERSION);
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

    public List<Album> getAlbumData(){
        List<Album> albumList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+PHOTO+" ; ",null);
        StringBuilder stringBuffer= new StringBuilder();

        while(cursor.moveToNext()){
            Album album = new Album();

            String file_path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
            String file_name= cursor.getString(cursor.getColumnIndexOrThrow("file_name"));

            album.setFile_name(file_name);
            album.setPath(file_path);

            stringBuffer.append(album);

            album.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
            album.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));

            albumList.add(album);
        }

        cursor.close();

        return albumList;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
