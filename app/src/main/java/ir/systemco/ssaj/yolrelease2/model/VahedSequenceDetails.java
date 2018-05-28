package ir.systemco.ssaj.yolrelease2.model;

import android.database.Cursor;
import android.util.Log;

import ir.systemco.ssaj.yolrelease2.LocationsDB;

import java.util.ArrayList;

public class VahedSequenceDetails {

    public static ArrayList<VahedSequence> vahedList;
    private static String Name;

    public VahedSequenceDetails(){
        Log.i("VahedSequenceData","VahedSequenceDetails");

        vahedList = new ArrayList<>();
    }

    public ArrayList<VahedSequence> getList(){
        Log.i("VahedSequenceData","getList");

        return vahedList;
    }

    public void setList(Cursor cursor){
        Log.i("VahedSequenceData","fist line of setList");
        int i;
        int cnt= cursor.getCount();
        vahedList.clear();
        Log.i("VahedSequenceData","cnt = " + Integer.toString(cnt));
        if(cursor.moveToFirst()) {
            for (i = 0; i < cnt; i++) {
                Name = cursor.getString(cursor.getColumnIndex(LocationsDB.tblActivity_FIELD2_UnitName));

                vahedList.add(new VahedSequence( Name, i));
                cursor.moveToNext();
            }
        }
    }
}
