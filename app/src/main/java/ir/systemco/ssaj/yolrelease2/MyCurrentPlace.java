package ir.systemco.ssaj.yolrelease2;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MyCurrentPlace extends Fragment implements OnMapReadyCallback{

    public static GoogleMap gMap;
    public Cursor locations;

    public List<Marker> list = new ArrayList<>();
    public static double lat,lng;
    public Bundle bndl;
   // private static boolean firstRun = true;

    public MyCurrentPlace() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_map, container, false);
        Log.i("moveCamera", "in onCreateView");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

            super.onViewCreated(view, savedInstanceState);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
            mapFragment.getMapAsync(this);
            Log.i("moveCamera", "in onViewCreated");
            //firstRun = false;

    }
   // private static final LatLng SETAK = new LatLng(38.052840,46.363377);
   // private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
   // private LatLngBounds TabrizView = new LatLngBounds(
   //         new LatLng(38.065345, 46.205543),new LatLng(38.065345, 46.394329));  //Center of TABRIZ

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        final MainActivity mainActivity = (MainActivity) getActivity();
        lat = mainActivity.getLat();    // get Latitude from MainActivity
        lng = mainActivity.getLng();    // get Longitude from MainActivity

        LatLng LL = new LatLng(lat, lng);
        LatLng currentPlace = LL;
        LatLng centerOfMap;
        CameraPosition cam = new CameraPosition(LL, 15, 40, 0);
        MarkerOptions options = new MarkerOptions();
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_icon));
        options.position(LL).title("من اینجام");
        //options.position(LL).getIcon().zzJm();
        //***************** return setInfoWindowAdapter to DEFAULT ***************
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
//***************** return setInfoWindowAdapter to DEFAULT *************** END

        gMap.addMarker(options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icn_i_m_here))).showInfoWindow();
        //gMap.moveCamera(CameraUpdateFactory.newLatLng(LL));
        //gMap.setMinZoomPreference(6.0f);
        //gMap.setMaxZoomPreference(20.0f);
        // gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TabrizView.getCenter(),12));
        //gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam),2000,null);
        //centerOfMap = gMap.getCameraPosition().target;
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPlace, 15));
        Log.i("moveCamera", "after movaCamera in onMapReady");
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                 Toast.makeText(getContext(),"Marker clicked",Toast.LENGTH_SHORT).show();

//***************** return setInfoWindowAdapter to DEFAULT ***************
                gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });
//***************** return setInfoWindowAdapter to DEFAULT *************** END
                return false;
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                InfoWindowData info = new InfoWindowData();
                info.setImage("snowqualmie");
                info.setDetail1("آدرس:  خیابان-کوی-پلاک");
                info.setDetail2("ساعت کاری: از 8 صبح الی 10 شب");
                info.setDetail3("مدیریت: حسن روحانی");

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(mainActivity);
                gMap.setInfoWindowAdapter(customInfoWindow);

                marker.setTag(info);
                marker.showInfoWindow();
            }
        });
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if(mainActivity.finalVahedCount != 0 && mainActivity.subVahedSelected)
                    mainActivity.toggleRecycler(true);
            }
        });
    }

    public Cursor myNearByPlace(Context context, String absolutePath, int categoryNum,double myLat, double myLng){

        Log.i("testCatSelect","category in MyCurrentPlace = " + Integer.toString(categoryNum));
        return locationExtract(context, absolutePath,categoryNum,myLat,myLng);

    }

    public Cursor locationExtract(Context context, String absolutePath,int category_Num,double myLat, double myLng){
        LocationsDB locationsDB = new LocationsDB(context,absolutePath);
        int locationCount = 0;
        String UnitName,PhoneNumber;
        int ParentActivityId=0;
        String strlat="", strlng="";
        double dbllat = 0,dbllng = 0;


        float zoom=0,priority;
        Log.i("testCatSelect","category in LocationDB first call= " + Integer.toString(category_Num));
        //Cursor location = locationsDB.getLocationInfo(category_Num);
        locations = locationsDB.getSubVahedLocationInfo(category_Num,myLat,myLng);
        // Number of locations available in the SQLite database table
        locationCount = locations.getCount();
        Log.i("testCatSelect","category in LocationDB call getLocationInfo= " + Integer.toString(locationCount));
        Toast.makeText(context, Integer.toString(locationCount) + " مورد یافت شد...", Toast.LENGTH_SHORT).show();
        // Move the current record pointer to the first row of the table
        locations.moveToFirst();
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng locationCoordination = null;
        CameraPosition cam = null;
        gMap.clear();
        MyPlace();
        for(int i=0;i<locationCount;i++){
            UnitName = locations.getString(locations.getColumnIndex(LocationsDB.FIELD_UnitName));
            ParentActivityId = locations.getInt(locations.getColumnIndex(LocationsDB.FIELD_ParentActivityId));
            PhoneNumber = locations.getString(locations.getColumnIndex(LocationsDB.FIELD_PhoneNumber));
            strlat = locations.getString(locations.getColumnIndex(LocationsDB.FIELD_Latitude));
            strlng = locations.getString(locations.getColumnIndex(LocationsDB.FIELD_longitude));
            zoom = locations.getFloat(locations.getColumnIndex(LocationsDB.FIELD_priority));
            // Creating an instance of LatLng to plot the location in Google Maps
            dbllat = Double.parseDouble(strlat);
            dbllng = Double.parseDouble(strlng);
            locationCoordination = new LatLng (dbllat,dbllng);
            // Drawing the marker in the Google Maps
            drawMarker(locationCoordination,UnitName,PhoneNumber);
            locations.moveToNext();
        }

      //  double dis = distance(lat,lng,dbllat,dbllng);
      //  Toast.makeText(context, "فاصله شما تا اولین مقصد " + String.format("%.02f", dis) + " کیلومتره "
        //        , Toast.LENGTH_LONG).show();

        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                for(Marker m:list){
                    m.setVisible(cameraPosition.zoom>=15);
                    //15 is default zoom level
                }
            }
        });

     //   Toast.makeText(getActivity(),"number of markers is = " + Integer.toString(list.size()),Toast.LENGTH_LONG).show();

       // Toast.makeText(getActivity(),"Parent = " + Integer.toString(ParentActivityId),Toast.LENGTH_SHORT).show();
   /*     if(locationCount>0){
            // Moving CameraPosition to last Found position
            cam = new CameraPosition(locationCoordination, 15, 40, 0);   // (LatLng, zoom , tilt , bearing)
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam),3000,null);
        }*/
        return locations;
    }

    public void MyPlace(){
        LatLng LL = new LatLng(lat,lng);
        CameraPosition cam = new CameraPosition(LL, 15, 40, 0);
        MarkerOptions options = new MarkerOptions();
        options.position(LL).title("من اینجام");
        //***************** return setInfoWindowAdapter to DEFAULT ***************
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
//***************** return setInfoWindowAdapter to DEFAULT *************** END
        gMap.addMarker(options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icn_i_m_here))).showInfoWindow();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam),2000,null);

    }

    private void drawMarker(LatLng point,String unitName,String PhoneNumber){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(unitName).snippet(PhoneNumber);
        // Adding marker on the Google Map
        Marker marker = gMap.addMarker(markerOptions);


        list.add(marker);
        //marker.setVisible();



    }
}