package com.example.trimob;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trimob.saveddata.addsavednews;
import com.example.trimob.saveddata.savednews;
import com.squareup.picasso.Picasso;

import java.util.List;

public class savedpagecustAdapter extends RecyclerView.Adapter<savedpagecustAdapter.viewHolder> {
    List<savednews> sn;
    Context context;
private addsavednews asn;
    public savedpagecustAdapter( Context context,addsavednews a) {
        this.sn = a.getAllSavedNews();
        this.context = context;
        this.asn=a;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.saved_news_box, parent, false);
        viewHolder VH = new viewHolder(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.titlebox.setText(this.sn.get(position).newsTitle);
        Uri imgurl = Uri.parse(sn.get(position).newsImgUrl);
        Picasso.get().load(imgurl).into(holder.Imagebox);
        holder.rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("url", sn.get(position).newsUrl);
                context.startActivity(intent);
            }
        });
        holder.rev.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove Save News");
                builder.setMessage("Do you want to remove these Saved News");

                // Add the positive button
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result=asn.DeleteTheNews(sn.get(position).newsTitle);
                    if(result==1){
                        Toast.makeText(context, "Removed Successful", Toast.LENGTH_SHORT).show();
                        sn=asn.getAllSavedNews();
                        notifyDataSetChanged();

                    }
                    else{
                        Toast.makeText(context, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
                    }





                    }
                });

                // Add the negative button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "You click cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
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
            this.rev = v;


        }
    }
}
