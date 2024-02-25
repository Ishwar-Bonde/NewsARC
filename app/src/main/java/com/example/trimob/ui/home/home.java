package com.example.trimob.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.trimob.Articles;
import com.example.trimob.NewsModal;
import com.example.trimob.NewsRVAdapter;
import com.example.trimob.R;
import com.example.trimob.RetrofitAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class home extends Fragment {

    private RecyclerView newsRV,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private NewsRVAdapter newsRVAdapter;

    private HomeViewModel mViewModel;

    public static home newInstance() {
        return new home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_home2, container, false);

        newsRV = root.findViewById(R.id.idRVNews);
        loadingPB = root.findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,requireContext());
        newsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsRV.setAdapter(newsRVAdapter);

        getNews("");
        newsRVAdapter.notifyDataSetChanged();

        return root;
    }

    private void getNews(String query){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String url;
        String BASE_URL = "https://newsapi.org/v2/";
        if (query.isEmpty()) {
            // If no query is entered, fetch top headlines
            url = "https://newsapi.org/v2/top-headlines?country=in&category=general&apiKey=8c4e78e7541d45be95fb3a8b6e93c48a";
        } else {
            // If a query is entered, perform a search
            url = "https://newsapi.org/v2/everything?q=" +query+ "&sortBy=publishedAt&language=en&apikey=8c4e78e7541d45be95fb3a8b6e93c48a";
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        call = retrofitAPI.getAllNews(url);
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {

                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles>articles = newsModal.getArticles();
                for(int i=0 ;i<articles.size();i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));

                }
                newsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(requireContext(),"Failed to get News",Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.home_page, menu);

        MenuItem searchItem = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search News Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the search with the specified query
                getNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change if needed
                getNews(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }



}