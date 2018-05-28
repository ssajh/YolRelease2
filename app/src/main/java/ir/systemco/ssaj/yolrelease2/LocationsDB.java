package ir.systemco.ssaj.yolrelease2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class LocationsDB extends SQLiteOpenHelper{

    public Cursor crsr;
    private String pathToSaveDBFile;
    private final static String TAG = "testDB";
    private final Context myContext;

    // public LocationInfo locationInfo= new LocationInfo();

    /** Database name */
    private static String DBNAME = "YOL.db";

    /** Version number of the database */
    private static int DB_VERSION = 1;

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE1 = "tblVahed";
    private static final String DATABASE_TABLE2 = "tblDBversion";
    private static final String DATABASE_TABLE3 = "tblActivity";
    private static final String DATABASE_TABLE4 = "tblVahedsActivities";

    /** DATABASE_TABLE1 Fields */
    public static final String FIELD_Id = "Id";
    public static final String FIELD_UnitName = "UnitName";
    public static final String FIELD_ManagerName = "ManagerName";
    public static final String FIELD_StartTime = "StartTime";
    public static final String FIELD_CloseTime = "CloseTime";
    public static final String FIELD_PauseTime = "PauseTime";
    public static final String FIELD_StartPauseTime = "StartPauseTime";
    public static final String FIELD_ClosePauseTime = "ClosePauseTime";
    public static final String FIELD_IsClosedOnHolidays = "IsClosedOnHolidays";
    public static final String FIELD_ParentActivityId = "ParentActivityId";
    public static final String FIELD_PhoneNumber = "PhoneNumber";
    public static final String FIELD_MobileNumber = "MobileNumber";
    public static final String FIELD_FaxNumber = "FaxNumber";
    public static final String FIELD_Address = "Address";
    public static final String FIELD_CreateTime = "CreateTime";
    public static final String FIELD_UserId = "UserId";
    public static final String FIELD_Latitude = "Latitude";
    public static final String FIELD_longitude = "longitude";
    public static final String FIELD_priority = "priority";
    /** DATABASE_TABLE1 Fields Finished */

    /** DATABASE_TABLE2 Fields */
    public static final String FIELD_version_id = "version_id";
    /** DATABASE_TABLE2 Fields Finished */

    /** DATABASE_TABLE3 Fields */
    public static final String tblActivity_FIELD1_Id = "Id";
    public static final String tblActivity_FIELD2_UnitName = "UnitName";
    public static final String tblActivity_FIELD3_IsMainActivity = "IsMainActivity";
    public static final String tblActivity_FIELD4_ParentId = "ParentId";
    /** DATABASE_TABLE3 Fields Finished */

    /** DATABASE_TABLE4 Fields */
    public static final String tbl4_FIELD1_VahedId = "VahedId";
    public static final String tbl4_FIELD2_ActivityId = "ActivityId";
    /** DATABASE_TABLE4 Fields Finished */

    private SQLiteDatabase mDB;

    /** Constructor */
    public LocationsDB(Context context,String filePath) {
        super(context, DBNAME, null, DB_VERSION);
        Log.i("test1","insert to LocationsDB(Context context)");
        this.mDB = getWritableDatabase();
        Log.i("test1","after getWritableDatabase()");
        this.myContext = context;
        pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DBNAME).toString();
    }

    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql1 = "CREATE TABLE " + DATABASE_TABLE1 + " (" +
                    FIELD_Id                 + " INTEGER         PRIMARY KEY AUTOINCREMENT" +
                                                      " NOT NULL," +
                    FIELD_UnitName           + " NVARCHAR (2000) DEFAULT NULL," +
                    FIELD_ManagerName        + " NVARCHAR (200)  DEFAULT NULL," +
                    FIELD_StartTime          + " TIME            DEFAULT NULL," +
                    FIELD_CloseTime          + " TIME            DEFAULT NULL," +
                    FIELD_PauseTime          + " BOOLEAN         NOT NULL DEFAULT 0," +
                    FIELD_StartPauseTime     + " TIME            DEFAULT NULL," +
                    FIELD_ClosePauseTime     + " TIME            DEFAULT NULL," +
                    FIELD_IsClosedOnHolidays + " BOOLEAN         NOT NULL," +
                    FIELD_ParentActivityId   + " INT             DEFAULT NULL," +
                    FIELD_PhoneNumber        + " NVARCHAR (50)   DEFAULT NULL," +
                    FIELD_MobileNumber       + " NVARCHAR (50)   DEFAULT NULL," +
                    FIELD_FaxNumber          + " NVARCHAR (50)   DEFAULT NULL," +
                    FIELD_Address            + " NVARCHAR (2000) DEFAULT NULL," +
                    FIELD_CreateTime         + " DATETIME        DEFAULT NULL," +
                    FIELD_UserId             + " NVARCHAR (128)  NOT NULL," +
                    FIELD_Latitude           + " NVARCHAR (100)  DEFAULT NULL," +
                    FIELD_longitude          + " NVARCHAR (100)  DEFAULT NULL," +
                    FIELD_priority           + " REAL            DEFAULT (20));";

        Log.i("testDB","first tbl created");

        String sql2 = "CREATE TABLE " + DATABASE_TABLE2 + " (" +
                    FIELD_version_id         + " INTEGER PRIMARY KEY NOT NULL DEFAULT (0));";

        String sql3 = "CREATE TABLE " + DATABASE_TABLE3 + " (" +
                tblActivity_FIELD1_Id               + " INTEGER PRIMARY KEY NOT NULL DEFAULT (0), " +
                tblActivity_FIELD2_UnitName         + " nvarchar(100)                DEFAULT NULL, " +
                tblActivity_FIELD3_IsMainActivity   + " BOOLEAN             NOT NULL DEFAULT NULL, " +
                tblActivity_FIELD4_ParentId         + " INTEGER                      DEFAULT NULL );";

        String sql4 = "CREATE TABLE " + DATABASE_TABLE4 + " (" +
                tbl4_FIELD1_VahedId               + " INTEGER REFERENCES tblVahed (Id), " +
                tbl4_FIELD2_ActivityId            + " ActivityId INTEGER REFERENCES tblActivity (Id)); ";
;
        try
        {
            db.execSQL(sql1);
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.execSQL(sql4);
            Log.i("testDB5","4th tbl created");
        }catch (Exception e){
            Log.i("testDB5",e.toString());
        }

    }

    /** Inserts a new location to the table locations */
    public long insert(ContentValues contentValues){
        long rowID = mDB.insert(DATABASE_TABLE1, null, contentValues);
        return rowID;
    }

    /** Deletes all locations from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE1, null , null);
        return cnt;
    }

    public Cursor getLocationInfo(int categoryNumber){

        Log.i("testCatSelect","category in LocationDB call getLocationInfo= " + Integer.toString(categoryNumber));
        crsr = mDB.query(DATABASE_TABLE1, new String[] { FIELD_UnitName,  FIELD_ParentActivityId,
                FIELD_PhoneNumber, FIELD_Latitude, FIELD_longitude, FIELD_priority } ,
                FIELD_ParentActivityId + " = ? ", new String[]{Integer.toString(categoryNumber)}, null, null, null);
        Log.i("testCatSelect"," mDB.query ran. Count = " + Integer.toString(crsr.getCount()));

        if(crsr.getCount()>0)
            Log.i("testNew","rawQuery RAN");
        return crsr;
    }

    public Cursor getSubVahedLocationInfo(int categoryNumber,double myLat, double myLng){

        Cursor innerCrsr= null;
        String sql1 = "select VahedId from tblVahedsActivities where ActivityId = " + Integer.toString(categoryNumber);
        String sql2 = "select * ,2"
                + " as distFromMyPlace from ( " + sql1 + " ) as vahed_IDs INNER JOIN (tblVahed) ON vahed_IDs.VahedId = Id" +
                " order by distFromMyPlace";
        Log.i("newSQL",sql2);
        //(acos((sin(Latitude)*sin(myLat)+cos(Latitude)*cos(myLat*cos(longitude-myLng)))*60*1.1515);
        //  acos((sin(Latitude*3.14/180)*sin(" + Double.toString(myLat) + "*3.14/180) +cos(Latitude*3.14/180)*cos(" + Double.toString(myLat) +
        //"*3.14/180*cos(longitude*3.14/180-" + Double.toString(myLng)+ ")))*60*1.1515)"
        try {
            innerCrsr = mDB.rawQuery(sql2, null);
            Log.i("testSQL2","innerCrsr RAN");
        }catch (Exception e){
            Log.i("testSQL2",e.toString());
        }

        //********************** add dist value to each row
        int i;
        int cnt= innerCrsr.getCount();
        double dbllat,dbllng;
        String strLat, strLng;
        double dist;

        if(innerCrsr.moveToFirst()) {
            for (i = 0; i < cnt; i++) {

                strLat = innerCrsr.getString(innerCrsr.getColumnIndex(LocationsDB.FIELD_Latitude));
                strLng = innerCrsr.getString(innerCrsr.getColumnIndex(LocationsDB.FIELD_longitude));
                dbllat = Double.parseDouble(strLat);
                dbllng = Double.parseDouble(strLng);
                dist = distance(dbllat,dbllng,myLat,myLng);
                innerCrsr.getColumnIndex("distFromMyPlace");
                Log.i("newSQL",innerCrsr.getString(innerCrsr.getColumnIndex(LocationsDB.FIELD_longitude)));

                innerCrsr.moveToNext();
            }
        }

        //********************** add dist value to each row

        Log.i("testSQL2"," getSubVahedLocationInfo RAN. Count = " + Integer.toString(innerCrsr.getCount()));

        //***************************
        String str3 = "";
        //***************************


   /*     int i = innerCrsr.getColumnCount();
        for(int j = 0; j < i;j++)
        {
            Log.i("colNames","col[" + Integer.toString(j) + "] = "  +innerCrsr.getColumnName(j));
        }
*/
        if(innerCrsr.getCount()>0)
            Log.i("testNew","rawQuery RAN");
        return innerCrsr;
    }

    public Cursor searchFromDatabase(String Name,int Type){

        Cursor search= null;
        String sql;

        if( Type == 1 )

            sql = "SELECT * FROM tblVahed WHERE UnitName LIKE '%" + Name + "%'";

        else {

            String sql1 = "SELECT Id FROM tblActivity WHERE IsMainActivity != 1 AND UnitName LIKE '%" + Name + "%'";
            String sql2 = "SELECT * FROM ( " + sql1 + " ) as viewActivity INNER JOIN tblVahedsActivities ON" +
                    " viewActivity.Id = tblVahedsActivities.ActivityId GROUP BY tblVahedsActivities.VahedId";
            sql = "SELECT * FROM ( " + sql2 + " ) as viewActivityVahed INNER JOIN tblVahed ON viewActivityVahed.VahedId = tblVahed.Id" ;

        }
        try {
            search = mDB.rawQuery(sql, null);
        }catch (Exception e){
            Log.i("testSQL2",e.toString());
        }

        if(search.getCount()>0)
            Log.i("testNew","rawQuery RAN");
        return search;
    }


    /** Returns all the locations from the table */
    public Cursor getAllLocations(){
        return crsr = mDB.query(DATABASE_TABLE1, new String[] { FIELD_UnitName,  FIELD_ParentActivityId,
                        FIELD_PhoneNumber, FIELD_Latitude, FIELD_longitude, FIELD_priority } , null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void prepareDatabase() throws IOException {
        Log.i("testDB", "in prepareDatabase. befor checkDatBasa");
        boolean dbExist = checkDataBase();
       // copyDataBase();
       // Log.i("testDB","DB Copied...");
        if(dbExist) {
            Log.i("testDB", "Database exists.");
            int currentDBVersion = getVersionId();
            if (DB_VERSION > currentDBVersion) {
                Log.d(TAG, "Database version is higher than old.");
                deleteDb();
                try {
                    copyDataBase();
                    Log.i("testDB","copyDataBase RAN in if-try");
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            try {
                copyDataBase();
                Log.i("testDB","copyDataBase RAN in else-try");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            Log.i("testDB", "in checkDataBase. pathToSaveDBFile = " + pathToSaveDBFile);
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
            Log.i("testDB", "in checkDataBase. checkDB = " + Boolean.toString(checkDB));

        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }


    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open(DBNAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }
    public void deleteDb() {
        File file = new File(pathToSaveDBFile);
        if(file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }

    private int getVersionId() {
        Cursor crsr1 = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        Log.i("testDB","pathToSaveDBFile = " + pathToSaveDBFile);

        //String query = "SELECT version_id FROM tblDBversion";
        //Cursor cursor = db.rawQuery(query, null);
        Log.i("testDB","insert getVersionId ");
        int count;
        try {
            crsr1 = db.query(DATABASE_TABLE2, new String[] { FIELD_version_id } , null, null, null, null, null);
            Log.i("testDB","Count = " + Integer.toString(crsr1.getCount()));
            crsr1.moveToFirst();
            Log.i("testDB","cursor.moveToFirst(); Ran ");
            count = crsr1.getInt(crsr1.getColumnIndex(LocationsDB.FIELD_version_id));
        }
        catch (Exception e) {
            Log.i("testDB",e.toString());
            count = -1;
        }
        Log.i("testDB","Query RAN v = " + Integer.toString(count));

        crsr1.close();
        return count;
    }

    public Cursor vahedTypeExtraction(int vahedType){

        Cursor crsr2 = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        int count = -2;
        try {
            if(vahedType == 1) {
                crsr2 = db.query(DATABASE_TABLE3, new String[]{tblActivity_FIELD1_Id, tblActivity_FIELD2_UnitName,
                        tblActivity_FIELD3_IsMainActivity, tblActivity_FIELD4_ParentId},tblActivity_FIELD4_ParentId + " = ? ",
                        new String[]{"1"}, null, null, null);
                count = crsr2.getCount();
            }
            else if(vahedType == 2){
                crsr2 = db.query(DATABASE_TABLE3, new String[]{tblActivity_FIELD1_Id, tblActivity_FIELD2_UnitName,
                                tblActivity_FIELD3_IsMainActivity, tblActivity_FIELD4_ParentId},tblActivity_FIELD4_ParentId + " != ? ",
                        new String[]{"1"}, null, null, null);
                count = crsr2.getCount();
            }
            crsr2.moveToFirst();
            Log.i("testDB4", "cursor.moveToFirst(); Ran ");
        }
        catch (Exception e) {
            Log.i("testDB4",e.toString());
            count = -1;
        }
        Log.i("testDB4", "Count = " + Integer.toString(count));

        Log.i("testDB4","Query RAN...  ");

        crsr2.close();

        return crsr2;
    }

    public Cursor subVahedTypeExtraction(int vahedType){

        Cursor crsr = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String selArg = Integer.toString(vahedType);
        int count = -2;
        try {
            crsr = db.query(DATABASE_TABLE3, new String[]{tblActivity_FIELD1_Id, tblActivity_FIELD2_UnitName,
                            tblActivity_FIELD3_IsMainActivity, tblActivity_FIELD4_ParentId},tblActivity_FIELD4_ParentId + " = ? ",
                    new String[]{selArg}, null, null, null);
            count = crsr.getCount();
            crsr.moveToFirst();
            Log.i("testDB4", "cursor.moveToFirst(); Ran ");
        }
        catch (Exception e) {
            Log.i("testDB4",e.toString());
            count = -1;
        }
        Log.i("testDB4", "inner Count = " + Integer.toString(count));

        Log.i("testDB4","Query RAN...  ");

        //crsr.close();

        return crsr;
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = sin(deg2rad(lat1))* sin(deg2rad(lat2))+ cos(deg2rad(lat1))* cos(deg2rad(lat2))* cos(deg2rad(theta));
        dist = acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.i("dist","your distance = " + Double.toString(dist));
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}