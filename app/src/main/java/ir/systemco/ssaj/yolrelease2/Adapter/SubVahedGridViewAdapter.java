package ir.systemco.ssaj.yolrelease2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ir.systemco.ssaj.yolrelease2.R;

public class SubVahedGridViewAdapter extends BaseAdapter {

    private Context context;
    private String[] subVahedName;

    public SubVahedGridViewAdapter(Context ctx, String[] str){
        this.context = ctx;
        this.subVahedName = str;
    }

    @Override
    public int getCount() {
        return subVahedName.length;
    }

    @Override
    public Object getItem(int position) {
        return subVahedName[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.sub_vahed_grid_model,null);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        name.setText(subVahedName[position]);


        return convertView;
    }
}
