package com.hes.easysales.easysales.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by sinopsys on 3/16/18.
 */

public class JSONUtil {
    public static JSONObject formPayload(Map<String, String> kvs) {
        JSONObject jsonPayload = new JSONObject();
        Iterator it = kvs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            try {
                jsonPayload.put(String.valueOf(pair.getKey()), pair.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            it.remove();
        }
        return jsonPayload;
    }
}
