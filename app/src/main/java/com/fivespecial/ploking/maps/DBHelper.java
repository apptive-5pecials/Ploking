package com.fivespecial.ploking.maps;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static String DB_PATH = ""; // assets 폴더에 있는 경우 "", 그 외 경로 기입
    private static String DB_NAME = "garbage_bin.db"; // assets 폴더에 있는 DB명 또는 별도의 데이터베이스 파일이름

    private SQLiteDatabase mDatabase;
    private Context mContext;

    DBHelper(Context context) {
        super(context, DB_NAME, null, 1);// 1은 데이터베이스 버젼

        this.mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
    }

    void createDataBase() {
        //데이터베이스가 없으면 asset폴더에서 복사해온다.
        boolean mDataBaseExist = checkDataBase();

        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();

            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");

            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    ///data/data/your package/databases/Da Name <-이 경로에서 데이터베이스가 존재하는지 확인한다
    private boolean checkDataBase() {

        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //assets폴더에서 데이터베이스를 복사한다.
    private void copyDataBase() throws IOException {

        InputStream mInput = mContext.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);

        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0) {

            mOutput.write(mBuffer, 0, mLength);
        }

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //데이터베이스를 열어서 쿼리를 쓸수있게만든다.
    void openDataBase() throws SQLException {

        String mPath = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        //Log.v("mPath", mPath);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    @Override
    public synchronized void close() {

        if(mDatabase != null){
            mDatabase.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
