package com.eclairios.controlespotter.Fragments;

import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForCategory;
import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.Others.CurrentLocation;
import com.eclairios.controlespotter.R;
import com.eclairios.controlespotter.Adapters.SheetAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.eclairios.controlespotter.Activities.MainActivity.prefs;

public class WarningsFragment extends Fragment {

    private View view;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private RecyclerView recyclerview;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();
    private CurrentLocation current_location_class;
    private double longitude, latitude;
    private LatLng latLng;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_dashboard, container, false);

         getcurrentlocation();

//        this.linearLayoutBSheet = view.findViewById(R.id.bottomSheet);

//        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        recyclerview = view.findViewById(R.id.recycler_warning);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        int checkseek = prefs.getInt("seekbar_value",400);

        String method = "near_place";
        String read_category = null;
        try {
            read_category = new Background(getActivity())
                    .execute(method,String.valueOf(latitude),String.valueOf(longitude),String.valueOf(checkseek),android_id).get();

            Log.e("responsedataback", "near_place: --> " + read_category);

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

                SheetAdapter adapter = new SheetAdapter(getActivity(),arrayList);
                recyclerview.setAdapter(adapter);

            }


        } catch (ExecutionException | InterruptedException | JSONException e) {

            e.printStackTrace();
        }

//        SheetAdapter adapter = new SheetAdapter(getActivity(),arrayList);
//        recyclerview.setAdapter(adapter);


//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(View view, int newState) {
//                if(newState == BottomSheetBehavior.STATE_EXPANDED){
//
//                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
//
//                }
//            }
//
//            @Override
//            public void onSlide(View view, float v) {
//
//            }
//        });

        return view;
    }

    private void getcurrentlocation() {

        current_location_class = new CurrentLocation(getActivity());
        if (current_location_class.canGetLocation()) {

            longitude = current_location_class.getLongitude();
            latitude = current_location_class.getLatitude();
            latLng = new LatLng(latitude, longitude);
            Log.e("latit", "onCreate: " + longitude);
        } else {

            current_location_class.showSettingsAlert();
        }
    }




}