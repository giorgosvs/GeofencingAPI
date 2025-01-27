package com.example.geofenceapi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.Nullable;

public class MapsProvider extends ContentProvider {

    SQLiteDatabase myDB;

    public MapsProvider() {
    }

    public static final String AUTHORITY = "com.example.geofenceapi";
    public static final Uri CONTENT_URI_1 = Uri.parse("content://" + AUTHORITY + "/points");
    public static final Uri CONTENT_URI_2 = Uri.parse("content://" + AUTHORITY + "/transitions");

    private static final String DB_NAME = "MapsDatabase";
    private static final String DB_TABLE = "points";
    private static final String DB_TABLE_2 = "transitions";
    private static final String FIELD_1 = "circleid";
    private static final String FIELD_2 = "lat";
    private static final String FIELD_3 = "lon";
    private static final String FIELD_4 = "sessionid";
    private static final String FIELD_5 = "circleid";
    private static final String FIELD_6 = "transition";
    private static final String FIELD_7 = "lat";
    private static final String FIELD_8 = "lon";

    private static final int DB_VER = 1;

    static int points = 1;
    static int points_id = 2;

    static UriMatcher myUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        myUri.addURI(AUTHORITY,"points", 1); // request code for the whole table
        myUri.addURI(AUTHORITY,"points/#",2); // request the id
        myUri.addURI(AUTHORITY,"transitions", 1); // request code for the whole table
        myUri.addURI(AUTHORITY,"transitions/#",2); // request the id
    }



    public class PointDatabase extends SQLiteOpenHelper { //class within class!


        //TODO check for constructor problems
        public PointDatabase(@Nullable Context context) {
            super(context, DB_NAME,null,DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String CREATE_QUERY_1 = "CREATE TABLE " + DB_TABLE + " ("
                    + "_id integer primary key autoincrement, "
                    + FIELD_1 + " TEXT,"
                    + FIELD_2  + " TEXT,"
                    + FIELD_3 + " TEXT,"
                    + FIELD_4 + " TEXT);";

            String CREATE_QUERY_2 = "CREATE TABLE " + DB_TABLE_2 + " ("
                    + "_id integer primary key autoincrement, "
                    + FIELD_4+ " TEXT,"
                    + FIELD_5  + " TEXT,"
                    + FIELD_6 + " TEXT,"
                    + FIELD_7 + " TEXT,"
                    + FIELD_8 + " TEXT);";

            sqLiteDatabase.execSQL(CREATE_QUERY_1);
            sqLiteDatabase.execSQL(CREATE_QUERY_2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table if exists " + DB_TABLE); //TODO check for problems here
            sqLiteDatabase.execSQL("drop table if exists " + DB_TABLE_2);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rows = 0;
        if(selection == null) selection = "1";

        Cursor c = myDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        System.out.println("STRING : " + c.toString() + "   " + c);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                if(c.getString(0).equals(DB_TABLE)){
                    switch (myUri.match(uri)) {
                        case 1 :
                            rows = myDB.delete(MapsProvider.DB_TABLE,selection,selectionArgs);
                            break;

                        default: throw new IllegalArgumentException("Unknown URI : " + uri);
                    }
                    if(rows != 0 ){
                        getContext().getContentResolver().notifyChange(uri,null);
                    }
                } else {
                    switch (myUri.match(uri)) {
                        case 1 :
                            rows = myDB.delete(MapsProvider.DB_TABLE_2,selection,selectionArgs);
                            break;

                        default: throw new IllegalArgumentException("Unknown URI : " + uri);
                    }
                    if(rows != 0 ){
                        getContext().getContentResolver().notifyChange(uri,null);
                    }
                }
                c.moveToNext();
            }
        }

        return rows;
    }



    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long row = myDB.insert(getUriTableName(uri),null,values);

        if(row > 0) { //Insertion succeeded
            if(uri.equals(CONTENT_URI_1)){
                uri = ContentUris.withAppendedId(CONTENT_URI_1, row);
            }else {
                uri = ContentUris.withAppendedId(CONTENT_URI_2, row);
            }

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    private String getUriTableName(Uri uri) {
        if(uri.equals(CONTENT_URI_1)){
            return DB_TABLE;
        } else {
            return DB_TABLE_2;
        }
    }

    @Override
    public boolean onCreate() {
        PointDatabase myHelper = new PointDatabase(getContext());
        myDB = myHelper.getWritableDatabase();

        if (myDB != null){
            return true;
        } else {
            return false;
        }

        /*AppDatabase myDB = Room.databaseBuilder(getContext(),AppDatabase.class,"points").build();
        pointDao = myDB.pointDao();
        return true;*/
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder myQuery = new SQLiteQueryBuilder();
        if(getUriTableName(uri).equals(DB_TABLE)){
            myQuery.setTables(DB_TABLE);
        }else {
            myQuery.setTables(DB_TABLE_2);
        }

        Cursor cr = myQuery.query(myDB,null,null,null,null,null,"_id");
        cr.setNotificationUri(getContext().getContentResolver(),uri);

        return cr;
       /* //MatrixCursor cursor = new MatrixCursor(new String[] {"_id","circleid","lat","lon"});
        Cursor cursor = null;
        switch (myUri.match(uri)) {

            case 1:
                cursor = pointDao.getAllCursorPoints();

                *//*List<Point> points = pointDao.getAllGeofencePoints();
                for(Point point : points){
                    cursor.newRow()
                            .add("_id",point.id)
                            .add("circleid",point.circleId)
                            .add("lat",point.latitude)
                            .add("lon",point.longitude);

                }*//*
                break;
            case 2:
                *//*int id = Integer.parseInt(uri.getLastPathSegment());
                Point point = pointDao.getAllGeofencePointsById(id);*//*
                break;
        }*/
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}