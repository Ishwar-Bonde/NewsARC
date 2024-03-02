package com.example.trimob.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.example.trimob.ApiKeys;
import com.example.trimob.Articles;
import com.example.trimob.NewsModal;
import com.example.trimob.NewsRVAdapter;
import com.example.trimob.R;
import com.example.trimob.RetrofitAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class home extends Fragment {

    private RecyclerView newsRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private NewsRVAdapter newsRVAdapter;

    private HomeViewModel mViewModel;
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    private boolean languageChanged = false;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;


    public static home newInstance() {
        return new home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home2, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("language", "");
        if (!selectedLanguage.isEmpty()) {
            setLocale(selectedLanguage);
        }
        newsRV = root.findViewById(R.id.idRVNews);
        loadingPB = root.findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList, requireContext());
        newsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsRV.setAdapter(newsRVAdapter);
        androidx.appcompat.widget.SearchView searchView = root.findViewById(R.id.searchView);
        swipeRefreshLayout = root.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews("");
            }
        });
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getNews(newText);
                return true;
            }
        });

        getNews("");
        newsRVAdapter.notifyDataSetChanged();


        return root;
    }


    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void getNews(String query){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayString = dateFormat.format(today);
        String yesterdayString = dateFormat.format(yesterday);

        // Retrieve the user-selected news language from SharedPreferences
        String selectedLanguage = sharedPreferences.getString(KEY_NEWS_LANGUAGE, "en");

        // Set the country code based on the selected language
        String country = "en".equals(selectedLanguage) ? "in" : "nl";

        String url;
        String BASE_URL = "https://newsapi.org/v2/";
        if (query.isEmpty()) {
            // If no query is entered, fetch top headlines
            url = "https://newsapi.org/v2/top-headlines?country="+country+"&category=general&from="+yesterdayString+"&to="+todayString+"&language="+selectedLanguage+"&apiKey="+ ApiKeys.NEWS_API_KEY;
        } else {
            // If a query is entered, perform a search
            url = "https://newsapi.org/v2/everything?q=" +query+ "&sortBy=publishedAt&language="+selectedLanguage+"&apikey="+ ApiKeys.NEWS_API_KEY;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        call = retrofitAPI.getAllNews(url);
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(@NonNull Call<NewsModal> call, @NonNull Response<NewsModal> response) {

                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                assert newsModal != null;
                ArrayList<Articles>articles = newsModal.getArticles();
                for(int i=0 ;i<articles.size();i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));

                }
                newsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show();

            }
        });
    }



}