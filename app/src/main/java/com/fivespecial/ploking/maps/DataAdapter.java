package com.fivespecial.ploking.maps;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fivespecial.ploking.maps.BinLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    protected static final String TABLE_NAME = "garbage_location";

    private final Context mContext;
    private SQLiteDatabase mDB;
    private DBHelper mDBHelper;

    public DataAdapter(Context context){
        this.mContext = context;
        mDBHelper = new DBHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException{
        try{
            mDBHelper.createDataBase();
        }
        catch (IOException mIOException){
            Log.e(TAG, mIOException.toString() + " UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException{
        try{
            mDBHelper.openDataBase();
            mDBHelper.close();
            mDB = mDBHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException){
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close(){
        mDBHelper.close();
    }

    public List getTableData(){
        try{

            String sql = "SELECT * FROM " + TABLE_NAME;

            List binList = new ArrayList();

            //모델 선언, null is redundant
            BinLocation binLocation;

            Cursor mCur = mDB.rawQuery(sql, null);
            if(mCur!=null){

                //칼럼의 마지막까지
                while(mCur.moveToNext()){
                    binLocation = new BinLocation();

                    binLocation.setId(mCur.getString(0));
                    binLocation.setLatitude(mCur.getDouble(1));
                    binLocation.setLongitude(mCur.getDouble(2));
                    binLocation.setDescription(mCur.getString(3));

                    binList.add(binLocation);
                }
            }
            return binList;
        }

        catch (SQLException mSQLException){
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
}
