package com.hes.easysales.easysales.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by sinopsys on 3/28/18.
 */

class ItemViewHolderWithoutChild extends RecyclerView.ViewHolder {
    ImageView ivItem;
    TextView tvName;
    TextView tvPrice;
    TextView tvCategory;
    Button btnAdd;

    ItemViewHolderWithoutChild(View itemView) {
        super(itemView);

        this.ivItem = itemView.findViewById(R.id.ivItem);
        this.tvCategory = itemView.findViewById(R.id.tvCategory);
        this.tvPrice = itemView.findViewById(R.id.tvPrice);
        this.tvName = itemView.findViewById(R.id.tvName);
        this.btnAdd = itemView.findViewById(R.id.btnAdd);
    }
}


class ItemViewHolderWithChild extends RecyclerView.ViewHolder {
    TextView tvCustomName;
    RecyclerView rvMatchingItems;
    RelativeLayout btnFold;
    ExpandableLinearLayout ell;

    ItemViewHolderWithChild(View itemView) {
        super(itemView);

        this.rvMatchingItems = itemView.findViewById(R.id.rvMatchingItems);
        this.tvCustomName = itemView.findViewById(R.id.tvCustomItemName);
        this.btnFold = itemView.findViewById(R.id.btnFold);
        this.ell = itemView.findViewById(R.id.expandableLayout);
    }
}

class MatchingItemViewHolder extends RecyclerView.ViewHolder {
    ImageView ivCustomItem;
    TextView tvCustomItem;
    TextView tvCustomPrice;

    MatchingItemViewHolder(View itemView) {
        super(itemView);

        this.ivCustomItem = itemView.findViewById(R.id.ivCustomItem);
        this.tvCustomItem = itemView.findViewById(R.id.tvCustomName);
        this.tvCustomPrice = itemView.findViewById(R.id.tvCustomPrice);
    }
}

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> items;
    private List<Item> tmpItems;
    private ArrayList<Item> itemsCopy;
    private Context context;
    private SparseBooleanArray expandState;
    private boolean type1Downloaded = false;
    private boolean type2Downloaded = false;

    public ItemAdapter(List<Item> items, Context c) {
        this.expandState = new SparseBooleanArray();
        this.items = items;
        this.tmpItems = new ArrayList<>();
        this.itemsCopy = new ArrayList<>();
        this.context = c;
        for (int i = 0; i < items.size(); ++i) {
            expandState.append(i, false);
        }
    }

    public ArrayList<Item> getItemsCopy() {
        return (ArrayList<Item>) itemsCopy.clone();
    }


    @Override
    public int getItemViewType(int pos) {
        if (items.get(pos).isMatched()) {
            return 2;
        } else if (items.get(pos).isExpandable()) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Without children.
        //
        if (viewType == 0) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.item_container, parent, false);
            return new ItemViewHolderWithoutChild(v);
        }

        // With children.
        //
        else if (viewType == 1) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.item_container_with_child, parent, false);
            return new ItemViewHolderWithChild(v);
        }

        // Matched item.
        //
        else {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.matching_item, parent, false);
            return new MatchingItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                ItemViewHolderWithoutChild viewHolder = (ItemViewHolderWithoutChild) holder;
                Item item = items.get(position);
//                viewHolder.setIsRecyclable(true);
                viewHolder.tvName.setText(item.getName());
                viewHolder.tvPrice.setText(String.valueOf(item.getNewPrice()));
                viewHolder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolder.tvCategory.setText(item.getCategory());
                // TODO fix FileNotFoundException for lost images
                //
                Glide.with(context).load(item.getImageUrl()).into(viewHolder.ivItem);
                break;
            }
            case 1: {
                // TODO: Horizontal RV do not forget..
                //
                final ItemViewHolderWithChild viewHolder = (ItemViewHolderWithChild) holder;
                Item item = items.get(position);
                viewHolder.setIsRecyclable(false);
                viewHolder.tvCustomName.setText(item.getName());
                ItemAdapter rvMatchingAdapter = new ItemAdapter(items.get(position).getMatchingItems(), context);
                viewHolder.rvMatchingItems.setAdapter(rvMatchingAdapter);
                viewHolder.rvMatchingItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                viewHolder.ell.setInRecyclerView(true);
                viewHolder.ell.setExpanded(expandState.get(position));
                viewHolder.ell.setListener(new ExpandableLayoutListenerAdapter() {
                    @Override
                    public void onPreOpen() {
                        changeRotate(viewHolder.btnFold, 0f, 180f).start();
                        expandState.put(position, true);
                    }

                    @Override
                    public void onPreClose() {
                        changeRotate(viewHolder.btnFold, 180f, 0f).start();
                        expandState.put(position, false);
                    }
                });
                viewHolder.btnFold.setRotation(expandState.get(position) ? 180f : 0f);
                viewHolder.btnFold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.ell.toggle();
                    }
                });
                break;
            }
            case 2: {
                MatchingItemViewHolder viewHolder = (MatchingItemViewHolder) holder;
                Item item = items.get(position);
                viewHolder.tvCustomItem.setText(item.getName());
                viewHolder.tvCustomPrice.setText(String.valueOf(item.getNewPrice()));
                viewHolder.tvCustomPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                // TODO fix FileNotFoundException for lost images
                //
                Glide.with(context).load(item.getImageUrl()).into(viewHolder.ivCustomItem);
                break;
            }
            default:
                break;
        }
    }

    private ObjectAnimator changeRotate(RelativeLayout btnFold, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(btnFold, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Item newItem) {
        items.add(newItem);
        notifyItemRangeInserted(items.size() - 1, 1);
        itemsCopy.add(newItem);
    }

    public void add(Item item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public Item getItemAtPosition(int position) {
        return items.get(position);
    }

    public void addAll(List<Item> newItems) {
        int oldSz = items.size();
        items.clear();
        notifyItemRangeRemoved(0, oldSz);
        items.addAll(newItems);
        notifyItemRangeInserted(0, items.size());
        itemsCopy.clear();
        itemsCopy.addAll(newItems);
    }

    public void clearTmpItems() {
        tmpItems.clear();
        type1Downloaded = type2Downloaded = false;
    }

    public void addAllTmpItems(List<Item> newItems) {
        tmpItems.addAll(newItems);
        if (type1Downloaded ^ type2Downloaded) {
            type1Downloaded = type2Downloaded = true;
        }
        if (!type1Downloaded && !type2Downloaded) {
            type1Downloaded = true;
        }
    }

    public void subsItemsWithTemp() {
        if (type1Downloaded && type2Downloaded) {
            this.addAll(tmpItems);
            type1Downloaded = type2Downloaded = false;
            if (context instanceof MainActivity) {
                ((MainActivity) context).fetchData.afterDownload();
                Collections.sort(items, Item.getShopIdComparator());
                notifyDataSetChanged();
            }
        }
    }

    public void removeAt(int adapterPosition) {
        items.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void returnToOld() {
        items.clear();
        items.addAll(itemsCopy);
        Collections.sort(items, Item.getShopIdComparator());
        notifyDataSetChanged();
    }

    public void filter(String key, String query, boolean multipleFilters) {
        if (query.isEmpty()) {
            items.clear();
            items.addAll(itemsCopy);
            Collections.sort(items, Item.getShopIdComparator());
        } else {
            if (!multipleFilters) {
                items.clear();
            }
            items.addAll(itemsMatchingQuery(key, query));
        }
        notifyDataSetChanged();
    }

    private List<Item> itemsMatchingQuery(String key, String query) {
        ArrayList<Item> queryResult = new ArrayList<>();
        query = query.toLowerCase();
        if (!query.isEmpty()) {
            for (Item item : itemsCopy) {
                String compare = "";
                switch (key) {
                    case "category":
                        compare = item.getCategory().toLowerCase();
                        break;
                    case "name":
                        compare = item.getName().toLowerCase();
                        break;
                    case "shopId":
                        compare = String.valueOf(item.getShopId());
                        break;
                }
                if (compare.contains(query)) {
                    queryResult.add(item);
                }
            }
        }
        return queryResult;
    }

    public class ComplexQuery {

        // Need to use Map<String, List<String>>
        private String key;
        private List<String> query;

        public ComplexQuery(String key, List<String> query) {
            this.key = key;
            this.query = query;
        }

        public void apply() {
            items.clear();
            for (String value : query) {
                filter(key, value, true);
            }
        }
    }
}


// EOF
