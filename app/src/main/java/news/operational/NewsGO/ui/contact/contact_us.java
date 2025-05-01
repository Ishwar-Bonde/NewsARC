package news.operational.NewsGO.ui.contact;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import news.operational.NewsGO.ApiKeys;
import news.operational.NewsGO.Articles;
import news.operational.NewsGO.NewsModal;
import news.operational.NewsGO.NewsRVAdapter;
import news.operational.NewsGO.R;
import news.operational.NewsGO.RetrofitAPI;

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

public class contact_us extends Fragment {

    private RecyclerView newsRV,categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private NewsRVAdapter newsRVAdapter;

    private ContactUsViewModel mViewModel;
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;

    public static contact_us newInstance() {
        return new contact_us();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_us, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        newsRV = root.findViewById(R.id.idRVNews);
        loadingPB = root.findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,requireContext());
        newsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsRV.setAdapter(newsRVAdapter);
        swipeRefreshLayout = root.findViewById(R.id.swipe_contact);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews("sports");
            }
        });

        getNews("sports");
        newsRVAdapter.notifyDataSetChanged();

        return root;
    }

    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        // Format dates to match API request format (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayString = dateFormat.format(today);
        String yesterdayString = dateFormat.format(yesterday);
        String selectedLanguage = sharedPreferences.getString(KEY_NEWS_LANGUAGE, "en");

        // Set the country code based on the selected language
        String country = "en".equals(selectedLanguage) ? "in" : "nl";

        String url;
        String BASE_URL = "https://newsapi.org/v2/";
        if (category.isEmpty()) {
            // If no query is entered, fetch top headlines
            url = "https://newsapi.org/v2/top-headlines?country="+country+"&category=sports&from="+yesterdayString+"&to="+todayString+"&language="+selectedLanguage+"&apiKey="+ ApiKeys.NEWS_API_KEY;
        } else {
            // If a query is entered, perform a search
            url = "https://newsapi.org/v2/everything?q=" +category+ "&sortBy=publishedAt&language="+selectedLanguage+"&apikey="+ ApiKeys.NEWS_API_KEY;
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
                swipeRefreshLayout.setRefreshing(false);
                ArrayList<Articles> articles = newsModal.getArticles();
                for(int i=0 ;i<articles.size();i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));

                }
                newsRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(requireActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }


}