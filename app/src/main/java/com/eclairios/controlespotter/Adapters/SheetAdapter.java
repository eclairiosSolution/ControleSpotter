package com.eclairios.controlespotter.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.myholder> {

    private Context context;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();

    public SheetAdapter(Context context, ArrayList<ModelForMarker> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlistbottomsheet, parent, false);
        return new myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {

        holder.textviewname.setText(arrayList.get(position).getName());
        holder.address.setText(arrayList.get(position).getAddress());

        String method_category = "read_category_id";
        String read_category = null;
        try {
            read_category = new Background(context)
                    .execute(method_category, arrayList.get(position).getCategory()).get();

            Log.e("responsedataback", "read_category_id: --> " + read_category);
            JSONArray jsonArray_category = new JSONArray(read_category);

            for (int k = 0; k < jsonArray_category.length(); k++) {
                Log.e("dkfhdskfjas", "onMapReady: " + jsonArray_category.getJSONObject(k).getString("categoryimage"));

                Glide.with(context)
                        .load("http://192.168.18.11/myplacesapi/category/uploads/"+jsonArray_category.getJSONObject(k).getString("categoryimage"))
                        .into(holder.iv_warning);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(arrayList.get(position).getRadius())<1000){
            holder.radius.setText(arrayList.get(position).getRadius()+" m");
        }else if (Integer.parseInt(arrayList.get(position).getRadius())>=1000){
            float dis = Float.parseFloat(arrayList.get(position).getRadius())/1000;
            holder.radius.setText(String.format("%.01f", dis)+" Km");
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myholder extends RecyclerView.ViewHolder {

        private TextView textviewname;
        private TextView address,radius;
        private ImageView iv_warning;

        public myholder(@NonNull View itemView) {
            super(itemView);

            textviewname = itemView.findViewById(R.id.textviewname);
            address = itemView.findViewById(R.id.tv_date);
            radius = itemView.findViewById(R.id.text_view_countiing);
            iv_warning = itemView.findViewById(R.id.iv_warning);
        }
    }
}
