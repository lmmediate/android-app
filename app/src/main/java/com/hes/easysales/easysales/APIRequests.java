package com.hes.easysales.easysales;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

/**
 * Created by sinopsys on 3/27/18.
 */

public class APIRequests {

    public static RequestHandler formGETRequest(String requestUrl, Response.Listener rl, Response.ErrorListener rel, WeakReference<Context> c) {
        StringRequest sr = new StringRequest(Request.Method.GET, requestUrl, rl, rel);
        RequestHandler gr = new RequestHandler(sr, c.get());
        return gr;
    }

    public static RequestHandler formPOSTRequest(final boolean isJSONRequest, final JSONObject jsonPayload, String requestUrl, Response.Listener rl, Response.ErrorListener rel, WeakReference<Context> c) {
        StringRequest sr = new StringRequest(Request.Method.POST, requestUrl, rl, rel) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonPayload.toString() == null ? null : jsonPayload.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonPayload.toString(), "utf-8");
                    return null;
                }
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
        RequestHandler gr = new RequestHandler(sr, c.get());
        return gr;
    }

    public static class RequestHandler {
        private Request r;
        private Context c;
        private RequestQueue rq;

        private RequestHandler() {}

        private RequestHandler(Request r, Context c) {
            this.r = r;
            this.c = c;
        }

        public void launch() {
            rq = Volley.newRequestQueue(c);
            rq.add(r);
        }
    }
}


// EOF
