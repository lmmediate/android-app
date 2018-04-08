package com.hes.easysales.easysales.activities;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.Config;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.utilities.JSONUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private Map<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.etPassword = findViewById(R.id.etRegPassword);
        this.etUsername = findViewById(R.id.etRegUsername);
        this.btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(registerListener);
    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkFields()) {
                return;
            }

            final String username = etUsername.getText().toString();
            final String password = etPassword.getText().toString();
            userData = new HashMap<>();
            userData.put(Config.KEY_USERNAME, username);
            userData.put(Config.KEY_PASSWORD, password);
            final JSONObject jsonPayload = JSONUtil.formPayload(userData);

            Response.Listener<String> respListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (TextUtils.isEmpty(response) || response.contains(Config.BAD_API_AUTH_RESPONSE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage(R.string.invalidUser)
                                .setNegativeButton(R.string.loginRetry, null)
                                .create()
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.user_success_register) + " " + username, Toast.LENGTH_LONG).show();
                        RegisterActivity.this.finish();
                    }
                }
            };

            Response.ErrorListener errListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                    etUsername.setError(getString(R.string.invalidUser));
                }
            };

            APIRequests.RequestHandler rh = APIRequests.formPOSTRequest(
                    true,
                    jsonPayload,
                    null,
                    Config.URL_REGISTER,
                    respListener,
                    errListener,
                    new WeakReference<>((Context) RegisterActivity.this));
            rh.launch();
        }
    };

    // If the result is false then there is an error in one of the fields,
    // and an error on that field was shown.
    //
    private boolean checkFields() {
        boolean noErrors = true;
        if (TextUtils.isEmpty(etUsername.getText())) {
            etUsername.setError(getString(R.string.emptyUsername));
            noErrors = false;
        }

        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError(getString(R.string.emptyPassword));
            noErrors = false;
        }
        return noErrors;
    }
}
