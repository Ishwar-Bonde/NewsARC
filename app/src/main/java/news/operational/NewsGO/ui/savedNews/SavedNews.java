package news.operational.NewsGO.ui.savedNews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import news.operational.NewsGO.R;
import news.operational.NewsGO.saveddata.addsavednews;
import news.operational.NewsGO.savedpagecustAdapter;

public class SavedNews extends Fragment {

    private SavedNewsViewModel mViewModel;

    public static SavedNews newInstance() {
        return new SavedNews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.activity_saved_news, container, false);
        RecyclerView r=root.findViewById(R.id.savednewsRecyclerView);
        r.setLayoutManager(new LinearLayoutManager(requireContext()));
        addsavednews as=new addsavednews(requireContext());

        savedpagecustAdapter CA=new savedpagecustAdapter(requireContext(), as);
        r.setAdapter(CA);

        return root;
    }

}