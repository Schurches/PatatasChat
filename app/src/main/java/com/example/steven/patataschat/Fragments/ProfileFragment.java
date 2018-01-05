package com.example.steven.patataschat.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.steven.patataschat.R;

/**
 * Created by steven on 16/12/2017.
 */

public class ProfileFragment extends Fragment {

    public static final String TITLE = "Profile";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.profile_fragment,container,false);
        return currentView;
    }

}


