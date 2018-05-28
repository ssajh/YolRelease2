package ir.systemco.ssaj.yolrelease2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import ir.systemco.ssaj.yolrelease2.Adapter.MovieAdapter;
import ir.systemco.ssaj.yolrelease2.Adapter.RecyclerItemClickListener;
import ir.systemco.ssaj.yolrelease2.model.ListDetails;
import ir.systemco.ssaj.yolrelease2.model.Model;
import ir.systemco.ssaj.yolrelease2.model.VahedSequence;
import ir.systemco.ssaj.yolrelease2.model.VahedSequenceDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static ir.systemco.ssaj.yolrelease2.MyCurrentPlace.gMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public double myLat,myLng;
    public int finalVahedCount = 0;


// ***************** Auto-Hide Declaration ***************

    private static boolean icon_type = true;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private Cursor vahedInfos;
    public boolean subVahedSelected = false;
    static boolean firstSearch = true;
    public Cursor vahedInfos_ByName = null;
    public Cursor vahedInfos_BySubject = null;




    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };


    //private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

// ***************** Auto-Hide Declaration *************** END

    public static double Lat, Lng;
    public MyCurrentPlace myCurrentPlaceFragment = new MyCurrentPlace();                           // object created and linked to map
    private final static String TAG = "testDB";
    private Boolean MapGeted = false;
    public int category = 0;  // for select nearest places
//    public int parentIDofSubVahed = 2;
    public List<Integer> subActivityID = new ArrayList<Integer>();   // to get sub activity ID
    LinearLayoutManager linearLayoutManager;
    ArrayList<Model> models;
    MovieAdapter movieAdapter;
    RecyclerView recyclerView;
    public static int preCategoryCount = -1;
    public static boolean recyclerVisibility = true;
    SpinnerDialog spinnerDialog;
    Button btnShow;
    ArrayList<String> items = new ArrayList<>();
    Spinner spin;
    String destDir;

    TextView tv_RecyclerHideShow;

    Context appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        destDir = getFilesDir().getParentFile() + "/databases";
        final LocationsDB prepare = new LocationsDB(this, destDir);

        //*************** RecyclerView setting *************

        ListDetails listDetails = new ListDetails();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        models = listDetails.getList();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(MainActivity.this,models);
        recyclerView.setAdapter(movieAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        //*************** RecyclerView setting ************* END

        mControlsView = findViewById(R.id.app_bar_main_id);
        mContentView = findViewById(R.id.main_relative_layout);

        final Icon_Manager icon_manager = new Icon_Manager();
        final TextView tv_FullScreen = ((TextView) findViewById(R.id.tvFull_screen));
        tv_FullScreen.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        tv_FullScreen.setClickable(true);

        tv_FullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String icon_fullScreen = getString(R.string.icn_full_screen);
                String icon_normalScreen = getString(R.string.icn_normal_screen);
                toggle(icon_type);
                if (icon_type)
                    tv_FullScreen.setText(icon_fullScreen);
                else
                    tv_FullScreen.setText(icon_normalScreen);
            }
        });
        tv_RecyclerHideShow = ((TextView) findViewById(R.id.tvRecyclerHideShow));
        tv_RecyclerHideShow.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        tv_RecyclerHideShow.setClickable(true);
        tv_RecyclerHideShow.setVisibility(View.GONE);

        tv_RecyclerHideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String icon_Hide = getString(R.string.icn_recycler_hide);
                String icon_Show = getString(R.string.icn_recycler_show);
                toggleRecycler(false);
/*                if (recycler_HideShow_icon_type)
                    tv_RecyclerHideShow.setText(icon_Hide);
                else
                    tv_RecyclerHideShow.setText(icon_Show);*/
            }
        });

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationBar);

       /* TextDrawable main_drawer_icn_my_place = new TextDrawable(this);
        main_drawer_icn_my_place.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        main_drawer_icn_my_place.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        main_drawer_icn_my_place.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        main_drawer_icn_my_place.setText(getResources().getText(R.string.icn_my_place));
        try {
            navigation.getMenu().findItem(R.id.nav_myProfile).setIcon(R.drawable.ic_gps_icon);
        }catch (Exception e){
            Log.i("setIcon",e.toString());
        }
*/
        TextDrawable faIcon1 = new TextDrawable(this);
        faIcon1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon1.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon1.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        faIcon1.setText(getResources().getText(R.string.icn_public_places));
        navigation.getMenu().findItem(R.id.navigation_public_places).setIcon(faIcon1);

        TextDrawable faIcon2 = new TextDrawable(this);
        faIcon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon2.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon2.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        faIcon2.setText(getResources().getText(R.string.icn_navigation));
        navigation.getMenu().findItem(R.id.navigation_navigation).setIcon(faIcon2);

        TextDrawable faIcon3 = new TextDrawable(this);
        faIcon3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon3.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon3.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        faIcon3.setText(getResources().getText(R.string.icn_search));
        navigation.getMenu().findItem(R.id.navigation_search).setIcon(faIcon3);

        TextDrawable faIcon4 = new TextDrawable(this);
        faIcon4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon4.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon4.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", this));
        faIcon4.setText(getResources().getText(R.string.icn_jobs));
        navigation.getMenu().findItem(R.id.navigation_jobs).setIcon(faIcon4);

        // navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(3).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // update highlighted item in the navigation menu
                Dialog wholeVahedDialog;
                AlertDialog searchDialog;

                wholeVahedDialog = wholeVahed();

                switch (item.getItemId()) {
                    case R.id.navigation_jobs:

                        wholeVahedDialog.show();
                        return true;
                    case R.id.navigation_search:

                        searchResult();
                        return true;
                    case R.id.navigation_navigation:

                        Toast.makeText(MainActivity.this, "ناوبری", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_public_places:

                        Toast.makeText(MainActivity.this, "مراکز عمومی", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton myPlace = (FloatingActionButton) findViewById(R.id.myPlace);
        //FloatingActionButton myNearByPlace = (FloatingActionButton) findViewById(R.id.myNearByPlace);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        final Cursor vahedCursor;

        //**************** ListView ********************

        Log.i(TAG, "getFilesDir().getAbsolutePath() = " + destDir);
        try {
            Log.i(TAG, "insert prepare TRY");
            prepare.prepareDatabase();
            Log.i(TAG, "prepare completed");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        vahedCursor = prepare.vahedTypeExtraction(1);              // Extract Vaheds Type///  1 --> mainVahed / 2 -->subVahed

        ListView activityList;
        final String vahedTypesList[] = {"فروشگاهی", "تعمیرات", "خوردنی و آشامیدنی", "اماکن عمومی", "سازمان های دولتی",
                "خودرو", "مکانهای دولتی", "بانک", "مراکز آموزشی", "سرگرمی", "ورزش", "صنایع مادر",
                "خدماتی", "مراکز ضروری", "خدمات ساختمانی", "مراکز درمانی", "مراکز خرید", "مراکز ضروری روزانه",
                "لوازم موتور", "مجتمع ها", "فناوری اطلاعات و ارتباطات"};

        final String vahedFlags[] = {getString(R.string.icn_vahed_Type1),getString(R.string.icn_vahed_Type2),getString(R.string.icn_vahed_Type3),
                getString(R.string.icn_vahed_Type4),getString(R.string.icn_vahed_Type5),getString(R.string.icn_vahed_Type6),getString(R.string.icn_vahed_Type7),
                getString(R.string.icn_vahed_Type8),getString(R.string.icn_vahed_Type9),getString(R.string.icn_vahed_Type10),getString(R.string.icn_vahed_Type11),
                getString(R.string.icn_vahed_Type12),getString(R.string.icn_vahed_Type13),getString(R.string.icn_vahed_Type14),getString(R.string.icn_vahed_Type15),
                getString(R.string.icn_vahed_Type16),getString(R.string.icn_vahed_Type17),getString(R.string.icn_vahed_Type18),getString(R.string.icn_vahed_Type19),
                getString(R.string.icn_vahed_Type20),getString(R.string.icn_vahed_Type21)};

        int flags[] = {R.drawable.vahed_type1, R.drawable.vahed_type2, R.drawable.vahed_type3,
                R.drawable.vahed_type4, R.drawable.vahed_type5, R.drawable.vahed_type6,
                R.drawable.vahed_type7, R.drawable.vahed_type8, R.drawable.vahed_type9,
                R.drawable.vahed_type10, R.drawable.vahed_type11, R.drawable.vahed_type12,
                R.drawable.vahed_type13, R.drawable.vahed_type14, R.drawable.vahed_type15,
                R.drawable.vahed_type16, R.drawable.vahed_type17, R.drawable.vahed_type18,
                R.drawable.vahed_type19, R.drawable.vahed_type20, R.drawable.vahed_type21};

 /*       activityList = (ListView) findViewById(R.id.activity_list);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), vahedTypesList, flags);
        activityList.setAdapter(customAdapter);

        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectCategory(position);
                int nearByPlacesCount;
                if (MapGeted) {
                    Log.i("testCatSelect", "category in MainActivity = " + Integer.toString(category));
                    nearByPlacesCount = myCurrentPlaceFragment.myNearByPlace(MainActivity.this, getFilesDir().getAbsolutePath(), category);
                    Log.i("testNew", "Returned Locations = " + Integer.toString(nearByPlacesCount));
                } else
                    Snackbar.make(view, "مکانت معلوم نیست! اول کلید روبرو رو بزن", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this,"مکانت معلوم نیست! اول کلید روبرو رو بزن",Toast.LENGTH_LONG).show();

            }
        });*/

        //**************** ListView ******************** END

        //**************** Spinner ********************

        spin = (Spinner) findViewById(R.id.spnrVahed);
        CustomAdapterSpinner spnrAdapterVahed = new CustomAdapterSpinner(getApplicationContext(), flags, vahedTypesList, vahedFlags);
        spin.setAdapter(spnrAdapterVahed);

        final Spinner spinSubVahed = (Spinner) findViewById(R.id.spnrSubVahed);
        final ArrayList<String> labels = new ArrayList<>();          // to get sub activity name

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("spinPosition","pos = " + Integer.toString(position));
                if(position == -1) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerVisibility = false;
                    tv_RecyclerHideShow.setVisibility(view.GONE);

                    spinSubVahed.setVisibility(view.GONE);
                    subVahedSelected = false;
                }
                else {
                    spinSubVahed.setVisibility(view.VISIBLE);
                    subVahedSelected = true;
                    recyclerVisibility = true;
                }
                selectCategory(position);
                Cursor subVahedCursor;
                int subCategory = position+2;
                subActivityID.clear();
                labels.clear();
                if (MapGeted) {
                    //*********** SubVahed Spinner Listing ***************
                    try {
                        subVahedCursor = prepare.subVahedTypeExtraction(subCategory);  // Extract Vaheds Type///  1 --> mainVahed / else -->subVahed
                        if(subVahedCursor.getCount()==0){
                            recyclerView.setVisibility(View.GONE);
                            recyclerVisibility = false;
                            tv_RecyclerHideShow.setVisibility(View.GONE);
                            gMap.clear();
                            preCategoryCount = 0;
                        }
                        if (subVahedCursor.moveToFirst()) {
                            //parentIDofSubVahed = Integer.parseInt(subVahedCursor.getString(3));
                            do {
                                subActivityID.add(Integer.parseInt(subVahedCursor.getString(0)));
                                labels.add(subVahedCursor.getString(1));
                            } while (subVahedCursor.moveToNext());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_sub_spinner, labels);
                        dataAdapter.setDropDownViewResource(R.layout.custom_sub_spinner_dropdown_item);

                        spinSubVahed.setAdapter(dataAdapter);

                        // CustomAdapterSpinner spnrAdapterSubVahed = new CustomAdapterSpinner(getApplicationContext(), subVahedFlag, subVahedTypesList);
                        // spinSubVahed.setAdapter(spnrAdapterSubVahed);
                    }catch (Exception e) {
                        Log.i("subVahed",e.toString());
                    }
                    //*********** SubVahed Spinner Listing *************** END
                }else
                    Snackbar.make(view, "مکانت معلوم نیست! اول کلید روبرو رو بزن", Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("spinPosition","pos not selected");
            }
        });

        //*************** SpinnerDialog Setting ************

        initItems();
        spinnerDialog = new SpinnerDialog(MainActivity.this,labels,"گزینه مورد نظرت رو انتخاب کن");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(MainActivity.this,"Selected : " + item + "\nposition : " + Integer.toString(position)
                        ,Toast.LENGTH_SHORT).show();
            }

        });

        btnShow = (Button) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        //*************** SpinnerDialog Setting ************
        spinSubVahed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == -1) {
                    recyclerView.setVisibility(view.GONE);
                    tv_RecyclerHideShow.setVisibility(view.GONE);
                    recyclerVisibility = false;
                    subVahedSelected = false;
                    gMap.clear();

                }
                else {
                    if(recyclerVisibility) {
                        recyclerView.setVisibility(view.VISIBLE);
                        recyclerVisibility = true;
                    }
                    subVahedSelected = true;
                }

                ListDetails listDetails =null;
                try{

                vahedInfos = myCurrentPlaceFragment.myNearByPlace(MainActivity.this,
                        getFilesDir().getAbsolutePath(), subActivityID.get(position),myLat,myLng);
                    listDetails.setList(vahedInfos);
                    models = listDetails.getList();
                    movieAdapter = new MovieAdapter(MainActivity.this,models);
                    recyclerView.setAdapter(movieAdapter);
                    finalVahedCount = vahedInfos.getCount();
                    if( finalVahedCount == 0 ) {
                        recyclerView.setVisibility(view.GONE);
                        recyclerVisibility = false;
                        tv_RecyclerHideShow.setVisibility(view.GONE);
                    }
                    else if( finalVahedCount == 1 ) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                        params.height = 150;
                        recyclerView.setLayoutParams(params);
                        Log.i("preCategoryCount", "preCategoryCount = " + Integer.toString(preCategoryCount));
                       // if (preCategoryCount == 0)
                       //     tv_RecyclerHideShow.setVisibility(view.VISIBLE);
                        if(preCategoryCount == 0 || subVahedSelected) {
                            if(firstSearch) {
                                recyclerView.setVisibility(view.VISIBLE);
                                recyclerVisibility = true;
                                firstSearch = false;
                            }else if(!recyclerVisibility)
                                tv_RecyclerHideShow.setVisibility(view.VISIBLE);
                        }
                    }
                    else{
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                        params.height = 300;
                        recyclerView.setLayoutParams(params);
                        if(preCategoryCount == 0 || subVahedSelected) {
                            if(firstSearch) {
                                recyclerView.setVisibility(view.VISIBLE);
                                recyclerVisibility = true;
                                firstSearch = false;
                            }else if(!recyclerVisibility)
                                tv_RecyclerHideShow.setVisibility(view.VISIBLE);
                        }
                    }
                    preCategoryCount = finalVahedCount;
                }catch (Exception e){
                    Log.i("testSQL2",e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        vahedInfos.moveToPosition(position);
                        String strlat = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_Latitude));
                        String strlng = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_longitude));
                        String UnitName = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_UnitName));
                        goToVahedOnMap(Double.parseDouble(strlat),Double.parseDouble(strlng),UnitName);
                    }
                })
        );


        //**************** Spinner ******************** END


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("testLatLng", "inside first if-  Lat is: " + Double.toString(Lat) + "/ Lng is : " + Double.toString(Lng));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
                Log.i("testLatLng", "inside second if-  Lat is: " + Double.toString(Lat) + "/ Lng is : " + Double.toString(Lng));

                return;
            } else {
                getCurrentPlace();
                Log.i("testLatLng", "inside else-  Lat is: " + Double.toString(Lat) + "/ Lng is : " + Double.toString(Lng));
            }

        }

        //**********Defaulty go to current place and show on MAP *********

        getCurrentPlace();  // Lat and Lng is in hand
        goToCurrentPlace();
        MapGeted = true;

        //**********Defaulty go to current place and show on MAP ******END


        myPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //********** Like MyCurrentPlace.MyPlace Code
                myLat = getLat();
                myLng = getLng();
                LatLng LL = new LatLng(myLat,myLng);
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
                MapGeted = true;
            }
        });

        //***************** Auto-Hide *********************


        // setContentView(R.layout.activity_main);       //Hide whole view on main screen

        //mVisible = true;

        Log.i("autohide", "before mContentView.onClick");

        // Set up the user interaction to manually show or hide the system UI.
  /*      mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle();
                Log.i("autohide","insert mContentView.onClick");
            }
        });
*/
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        //***************** Auto-Hide ********************* END




      /*  myNearByPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nearByPlacesCount;
                if(MapGeted) {
                    Log.i("testCatSelect","category in MainActivity = " + Integer.toString(category));
                    nearByPlacesCount = myCurrentPlaceFragment.myNearByPlace(MainActivity.this, getFilesDir().getAbsolutePath(),category);
                    Log.i("testNew", "Returned Locations = " + Integer.toString(nearByPlacesCount));
                }
                else
                    Snackbar.make(v,"مکانت معلوم نیست! اول کلید روبرو رو بزن", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this,"مکانت معلوم نیست! اول کلید روبرو رو بزن",Toast.LENGTH_LONG).show();
            }
        });*/

    }

    private void initItems() {
        for(int i=0; i<100; i++)
        {
            items.add("گزینه  " + (i+1));
        }
        for(int i=0; i<100; i++)
        {
            items.add("عدد  " + (i+1));
        }
        for(int i=0; i<100; i++)
        {
            items.add("شماره  " + (i+1));
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, "خوشحال شدم ازم کمک گرفتی. بازم بیا", Toast.LENGTH_SHORT).show();
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "تنظمیات انتخاب شد", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myCurrentPlace) {
            getCurrentPlace();  // Lat and Lng is in hand
            goToCurrentPlace();
            MapGeted = true;
        } else if (id == R.id.nav_myProfile) {
            Toast.makeText(MainActivity.this, "پروفایل من", Toast.LENGTH_SHORT).show();

            // Handle the camera action
        } else if (id == R.id.nav_myFriend) {
            Toast.makeText(MainActivity.this, "دوستام کو؟!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_myLikes) {
            Toast.makeText(MainActivity.this, "اونایی که دوس داشتم", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_getCoin) {
            Toast.makeText(MainActivity.this, "امتیاز میخام", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "از این برنامه خوشم میام. میخام به دوستامم معرفی کنم", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_contactus) {
            Toast.makeText(MainActivity.this, "کی اینو کار کرده؟!!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about_yol) {
            Toast.makeText(MainActivity.this, "راهنمای یول", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getCurrentPlace();
                return;
        }

    }

    private void getCurrentPlace() {


        GPS_Tracker gpsTracker = new GPS_Tracker(MainActivity.this);
        Lat = gpsTracker.getLatitude();
        Lng = gpsTracker.getLongitude();
        gpsTracker.stopUsingGPS();

    }


    public void goToCurrentPlace() {

        MyCurrentPlace myCurrentPlaceFragment = new MyCurrentPlace();                           // object created and linked to map
        FragmentManager Manager = getSupportFragmentManager();                                  // to Manage Map
        Manager.beginTransaction().replace(R.id.mainLayout, myCurrentPlaceFragment).commit();   // replace mainLayout with myCurrentPlaceFragment

    }

    public static double getLat() {
        return Lat;
    }

    public static double getLng() {
        return Lng;
    }

    public void selectCategory(int item) {
        switch (item) {
            case 0:
                // Toast.makeText(MainActivity.this, "فروشگاهی", Toast.LENGTH_SHORT).show();
                category = 2;
                break;
            case 1:
                // Toast.makeText(MainActivity.this, "تعمیرات", Toast.LENGTH_SHORT).show();
                category = 3;
                break;
            case 2:
                // Toast.makeText(MainActivity.this, "خوردنی و آشامیدنی", Toast.LENGTH_SHORT).show();
                category = 4;
                break;
            case 3:
                // Toast.makeText(MainActivity.this, "اماکن عمومی", Toast.LENGTH_SHORT).show();
                category = 5;
                break;
            case 4:
                // Toast.makeText(MainActivity.this, "سازمان های دولتی", Toast.LENGTH_SHORT).show();
                category = 6;
                break;
            case 5:
                // Toast.makeText(MainActivity.this, "خودرو", Toast.LENGTH_SHORT).show();
                category = 7;
                break;
            case 6:
                // Toast.makeText(MainActivity.this, "مکانهای مرتبط با سازمان های دولتی", Toast.LENGTH_SHORT).show();
                category = 8;
                break;
            case 7:
                // Toast.makeText(MainActivity.this, "بانک", Toast.LENGTH_SHORT).show();
                category = 9;
                break;
            case 8:
                //Toast.makeText(MainActivity.this, "مراکز آموزشی", Toast.LENGTH_SHORT).show();
                category = 10;
                break;
            case 9:
                // Toast.makeText(MainActivity.this, "سرگرمی", Toast.LENGTH_SHORT).show();
                category = 11;
                break;
            case 10:
                // Toast.makeText(MainActivity.this, "ورزشی", Toast.LENGTH_SHORT).show();
                category = 12;
                break;
            case 11:
                // Toast.makeText(MainActivity.this, "صنایع مادر", Toast.LENGTH_SHORT).show();
                category = 13;
                break;
            case 12:
                // Toast.makeText(MainActivity.this, "خدماتی", Toast.LENGTH_SHORT).show();
                category = 14;
                break;
            case 13:
                // Toast.makeText(MainActivity.this, "مراکز ضروری ", Toast.LENGTH_SHORT).show();
                category = 15;
                break;
            case 14:
                // Toast.makeText(MainActivity.this, "خدمات ساختمانی", Toast.LENGTH_SHORT).show();
                category = 16;
                break;
            case 15:
                // Toast.makeText(MainActivity.this, "مراکز درمانی", Toast.LENGTH_SHORT).show();
                category = 17;
                break;
            case 16:
                // Toast.makeText(MainActivity.this, "مراکز خرید", Toast.LENGTH_SHORT).show();
                category = 18;
                break;
            case 17:
                // Toast.makeText(MainActivity.this, "مراکز ضروری روزانه", Toast.LENGTH_SHORT).show();
                category = 312;
                break;
            case 18:
                //Toast.makeText(MainActivity.this, "لوازم موتور", Toast.LENGTH_SHORT).show();
                category = 338;
                break;
            case 19:
                //Toast.makeText(MainActivity.this, "مجتمع ها", Toast.LENGTH_SHORT).show();
                category = 367;
                break;
            case 20:
                // Toast.makeText(MainActivity.this, "فناوری اطلاعات و ارتباطات", Toast.LENGTH_SHORT).show();
                category = 565;
                break;
        }
    }

    //************************ Auto-Hide *******************


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        ///delayedHide(100);
    }

    public void toggle(boolean icn_type) {
        if (icn_type) {
            hide();
        } else {
            show();
        }
    }

    public final void toggleRecycler(boolean icn_type) {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        TextView tv = (TextView) findViewById(R.id.tvRecyclerHideShow);
        Log.i("toggleRecycler","inside toggleRecycler");
        if (icn_type) {
            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    0,                 // fromYDelta
                    recyclerView.getHeight()); // toYDelta
            animate.setDuration(500);
            animate.setFillAfter(true);
            recyclerView.startAnimation(animate);
            recyclerView.setVisibility(View.GONE);
            recyclerVisibility = false;
            tv.setVisibility(View.VISIBLE);
            //recycler_HideShow_icon_type = false;
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    recyclerView.getHeight(),  // fromYDelta
                    0);                // toYDelta
            animate.setDuration(500);
            animate.setFillAfter(true);
            recyclerView.startAnimation(animate);
            tv.setVisibility(View.GONE);
            recyclerVisibility = true;
            //recycler_HideShow_icon_type = true;

        }
    }


    private void hide() {
        // Hide UI first
        Log.i("autohide", "in hide before getSupportActionBar");
        ActionBar actionBar = getSupportActionBar();
        BottomNavigationView navigation1 = (BottomNavigationView) findViewById(R.id.navigationBar);
        Log.i("autohide", "in hide after getSupportActionBar");

        if (actionBar != null) {
            Log.i("autohide", "in hide in if before   actionBar.hide();");
            actionBar.hide();
            navigation1.setVisibility(View.GONE);
            Log.i("autohide", "in hide in if after   actionBar.hide();");

        }
        //mControlsView.setVisibility(View.GONE);
        icon_type = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        //  mHideHandler.removeCallbacks(mShowPart2Runnable);
        //  mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        ActionBar actionBar = getSupportActionBar();
        BottomNavigationView navigation2 = (BottomNavigationView) findViewById(R.id.navigationBar);

        if (actionBar != null) {
            Log.i("autohide", "in hide in if before   actionBar.hide();");
            actionBar.show();
            navigation2.setVisibility(View.VISIBLE);
            Log.i("autohide", "in hide in if after   actionBar.hide();");

        }

       /* mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);*/
        icon_type = true;

        // Schedule a runnable to display UI elements after a delay
        //  mHideHandler.removeCallbacks(mHidePart2Runnable);
        //  mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        //    mHideHandler.removeCallbacks(mHideRunnable);
        //    mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
//************************ Auto-Hide ******************* END

    private static void goToVahedOnMap(double lat, double lng, String UnitName){

        LatLng LL = new LatLng(lat,lng);
        distance(getLat(),getLng(),lat,lng);
        CameraPosition cam = new CameraPosition(LL, 15, 40, 0);
        MarkerOptions options = new MarkerOptions();
        options.position(LL).title(UnitName);

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
        gMap.addMarker(options).showInfoWindow();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam),2000,null);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
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

    private Dialog wholeVahed(){
        Dialog dialog = new Dialog(MainActivity.this);
        Icon_Manager icon_manager = new Icon_Manager();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list);

        final Dialog dialog2 = new Dialog(MainActivity.this);

        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.list);

        TextView tvIcon1 = ((TextView)dialog.findViewById(R.id.tvIcon1));
        TextView tvIcon2 = ((TextView)dialog.findViewById(R.id.tvIcon2));
        TextView tvIcon3 = ((TextView)dialog.findViewById(R.id.tvIcon3));
        TextView tvIcon4 = ((TextView)dialog.findViewById(R.id.tvIcon4));
        TextView tvIcon5 = ((TextView)dialog.findViewById(R.id.tvIcon5));
        TextView tvIcon6 = ((TextView)dialog.findViewById(R.id.tvIcon6));
        TextView tvIcon7 = ((TextView)dialog.findViewById(R.id.tvIcon7));
        TextView tvIcon8 = ((TextView)dialog.findViewById(R.id.tvIcon8));
        TextView tvIcon9 = ((TextView)dialog.findViewById(R.id.tvIcon9));
        TextView tvIcon10 = ((TextView)dialog.findViewById(R.id.tvIcon10));
        TextView tvIcon11 = ((TextView)dialog.findViewById(R.id.tvIcon11));
        TextView tvIcon12 = ((TextView)dialog.findViewById(R.id.tvIcon12));
        TextView tvIcon13 = ((TextView)dialog.findViewById(R.id.tvIcon13));
        TextView tvIcon14 = ((TextView)dialog.findViewById(R.id.tvIcon14));
        TextView tvIcon15 = ((TextView)dialog.findViewById(R.id.tvIcon15));
        TextView tvIcon16 = ((TextView)dialog.findViewById(R.id.tvIcon16));
        TextView tvIcon17 = ((TextView)dialog.findViewById(R.id.tvIcon17));
        TextView tvIcon18 = ((TextView)dialog.findViewById(R.id.tvIcon18));
        TextView tvIcon19 = ((TextView)dialog.findViewById(R.id.tvIcon19));
        TextView tvIcon20 = ((TextView)dialog.findViewById(R.id.tvIcon20));
        TextView tvIcon21 = ((TextView)dialog.findViewById(R.id.tvIcon21));

        tvIcon1.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon2.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon3.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon4.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon5.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon6.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon7.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon8.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon9.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon10.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon11.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon12.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon13.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon14.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon15.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon16.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon17.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon18.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon19.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon20.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));
        tvIcon21.setTypeface(icon_manager.get_icon("fonts/ionicons.ttf", MainActivity.this));

        tvIcon1.setText(getString(R.string.icn_vahed_Type1));
        tvIcon2.setText(getString(R.string.icn_vahed_Type2));
        tvIcon3.setText(getString(R.string.icn_vahed_Type3));
        tvIcon4.setText(getString(R.string.icn_vahed_Type4));
        tvIcon5.setText(getString(R.string.icn_vahed_Type5));
        tvIcon6.setText(getString(R.string.icn_vahed_Type6));
        tvIcon7.setText(getString(R.string.icn_vahed_Type7));
        tvIcon8.setText(getString(R.string.icn_vahed_Type8));
        tvIcon9.setText(getString(R.string.icn_vahed_Type9));
        tvIcon10.setText(getString(R.string.icn_vahed_Type10));
        tvIcon11.setText(getString(R.string.icn_vahed_Type11));
        tvIcon12.setText(getString(R.string.icn_vahed_Type12));
        tvIcon13.setText(getString(R.string.icn_vahed_Type13));
        tvIcon14.setText(getString(R.string.icn_vahed_Type14));
        tvIcon15.setText(getString(R.string.icn_vahed_Type15));
        tvIcon16.setText(getString(R.string.icn_vahed_Type16));
        tvIcon17.setText(getString(R.string.icn_vahed_Type17));
        tvIcon18.setText(getString(R.string.icn_vahed_Type18));
        tvIcon19.setText(getString(R.string.icn_vahed_Type19));
        tvIcon20.setText(getString(R.string.icn_vahed_Type20));
        tvIcon21.setText(getString(R.string.icn_vahed_Type21));


        RelativeLayout relativeLayout1 = (RelativeLayout)dialog.findViewById(R.id.relative1);
        RelativeLayout relativeLayout2 = (RelativeLayout)dialog.findViewById(R.id.relative2);
        RelativeLayout relativeLayout3 = (RelativeLayout)dialog.findViewById(R.id.relative3);
        RelativeLayout relativeLayout4 = (RelativeLayout)dialog.findViewById(R.id.relative4);
        RelativeLayout relativeLayout5 = (RelativeLayout)dialog.findViewById(R.id.relative5);
        RelativeLayout relativeLayout6 = (RelativeLayout)dialog.findViewById(R.id.relative6);
        RelativeLayout relativeLayout7 = (RelativeLayout)dialog.findViewById(R.id.relative7);
        RelativeLayout relativeLayout8 = (RelativeLayout)dialog.findViewById(R.id.relative8);
        RelativeLayout relativeLayout9 = (RelativeLayout)dialog.findViewById(R.id.relative9);
        RelativeLayout relativeLayout10 = (RelativeLayout)dialog.findViewById(R.id.relative10);
        RelativeLayout relativeLayout11 = (RelativeLayout)dialog.findViewById(R.id.relative11);
        RelativeLayout relativeLayout12 = (RelativeLayout)dialog.findViewById(R.id.relative12);
        RelativeLayout relativeLayout13 = (RelativeLayout)dialog.findViewById(R.id.relative13);
        RelativeLayout relativeLayout14 = (RelativeLayout)dialog.findViewById(R.id.relative14);
        RelativeLayout relativeLayout15 = (RelativeLayout)dialog.findViewById(R.id.relative15);
        RelativeLayout relativeLayout16 = (RelativeLayout)dialog.findViewById(R.id.relative16);
        RelativeLayout relativeLayout17 = (RelativeLayout)dialog.findViewById(R.id.relative17);
        RelativeLayout relativeLayout18 = (RelativeLayout)dialog.findViewById(R.id.relative18);
        RelativeLayout relativeLayout19 = (RelativeLayout)dialog.findViewById(R.id.relative19);
        RelativeLayout relativeLayout20 = (RelativeLayout)dialog.findViewById(R.id.relative20);
        RelativeLayout relativeLayout21 = (RelativeLayout)dialog.findViewById(R.id.relative21);

        final android.app.FragmentManager fragmentManager = getFragmentManager();
        final SubVahedDialogFragment subVahedDialogFragment = new SubVahedDialogFragment();
        final SubVahedDialogFragment2 subVahedDialogFragment2 = new SubVahedDialogFragment2();

        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"فروشگاهی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("listAdapter","before ArrayList<VahedSequence>");

                ArrayList<VahedSequence> subVahedList = prepareSubVahed(3);
                int subVahedListCount = subVahedList.size();
                String[] vahedNames = new String[subVahedListCount];
                int[] listPosition = new int[subVahedListCount];
                //ArrayList<String> subVahedListNames = new ArrayList<>();
                //ArrayList<String> subVahedListNums = new ArrayList<>();

                for(int i = 0 ; i<subVahedListCount ; i++)
                    vahedNames[i] = subVahedList.get(i).getName();

                for(int i = 0 ; i<subVahedListCount ; i++)
                    listPosition[i] = subVahedList.get(i).getNum();

                Log.e("listAdapter","before Bundle");

                Bundle bundle = new Bundle();
                Log.i("###",Integer.toString(subVahedList.size()));
                bundle.putStringArray("subVahedNames", vahedNames);
                bundle.putIntArray("subVahedNums", listPosition);
                //subVahedDialogFragment.setArguments(bundle);
                //subVahedDialogFragment.show(fragmentManager,null);
                Log.e("listAdapter","before subVahedDialogFragment2");

                subVahedDialogFragment2.setArguments(bundle);
                subVahedDialogFragment2.show(fragmentManager,null);

            }
        });

        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"خوردن آشامیدن کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"اماکن عمومی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"سازمانهای دولتی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"خودرو کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مکانهای دولتی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"بانک کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مراکز آوزشی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"سرگرمی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"ورزش کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"صنایع مادر کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"خدماتی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مراکز ضروری کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"خدمات ساختمانی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مراکز درمانی کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مراکز خرید کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مراکز ضروری روزانه کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"لوازم موتور کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"مجتمع ها کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayout21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"فناوری اطلاعات و ارتباطات کلیک شد",Toast.LENGTH_SHORT).show();
            }
        });

        return dialog;
    }

    private AlertDialog searchResult(){
        //Dialog dialog = new Dialog(MainActivity.this);
       // Icon_Manager icon_manager = new Icon_Manager();

        //private String HALLOWEEN_ORANGE = "#FF7F27";

        final View view = getLayoutInflater().inflate( R.layout.search_dialog_view,null);
        final AlertDialog.Builder customDialogBuilder = new AlertDialog.Builder(view.getContext()).
                //setTitle("دنبال چی هستی؟").
                        setIcon(R.drawable.ic_search_black_36dp).
                        setCancelable(true).setView(view);
                        //setView(getLayoutInflater().inflate( R.layout.search_dialog_view,null));
        final AlertDialog dialog = customDialogBuilder.create();
        dialog.show();

        //  .setMessage("You are now entering the 10th dimension.").
                              /*  .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(),"منصرف شدم",Toast.LENGTH_SHORT).show();
                                    }
                                }).*/

        final EditText etVahedName = ((EditText)view.findViewById(R.id.etVahedName));
        final EditText etVahedSubject = ((EditText)view.findViewById(R.id.etVahedSubject));

        final Button btnSearch = (Button)view.findViewById(R.id.btnSearch);
        Button btnCancel = (Button)view.findViewById(R.id.btnCancel);

        final TextView  tvResultCountByName = (TextView)view.findViewById(R.id.tvResultCountByName);
        final TextView  tvResultCountBySubject = (TextView)view.findViewById(R.id.tvResultCountBySubject);

        final LinearLayout  llSearchBySubject = (LinearLayout)view.findViewById(R.id.llSearchBySubject);
        final LinearLayout  llSearchByName = (LinearLayout)view.findViewById(R.id.llSearchByName);

        final RecyclerView recyclerView1 = (RecyclerView)view.findViewById(R.id.rvSarchResultsByName);
        final RecyclerView recyclerView2 = (RecyclerView)view.findViewById(R.id.rvSarchResultsBySubject);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));      // this line is my Nejat Dahande (*_*)
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));      // this line is my Nejat Dahande (*_*)
        final LocationsDB prepare2 = new LocationsDB(MainActivity.this, destDir);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //*******************************************************************************************
                Cursor vahedInfosByName = prepare2.searchFromDatabase(etVahedName.getText().toString(),1);  // 1 --> VahedName base
                vahedInfos_ByName = vahedInfosByName;
                int searchVahedCountByName = 0;
                                                                                                                  // 2 --> VahedType base
                Log.i("btnSearch","vahedInfosByName resultCount = " + Integer.toString(vahedInfosByName.getCount()));
                if(!etVahedName.getText().toString().equals("")) {
                    llSearchByName.setVisibility(View.VISIBLE);
                    tvResultCountByName.setText(" نتیجه: " + Integer.toString(vahedInfosByName.getCount()) + " مورد ");
                    ListDetails listDetails1 = new ListDetails();
                    listDetails1.setList(vahedInfosByName);
                    ArrayList<Model> localModels = listDetails1.getList();
                    MovieAdapter localMovieAdapter = new MovieAdapter(appContext, localModels);
                    try {
                        recyclerView1.setAdapter(localMovieAdapter);
                    } catch (Exception e) {
                        Log.i("btnSearch", e.toString());
                    }

                    searchVahedCountByName = vahedInfosByName.getCount();
                    if(searchVahedCountByName>0)
                        recyclerView1.setVisibility(View.VISIBLE);
                }
                else {
                    llSearchByName.setVisibility(View.GONE);
                    recyclerView1.setVisibility(View.GONE);

                }
                //Log.i("btnSearch","movieAdapter.getItemCount() = " + Integer.toString(localMovieAdapter.getItemCount()));


                final Cursor vahedInfosBySubject = prepare2.searchFromDatabase(etVahedSubject.getText().toString(),2);  // 1 --> VahedName base
                vahedInfos_BySubject = vahedInfosBySubject;
                int searchVahedCountBySubject = 0;                                                              // 2 --> VahedType base
                Log.i("btnSearch","vahedInfosBySubject resultCount = " + Integer.toString(vahedInfosBySubject.getCount()));
                if(!etVahedSubject.getText().toString().equals("")) {
                    llSearchBySubject.setVisibility(View.VISIBLE);

                    tvResultCountBySubject.setText(" نتیجه: " + Integer.toString(vahedInfosBySubject.getCount()) + " مورد ");
                    ListDetails listDetails2 = new ListDetails();
                    listDetails2.setList(vahedInfosBySubject);
                    ArrayList<Model> localModels2 = listDetails2.getList();
                    MovieAdapter localMovieAdapter2 = new MovieAdapter(appContext, localModels2);
                    try {
                        recyclerView2.setAdapter(localMovieAdapter2);
                    } catch (Exception e) {
                        Log.i("btnSearch", e.toString());
                    }

                    searchVahedCountBySubject = vahedInfosBySubject.getCount();
                    if(searchVahedCountBySubject>0)
                        recyclerView2.setVisibility(View.VISIBLE);
                    else
                        recyclerView2.setVisibility(View.GONE);

                }
                else {
                    llSearchBySubject.setVisibility(View.GONE);
                    recyclerView2.setVisibility(View.GONE);
                }
                //Log.i("btnSearch","movieAdapter2.getItemCount() = " + Integer.toString(localMovieAdapter2.getItemCount()));
                //Log.i("btnSearch","movieAdapter2.getItemCount() = " + Integer.toString(localMovieAdapter2.getItemCount()));

                if( etVahedName.getText().toString().equals("") && etVahedSubject.getText().toString().equals("") ) {
                    //recyclerView1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "اگه بخوای جایی رو برات پیدا کنم، باید چیزی اون بالا بنویسی ", Toast.LENGTH_LONG).show();
                }
                else{

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView1.getLayoutParams();
                    params.height = 300;
                    recyclerView1.setLayoutParams(params);
                    //recyclerView1.setVisibility(View.VISIBLE);

                    LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) recyclerView2.getLayoutParams();
                    params2.height = 300;
                    recyclerView2.setLayoutParams(params2);
                    //recyclerView1.setVisibility(View.VISIBLE);
                }
                //preCategoryCount = searchVahedCount;
            }
//*******************************************************************************************
        });

        /*vahedInfos_ByName.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        vahedInfos.moveToPosition(position);
                        String strlat = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_Latitude));
                        String strlng = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_longitude));
                        String UnitName = vahedInfos.getString(vahedInfos.getColumnIndex(LocationsDB.FIELD_UnitName));
                        goToVahedOnMap(Double.parseDouble(strlat),Double.parseDouble(strlng),UnitName);
                    }
                })
        );
*/
        recyclerView1.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gMap.clear();
                        vahedInfos_ByName.moveToPosition(position);
                        String strlat = vahedInfos_ByName.getString(vahedInfos_ByName.getColumnIndex(LocationsDB.FIELD_Latitude));
                        String strlng = vahedInfos_ByName.getString(vahedInfos_ByName.getColumnIndex(LocationsDB.FIELD_longitude));
                        String UnitName = vahedInfos_ByName.getString(vahedInfos_ByName.getColumnIndex(LocationsDB.FIELD_UnitName));
                        goToVahedOnMap(Double.parseDouble(strlat),Double.parseDouble(strlng),UnitName);
                        dialog.dismiss();

                    }
                })
        );
        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gMap.clear();
                        vahedInfos_BySubject.moveToPosition(position);
                        String strlat = vahedInfos_BySubject.getString(vahedInfos_BySubject.getColumnIndex(LocationsDB.FIELD_Latitude));
                        String strlng = vahedInfos_BySubject.getString(vahedInfos_BySubject.getColumnIndex(LocationsDB.FIELD_longitude));
                        String UnitName = vahedInfos_BySubject.getString(vahedInfos_BySubject.getColumnIndex(LocationsDB.FIELD_UnitName));
                        goToVahedOnMap(Double.parseDouble(strlat),Double.parseDouble(strlng),UnitName);
                        dialog.dismiss();

                    }
                })
        );

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              dialog.dismiss();

            }
        });

        return dialog;
    }


    final ArrayList<String> labels = new ArrayList<>();          // to get sub activity name
    final android.app.FragmentManager fragmentManager = getFragmentManager();
    final SubVahedDialogFragment subVahedDialogFragment = new SubVahedDialogFragment();

//    String destDir = getFilesDir().getParentFile() + "/databases";

    public ArrayList<VahedSequence> prepareSubVahed(int position) {
        Log.i("VahedSequenceData","before vahedSequenceDetails tarif");
        VahedSequenceDetails vahedSequenceDetails = new VahedSequenceDetails();
        Log.i("VahedSequenceData","after vahedSequenceDetails tarif");

        final LocationsDB prepare = new LocationsDB(this, destDir);
        selectCategory(position-2);
        final Cursor subVahedCursor;
        int subCategory = position;
        subActivityID.clear();
        labels.clear();
        subVahedCursor = prepare.subVahedTypeExtraction(subCategory);  // Extract Vaheds Type///  1 --> mainVahed / else -->subVahed

        vahedSequenceDetails.setList(subVahedCursor);

        ArrayList<VahedSequence> vahedSequenceArrayList = vahedSequenceDetails.getList();
        Log.i("VahedSequenceData","Num = " + Integer.toString(vahedSequenceArrayList.get(0).getNum()));

        return vahedSequenceArrayList;

    }


}

