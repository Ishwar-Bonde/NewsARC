package com.example.trimob;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trimob.databinding.ActivityHomePageBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class homePage extends AppCompatActivity {


    TextView user_name, user_email;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private RecyclerView newsRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsRVAdapter newsRVAdapter;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomePage.toolbar);

        DrawerLayout drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_categories, R.id.nav_favorites, R.id.nav_contact, R.id.nav_about, R.id.nav_entertainment, R.id.nav_health, R.id.nav_bitcoin, R.id.nav_apple, R.id.nav_techcrunch, R.id.nav_saved)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.username);

        swipeRefreshLayout = findViewById(R.id.swipe);

        newsRV = findViewById(R.id.idRVNews);
        loadingPB = findViewById(R.id.idPBLoading);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList, this);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews("");
            }
        });


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                // Start the shimmer effect
        getNews("");
        newsRVAdapter.notifyDataSetChanged();


    }

    private void getNews(String query) {

        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String url;
        String BASE_URL = "https://newsapi.org/v2/";
        if (query.isEmpty()) {
            // If no query is entered, fetch top headlines
            url = "https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apikey=8c4e78e7541d45be95fb3a8b6e93c48a";
        } else {
            // If a query is entered, perform a search
            url = "https://newsapi.org/v2/everything?q=" + query + "&sortBy=publishedAt&language=en&apikey=8c4e78e7541d45be95fb3a8b6e93c48a";
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
                ArrayList<Articles> articles = newsModal.getArticles();
                for (int i = 0; i < articles.size(); i++) {
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));

                }
                newsRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(homePage.this, "Failed to get News", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);

        MenuItem searchItem = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settings_btn) {
            Intent intent = new Intent(homePage.this, Settings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.profile_btn) {
            Intent intent = new Intent(homePage.this, profilePage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}