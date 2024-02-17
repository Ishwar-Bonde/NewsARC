package com.example.trimob;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trimob.saveddata.*;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class savedpagecustAdapter extends RecyclerView.Adapter<savedpagecustAdapter.viewHolder> {
    List<savednews> sn;
    Context context;

    public savedpagecustAdapter(List<savednews> sn, Context context) {
        this.sn = sn;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.savednewsdesign, parent, false);
        viewHolder VH = new viewHolder(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.titlebox.setText(this.sn.get(position).newsTitle);
        Uri imgurl= Uri.parse(sn.get(position).newsImgUrl);
        Picasso.get().load(imgurl).into(holder.Imagebox);
        holder.rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,NewsDetailsActivity.class);
                intent.putExtra("url",sn.get(position).newsUrl);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.sn.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView titlebox;
        ImageView Imagebox;
        View rev;

        public viewHolder(@NonNull View v) {
            super(v);
            this.titlebox = v.findViewById(R.id.titleBox);
            this.Imagebox = v.findViewById(R.id.imageBox);
            this.rev=v;


        }
    }
}
