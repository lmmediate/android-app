package com.hes.easysales.easysales;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sinopsys on 3/12/18.
 */

public class AuthRequest extends JsonObjectRequest {

    private static final String LOGIN_REQUEST_URL = "http://46.17.44.125:8080/auth/login";
    private Map<String, String> params;
    private RequestQueue queue;

    public AuthRequest(String username, String password, Response.Listener<JSONObject> listener, RequestQueue queue) {
        super(Method.POST, LOGIN_REQUEST_URL, null, listener, null);
        this.queue = queue;
        params = new HashMap<>();
        params.put("Content-Type", "application/json; charset=utf-8");
        params.put("username", username);
        params.put("password", password);
    }

    public void login(String username, String password) {
        // Send request to the server, get token and put it in the local storage.
        //
        queue.add(this);
    }

    public String register(String username, String email, String age, String password) {
        return "";
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return params;
    }
}


// EOF
