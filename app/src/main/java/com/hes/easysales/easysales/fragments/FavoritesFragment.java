package com.hes.easysales.easysales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hes.easysales.easysales.R;

/**
 * Created by sinopsys on 2/25/18.
 */

public class FavoritesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment ff = new FavoritesFragment();
        Bundle args = new Bundle();
        ff.setArguments(args);
        return ff;
    }
}


// EOF
