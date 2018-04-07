package com.hes.easysales.easysales.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.Config;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.ItemClickListener;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.Shop;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.activities.MainActivity;
import com.hes.easysales.easysales.activities.ShopListActivity;
import com.hes.easysales.easysales.utilities.SharedPrefsUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sinopsys on 3/28/18.
 */

class ItemViewHolderWithoutChild extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    ImageView ivItem;
    TextView tvName;
    TextView tvPrice;
    TextView tvCategory;
    Button btnAdd;
    Button btnRemove;
    TextView tvQuantity;
    TextView tvShop;
    private ItemClickListener itemClickListener;

    ItemViewHolderWithoutChild(View itemView) {
        super(itemView);

        this.ivItem = itemView.findViewById(R.id.ivItem);
        this.tvCategory = itemView.findViewById(R.id.tvCategory);
        this.tvPrice = itemView.findViewById(R.id.tvPrice);
        this.tvName = itemView.findViewById(R.id.tvName);
        this.btnAdd = itemView.findViewById(R.id.btnAdd);
        this.btnRemove = itemView.findViewById(R.id.btnRemove);
        this.tvQuantity = itemView.findViewById(R.id.tvQuantity);
        this.tvShop = itemView.findViewById(R.id.tvShop);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), true);
        return true;
    }
}


class ItemViewHolderWithChild extends RecyclerView.ViewHolder {
    TextView tvCustomName;
    RecyclerView rvMatchingItems;
    RelativeLayout btnFold;
    CardView cvFold;
    ExpandableLinearLayout ell;

    ItemViewHolderWithChild(View itemView) {
        super(itemView);

        this.rvMatchingItems = itemView.findViewById(R.id.rvMatchingItems);
        this.tvCustomName = itemView.findViewById(R.id.tvCustomItemName);
        this.btnFold = itemView.findViewById(R.id.btnFold);
        this.cvFold = itemView.findViewById(R.id.cvCustomItem);
        this.ell = itemView.findViewById(R.id.expandableLayout);
    }
}

class MatchingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    ImageView ivCustomItem;
    TextView tvCustomItem;
    TextView tvCustomPrice;
    private ItemClickListener itemClickListener;

    MatchingItemViewHolder(View itemView) {
        super(itemView);

        this.ivCustomItem = itemView.findViewById(R.id.ivCustomItem);
        this.tvCustomItem = itemView.findViewById(R.id.tvCustomName);
        this.tvCustomPrice = itemView.findViewById(R.id.tvCustomPrice);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), true);
        return true;
    }
}

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> items;
    private List<Item> tmpItems;
    private ArrayList<Item> itemsCopy;
    private Context context;
    private Dialog itemFullPreview;
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
            expandState.put(i, false);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0: {
                final ItemViewHolderWithoutChild viewHolder = (ItemViewHolderWithoutChild) holder;
                final Item item = items.get(position);
//                viewHolder.setIsRecyclable(false);
                if (context instanceof ShopListActivity) {
                    viewHolder.setIsRecyclable(false);
                }
                viewHolder.tvName.setText(item.getName());
                viewHolder.tvPrice.setText(String.valueOf(item.getNewPrice()));
                viewHolder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolder.tvCategory.setText(item.getCategory());
                viewHolder.tvShop.setText(item.getShopName());
                // TODO fix FileNotFoundException for lost images
                //
                Glide.with(context).load(item.getImageUrl()).into(viewHolder.ivItem);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int postition, boolean isLongClick) {
                        if (isLongClick) {
//                            Toast.makeText(context, "Long Click: " + ((ItemViewHolderWithoutChild) holder).tvName.getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            ItemAdapter.this.showFullItem(items.get(position));
                        }
                    }
                });

                // Set button add and remove behaviour.
                //
                if (context instanceof MainActivity) {
                    viewHolder.btnRemove.setVisibility(View.INVISIBLE);
                    viewHolder.btnAdd.setVisibility(View.VISIBLE);

                    viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String[] shopLists = new String[((MainActivity) context).shopListsPreviewAdapter.shopLists.size()];
                            for (int i = 0; i < shopLists.length; i++) {
                                shopLists[i] = ((MainActivity) context).shopListsPreviewAdapter.shopLists.get(i).getName();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.select_shoplist);
                            builder.setItems(shopLists, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int pos) {
                                    ItemAdapter.this.addItemToShopList(item, ((MainActivity) context).shopListsPreviewAdapter.shopLists.get(pos));
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                } else if (context instanceof ShopListActivity) {
                    viewHolder.btnRemove.setVisibility(View.VISIBLE);
                    viewHolder.btnAdd.setVisibility(View.VISIBLE);


                    viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ItemAdapter.this.deleteItemFromShopList(item, ((ShopListActivity) context).selectedShopList);
                        }
                    });
                }
                break;
            }
            case 1: {
                final ItemViewHolderWithChild viewHolder = (ItemViewHolderWithChild) holder;
                Item item = items.get(position);
                if (context instanceof ShopListActivity) {
                    viewHolder.setIsRecyclable(false);
                }
                viewHolder.tvCustomName.setText(item.getName());
                ItemAdapter rvMatchingAdapter = new ItemAdapter(items.get(position).getMatchingItems(), context);
                viewHolder.rvMatchingItems.setAdapter(rvMatchingAdapter);
                viewHolder.rvMatchingItems.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                viewHolder.ell.setInRecyclerView(true);
                viewHolder.ell.setExpanded(expandState.get(position));
                if (item.getMatchingItems().size() > 0) {
                    viewHolder.tvCustomName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
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
                viewHolder.cvFold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.ell.toggle();
                    }
                });
                break;
            }
            case 2: {
                final MatchingItemViewHolder viewHolder = (MatchingItemViewHolder) holder;
                final Item item = items.get(position);
                viewHolder.tvCustomItem.setText(item.getName());
                viewHolder.tvCustomPrice.setText(String.valueOf(item.getNewPrice()));
                viewHolder.tvCustomPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                // TODO fix FileNotFoundException for lost images
                //
                Glide.with(context).load(item.getImageUrl()).into(viewHolder.ivCustomItem);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int postition, boolean isLongClick) {
                        if (isLongClick) {
                            addItemToShopList(item, ((ShopListActivity) context).selectedShopList);
                        } else {
                            ItemAdapter.this.showFullItem(items.get(position));
                        }
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

    private void addItemToShopList(final Item item, ShopList shopList) {
        final String url = new APIRequests.ShopListPOSTRequest(String.valueOf(shopList.getId()), String.valueOf(item.getId())).getAddURL();
        Response.Listener respListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).fetchData.beforeDownload();
                    ((MainActivity) context).fetchData.downloadShopLists();
                    ((MainActivity) context).fetchData.afterDownload();
                } else if (context instanceof ShopListActivity) {
                    Item i = new Item(item);
                    try {
                        ((ShopListActivity) context).adapter.add(i, getInsertNormalItemIndex());
                    } catch (IndexOutOfBoundsException exe) {
                        ((ShopListActivity) context).adapter.add(i, 0);
                    }
                    ((ShopListActivity) context).selectedShopList.getItems().add(i);
                }
            }
        };
        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.error_add_item_to_sl, Toast.LENGTH_LONG).show();
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
            }
        };
        String userToken = SharedPrefsUtil.getStringPref(context, Config.KEY_TOKEN);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);
        APIRequests.RequestHandler rh = APIRequests.formPOSTRequest(false,
                null,
                headers,
                url,
                respListener,
                errListener,
                new WeakReference<>(context));
        rh.launch();
    }

    private void deleteItemFromShopList(final Item item, ShopList shopList) {
        final String url = new APIRequests.ShopListPOSTRequest(String.valueOf(shopList.getId()), String.valueOf(item.getId())).getDeleteURL();
        Response.Listener respListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                int pos = ItemAdapter.this.items.indexOf(item);
                ItemAdapter.this.removeAt(pos);
//                ((ShopListActivity) context).selectedShopList.getItems().remove(pos);
                notifyDataSetChanged();
            }
        };
        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.error_removing_item_from_sl, Toast.LENGTH_LONG).show();
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
            }
        };
        String userToken = SharedPrefsUtil.getStringPref(context, Config.KEY_TOKEN);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);
        APIRequests.RequestHandler rh = APIRequests.formDELETERequest(
                headers,
                url,
                respListener,
                errListener,
                new WeakReference<>(context));
        rh.launch();
    }

    private void showFullItem(Item item) {
        itemFullPreview = new Dialog(context);
        itemFullPreview.setContentView(R.layout.item_full_specs);
        ImageView image;
        TextView name, category, oldPrice, newPrice, dateIn, dateOut, condition, shop;
        RelativeLayout rl;

        rl = itemFullPreview.findViewById(R.id.rlFullItem);
        image = itemFullPreview.findViewById(R.id.ivFullItem);
        name = itemFullPreview.findViewById(R.id.tvFullName);
        category = itemFullPreview.findViewById(R.id.tvFullCategory);
        oldPrice = itemFullPreview.findViewById(R.id.tvFullOldPrice);
        newPrice = itemFullPreview.findViewById(R.id.tvFullNewPrice);
        dateIn = itemFullPreview.findViewById(R.id.tvFullDateIn);
        dateOut = itemFullPreview.findViewById(R.id.tvFullDateOut);
        condition = itemFullPreview.findViewById(R.id.tvFullCondition);
        shop = itemFullPreview.findViewById(R.id.tvFullShop);

        Glide.with(context).load(item.getImageUrl()).into(image);
        name.setText(item.getName());
        category.setText(item.getCategory());
        oldPrice.setText(String.valueOf(item.getOldPrice()));
        newPrice.setText(String.valueOf(item.getNewPrice()));
        dateIn.setText(item.getDateIn());
        dateOut.setText(item.getDateOut());
        condition.setText(item.getCondition());
        shop.setText(shop.getText() + " " + item.getShopName());

        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemFullPreview.dismiss();
            }
        });
        itemFullPreview.show();
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

    public void appendAll(List<Item> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
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
//            if (!multipleFilters) {
//                items.clear();
//            }
            items.addAll(itemsMatchingQuery(key, query));
        }
        notifyDataSetChanged();
    }

    private int getInsertNormalItemIndex() {
        ShopList sl = ((ShopListActivity) context).selectedShopList;
        return sl.getItems().size();
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
