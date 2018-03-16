package com.hes.easysales.easysales;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by sinopsys on 3/12/18.
 */

public class AuthRequest {
    private StringRequest stringRequest;
    private Response.Listener<String> respListener;
    private Response.ErrorListener errListener;

    public AuthRequest(Response.Listener<String> listener, Response.ErrorListener errListener) {
        this.respListener = listener;
        this.errListener = errListener;
    }

    public StringRequest login(final JSONObject jsonPayload) {
        stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN, respListener, errListener) {
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
                return Config.REQUESTS_CONTENT_TYPE;
            }
        };
        return stringRequest;
    }
}


// EOF
