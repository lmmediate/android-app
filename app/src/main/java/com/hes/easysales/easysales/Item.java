package com.hes.easysales.easysales;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sinopsys on 2/21/18.
 */

public class Item implements Parcelable {

    private long id;
    private String name;
    private String category;
    private String imageUrl;
    private double oldPrice;
    private double newPrice;
    private String discount;
    private String dateIn;
    private String dateOut;
    private String condition;
    private Shop shop;
    // These fields are initialized explicitly using setters!
    // Factory method keeps them default.
    // However, Parcel keeps them for future reuse.
    //
    private boolean expandable = false;
    private boolean matched = false;
    private List<Item> matchingItems = new ArrayList<>();

    // Factory method to construct an Item from JSONObject.
    //
    public static Item fromJSONObject(JSONObject jo) throws JSONException {
        return new Item(
                jo.getLong("id"),
                jo.optString("name"),
                jo.optString("category"),
                jo.optString("imageUrl"),
                jo.optDouble("oldPrice"),
                jo.optDouble("newPrice"),
                Shop.fromJSONObject(jo.optJSONObject("shop")),
                jo.optString("discount"),
                jo.optString("dateIn"),
                jo.optString("dateOut"),
                jo.optString("condition")
        );
    }

    public Item(Item i) {
        this.id = i.getId();
        this.name = i.getName();
        this.category = i.getCategory();
        this.imageUrl = i.getImageUrl();
        this.oldPrice = i.getOldPrice();
        this.newPrice = i.getNewPrice();
        this.shop = i.getShop();
        this.discount = i.getDiscount();
        this.dateIn = i.getDateIn();
        this.dateOut = i.getDateOut();
        this.condition = i.getCondition();
    }

    // Factory method to construct a CustomItem from JSONObject and an array of matching objects.
    //
    public static Item customItemFromJSONObject(JSONObject jo, JSONArray jMatchingItems) throws JSONException {
        Item item = new Item();
        List<Item> matchingItems = new ArrayList<>();
        for (int i = 0; i < jMatchingItems.length(); i++) {
            JSONObject curr = jMatchingItems.getJSONObject(i);
            Item ii = fromJSONObject(curr);
            ii.setMatched(true);
            matchingItems.add(ii);
        }
        item.setName(jo.optString("name"));
        item.setExpandable(true);
        item.setMatchingItems(matchingItems);
        return item;
    }

    public Item(Parcel in) {
        id = in.readLong();
        name = in.readString();
        category = in.readString();
        imageUrl = in.readString();
        oldPrice = in.readDouble();
        newPrice = in.readDouble();
        discount = in.readString();
        dateIn = in.readString();
        dateOut = in.readString();
        condition = in.readString();
        shop = in.readParcelable(Shop.class.getClassLoader());
        expandable = in.readByte() != 0;
        matched = in.readByte() != 0;
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

    private Item() {
    }

    private Item(long id,
                 String name,
                 String category,
                 String imageUrl,
                 double oldPrice,
                 double newPrice,
                 Shop shop,
                 String discount,
                 String dateIn,
                 String dateOut,
                 String condition) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.shop = shop;
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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(imageUrl);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
        dest.writeString(discount);
        dest.writeString(dateIn);
        dest.writeString(dateOut);
        dest.writeString(condition);
        dest.writeParcelable(shop, flags);
        dest.writeByte((byte) (expandable ? 1 : 0));
        dest.writeByte((byte) (matched ? 1 : 0));
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
        return shop.getId();
    }

    public Shop getShop() {
        return shop;
    }

    public String getShopName() {
        return shop.getName();
    }

    public String getShopAlias() {
        return shop.getAlias();
    }

    public long getId() {
        return id;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public boolean isMatched() {
        return matched;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public void setMatchingItems(List<Item> matchingItems) {
        this.matchingItems = matchingItems;
    }

    public static Comparator<Item> getShopIdComparator() {
        return new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getShopId() < o2.getShopId() ? -1 : 1;
            }
        };
    }

    public static Comparator<Item> getNewPriceComparator() {
        return new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.newPrice < o2.newPrice) {
                    return -1;
                } else if (o1.newPrice > o2.newPrice) {
                    return 1;
                }
                return 0;
            }
        };
    }
}


// EOF
