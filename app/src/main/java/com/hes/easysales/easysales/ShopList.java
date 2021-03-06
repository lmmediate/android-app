package com.hes.easysales.easysales;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 3/30/18.
 */

public class ShopList implements Parcelable {

    private long id;
    private String name;
    private List<Item> items;
    private List<Item> customItems;


    protected ShopList(Parcel in) {
        id = in.readLong();
        name = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
        customItems = in.createTypedArrayList(Item.CREATOR);
    }

    public static final Creator<ShopList> CREATOR = new Creator<ShopList>() {
        @Override
        public ShopList createFromParcel(Parcel in) {
            return new ShopList(in);
        }

        @Override
        public ShopList[] newArray(int size) {
            return new ShopList[size];
        }
    };

    // Factory method to construct a ShopList from JSONObject.
    //
    public static ShopList fromJSONObject(JSONObject jo) throws JSONException {
        List<Item> items = new ArrayList<>();
        List<Item> customItems = new ArrayList<>();
        JSONArray itemsJSONArray = jo.getJSONArray("items");
        for (int i = 0; i < itemsJSONArray.length(); i++) {
            JSONObject curr = itemsJSONArray.getJSONObject(i);
            items.add(Item.fromJSONObject(curr));
        }
        JSONArray customItemsJSONArray = jo.optJSONArray("customItems");
        for (int i = 0; i < customItemsJSONArray.length(); i++) {
            JSONObject curr = customItemsJSONArray.getJSONObject(i);
            JSONArray matchingItems = curr.optJSONArray("matchingItems");
            customItems.add(Item.customItemFromJSONObject(curr, matchingItems));
        }
        return new ShopList(
                jo.getLong("id"),
                jo.getString("name"),
                items,
                customItems
        );
    }

    public ShopList(long id, String name, List<Item> items, List<Item> customItems) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.customItems = customItems;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getCustomItems() {
        return customItems;
    }

    public void setCustomItems(List<Item> customItems) {
        this.customItems = customItems;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeTypedList(items);
        dest.writeTypedList(customItems);
    }
}


// EOF
