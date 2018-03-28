package com.hes.easysales.easysales;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sinopsys on 2/21/18.
 */

public class Item {
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
    private boolean expandable = false;

    // Factory method to construct an Item from JSONObject.
    //
    public static Item fromJSONObject(JSONObject jo) throws JSONException {
        return new Item(
                jo.getString("name"),
                jo.getString("category"),
                jo.getString("imageUrl"),
                jo.getDouble("oldPrice"),
                jo.getDouble("newPrice"),
                jo.getInt("shopId"),
                jo.getString("discount"),
                jo.getString("dateIn"),
                jo.getString("dateOut"),
                jo.getString("condition")
        );
    }

    private Item() {
    }

    public Item(String name,
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
        this.discount = discount;
        this.dateIn = dateIn;
        this.dateOut = dateOut;
        this.condition = condition;
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

    public String getCondition() {
        return condition;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public int getShopId() {
        return shopId;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
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
}


// EOF
