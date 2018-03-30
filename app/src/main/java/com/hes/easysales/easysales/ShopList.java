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

    private String name;
    private List<Item> items;
    private List<Item> customItems;


    protected ShopList(Parcel in) {
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
        JSONArray customItemsJSONArray = jo.getJSONArray("customItems");
        for (int i = 0; i < customItemsJSONArray.length(); i++) {
            JSONObject curr = customItemsJSONArray.getJSONObject(i);
            JSONArray matchingItems = curr.getJSONArray("matchingItems");
            customItems.add(Item.customItemFromJSONObject(curr, matchingItems));
        }
        return new ShopList(
                jo.getString("name"),
                items,
                customItems
        );
    }

    public ShopList(String name, List<Item> items, List<Item> customItems) {
        this.name = name;
        this.items = items;
        this.customItems = customItems;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(items);
        dest.writeTypedList(customItems);
    }
}


// EOF
