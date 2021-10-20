package it.bonny.app.wiseframe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import it.bonny.app.wiseframe.bean.ImageBean;

public class ManagerDB {
    public static final String KEY_ID = "id";
    public static final String KEY_PATH = "image_path";
    public static final String KEY_NAME = "image_name";
    private static final String DATABASE_NAME = "WiseFrameDB";
    private static final String DATABASE_TABLE = "Image";
    private static final int DATABASE_VERSION = 8;

    private static final String DATABASE_CREATION = "CREATE TABLE " + DATABASE_TABLE +
            " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_PATH + " " +
            "TEXT, " + KEY_NAME + " TEXT )";
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ManagerDB(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATION);
            }catch (SQLException e) {
                FirebaseCrashlytics.getInstance().log("ManagerDB 'onCreate'");
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public void openWriteDB() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }
    public void openReadDB() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public long insert(ImageBean imageBean) {
        long _id;
        try {
            ContentValues iniContentValues = new ContentValues();
            iniContentValues.put(KEY_PATH, imageBean.getImagePath());
            iniContentValues.put(KEY_NAME, imageBean.getName());
            _id = db.insert(DATABASE_TABLE, null, iniContentValues);
        }catch (Exception e ) {
            _id = -1;
            FirebaseCrashlytics.getInstance().log("ManagerDB 'insert'");
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return _id;
    }

    public boolean deleteById(long _id) {
        boolean result;
        try {
            result = db.delete(DATABASE_TABLE, KEY_ID + "=" + _id, null) > 0;
        }catch (Exception e) {
            result = false;
            FirebaseCrashlytics.getInstance().log("ManagerDB 'deleteById'");
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return result;
    }

    public Cursor getAllImages() {
        return getAllImages(0);
    }

    public Cursor getAllImages(int orderType) {
        Cursor cursor;
        String orderBy;
        try {
            if(orderType == 2 || orderType == 3)
                orderBy = KEY_ID + " ASC";
            else
                orderBy = KEY_ID + " DESC";
            cursor = db.query(DATABASE_TABLE,
                    new String[] {KEY_ID, KEY_PATH, KEY_NAME},
                    null, null, null, null,
                    orderBy);
        }catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("ManagerDB 'getAllImages'");
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return cursor;
    }

}
