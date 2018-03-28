package com.hes.easysales.easysales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.activities.MainActivity;
import com.hes.easysales.easysales.adapters.MyAdapter;

/**
 * Created by sinopsys on 2/25/18.
 */

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get RecyclerView and its adapter.
        //
        RecyclerView rv = ((MainActivity) getActivity()).findViewById(R.id.itemList);
        MyAdapter myAdapter = (MyAdapter) rv.getAdapter();
        if (myAdapter == null) {
            myAdapter = new MyAdapter(getActivity());
        }
        ((MainActivity) getActivity()).adapter = myAdapter;
        ((MainActivity) getActivity()).mRecyclerView = rv;

        // Add items to the adapter.
        //
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}


// EOF
