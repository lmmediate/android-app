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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;

import java.util.List;

/**
 * Created by sinopsys on 3/28/18.
 */

class ShopListItemViewHolderWithoutChild extends RecyclerView.ViewHolder {
    public ImageView ivItem;
    public TextView tvName;
    public TextView tvPrice;
    public TextView tvCategory;


    public ShopListItemViewHolderWithoutChild(View itemView) {
        super(itemView);

        this.ivItem = itemView.findViewById(R.id.ivItem);
        this.tvCategory = itemView.findViewById(R.id.tvCategory);
        this.tvPrice = itemView.findViewById(R.id.tvPrice);
        this.tvName = itemView.findViewById(R.id.tvName);
    }
}


class ShopListItemViewHolderWithChild extends RecyclerView.ViewHolder {
    public TextView tvCustomName, tvChildText;
    public RelativeLayout btnFold;
    public ExpandableLinearLayout ell;

    public ShopListItemViewHolderWithChild(View itemView) {
        super(itemView);

        this.tvChildText = itemView.findViewById(R.id.tvChildText);
        this.tvCustomName = itemView.findViewById(R.id.tvCustomItemName);
        this.btnFold = itemView.findViewById(R.id.btnFold);
        this.ell = itemView.findViewById(R.id.expandableLayout);
    }
}

public class ShopListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Item> items;
    Context context;
    SparseBooleanArray expandState;

    public ShopListItemAdapter(List<Item> items) {
        expandState = new SparseBooleanArray();
        this.items = items;
        for (int i = 0; i < items.size(); ++i) {
            expandState.append(i, false);
        }
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
        this.context = parent.getContext();
        // Without children.
        //
        if (viewType == 0) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.item_container, parent, false);
            return new ShopListItemViewHolderWithoutChild(v);
        }

        // With children.
        //
        else {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View v = inflater.inflate(R.layout.item_container_with_child, parent, false);
            return new ShopListItemViewHolderWithChild(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                ShopListItemViewHolderWithoutChild viewHolder = (ShopListItemViewHolderWithoutChild) holder;
                Item item = items.get(position);
                viewHolder.setIsRecyclable(false);
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
                final ShopListItemViewHolderWithChild viewHolder = (ShopListItemViewHolderWithChild) holder;
                Item item = items.get(position);
                viewHolder.setIsRecyclable(false);
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
}


// EOF
