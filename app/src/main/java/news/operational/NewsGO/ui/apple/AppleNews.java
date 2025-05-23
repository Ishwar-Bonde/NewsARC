package news.operational.NewsGO.ui.apple;

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

public class AppleNews extends Fragment {
    private RecyclerView newsRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private NewsRVAdapter newsRVAdapter;

    private AppleNewsViewModel mViewModel;
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;

    public static AppleNews newInstance() {
        return new AppleNews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_apple_news, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString(KEY_NEWS_LANGUAGE, "en");
        // Set the country code based on the selected language
        if(selectedLanguage.equals("nl")){
            Toast.makeText(getContext(), "No support for Dutch language for this category", Toast.LENGTH_SHORT).show();
        }

        newsRV = root.findViewById(R.id.idRVNews);
        loadingPB = root.findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,requireContext());
        newsRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        newsRV.setAdapter(newsRVAdapter);
        swipeRefreshLayout = root.findViewById(R.id.swipe_apple);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews("");
            }
        });

        getNews("");
        newsRVAdapter.notifyDataSetChanged();
        return root;
    }
    private void getNews(String query){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedYesterday = dateFormat.format(yesterday);
        String formattedToday = dateFormat.format(new Date());
        Toast.makeText(getContext(), formattedYesterday, Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), formattedToday, Toast.LENGTH_SHORT).show();

        String url2 = "https://newsapi.org/v2/everything?q=apple&from=" + formattedYesterday + "&to=" + formattedToday + "&sortBy=popularity&apiKey="+ ApiKeys.NEWS_API_KEY;
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        call = retrofitAPI.getAllNews(url2);
        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(@NonNull Call<NewsModal> call, @NonNull Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                assert newsModal != null;
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