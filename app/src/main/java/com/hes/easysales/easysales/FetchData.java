package com.hes.easysales.easysales;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sinopsys on 2/18/18.
 */

public class FetchData extends AsyncTask<Void, Void, String> {

    private StringBuilder data = new StringBuilder();
    private StringBuilder dataParsed = new StringBuilder();
    private StringBuilder singleParsed = new StringBuilder();

    // To prevent the leak of Context.
    //
    private WeakReference<Activity> activityRef;
    FetchData(Activity a){
        activityRef = new WeakReference<>(a);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("http://46.17.44.125:8080/api/sales");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line != null) {
                line = br.readLine();
                data.append(line);
            }

            JSONArray jsonArray = new JSONArray(this.data.toString());
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                singleParsed
                        .append("Name: ")    .append(jo.get("name")).append("\n")
                        .append("Category: ").append(jo.get("category")).append("\n")
                        .append("ImageUrl: ").append(jo.get("imageUrl")).append("\n")
                        .append("newPrice: ").append(jo.get("newPrice")).append("\n");

                dataParsed.append(singleParsed).append("\n");
                singleParsed = new StringBuilder();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return dataParsed.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        TextView tv = activityRef.get().findViewById(R.id.fetchedData);
        tv.setText(s);
    }
}


// EOF
