package com.example.trimob;
import  com.example.trimob.saveddata.*;
import com.example.trimob.saveddata.savednews;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.ViewHolder> {
    private ArrayList<Articles> articlesArrayList;
    private Context context;

    public NewsRVAdapter(ArrayList<Articles> articlesArrayList, Context context) {
        this.articlesArrayList = articlesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Articles articles = articlesArrayList.get(position);

        holder.titleTV.setText(articles.getTitle());

        // Check if the URL is not empty or null
        if (articles.getUrlToImage() != null && !articles.getUrlToImage().isEmpty()) {
            // If the image URL is available, load the image using Picasso
            holder.newsIV.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);

            Picasso.get().load(articles.getUrlToImage()).into(holder.newsIV, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.newsIV.setImageResource(R.drawable.no_image_svg);
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            // If the image URL is not available, set a specific vector asset
            holder.newsIV.setImageResource(R.drawable.no_image_svg);
            holder.progressBar.setVisibility(View.GONE);
        }

        // Set save indicator visibility based on the isSaved field
        if (articles.isSaved()) {
            holder.saveIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.saveIndicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NewsDetailsActivity.class);
                i.putExtra("url", articles.getUrl());
                context.startActivity(i);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSaveNewsAlertDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return articlesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTV;
        private ImageView newsIV;
        private ProgressBar progressBar;
        private ImageView saveIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.idTVNewsHeading);
            newsIV = itemView.findViewById(R.id.idIVNews);
            progressBar = itemView.findViewById(R.id.progress_bar);
            saveIndicator = itemView.findViewById(R.id.save_indicator);
        }
    }

    private void showSaveNewsAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save News");
        builder.setMessage("Do you want to save this news article?");

        // Add the positive button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addsavednews newsadd=new addsavednews(context);
                newsadd.insertingnews(new savednews(articlesArrayList.get(position).getTitle(),articlesArrayList.get(position).getUrlToImage(),articlesArrayList.get(position).getUrl(),articlesArrayList.get(position).getContent()));
            }
        });

        // Add the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setArticles(ArrayList<Articles> newArticlesList) {
        articlesArrayList.clear();
        articlesArrayList.addAll(newArticlesList);
        notifyDataSetChanged();
    }
}
