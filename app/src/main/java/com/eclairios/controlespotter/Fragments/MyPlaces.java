package com.eclairios.controlespotter.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eclairios.controlespotter.Activities.SelectLocationOnMap_Activity;
import com.eclairios.controlespotter.Adapters.AdapterCategory;
import com.eclairios.controlespotter.Adapters.PlaceAdapter;
import com.eclairios.controlespotter.Adapters.SheetAdapter;
import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForCategory;
import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class MyPlaces extends Fragment {

    private View view;
    private FloatingActionButton floationaction_btn;
    private RecyclerView recycler_myplace;
    private ProgressDialog progressDialog;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();
    private ArrayList<ModelForCategory> dropdownarraylist = new ArrayList<>();
    private PlaceAdapter adapter;
    private double latitudefrommap,longitudefrommap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_places, container, false);

        Log.e("lekfjlkadsjfkla", "onCreate: myplace" );
        init();
        listen();

        return view;
    }

    private void init() {
        floationaction_btn = view.findViewById(R.id.floationaction_btn);
        recycler_myplace = view.findViewById(R.id.recycler_myplace);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_myplace.setLayoutManager(layoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        readdata();
    }

    private void readdata() {

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        String method_category = "read_category";
        String read_category = null;
        try {
            read_category = new Background(getActivity())
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


        } catch (ExecutionException | InterruptedException | JSONException e) {

            e.printStackTrace();
        }


        String method = "read_place";
        String read_place = null;
        try {
            read_place = new Background(getActivity())
                    .execute(method, android_id).get();

            Log.e("responsedataback", "read_place: --> " + read_place);


                if (read_place!=null) {
                    arrayList.clear();
                    JSONArray jsonArray = new JSONArray(read_place);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelForMarker model = new ModelForMarker();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        model.setId(jsonObject.getString("id"));
                        model.setUserid(jsonObject.getString("userid"));
                        Log.e("dfjadklfjakf", "readdata: " + jsonObject.getString("radius"));
                        model.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                        model.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                        model.setRadius(jsonObject.getString("radius"));
                        model.setAddress(jsonObject.getString("address"));
                        model.setName(jsonObject.getString("name"));
                        model.setCategory(jsonObject.getString("categoryid"));

                        arrayList.add(model);

                        PlaceAdapter adapter = new PlaceAdapter(getActivity(), arrayList);
                        recycler_myplace.setAdapter(adapter);


//                        adapter.notifyDataSetChanged();
                    }
                }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.e("geterrormessage", "readdata: "+e.getMessage() );
            e.printStackTrace();
        }
    }

    private void listen() {
        floationaction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View rview = getLayoutInflater().inflate(R.layout.dialogbox_add_place, null);
                builder.setCancelable(false);
                builder.setView(rview);

                final AlertDialog dialog = builder.create();

                final Spinner spinner = rview.findViewById(R.id.spinner);
                final EditText et_name_place = rview.findViewById(R.id.et_name_place);
                final EditText et_address_place = rview.findViewById(R.id.et_address_place);
                final TextView tv_latitude_place = rview.findViewById(R.id.tv_latitude_place);
                final TextView tv_longitude_place = rview.findViewById(R.id.tv_longitude_place);
                final SeekBar seekBar = rview.findViewById(R.id.seekBar);
                final TextView tv_seek = rview.findViewById(R.id.tv_seek);
                TextView marklocation = rview.findViewById(R.id.marklocation);

                AdapterCategory mAdapter = new AdapterCategory(getActivity(), dropdownarraylist);
                spinner.setAdapter(mAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ModelForCategory clickedItem = (ModelForCategory) parent.getItemAtPosition(position);
                        String clickedCountryName = clickedItem.getCategoryId();
                        Toast.makeText(getActivity(), clickedCountryName + " selected", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


                BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {


                         latitudefrommap = intent.getDoubleExtra("lat",0);
                         longitudefrommap  = intent.getDoubleExtra("lng",0);

                        tv_latitude_place.setText(String.valueOf(latitudefrommap));
                        tv_longitude_place.setText(String.valueOf(longitudefrommap));
                    }
                };
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("message_subject_intent"));

//                if (!Places.isInitialized()) {
//                    Places.initialize(getActivity(), "AIzaSyBfqWw4Q5NY0qeUiiQN69CpcxY5sJns45k");
//                }


// Initialize the AutocompleteSupportFragment.

//                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                        getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//
//                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//
//                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                    @Override
//                    public void onPlaceSelected(Place place) {
//                        // TODO: Get info about the selected place.
//                        Log.i("dkfdkfn", "Place: " + place.getName() + ", " + place.getId());
//                    }
//
//                    @Override
//                    public void onError(Status status) {
//                        // TODO: Handle the error.
//                        Log.i("fdklfjdlkjf", "An error occurred: " + status);
//                    }
//                });

//                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                        getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_add);
//
//                // Specify the types of place data to return.
//                if (autocompleteFragment != null) {
//                    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//                }
//
//                // Set up a PlaceSelectionListener to handle the response.
//                if (autocompleteFragment != null) {
//                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                        @Override
//                        public void onPlaceSelected(@NotNull Place place) {
//                            // TODO: Get info about the selected place.
//                            Log.i("lkdjsjdfn", "Place: " + place.getName() + ", " + place.getId());
//                        }
//
//
//                        @Override
//                        public void onError(@NotNull Status status) {
//                            // TODO: Handle the error.
//                            Log.i("kldfjdklfj", "An error occurred: " + status);
//                        }
//                    });
//                }

                Button btn_cancel = rview.findViewById(R.id.btn_cancel);
                Button btn_yess = rview.findViewById(R.id.btn_yess);


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        tv_seek.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                marklocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(getActivity(),SelectLocationOnMap_Activity.class));

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_yess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String stringname = et_name_place.getText().toString();
                        String stringaddress = et_address_place.getText().toString();
                        String stringlat = tv_latitude_place.getText().toString();
                        String stringlng = tv_longitude_place.getText().toString();
                        String radius = String.valueOf(seekBar.getProgress());

                        if (stringname.isEmpty()) {
                            et_name_place.setError("Please enter name");
                        } else if (stringaddress.isEmpty()) {
                            et_address_place.setError("Please enter address");
                        } else if (stringlat.isEmpty()) {
                            tv_latitude_place.setError("Please enter latitude");
                        } else if (stringlng.isEmpty()) {
                            tv_longitude_place.setError("Please enter longitude");
                        } else if (radius.equals("0")) {
                            Toast.makeText(getActivity(), "Radius is 0!", Toast.LENGTH_SHORT).show();
                        } else if (spinner.getSelectedItem().toString().equals("Select Category")) {
                            Toast.makeText(getActivity(), "Please select category", Toast.LENGTH_SHORT).show();
                        } else {
                            String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID);

                            String method = "insert_place";
                            String insert_place = null;
                            try {
                                insert_place = new Background(getActivity())
                                        .execute(method, stringname, stringaddress, stringlat, stringlng,
                                                radius, android_id, spinner.getSelectedItem().toString()).get();

                                Log.e("responsedataback", "insert_place: --> " + insert_place);

                                JSONArray jsonArray = new JSONArray(insert_place);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("message").contains("New location added")) {

                                        dialog.dismiss();
                                        readdata();
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (ExecutionException | InterruptedException | JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounds);
                dialog.show();

            }
        });
    }


}