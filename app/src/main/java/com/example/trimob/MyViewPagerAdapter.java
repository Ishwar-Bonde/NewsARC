package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;



public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
//                return new fragment_mypost();
            case 1:
//                return new fragment_saved();
            case 2:
//                return new fragment_activity();
            case 3:
//                return new fragment_history();
            default:
//                return new fragment_mypost();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
