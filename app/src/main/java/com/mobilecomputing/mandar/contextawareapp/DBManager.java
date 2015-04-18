package com.mobilecomputing.mandar.contextawareapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mandar on 4/14/2015.
 */
public class DBManager extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ContextDB";

    private static final String TABLE_USERINFO = "userInfo";

    //Constants to access database columns
    private static final String KEY_RECORDID =  "recordID";
    private static final  String KEY_FROMTIMESTAMP = "fromTimeStamp";
    private static final String KEY_TOTIMESTAMP = "toTimeStamp";
    private static final String KEY_DAY = "day";
    private static final String KEY_WORKLOCATIONLAT = "workLocationLat";
    private static final String KEY_WORKLOCATIONLONG = "workLocationLong";
    private static final String KEY_HOMELOCATIONLAT = "homeLocationLat";
    private static final String KEY_HOMELOCATIONLONG = "homeLocationLong";

    private static final String[] COLUMNS = {KEY_RECORDID,KEY_FROMTIMESTAMP,KEY_TOTIMESTAMP,KEY_WORKLOCATIONLAT,KEY_WORKLOCATIONLONG,KEY_HOMELOCATIONLAT,KEY_HOMELOCATIONLONG};


    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_USERINFO_TABLE = "CREATE TABLE userInfo ( " +
                "recordID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fromTimeStamp TEXT, "+"toTimeStamp TEXT, "+"day TEXT, "+"workLocationLat REAL, "+"workLocationLong REAL, "+"homeLocationLat REAL, "+"homeLocationLong REAL )";

        // create books table
        db.execSQL(CREATE_USERINFO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS userInfo");

        // create fresh books table
        this.onCreate(db);
    }

    /**
     * Inserts userInfo record in database and returns Id of inserted item
     * @param userInfo
     * @return
     */
    public int addUserInfo(UserInfo userInfo){

        //for logging
        Log.d("userInfo", userInfo.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(KEY_FROMTIMESTAMP, userInfo.getFromTimeStamp()); // get FromTimeStamp
        values.put(KEY_TOTIMESTAMP, userInfo.getToTimeStamp()); // get author
        values.put(KEY_DAY,userInfo.getDay());
        values.put(KEY_WORKLOCATIONLAT, userInfo.getWorkLocationLat());
        values.put(KEY_WORKLOCATIONLONG, userInfo.getWorkLocationLon());
        values.put(KEY_HOMELOCATIONLAT, userInfo.getHomeLocationLat());
        values.put(KEY_HOMELOCATIONLONG, userInfo.getGetHomeLocationLong());

        // 3. insert
        long recordID = db.insert(TABLE_USERINFO, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        return (int) recordID;
    }

    public UserInfo getUserInfo(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_USERINFO, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build userInfo object
        UserInfo userInfo = new UserInfo();
        userInfo.setRecordID(Integer.parseInt(cursor.getString(0)));
        userInfo.setFromTimeStamp(cursor.getString(1));
        userInfo.setToTimeStamp(cursor.getString(2));
        userInfo.setDay(cursor.getString(3));
        userInfo.setWorkLocationLat(Double.parseDouble(cursor.getString(4)));
        userInfo.setWorkLocationLon(Double.parseDouble(cursor.getString(5)));
        userInfo.setHomeLocationLat(Double.parseDouble(cursor.getString(6)));
        userInfo.setGetHomeLocationLong(Double.parseDouble(cursor.getString(7)));

        Log.d("getBook("+id+")", userInfo.toString());

        // 5. return userInfo
        return userInfo;
    }

    // Get All userInfo
    public List<UserInfo> getAllUserInfo() {
        List<UserInfo> userInfoList = new LinkedList<UserInfo>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_USERINFO;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        UserInfo userInfo = null;
        if (cursor.moveToFirst()) {
            do {
                userInfo = new UserInfo();
                userInfo.setRecordID(Integer.parseInt(cursor.getString(0)));
                userInfo.setFromTimeStamp(cursor.getString(1));
                userInfo.setToTimeStamp(cursor.getString(2));
                userInfo.setDay(cursor.getString(3));
                userInfo.setWorkLocationLat(Double.parseDouble(cursor.getString(4)));
                userInfo.setWorkLocationLon(Double.parseDouble(cursor.getString(5)));
                userInfo.setHomeLocationLat(Double.parseDouble(cursor.getString(6)));
                userInfo.setGetHomeLocationLong(Double.parseDouble(cursor.getString(7)));


                // Add book to books
                userInfoList.add(userInfo);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", userInfoList.toString());

        // return books
        return userInfoList;
    }

    // Updating single userInfo
    public int updateBook(UserInfo userInfo) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_FROMTIMESTAMP, userInfo.getFromTimeStamp()); // get FromTimeStamp
        values.put(KEY_TOTIMESTAMP, userInfo.getToTimeStamp()); // get author
        values.put(KEY_WORKLOCATIONLAT, userInfo.getWorkLocationLat());
        values.put(KEY_WORKLOCATIONLONG, userInfo.getWorkLocationLon());
        values.put(KEY_HOMELOCATIONLAT, userInfo.getHomeLocationLat());
        values.put(KEY_HOMELOCATIONLONG, userInfo.getGetHomeLocationLong());
        // 3. updating row
        int i = db.update(TABLE_USERINFO, //table
                values, // column/value
                KEY_RECORDID+" = ?", // selections
                new String[] { String.valueOf(userInfo.getRecordID()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single userInfo record
    public void deleteRecord(UserInfo userInfo) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();



        // 2. delete
        db.delete(TABLE_USERINFO,
                KEY_RECORDID+" = ?",
                new String[] { String.valueOf(userInfo.getRecordID()) });

        // 3. close
        db.close();

        Log.d("deleted user info", userInfo.toString());

    }
    public void deleteAllUserInfo() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.execSQL("delete from "+ TABLE_USERINFO);

        // 3. close
        db.close();

        Log.d("delete user info","Deleted all records");

    }
}
