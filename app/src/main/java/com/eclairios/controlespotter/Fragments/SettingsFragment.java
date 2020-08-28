package com.eclairios.controlespotter.Fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eclairios.controlespotter.Adapters.ShowCatAdapter;
import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForCategory;
import com.eclairios.controlespotter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.eclairios.controlespotter.Activities.MainActivity.prefs;

public class SettingsFragment extends Fragment {

    private View view;
    SwitchCompat dark_mode_switch,notification_swithc;
    private SeekBar seekBar_setting, sekBarDur;
    private TextView tv_seek_setting, tvSekDur;
    private RecyclerView rvSetting;
    private ArrayList<ModelForCategory> dropdownarraylist = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_notifications, container, false);

        init();
        listen();
        seekbarlisten();

        return view;
    }

    private void init() {
        dark_mode_switch = view.findViewById(R.id.dark_mode_switch);
        notification_swithc = view.findViewById(R.id.notification_swithc);
        seekBar_setting = view.findViewById(R.id.seekBar_setting);
        tv_seek_setting = view.findViewById(R.id.tv_seek_setting);

        int checkseek = prefs.getInt("seekbar_value",400);
        boolean noti_int = prefs.getBoolean("noti",false);
        notification_swithc.setChecked(noti_int);
        seekBar_setting.setProgress(checkseek);
        tv_seek_setting.setText(String.valueOf(checkseek)+" meters");

        boolean darkmode_switch = prefs.getBoolean("darkmode_switch",false);
        Log.e("doijfalksdjf", "init: "+darkmode_switch);
        if (darkmode_switch){
            dark_mode_switch.setChecked(darkmode_switch);
        }

        sekBarDur = view.findViewById(R.id.seekBar_dur_setting);
        tvSekDur = view.findViewById(R.id.tv_seek_dur_setting);
        rvSetting = view.findViewById(R.id.rv_setting);
        int durSeek = prefs.getInt("durValue", 30);
        sekBarDur.setProgress(durSeek);
        tvSekDur.setText(String.valueOf(durSeek) + "sec.");

        if (notification_swithc.isChecked()) {
            rvSetting.setVisibility(View.VISIBLE);
            getCategory();
        }
        notification_swithc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    prefs.edit().putBoolean("noti", true).apply();
                    rvSetting.setVisibility(View.VISIBLE);
                    getCategory();
                } else {
                    prefs.edit().putBoolean("noti", false).apply();
                    rvSetting.setVisibility(View.GONE);
                }
            }
        });
    }

    private void listen(){
        dark_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("darkmode_switch",b).apply();
            }
        });
    }

    private void seekbarlisten(){
        seekBar_setting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tv_seek_setting.setText(String.valueOf(progress)+" meters");
                prefs.edit().putInt("seekbar_value",progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sekBarDur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvSekDur.setText(String.valueOf(i) + "sec.");
                prefs.edit().putInt("durValue", i).apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void getCategory() {
        String method_category = "read_category";
        String read_category = null;
        try {
            read_category = new Background(getActivity())
                    .execute(method_category).get();
            Log.e("responsedataback", "read_category: --> " + read_category);
            JSONArray jsonArray = new JSONArray(read_category);
            dropdownarraylist.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ModelForCategory modelForCategory = new ModelForCategory();
                modelForCategory.setCategoryId(object.getString("id"));
                modelForCategory.setCategoryname(object.getString("categorayname"));
                modelForCategory.setCategoryimage(object.getString("categoryimage"));
                dropdownarraylist.add(modelForCategory);
            }
            ShowCatAdapter mAdapter = new ShowCatAdapter(getActivity(), dropdownarraylist);
            rvSetting.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            rvSetting.setAdapter(mAdapter);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
}