package com.hes.easysales.easysales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Created by sinopsys on 2/23/18.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Item> data;
    private LayoutInflater inflater;

    ItemAdapter(Context context, List<Item> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_container, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in RecyclerView to bind data and assign values from list.
        //
        MyHolder myHolder = (MyHolder) holder;
        Item current = data.get(position);
        myHolder.tvName.setText(current.getName());
        myHolder.tvCategory.setText(context.getString(R.string.category) + current.getCategory());
        myHolder.tvPrice.setText(current.getNewPrice() + context.getString(R.string.currency));
        myHolder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // Load image into ImageView using Glide.
        //
        Glide.with(context).load(current.getImageUrl()).into(myHolder.ivItem);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivItem;
        TextView tvPrice;
        TextView tvCategory;

        MyHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

    }
}


// EOF
