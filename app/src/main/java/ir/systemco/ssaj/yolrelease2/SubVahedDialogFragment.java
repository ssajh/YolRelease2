package ir.systemco.ssaj.yolrelease2;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import ir.systemco.ssaj.yolrelease2.Adapter.SubVahedGridViewAdapter;

/**
 * Created by RVCO on 4/18/2018.
 */

public class SubVahedDialogFragment extends DialogFragment {

    GridView gv;
    String[] names;

    //String[] names = {"sdfsdf", "sdfsdf","sdfsfsf","fdghhgfh","asdqd","dfghrtjh","adsadqwef","cgrngthn"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        names = getArguments().getStringArray("subVahedNames");

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displaymetrics = new DisplayMetrics();

        final View rootView = inflater.inflate(R.layout.sub_vahed_grid_view,null);
        gv = (GridView)rootView.findViewById(R.id.gvSubVahed);

        //AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //Dialog d = .setView(new View(this)).create();
        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)

        getDialog().setTitle("انتخاب زیر واحد");


        SubVahedGridViewAdapter subVahedGridViewAdapter = new SubVahedGridViewAdapter(getActivity(),names);
        gv.setAdapter(subVahedGridViewAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),names[position],Toast.LENGTH_SHORT).show();
            }
        });

        Button btnReturn = (Button)rootView.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().hide();
            }
        });

        return rootView;
    }
}
