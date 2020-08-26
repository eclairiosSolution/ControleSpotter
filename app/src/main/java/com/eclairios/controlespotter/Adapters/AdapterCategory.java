package com.eclairios.controlespotter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eclairios.controlespotter.ModelClasses.ModelForCategory;
import com.eclairios.controlespotter.R;

import java.util.ArrayList;

public class AdapterCategory extends ArrayAdapter<ModelForCategory> {

    public AdapterCategory(Context context, ArrayList<ModelForCategory> countryList) {
        super(context, 0, countryList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.customlist_category, parent, false
            );
        }
        ImageView category_image = convertView.findViewById(R.id.category_image);
        TextView category_name = convertView.findViewById(R.id.category_name);
        ModelForCategory currentItem = getItem(position);
        if (currentItem != null) {
            Log.e("dkfjfdksdsf", "initView: "+currentItem.getCategoryimage() );
            Glide.with(getContext()).load("http://localhost/myplacesapi/category/uploads/"+currentItem.getCategoryimage()).centerCrop().into(category_image);
            category_name.setText(currentItem.getCategoryname());
        }
        return convertView;
    }
}
