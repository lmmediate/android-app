package com.hes.easysales.easysales.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 2/23/18.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Item> data;
    private LayoutInflater inflater;

    public ItemAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_container, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in RecyclerView to bind data and assign values from list.
        //
        MyHolder myHolder = (MyHolder) holder;
        Item current = data.get(position);
        myHolder.tvName.setText(current.getName());
        myHolder.tvCategory.setText(current.getCategory());
        myHolder.tvPrice.setText(current.getNewPrice() + context.getString(R.string.currency));
        myHolder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // Load image into ImageView using Glide.
        //
        Glide.with(context).load(current.getImageUrl()).into(myHolder.ivItem);
    }


    public void add(Item newItem) {
        data.add(newItem);
        notifyItemRangeInserted(data.size() - 1, 1);
    }

    public void add(Item item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public Item getItemAtPosition(int position) {
        return data.get(position);
    }

    public void addAll(List<Item> newItems) {
        int oldSz = data.size();
        data.clear();
        notifyItemRangeRemoved(0, oldSz);
        data.addAll(newItems);
        notifyItemRangeInserted(0, data.size());
    }

    public void removeAt(int adapterPosition) {
        data.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        ImageView ivItem;
        TextView tvPrice;
        TextView tvCategory;
        int itemId;

        MyHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivItem = itemView.findViewById(R.id.ivItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        public void setItem(int item) {
            this.itemId = item;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
                Toast.makeText(v.getContext(), "Image view clicked: " + this.itemId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), this.itemId + " ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


// EOF
