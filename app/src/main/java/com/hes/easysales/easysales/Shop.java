package com.hes.easysales.easysales;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hes.easysales.easysales.Config.URL_SALES_SHOP;

/**
 * Created by sinopsys on 4/3/18.
 */

public class Shop implements Parcelable {

    private int id;
    private String alias;
    private String name;

    public Shop(Parcel in) {
        id = in.readInt();
        alias = in.readString();
        name = in.readString();
    }

    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(alias);
        dest.writeString(name);
    }

    public static Shop fromJSONObject(JSONObject jo) throws JSONException {
        return jo == null ? null : new Shop(
                jo.getInt("id"),
                jo.getString("alias"),
                jo.getString("name")
        );
    }

    public Shop(int id, String alias, String name) {
        this.id = id;
        this.alias = alias;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public String getURL() {
        return URL_SALES_SHOP + getId();
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


// EOF
