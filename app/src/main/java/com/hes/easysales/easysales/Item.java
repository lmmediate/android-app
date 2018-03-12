package com.hes.easysales.easysales;

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

    public Item() {
    }

    public Item(String name,
                String category,
                String imageUrl,
                double oldPrice,
                double newPrice,
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
}


// EOF
