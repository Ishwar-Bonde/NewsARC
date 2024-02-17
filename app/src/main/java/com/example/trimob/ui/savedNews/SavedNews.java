package com.example.trimob.ui.savedNews;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trimob.R;
import com.example.trimob.saveddata.addsavednews;
import com.example.trimob.savedpagecustAdapter;

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

        savedpagecustAdapter CA=new savedpagecustAdapter(as.getAllSavedNews(),requireContext());
        r.setAdapter(CA);

        return root;
    }

}