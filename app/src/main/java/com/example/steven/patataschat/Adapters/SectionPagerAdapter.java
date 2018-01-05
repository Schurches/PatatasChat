package com.example.steven.patataschat.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 16/12/2017.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> allFragments = new ArrayList<>();
    private final List<String> fragmentsTitles = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment, String TITLE){
        allFragments.add(fragment);
        fragmentsTitles.add(TITLE);
    }

    public CharSequence getPageTitle(int position){
        return fragmentsTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return allFragments.get(position);
    }

    @Override
    public int getCount() {
        return allFragments.size();
    }
}
