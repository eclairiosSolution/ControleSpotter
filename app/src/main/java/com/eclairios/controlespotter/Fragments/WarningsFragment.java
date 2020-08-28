package com.eclairios.controlespotter.Fragments;

import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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

    private static final String TAG = "WarningsFragment";
    private View view;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private RecyclerView recyclerview;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();
    private CurrentLocation current_location_class;
    private double longitude, latitude;
    private LatLng latLng;
    Handler callScheduleWork = new Handler();
    int durSeek;
    private TextToSpeech mTts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_dashboard, container, false);

         getcurrentlocation();

        mTts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTts.setLanguage(Locale.US);
                    Log.e(TAG, "onInit: Text speech Working");
                    boolean noti = prefs.getBoolean("noti", false);
                    if (noti) {
                        timerRunnable.run();
                    } else {
                        viewObj();
                    }
                } else {
                    Log.e(TAG, "onInit: Text speech found Error!");
                }
            }
        });
        durSeek = prefs.getInt("durValue", 30);

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

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: enter in timerRunnable");
            nearObjects();
            callScheduleWork.postDelayed(this, durSeek * 1000);
        }
    };
    private void viewObj() {
        arrayList.clear();
        Log.d(TAG, "nearObjects: count");
        getcurrentlocation();
//        this.linearLayoutBSheet = view.findViewById(R.id.bottomSheet);
//        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        recyclerview = view.findViewById(R.id.recycler_warning);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        Log.d(TAG, "nearObjects latitude: " + latitude);
        Log.d(TAG, "nearObjects longitude: " + longitude);
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
                SheetAdapter adapter = new SheetAdapter(getActivity(), arrayList);
                recyclerview.setAdapter(adapter);
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
    //    private void speak( final String msg) {
//
//        mTts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    mTts.setLanguage(Locale.ENGLISH);
//                    mTts.setSpeechRate(.5f);
//                    mTts.setPitch(.8f);
////                    mTts.setOnUtteranceProgressListener(mProgressListenerIntroduction);
//                    mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);
//
//
//                } else {
//                    Log.e(TAG, "onInit: error occurred");
//                }
//            }
//        });
//    }
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
            Log.e(TAG, "onCreate: " + longitude);
        } else {
            current_location_class.showSettingsAlert();
        }
    }
    private final UtteranceProgressListener mProgressListenerIntroduction = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        }
        @Override
        public void onDone(String utteranceId) {
        }
        @Override
        public void onError(String utteranceId) {
        }
    };
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


}