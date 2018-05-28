package ir.systemco.ssaj.yolrelease2;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        TextView tvName = (TextView)view.findViewById(R.id.name);
        TextView tvDetails1 = (TextView)view.findViewById(R.id.details1);
        ImageView img = (ImageView)view.findViewById(R.id.pic);

        TextView tvDetails2 = (TextView)view.findViewById(R.id.details2);
        TextView tvDetails3 = (TextView)view.findViewById(R.id.details3);
        TextView tvDetails4 = (TextView)view.findViewById(R.id.details4);

        tvName.setText(marker.getTitle());
        tvDetails1.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        /*int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
                "drawable", context.getPackageName());*/
        img.setImageResource(R.drawable.vahed_type1);

        tvDetails2.setText(infoWindowData.getDetail1());
        tvDetails3.setText(infoWindowData.getDetail2());
        tvDetails4.setText(infoWindowData.getDetail3());


        return view;
    }
}