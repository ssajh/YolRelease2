package ir.systemco.ssaj.yolrelease2;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ir.systemco.ssaj.yolrelease2.Adapter.SubVahedGridViewAdapter;

import static ir.systemco.ssaj.yolrelease2.R.menu.main;


public class SubVahedDialogFragment2 extends DialogFragment {

    ListView lv;
    String[] names;
    int[] Nums;
    private EditText filterText;
    ArrayAdapter<String> listAdapter;
    String[] result ={};// {"ali","guli","hasi","ali","guli","hasi","ali","guli","hasi"};



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        names = getArguments().getStringArray("subVahedNames");
        Nums = getArguments().getIntArray("subVahedNums");
        Log.e("listAdapter","getIntArray(subVahedNums)");

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Log.e("listAdapter","getIntArray(subVahedNums)");

        final View rootView = inflater.inflate(R.layout.sb_vahed_list_view,null);
        lv = (ListView)rootView.findViewById(R.id.lvSubVahedList);
        Log.e("listAdapter","getIntArray(subVahedNums)");

        getDialog().setTitle("انتخاب زیر واحد");

        final SubVahedGridViewAdapter subVahedGridViewAdapter = new SubVahedGridViewAdapter(getActivity(),names);
        lv.setAdapter(subVahedGridViewAdapter);
       /* try {
            listAdapter = new ArrayAdapter<>(getContext(), R.layout.sb_vahed_list_view, names);
        }catch (Exception e){
            Log.e("listAdapter1",e.toString());
        }
        try {
            lv.setAdapter(listAdapter);
        }catch (Exception e){
            Log.i("listAdapter",e.toString());
        }*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),Integer.toString(Nums[position]),Toast.LENGTH_SHORT).show();
            }
        });

        Button btnReturn = (Button)rootView.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();
                getDialog().hide();
                getDialog().cancel();

            }
        });

        filterText = (EditText)rootView.findViewById(R.id.etSearch);
        filterText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    Log.i("testSearch", s.toString());
                    result = search(s.toString());
                }
              //  }
              //  SubVahedGridViewAdapter subVahedGridViewAdapter = new SubVahedGridViewAdapter(getActivity(),result);
              //  lv.setAdapter(subVahedGridViewAdapter);
               // subVahedGridViewAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        return rootView;
    }
    public String[] search(String txtsearch) {
        String arr[]=null;
        int pos=0;
        for (int i = 0; i < names.length; i++) {

            if(names[i].contains(txtsearch)){
                arr[pos]=names[i];
                Log.i("testSearch",names[i]);
                pos++;
            }
        }
        return arr;
    }
}
