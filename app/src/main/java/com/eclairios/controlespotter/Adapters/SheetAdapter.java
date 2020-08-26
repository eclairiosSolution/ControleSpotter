package com.eclairios.controlespotter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.R;

import java.util.ArrayList;

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
        holder.radius.setText(arrayList.get(position).getRadius()+" Km radius");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myholder extends RecyclerView.ViewHolder {

        private TextView textviewname;
        private TextView address,radius;

        public myholder(@NonNull View itemView) {
            super(itemView);

            textviewname = itemView.findViewById(R.id.textviewname);
            address = itemView.findViewById(R.id.tv_date);
            radius = itemView.findViewById(R.id.text_view_countiing);
        }
    }
}
