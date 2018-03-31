package com.hes.easysales.easysales;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 2/21/18.
 */

public class Item implements Parcelable {

    private String name;
    private String category;
    private String imageUrl;
    private double oldPrice;
    private double newPrice;
    private String discount;
    private String dateIn;
    private String dateOut;
    private String condition;
    private int shopId;
    // These fields are initialized explicitly using setters!
    // Factory method keeps them default.
    // However, Parcel keeps them for future reuse.
    //
    private boolean expandable = false;
    private List<Item> matchingItems = new ArrayList<>();

    // Factory method to construct an Item from JSONObject.
    //
    public static Item fromJSONObject(JSONObject jo) throws JSONException {
        return new Item(
                jo.optString("name"),
                jo.optString("category"),
                jo.optString("imageUrl"),
                jo.optDouble("oldPrice"),
                jo.optDouble("newPrice"),
                jo.optInt("shopId"),
                jo.optString("discount"),
                jo.optString("dateIn"),
                jo.optString("dateOut"),
                jo.optString("condition")
        );
    }

    // Factory method to construct a CustomItem from JSONObject and an array of matching objects.
    //
    public static Item customItemFromJSONObject(JSONObject jo, JSONArray jMatchingItems) throws JSONException {
        Item item = new Item();
        List<Item> matchingItems = new ArrayList<>();
        for (int i = 0; i < jMatchingItems.length(); i++) {
            JSONObject curr = jMatchingItems.getJSONObject(i);
            matchingItems.add(fromJSONObject(curr));
        }
        item.setName(jo.optString("name"));
        item.setExpandable(true);
        item.setMatchingItems(matchingItems);
        return item;
    }

    public Item(Parcel in) {
        name = in.readString();
        category = in.readString();
        imageUrl = in.readString();
        oldPrice = in.readDouble();
        newPrice = in.readDouble();
        discount = in.readString();
        dateIn = in.readString();
        dateOut = in.readString();
        condition = in.readString();
        shopId = in.readInt();
        expandable = in.readByte() != 0;
        in.readTypedList(matchingItems, Item.CREATOR);
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    // FIXME it is public for debugging purposes.
    //
    public Item() {
    }

    private Item(String name,
                 String category,
                 String imageUrl,
                 double oldPrice,
                 double newPrice,
                 int shopId,
                 String discount,
                 String dateIn,
                 String dateOut,
                 String condition) {

        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.shopId = shopId;
        this.discount = discount;
        this.dateIn = dateIn;
        this.dateOut = dateOut;
        this.condition = condition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(imageUrl);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
        dest.writeString(discount);
        dest.writeString(dateIn);
        dest.writeString(dateOut);
        dest.writeString(condition);
        dest.writeInt(shopId);
        int exe = isExpandable() ? 1 : 0;
        dest.writeByte((byte) exe);
        dest.writeTypedList(matchingItems);
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public String getDateIn() {
        return dateIn;
    }

    public String getDateOut() {
        return dateOut;
    }

    public int getShopId() {
        return shopId;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public List<Item> getMatchingItems() {
        return matchingItems;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public void setMatchingItems(List<Item> matchingItems) {
        this.matchingItems = matchingItems;
    }
}


// EOF
