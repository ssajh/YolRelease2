package ir.systemco.ssaj.yolrelease2.model;

import android.database.Cursor;
import android.util.Log;

import ir.systemco.ssaj.yolrelease2.LocationsDB;

import java.util.ArrayList;


public class ListDetails {

    public static ArrayList<Model> vahedList;
    private static int locationCount;
    private static String UnitName,PhoneNumber;
    private static int ParentActivityId;
    private static String strlat, strlng;
    private static double dbllat,dbllng;
    private static float fltZoom;

    public ListDetails(){
        vahedList = new ArrayList<>();
        //movieList.add(new Model(android.R.drawable.sym_def_app_icon,"عنوان","توضیحات "));
    }

    public static ArrayList<Model> getList(){
        return vahedList;
    }

    public static void setList(Cursor cursor){
        int i=0;
        int cnt= cursor.getCount();

        Log.i("lstDetails","before clear size = " + Integer.toString(vahedList.size()));
        vahedList.clear();
        Log.i("lstDetails","before clear size = " + Integer.toString(vahedList.size()));

        Log.i("lstDetails","inserted to setList.getCount = " + Integer.toString(cnt));
        if(cursor.moveToFirst()) {
            for (i = 0; i < cnt; i++) {
                Log.i("lstDetails", "inside for");

                UnitName = cursor.getString(cursor.getColumnIndex(LocationsDB.FIELD_UnitName));
                ParentActivityId = cursor.getInt(cursor.getColumnIndex(LocationsDB.FIELD_ParentActivityId));
                PhoneNumber = cursor.getString(cursor.getColumnIndex(LocationsDB.FIELD_PhoneNumber));
                strlat = cursor.getString(cursor.getColumnIndex(LocationsDB.FIELD_Latitude));
                strlng = cursor.getString(cursor.getColumnIndex(LocationsDB.FIELD_longitude));
                fltZoom = cursor.getFloat(cursor.getColumnIndex(LocationsDB.FIELD_priority));
                // Creating an instance of LatLng to plot the location in Google Maps
                dbllat = Double.parseDouble(strlat);
                dbllng = Double.parseDouble(strlng);

                vahedList.add(new Model(android.R.drawable.sym_def_app_icon, UnitName, "تلفن: " + PhoneNumber));

                cursor.moveToNext();
            }
        }
        Log.i("lstDetails","i = " + Integer.toString(i));
    }
}
