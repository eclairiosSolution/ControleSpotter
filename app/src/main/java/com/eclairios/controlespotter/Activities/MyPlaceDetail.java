package com.eclairios.controlespotter.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eclairios.controlespotter.Adapters.AdapterCategory;
import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForCategory;
import com.eclairios.controlespotter.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MyPlaceDetail extends AppCompatActivity {

    private Button btn_update_place;
    private EditText et_name_place_update, et_address_place_update;
    private SeekBar seekBar_update;
    private TextView tv_seek_update, marklocation_update, tv_latitude_place_update, tv_longitude_place_update;
    private Spinner spinner_update;
    private ArrayList<ModelForCategory> dropdownarraylist = new ArrayList<>();
    private String intent_id, click_categoryId,userstatus;
    private LinearLayout linear_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place_detail);

        init();
        intentdata();
        seekbarchange();
        listen();

        if (!Places.isInitialized()) {
            Places.initialize(MyPlaceDetail.this, "AIzaSyBfqWw4Q5NY0qeUiiQN69CpcxY5sJns45k");
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Address of Event");
// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
//                AddressComponents addressComponents=place.getAddressComponents();
//                for (int i=0;i<addressComponents.asList().size();i++){
//                    address+=addressComponents.asList().get(i).getName();
//                }
                String address = place.getAddress();
                try {
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                } catch (NullPointerException n) {
                    n.printStackTrace();
                }
                Log.i("dkjfhdkfha", "Place: " + place.getName() + " :: " + place.getId() + " :: " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dkjfhkjdfh", "An error occurred: " + status);
            }
        });

//        if (!Places.isInitialized()) {
//            Places.initialize(MyPlaceDetail.this, "AIzaSyBfqWw4Q5NY0qeUiiQN69CpcxY5sJns45k");
//        }
//
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        // Specify the types of place data to return.
//        if (autocompleteFragment != null) {
//            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        }
//
//        // Set up a PlaceSelectionListener to handle the response.
//        if (autocompleteFragment != null) {
//            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(@NotNull Place place) {
//                    // TODO: Get info about the selected place.
//                    Log.i("lkdjsjdfn", "Place: " + place.getName() + ", " + place.getId());
//                }
//
//
//                @Override
//                public void onError(@NotNull Status status) {
//                    // TODO: Handle the error.
//                    Log.i("kldfjdklfj", "An error occurred: " + status);
//                }
//            });
//        }
    }

    private void init() {
        btn_update_place = findViewById(R.id.btn_update_place);
        et_name_place_update = findViewById(R.id.et_name_place_update);
        tv_latitude_place_update = findViewById(R.id.tv_latitude_place_update);
        tv_longitude_place_update = findViewById(R.id.tv_longitude_place_update);
        spinner_update = findViewById(R.id.spinner_update);
        seekBar_update = findViewById(R.id.seekBar_update);
        tv_seek_update = findViewById(R.id.tv_seek_update);
        marklocation_update = findViewById(R.id.marklocation_update);
        linear_place = findViewById(R.id.linear_place);

    }

    private void getcategory(String in_category) {
        String method_category = "read_category";
        String read_category = null;
        try {
            read_category = new Background(MyPlaceDetail.this)
                    .execute(method_category).get();

            Log.e("responsedataback", "read_category: --> " + read_category);
            JSONArray jsonArray = new JSONArray(read_category);

            dropdownarraylist.clear();
            ModelForCategory modelrCategory = new ModelForCategory();
            modelrCategory.setCategoryname("Select Category");
            dropdownarraylist.add(modelrCategory);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ModelForCategory modelForCategory = new ModelForCategory();

                modelForCategory.setCategoryId(object.getString("id"));
                modelForCategory.setCategoryname(object.getString("categorayname"));
                modelForCategory.setCategoryimage(object.getString("categoryimage"));
                dropdownarraylist.add(modelForCategory);
            }

//            for (int i = 0; i < spinner_update.getCount(); i++) {
//                if (spinner_update.getItemAtPosition(i).equals(in_category)) {
//                    spinner_update.setSelection(i);
//                    break;
//                }
//            }
            AdapterCategory mAdapter = new AdapterCategory(MyPlaceDetail.this, dropdownarraylist);
            spinner_update.setAdapter(mAdapter);
            spinner_update.setSelection(Integer.parseInt(in_category));

            Log.e("dfkldjakfc", "getcategory: " + in_category);


            spinner_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ModelForCategory clickedItem = (ModelForCategory) parent.getItemAtPosition(position);
                    click_categoryId = clickedItem.getCategoryId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        } catch (ExecutionException | InterruptedException | JSONException e) {

            e.printStackTrace();
        }
    }

    private void intentdata() {
        intent_id = getIntent().getStringExtra("id");
        userstatus = getIntent().getStringExtra("status");
        String intent_name = getIntent().getStringExtra("name");
        String intent_address = getIntent().getStringExtra("address");
        String intent_radius = getIntent().getStringExtra("radius");
        String intent_category = getIntent().getStringExtra("category");
        Double intent_lat = getIntent().getDoubleExtra("latitude", 0);
        Double intent_lng = getIntent().getDoubleExtra("longitude", 0);

        getcategory(intent_category);

        et_name_place_update.setText(intent_name);
        tv_latitude_place_update.setText(String.valueOf(intent_lat));
        tv_longitude_place_update.setText(String.valueOf(intent_lng));
        seekBar_update.setProgress(Integer.parseInt(String.valueOf(intent_radius)));
        tv_seek_update.setText(String.valueOf(intent_radius) + " meter");

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent != null) {

                    double latitudefrommap = intent.getDoubleExtra("lat", 0);
                    double longitudefrommap = intent.getDoubleExtra("lng", 0);

                    tv_latitude_place_update.setText(String.valueOf(latitudefrommap));
                    tv_longitude_place_update.setText(String.valueOf(longitudefrommap));
                }
            }
        };
        LocalBroadcastManager.getInstance(MyPlaceDetail.this).registerReceiver(mMessageReceiver, new IntentFilter("message_subject_intent"));
    }

    private void seekbarchange() {
        seekBar_update.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tv_seek_update.setText(String.valueOf(progress) + " meter");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void listen() {
        btn_update_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPlaceDetail.this);
                builder.setTitle("Update!");
                builder.setMessage("Do you want update Data");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String stringname = et_name_place_update.getText().toString();
                        String stringaddress = et_address_place_update.getText().toString();
                        String stringlat = tv_latitude_place_update.getText().toString();
                        String stringlng = tv_longitude_place_update.getText().toString();
                        String radius = String.valueOf(seekBar_update.getProgress());

                        if (stringname.isEmpty()) {
                            et_name_place_update.setError("Please enter name");
//                        } else if (stringaddress.isEmpty()) {
//                            et_address_place_update.setError("Please enter address");
                        } else if (stringlat.isEmpty()) {
                            tv_latitude_place_update.setError("Please enter latitude");
                        } else if (stringlng.isEmpty()) {
                            tv_longitude_place_update.setError("Please enter longitude");
                        } else if (radius.equals("0")) {
                            Toast.makeText(MyPlaceDetail.this, "Radius is 0!", Toast.LENGTH_SHORT).show();
                        } else if (click_categoryId == null) {
                            Toast.makeText(MyPlaceDetail.this, "Please select category", Toast.LENGTH_SHORT).show();
                        } else {

                            String android_id = Settings.Secure.getString(MyPlaceDetail.this.getContentResolver(),
                                    Settings.Secure.ANDROID_ID);

                            String method = "update_place";
                            String update_place = null;
                            try {
                                update_place = new Background(MyPlaceDetail.this)
                                        .execute(method, stringname, "dummyaddress because place api not working", stringlat, stringlng,
                                                radius, android_id, click_categoryId, intent_id,userstatus).get();

                                Log.e("responsedataback", "insert_place: --> " + update_place);

                                JSONArray jsonArray = new JSONArray(update_place);
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                                    if (jsonObject.getString("message").contains("Data is updated successfuly")) {
                                        onBackPressed();
                                        Toast.makeText(MyPlaceDetail.this, "Data is updated successfuly", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (ExecutionException | InterruptedException | JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                builder.create().show();
            }
        });


        marklocation_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPlaceDetail.this, SelectLocationOnMap_Activity.class));
            }
        });
    }

}