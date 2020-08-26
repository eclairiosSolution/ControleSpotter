package com.eclairios.controlespotter.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eclairios.controlespotter.Activities.MyPlaceDetail;
import com.eclairios.controlespotter.BackgroundWorks.Background;
import com.eclairios.controlespotter.ModelClasses.ModelForMarker;
import com.eclairios.controlespotter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.viewholder> {

    private Context context;
    private ArrayList<ModelForMarker> arrayList = new ArrayList<>();

    public PlaceAdapter(Context context, ArrayList<ModelForMarker> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        Log.e("adapterconstructor", "PlaceAdapter: "+arrayList.size() );
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlistplace, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {

        holder.title_place.setText(arrayList.get(position).getName());
        holder.address_place.setText(arrayList.get(position).getAddress());
        holder.radius_place.setText(arrayList.get(position).getRadius()+" Km radius");

        holder.btn_delete_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                delete_btn_method(position);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MyPlaceDetail.class);
                intent.putExtra("id",arrayList.get(position).getId());
                intent.putExtra("name", arrayList.get(position).getName());
                intent.putExtra("address",arrayList.get(position).getAddress());
                intent.putExtra("latitude",arrayList.get(position).getLatitude());
                intent.putExtra("longitude",arrayList.get(position).getLongitude());
                intent.putExtra("radius",arrayList.get(position).getRadius());
                intent.putExtra("category",arrayList.get(position).getCategory());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {

        Log.e("sizeofarraylist", "getItemCount: "+arrayList.size() );
        return arrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        private TextView title_place,address_place,radius_place;
        private Button btn_delete_place;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            title_place = itemView.findViewById(R.id.title_place);
            address_place = itemView.findViewById(R.id.address_place);
            radius_place = itemView.findViewById(R.id.radius_place);
            btn_delete_place = itemView.findViewById(R.id.btn_delete_place);
        }
    }

    //      <<---------------------------------------------------------------------------------------------------->>
    //      <<------------------------------------------  Mehthods  ---------------------------------------------->>
    //      <<---------------------------------------------------------------------------------------------------->>


    private void delete_btn_method(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete!");
        builder.setMessage("Do you want to delete this record");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String method = "delete_place";
                String delete_place = null;
                try {
                    delete_place = new Background(context)
                            .execute(method, arrayList.get(position).getId(),arrayList.get(position).getUserid()).get();

                    Log.e("responsedataback", "insert_place: --> " + delete_place);

                    JSONArray jsonArray = new JSONArray(delete_place);
                    for (int j=0; i<jsonArray.length();j++){
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        Log.e("chekcdeletedata", "insert_place: --> came in loop");
                        if (jsonObject.getString("message").contains("Data deleted successfully")){
                            arrayList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                            Log.e("chekcdeletedata", "insert_place: --> came in if");
                        }
                    }

                } catch (ExecutionException | InterruptedException | JSONException e) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }
}
