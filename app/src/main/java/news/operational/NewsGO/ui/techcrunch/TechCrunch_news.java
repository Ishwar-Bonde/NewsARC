package news.operational.NewsGO.ui.techcrunch;

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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TechCrunch_news extends Fragment {

    private RecyclerView newsRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private NewsRVAdapter newsRVAdapter;
    private TechCrunchNewsViewModel mViewModel;
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;


    public static TechCrunch_news newInstance() {
        return new TechCrunch_news();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_tech_crunch_news, container, false);
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
        swipeRefreshLayout = root.findViewById(R.id.swipe_techcrunch);
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

        // Construct the URL with the date range
        String url2 = "https://newsapi.org/v2/everything?domains=techcrunch.com,thenextweb.com&apiKey="+ ApiKeys.NEWS_API_KEY;
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        call = retrofitAPI.getAllNews(url2);
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