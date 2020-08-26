package com.eclairios.controlespotter.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import static com.eclairios.controlespotter.Activities.MainActivity.prefs;

import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private GoogleMap mMap;
    private ArrayList<LatLng> latlngs = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.hotel_map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean darkmode_switch = prefs.getBoolean("darkmode_switch",false);
        if (darkmode_switch){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
        }

//        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//
//        String method = "near_place";
//        String read_category = null;
//        try {
//            read_category = new Background(getActivity())
//                    .execute(method,String.valueOf(latitude),String.valueOf(longitude),"30",android_id).get();
//
//            Log.e("responsedataback", "near_place: --> " + read_category);
//
//
//        } catch (ExecutionException | InterruptedException e) {
//
//            e.printStackTrace();
//        }

        // Add a marker in Sydney and move the camera
        latlngs.add(new LatLng(-34, 151));
        latlngs.add(new LatLng(33.565109, 73.016914));
        latlngs.add(new LatLng(33.601921, 73.038078));
        latlngs.add(new LatLng(33.601921, 73.038078));

        for (int i=0; i<latlngs.size();i++){
            Log.e("ParseGeoPoint", "ParseGeoPoint: "+latlngs.get(i) );
            mMap.addMarker(new MarkerOptions().position(latlngs.get(i)).title("")
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.camera)));

        }

//        mMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(25), 5000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngs.get(0)));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            public void onMapClick(LatLng point){
                Toast.makeText(getContext(),
                        point.latitude + ", " + point.longitude,
                        Toast.LENGTH_SHORT).show();
                Log.e("getmaplatlngher", "onMapClick: "+point.latitude + ", " + point.longitude );
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth()
                ,vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}