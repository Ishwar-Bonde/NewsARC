package news.operational.NewsGO;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import news.operational.NewsGO.Utility.NetworkChangeListener;
import news.operational.NewsGO.databinding.ActivityHomePageBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class homePage extends AppCompatActivity {
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    private static final int SETTINGS_REQUEST_CODE = 100;
    TextView user_name, user_email;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    SharedPreferences sharedPreferences;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
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
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("language", "");
        if (!selectedLanguage.isEmpty()) {
            setLocale(selectedLanguage);
        }
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_categories, R.id.nav_favorites, R.id.nav_contact, R.id.nav_about, R.id.nav_entertainment, R.id.nav_health, R.id.nav_bitcoin, R.id.nav_apple, R.id.nav_techcrunch, R.id.nav_saved)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        swipeRefreshLayout = findViewById(R.id.swipe);
        newsRV = findViewById(R.id.idRVNews);
        loadingPB = findViewById(R.id.idPBLoading);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList, this);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        getFCMToken();
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews("");
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        getNews("");
        newsRVAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        String savedLanguage = sharedPreferences.getString("language", "");
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        if (!savedLanguage.equals(currentLanguage)) {
            setLocale(savedLanguage);
            recreate();
        }
    }
//    This two methods for updating the fcm token that helps to send the notification to particular or all users by admin
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                updateFCMToken(token);
            }
        });
    }
    private void updateFCMToken(String token) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("datauser");
            usersRef.child(userId).child("fcmToken").setValue(token)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("FCM token updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Failed to update FCM token: " + e.getMessage());
                    });
        }
    }
//    This method is for setting the selected language which language is selected by user in settings
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
//    Getting the news from api and assigning to Articles class
    private void getNews(String query) {
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String selectedLanguage = sharedPreferences.getString(KEY_NEWS_LANGUAGE, "en");
        String country = "en".equals(selectedLanguage) ? "in" : "nl";
        String url;
        String BASE_URL = "https://newsapi.org/v2/";
        if (query.isEmpty()) {
            url = "https://newsapi.org/v2/top-headlines?country=us&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=" + selectedLanguage + "&apiKey=" + ApiKeys.NEWS_API_KEY;
        } else {
            url = "https://newsapi.org/v2/everything?q=" + query + "&sortBy=publishedAt&language=" + selectedLanguage + "&apiKey=" + ApiKeys.NEWS_API_KEY;
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
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(), articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_btn) {
            openSettings();
            return true;
        } else if (id == R.id.profile_btn) {
            Intent intent = new Intent(homePage.this, profilePage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }
//    onStart and onStop method is for checking the internet connection continuously
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
//    We are opening the settings by sending the request code
    private void openSettings() {
        Intent intent = new Intent(homePage.this, Settings.class);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }
//    This Activity result is for Notify that the language has been changed and if language changed then recreate the navigation drawer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            recreate();
        }
    }
}