package com.eclairios.controlespotter.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import static com.eclairios.controlespotter.Activities.MainActivity.prefs;
import static com.eclairios.controlespotter.Others.Constants.imageurl;

import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.Others.CurrentLocation;
import com.eclairios.controlespotter.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koushikdutta.ion.Ion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private static final String TAG = "kdfnd";
    private View view;
    private GoogleMap mMap;
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private CurrentLocation current_location_class;
    private double longitude, latitude;
    private LatLng latLng;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();
    private ModelForMarker model;
    private View mapView;
    private FloatingActionButton buttonnavigation,buttonzoommap,buttonzoomoutmap;
    Marker userLocationMarker;
    Handler callScheduleWork = new Handler();
    int durSeek;
    private TextToSpeech mTts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        getcurrentlocation();

        //        mTts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    mTts.setLanguage(Locale.US);
//                    Log.e(TAG, "onInit: Text speech Working");
//                    boolean noti = prefs.getBoolean("noti", false);
//                    if (noti) {
//
//                        timerRunnable.run();
//                    } else {
//                        viewObj();
//                    }
//                } else {
//                    Log.e(TAG, "onInit: Text speech found Error!");
//                }
//            }
//        });
//
//        durSeek = prefs.getInt("durValue", 30);

        init();
        flationlisten();

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.hotel_map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        builder.setView(rview);

        final AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();

        if (window != null) {
            window.setGravity(Gravity.TOP);
            window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        }

        Button btn_cancel = rview.findViewById(R.id.btn_cancel);
        Button btn_yess = rview.findViewById(R.id.btn_yess);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_yess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounds);
        dialog.show();

        return view;
    }

    private void init() {

        buttonnavigation = view.findViewById(R.id.buttonnavigation);
        buttonzoommap = view.findViewById(R.id.buttonzoommap);
        buttonzoomoutmap = view.findViewById(R.id.buttonzoomoutmap);

    }

    private void flationlisten(){
        buttonnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng latLng = new LatLng(latitude,longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);

            }
        });

        buttonzoommap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        buttonzoomoutmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        float zoomLevel = 18.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        boolean darkmode_switch = prefs.getBoolean("darkmode_switch", false);
        if (darkmode_switch) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
        }

        mMap.setMyLocationEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_car));
//        markerOptions.rotation(location.getBearing());
        markerOptions.anchor((float) 0.5, (float) 0.5);
        userLocationMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        int checkseek = prefs.getInt("seekbar_value", 400);

        String method = "near_place";
        String read_current_places = null;
        try {
            read_current_places = new Background(getActivity())
                    .execute(method, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(checkseek), android_id).get();

            Log.e("responsedataback", "near_place: --> " + read_current_places);


            JSONArray jsonArray = new JSONArray(read_current_places);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model = new ModelForMarker();
                model.setId(jsonObject.getString("id"));
                model.setUserid(jsonObject.getString("userid"));
                model.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                model.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                model.setRadius(jsonObject.getString("radius"));
                model.setAddress(jsonObject.getString("address"));
                model.setName(jsonObject.getString("name"));
                model.setCategory(jsonObject.getString("categoryid"));
                model.setMilisecond(Long.parseLong(jsonObject.getString("milliseconds")));

                Log.e("slkerjiord", "onMapReady: " + jsonObject.getString("categoryid"));

                arrayList.add(model);
                latlngs.clear();
                try {
                    latlngs.add(new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String method_category = "read_category_id";
                String read_category = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        read_category = new Background(getActivity())
                                .execute(method_category, jsonObject.getString("categoryid")).get();
                    }

                    Log.e("responsedataback", "read_category_id: --> " + read_category);
                    JSONArray jsonArray_category = new JSONArray(read_category);

                    for (int k = 0; k < jsonArray_category.length(); k++) {
                        Log.e("dkfhdskfjas", "onMapReady: " + jsonArray_category.getJSONObject(k).getString("categoryimage"));


                        for (int j=0; j<latlngs.size();j++) {
                            ModelForMarker modell = arrayList.get(j);

                            Bitmap bmImg = null;
                            try {
                                bmImg = Ion.with(getActivity())
                                        .load(imageurl + jsonArray_category.getJSONObject(k).getString("categoryimage")).asBitmap().get();
                                Log.e("ParseGeoPoint", "ParseGeoPoint: " + latlngs.get(j));
                                mMap.addMarker(new MarkerOptions().position(latlngs.get(j)).title(model.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmImg)));
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth()
                , vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("my position").icon(bitmapDescriptorFromVector(getActivity(), R.drawable.mark));

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        Log.e("dfkljkalsdf", "onLocationChanged: "+location.getLatitude()+"         " +location.getLongitude() );
        mMap.addMarker(new MarkerOptions().position(latLng).title("")
                                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.mark)));


    }



    private void viewObj() {
        arrayList.clear();
        Log.d("TAG", "nearObjects: count");
        getcurrentlocation();
//        this.linearLayoutBSheet = view.findViewById(R.id.bottomSheet);
//        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
//        recyclerview = view.findViewById(R.id.recycler_warning);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerview.setLayoutManager(layoutManager);
//        Log.d(TAG, "nearObjects latitude: " + latitude);
//        Log.d(TAG, "nearObjects longitude: " + longitude);
        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        int checkseek = prefs.getInt("seekbar_value", 400);
        String method = "near_place";
        String read_category = null;
        try {
            read_category = new Background(getActivity())
                    .execute(method, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(checkseek), android_id).get();
            Log.d("responsedataback", "near_place: --> " + read_category);
            JSONArray jsonArray = new JSONArray(read_category);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ModelForMarker model = new ModelForMarker();
                model.setId(jsonObject.getString("id"));
                model.setUserid(jsonObject.getString("userid"));
                model.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                model.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                model.setRadius(jsonObject.getString("radius"));
                model.setAddress(jsonObject.getString("address"));
                model.setName(jsonObject.getString("name"));
                model.setCategory(jsonObject.getString("categoryid"));
                model.setMilisecond(Long.parseLong(jsonObject.getString("milliseconds")));
                arrayList.add(model);
//                SheetAdapter adapter = new SheetAdapter(getActivity(), arrayList);
//                recyclerview.setAdapter(adapter);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void nearObjects() {
        boolean camera = prefs.getBoolean("Camera", false);
        boolean shop = prefs.getBoolean("Shop", false);
        boolean police = prefs.getBoolean("Police Post", false);
        boolean gas = prefs.getBoolean("Gas Station", false);
        viewObj();
//            ArrayList<String> distList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            ModelForMarker m = arrayList.get(i);
            double test = distance(latitude, longitude, m.getLatitude(), m.getLongitude());
            String dist = String.format("%.02f", test);
            Log.d(TAG, "nearObjects dis: " + dist);
            double dis = Double.parseDouble(dist);
            Log.d(TAG, "nearObjects: "+getCatName(m.getCategory()));
            if (camera && getCatName(m.getCategory()).equals("Camera")) {
                if (dis < 2 && dis >= 1) {
                    final String msg = getCatName(m.getCategory()) + " nearby in " + dis + "kilometers";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else if (dis < 1) {
                    int d = (int) (dis * 1000);
                    final String msg = getCatName(m.getCategory()) + " nearby in " + d + "meters";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }else if(gas &&getCatName(m.getCategory()).equals("Gas Station")){
                if (dis < 2 && dis >= 1) {
                    final String msg = getCatName(m.getCategory()) + " nearby in " + dis + "kilometers";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else if (dis < 1) {
                    int d = (int) (dis * 1000);
                    final String msg = getCatName(m.getCategory()) + " nearby in " + d + "meters";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }else if(shop &&getCatName(m.getCategory()).equals("Shop")){
                if (dis < 2 && dis >= 1) {
                    final String msg = getCatName(m.getCategory()) + " nearby in " + dis + "kilometers";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else if (dis < 1) {
                    int d = (int) (dis * 1000);
                    final String msg = getCatName(m.getCategory()) + " nearby in " + d + "meters";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }else if(police &&getCatName(m.getCategory()).equals("Police Post")){
                if (dis < 2 && dis >= 1) {
                    final String msg = getCatName(m.getCategory()) + " nearby in " + dis + "kilometers";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else if (dis < 1) {
                    int d = (int) (dis * 1000);
                    final String msg = getCatName(m.getCategory()) + " nearby in " + d + "meters";
                    if (mTts.isSpeaking()) {
                        mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        mTts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        }
//        callScheduleWork.removeCallbacks(timerRunnable);
//        manageTimer();
    }
    private String getCatName(String id) {
        String method_category = "read_category_id";
        String read_category = null;
        String categoryName = null;
        try {
            read_category = new Background(getActivity())
                    .execute(method_category, id).get();
            Log.e("responsedataback", "read_category_id: --> " + read_category);
            JSONArray jsonArray_category = new JSONArray(read_category);
            for (int k = 0; k < jsonArray_category.length(); k++) {
                categoryName = jsonArray_category.getJSONObject(k).getString("categorayname");
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return categoryName;
    }
    private void getcurrentlocation() {
        current_location_class = new CurrentLocation(getActivity());
        if (current_location_class.canGetLocation()) {
            longitude = current_location_class.getLongitude();
            latitude = current_location_class.getLatitude();
            latLng = new LatLng(latitude, longitude);
            Log.e("TAG", "onCreate: " + longitude);
        } else {
            current_location_class.showSettingsAlert();
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    @Override
    public void onDestroy() {
        callScheduleWork.removeCallbacks(timerRunnable);
        super.onDestroy();
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: enter in timerRunnable");
            nearObjects();
            callScheduleWork.postDelayed(this, durSeek * 1000);
        }
    };
}