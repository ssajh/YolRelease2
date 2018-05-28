package ir.systemco.ssaj.yolrelease2;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Hashtable;

/**
 * Created by RVCO on 3/29/2018.
 */

public class Icon_Manager {

    private static Hashtable<String,Typeface> cached_icon = new Hashtable<>();

    public Typeface get_icon(String path, Context context){

        Typeface icons = cached_icon.get(path);
       // Log.i("iconic",icons.toString());


        try {
            //if (icons == null) {
                icons = Typeface.createFromAsset(context.getAssets(), path);
                cached_icon.put(path, icons);
                Log.i("iconic", "in if-end");
           // }
        }catch (Exception e){
            Log.i("iconic",e.toString());
        }
        Log.i("iconic","before return");

        return icons;
    }

}
