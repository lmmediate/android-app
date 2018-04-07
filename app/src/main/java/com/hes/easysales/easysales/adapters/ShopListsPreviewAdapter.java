package com.hes.easysales.easysales.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.Config;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.activities.MainActivity;
import com.hes.easysales.easysales.activities.ShopListActivity;
import com.hes.easysales.easysales.utilities.SharedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hes.easysales.easysales.Config.KEY_CURRENT_SHOPLIST;
import static com.hes.easysales.easysales.Config.MAX_ITEMS_PREVIEW;
import static com.hes.easysales.easysales.Config.MAX_LEN_PREVIEW;

/**
 * Created by sinopsys on 3/30/18.
 */

class ShopListPreviewViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitlePreview;
    TextView tvItemsPreview;
    CardView btnShowShopList;

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
        int itemCount = 0;
        final ShopList sl = shopLists.get(position);
        StringBuilder itemsPreview = new StringBuilder();
        for (Item i : sl.getItems()) {
            if (itemCount <= MAX_ITEMS_PREVIEW / 2) {
                String toAppend = truncate(i.getName(), MAX_LEN_PREVIEW);
                itemsPreview.append(toAppend);
                itemsPreview.append("\n");
                ++itemCount;
                continue;
            }
            itemsPreview.append("...\n");
            break;
        }
        if (sl.getCustomItems().size() > 0) {
            itemsPreview.append("\n");
        }
        for (Item i : sl.getCustomItems()) {
            if (itemCount <= MAX_ITEMS_PREVIEW) {
                String toAppend = truncate(i.getName(), MAX_LEN_PREVIEW);
                itemsPreview.append(toAppend);
                itemsPreview.append("\n");
                ++itemCount;
                continue;
            }
            itemsPreview.append("...\n");
            break;
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
        holder.btnShowShopList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(R.string.delete_sl);
                alertDialog.setMessage(R.string.confirm_deleteion);

                alertDialog.setPositiveButton(R.string.okk,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) context).fetchData.beforeDownload();
                                Response.Listener<String> respListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (TextUtils.isEmpty(response)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setMessage(R.string.error_deleting_sl)
                                                    .setNeutralButton(R.string.neutral_ok, null)
                                                    .create()
                                                    .show();
                                        } else {
                                            ((MainActivity) context).fetchData.beforeDownload();
                                            ((MainActivity) context).fetchData.downloadShopLists();
                                            ((MainActivity) context).fetchData.afterDownload();
                                        }
                                    }
                                };

                                Response.ErrorListener errListener = new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                                        Toast.makeText(context, R.string.error_deleting_sl, Toast.LENGTH_LONG).show();
                                    }
                                };

                                String userToken = SharedPrefsUtil.getStringPref(context, Config.KEY_TOKEN);
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer " + userToken);

                                APIRequests.RequestHandler rh = APIRequests.formDELETERequest(
                                        headers,
                                        Config.URL_SHOPLIST + sl.getId(),
                                        respListener,
                                        errListener,
                                        new WeakReference<>(context)
                                );

                                rh.launch();
                            }
                        });

                alertDialog.setNegativeButton(R.string.cancell,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
                return true;
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

    private String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            return str;
        }
    }
}


// EOF
