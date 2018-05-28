package ir.systemco.ssaj.yolrelease2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by RVCO on 4/4/2018.
 */

public class CustomAdapterSpinner extends BaseAdapter {

    Context context;
    int flags[];
    String[] vahedNames;
    String[] vahedFlags;
    LayoutInflater inflter;


    public CustomAdapterSpinner(Context applicationContext, int[] flags, String[] vahedNames, String[] vahedFlags) {
    //public CustomAdapterSpinner(Context applicationContext, String[] countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.vahedNames = vahedNames;
        this.vahedFlags = vahedFlags;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Icon_Manager icon_manager = new Icon_Manager();
        view = inflter.inflate(R.layout.custom_spinner_items, null);

        ImageView ivIcon = (ImageView) view.findViewById(R.id.imageView);
        TextView txtVahedName = (TextView) view.findViewById(R.id.textView);
        TextView txtVahedIcon = (TextView) view.findViewById(R.id.tvIcon);

        txtVahedIcon.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", context));

        ivIcon.setImageResource(flags[i]);
        txtVahedName.setText(vahedNames[i]);
        txtVahedIcon.setText(vahedFlags[i]);
        return view;
    }
}
