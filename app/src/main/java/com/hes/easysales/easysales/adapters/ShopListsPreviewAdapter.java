package com.hes.easysales.easysales.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.activities.ShopListActivity;

import java.util.List;

import static com.hes.easysales.easysales.Config.KEY_CURRENT_SHOPLIST;

/**
 * Created by sinopsys on 3/30/18.
 */

class ShopListPreviewViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitlePreview;
    TextView tvItemsPreview;
    RelativeLayout btnShowShopList;

    public ShopListPreviewViewHolder(View itemView) {
        super(itemView);
        this.btnShowShopList = itemView.findViewById(R.id.shopListPreviewLayout);
        this.tvTitlePreview = itemView.findViewById(R.id.tvTitlePreview);
        this.tvItemsPreview = itemView.findViewById(R.id.tvItemsPreview);
    }
}

public class ShopListsPreviewAdapter extends RecyclerView.Adapter<ShopListPreviewViewHolder> {
    public List<ShopList> shopLists;
    private Context context;

    public ShopListsPreviewAdapter(List<ShopList> shopLists, Context context) {
        this.shopLists = shopLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ShopListPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.shoplist_preview, parent, false);
        return new ShopListPreviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListPreviewViewHolder holder, final int position) {
        ShopList sl = shopLists.get(position);
        StringBuilder itemsPreview = new StringBuilder();
        for (Item i : sl.getItems()) {
            itemsPreview.append(i.getName());
            itemsPreview.append("\n");
        }
        if (sl.getCustomItems().size() > 0) {
            itemsPreview.append("\n");
        }
        for (Item i : sl.getCustomItems()) {
            itemsPreview.append(i.getName());
            itemsPreview.append("\n");
        }
        holder.tvTitlePreview.setText(sl.getName());
        holder.tvItemsPreview.setText(itemsPreview.toString());
        holder.btnShowShopList.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ShopListActivity.class);
                        i.putExtra(KEY_CURRENT_SHOPLIST, shopLists.get(position));
                        context.startActivity(i);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return shopLists.size();
    }

    public void addAll(List<ShopList> newShopLists) {
        int oldSz = shopLists.size();
        shopLists.clear();
        notifyItemRangeRemoved(0, oldSz);
        shopLists.addAll(newShopLists);
        notifyItemRangeInserted(0, shopLists.size());
    }
}


// EOF
