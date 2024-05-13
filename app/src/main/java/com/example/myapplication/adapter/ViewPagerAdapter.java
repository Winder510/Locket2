package com.example.myapplication.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.fragments.CameraFragment;
import com.example.myapplication.fragments.ViewPostFragment;


public class ViewPagerAdapter  extends FragmentStateAdapter  {
    boolean canSwipe;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,boolean canSwipe) {
        super(fragmentActivity);
        this.canSwipe = canSwipe;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: return new CameraFragment();
            case 1:
                return new ViewPostFragment();
            default:
                return  new CameraFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
