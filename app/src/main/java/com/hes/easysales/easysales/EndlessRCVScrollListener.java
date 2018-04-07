package com.hes.easysales.easysales;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hes.easysales.easysales.activities.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by sinopsys on 4/6/18.
 */

public abstract class EndlessRCVScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private WeakReference<Activity> actvityRef;


    public EndlessRCVScrollListener(LinearLayoutManager linearLayoutManager, WeakReference<Activity> activityRef) {
        this.linearLayoutManager = linearLayoutManager;
        this.actvityRef = activityRef;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int currentTotal = linearLayoutManager.getItemCount();
        int lastVisibleItem = 1 + linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int superTotalItems = ((MainActivity) actvityRef.get()).totalItemsCount;

        if (currentTotal < superTotalItems && lastVisibleItem == currentTotal && lastVisibleItem != 0) {
            ++((MainActivity) actvityRef.get()).currentPage;
            onLoadMore(((MainActivity) actvityRef.get()).currentPage);
        }
    }

    public abstract void onLoadMore(int currentPage);
}


// EOF
