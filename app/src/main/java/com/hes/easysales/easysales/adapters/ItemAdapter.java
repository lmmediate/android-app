package com.hes.easysales.easysales.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

/**
 * Created by sinopsys on 3/28/18.
 */

class ItemViewHolderWithoutChild extends RecyclerView.ViewHolder {
    ImageView ivItem;
    TextView tvName;
    TextView tvPrice;
    TextView tvCategory;
    private Button btnAdd;

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
    TextView tvCustomName, tvChildText;
    RelativeLayout btnFold;
    ExpandableLinearLayout ell;

    ItemViewHolderWithChild(View itemView) {
        super(itemView);

        this.tvChildText = itemView.findViewById(R.id.tvChildText);
        this.tvCustomName = itemView.findViewById(R.id.tvCustomItemName);
        this.btnFold = itemView.findViewById(R.id.btnFold);
        this.ell = itemView.findViewById(R.id.expandableLayout);
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
        expandState = new SparseBooleanArray();
        this.items = items;
        this.tmpItems = new ArrayList<>();
        this.context = c;
        for (int i = 0; i < items.size(); ++i) {
            expandState.append(i, false);
        }
    }

    public ArrayList<Item> getItemsCopy() {
        itemsCopy = new ArrayList<>();
        itemsCopy.addAll(items);
        return (ArrayList<Item>) itemsCopy.clone();
    }

    @Override
    public int getItemViewType(int pos) {
        if (items.get(pos).isExpandable()) {
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
        else {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.item_container_with_child, parent, false);
            return new ItemViewHolderWithChild(v);
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
                Glide.with(context).load(item.getImageUrl()).into(viewHolder.ivItem);
                break;
            }
            case 1: {
                // TODO: Horizontal RV do not forget..
                //
                final ItemViewHolderWithChild viewHolder = (ItemViewHolderWithChild) holder;
                Item item = items.get(position);
//                viewHolder.setIsRecyclable(false);
                viewHolder.tvCustomName.setText(item.getName());
                viewHolder.tvChildText.setText("asdfasdfasdf");
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
            }
        }
    }

    public void removeAt(int adapterPosition) {
        items.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }
}


// EOF
