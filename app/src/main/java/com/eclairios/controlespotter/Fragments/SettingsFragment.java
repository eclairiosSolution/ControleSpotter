package com.eclairios.controlespotter.Fragments;

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
import androidx.fragment.app.Fragment;
import com.eclairios.controlespotter.R;
import static com.eclairios.controlespotter.Activities.MainActivity.prefs;

public class SettingsFragment extends Fragment {

    private View view;
    private Switch dark_mode_switch,notification_swithc;
    private SeekBar seekBar_setting;
    private TextView tv_seek_setting;

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
        seekBar_setting.setProgress(checkseek);
        tv_seek_setting.setText(String.valueOf(checkseek));

        boolean darkmode_switch = prefs.getBoolean("darkmode_switch",false);
        Log.e("doijfalksdjf", "init: "+darkmode_switch);
        if (darkmode_switch){
            dark_mode_switch.setChecked(darkmode_switch);
        }
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
                tv_seek_setting.setText(String.valueOf(progress));
                prefs.edit().putInt("seekbar_value",progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}