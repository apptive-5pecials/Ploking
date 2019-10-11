package com.fivespecial.ploking.maps;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {

    private static final String TAG = "DataAdapter";
    private static final String TABLE_NAME = "garbage_location";

    private SQLiteDatabase mDB;
    private DBHelper mDBHelper;

    public DataAdapter(Context context){
        mDBHelper = new DBHelper(context);
    }

    public void createDatabase() throws SQLException{
        mDBHelper.createDataBase();
    }

    public void open() throws SQLException{

        try{
            mDBHelper.openDataBase();
            mDBHelper.close();
            mDB = mDBHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void close(){
        mDBHelper.close();
    }

    public ArrayList<BinLocation> getTableData(){
        try{

            String sql = "SELECT * FROM " + TABLE_NAME;

            ArrayList<BinLocation> binList = new ArrayList<>();

            //모델 선언, null is redundant
            BinLocation binLocation;

            Cursor mCur = mDB.rawQuery(sql, null);
            if(mCur != null){

                //칼럼의 마지막까지
                while(mCur.moveToNext()){
                    binLocation = new BinLocation();

                    binLocation.setId(mCur.getString(0));
                    binLocation.setLatitude(mCur.getDouble(1));
                    binLocation.setLongitude(mCur.getDouble(2));
                    binLocation.setDescription(mCur.getString(3));

                    binList.add(binLocation);
                }

                mCur.close();
            }

            return binList;

        } catch (SQLException mSQLException){
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
}
