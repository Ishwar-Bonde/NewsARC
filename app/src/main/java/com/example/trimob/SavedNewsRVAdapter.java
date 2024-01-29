package com.example.trimob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedNewsRVAdapter extends RecyclerView.Adapter<SavedNewsRVAdapter.ViewHolder> {
    private ArrayList<Articles> savedArticlesList;
    private Context context;

    public SavedNewsRVAdapter(ArrayList<Articles> savedArticlesList, Context context) {
        this.savedArticlesList = savedArticlesList != null ? savedArticlesList : new ArrayList<>();
        this.context = context;
    }

    public void setSavedArticlesList(ArrayList<Articles> savedArticlesList) {
        this.savedArticlesList = savedArticlesList != null ? savedArticlesList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Articles savedArticle = savedArticlesList.get(position);

        holder.savedTitleTV.setText(savedArticle.getTitle());

        if (savedArticle.getUrlToImage() != null && !savedArticle.getUrlToImage().isEmpty()) {
            Picasso.get().load(savedArticle.getUrlToImage()).into(holder.savedNewsIV);
        } else {
            holder.savedNewsIV.setImageResource(R.drawable.image_slash);
        }
    }

    @Override
    public int getItemCount() {
        return savedArticlesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView savedTitleTV;
        private ImageView savedNewsIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savedTitleTV = itemView.findViewById(R.id.savedTitleTextView);
            savedNewsIV = itemView.findViewById(R.id.savedNewsImageView);
        }
    }
}
