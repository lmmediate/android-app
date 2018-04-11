package com.hes.easysales.easysales;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hes.easysales.easysales.activities.MainActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sinopsys on 3/27/18.
 */

public class APIRequests {

    public static RequestHandler formGETRequest(String requestUrl, final Map<String, String> headers, Response.Listener rl, Response.ErrorListener rel, WeakReference<Activity> c) {
        StringRequest sr = new StringRequest(Request.Method.GET, requestUrl, rl, rel) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }
        };
        RequestHandler gr = new RequestHandler(sr, c.get());
        return gr;
    }

    public static RequestHandler formPOSTRequest(final boolean isJSONRequest, final JSONObject jsonPayload, final Map<String, String> headers, String requestUrl, Response.Listener rl, Response.ErrorListener rel, WeakReference<Context> c) {
        StringRequest sr = new StringRequest(Request.Method.POST, requestUrl, rl, rel) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    if (jsonPayload == null) {
                        return super.getBody();
                    }
                    return jsonPayload.toString() == null ? null : jsonPayload.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonPayload.toString(), "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                if (isJSONRequest) {
                    return Config.REQUESTS_CONTENT_TYPE;
                } else {
                    return null;
                }
            }
        };
        RequestHandler gr = new RequestHandler(sr, (Activity) c.get());
        return gr;
    }

    public static RequestHandler formDELETERequest(final Map<String, String> headers, String requestUrl, Response.Listener rl, Response.ErrorListener rel, WeakReference<Context> c) {
        StringRequest sr = new StringRequest(Request.Method.DELETE, requestUrl, rl, rel) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }
        };
        RequestHandler gr = new RequestHandler(sr, (Activity) c.get());
        return gr;
    }

    public static class RequestHandler {
        private Request r;
        private Activity c;
        private RequestQueue rq;

        private RequestHandler() {
        }

        private RequestHandler(Request r, Activity c) {
            this.r = r;
            this.c = c;
        }

        public void launch() {
            if (c instanceof MainActivity)
                rq = ((MainActivity) c).queue;
            else
                rq = Volley.newRequestQueue(c);
            rq.add(r);
        }
    }

    public static class ItemsGETRequest {
        private String shopId, category, pageNum;

        public ItemsGETRequest(String shopId, String category, String pageNum) {
            this.category = category;
            this.shopId = shopId;
            this.pageNum = pageNum;
        }

        public String getURL() {
            return Config.URL_SALES_SHOP + shopId +
                    Config.URL_ITEMS_IN_CATEGRY + category +
                    Config.URL_ITEMS_ON_PAGE + pageNum;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPageNum() {
            return pageNum;
        }

        public void setPageNum(String pageNum) {
            this.pageNum = pageNum;
        }
    }

    public static class ShopListPOSTRequest {
        private String shopListId, itemId;

        public ShopListPOSTRequest(String shopListId, String itemId) {
            this.shopListId = shopListId;
            this.itemId = itemId;
        }

        public String getAddURL() {
            return Config.URL_SHOPLIST + shopListId + "/" +
                    Config.URL_SL_ADD_ITEM + itemId;
        }

        public String getDeleteURL() {
            return Config.URL_SHOPLIST + shopListId + "/" +
                    Config.URL_SL_DELETE_ITEM + itemId;
        }

        public String getDeleteCustomURL() {
            return Config.URL_SHOPLIST + shopListId + "/" +
                    Config.URL_SL_DELETE_CUSTOM_ITEM + itemId;
        }

        public String getAddItemURL() {
            return Config.URL_SHOPLIST + shopListId + "/" +
                    Config.URL_SL_ADD_CUSTOM_ITEM + itemId;
        }
    }
}


// EOF
