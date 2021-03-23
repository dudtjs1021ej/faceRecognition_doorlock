package com.example.facerecognitionlock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    private ArrayList<Visitors> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<Visitors> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_visitors,parent,false);
        CustomViewHolder holder=new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getprofile())
             //   .error(R.drawable.ic_launcher_background)
                .into(holder.profile);
        holder.time.setText(arrayList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return (arrayList!=null ? arrayList.size():0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView time;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profile=itemView.findViewById(R.id.profile);
            this.time=itemView.findViewById(R.id.time);
        }
    }

}
