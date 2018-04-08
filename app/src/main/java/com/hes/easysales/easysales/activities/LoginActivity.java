package com.hes.easysales.easysales.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegister, tvHack;
    private Button btnLogin;
    private Button.OnClickListener btnLoginListener;
    private SharedPreferences sharedPrefs;
    private Map<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefs = getSharedPreferences(Config.SH_PREFS_NAME, Context.MODE_PRIVATE);
        if (!getUserToken().equals(Config.DEF_NO_TOKEN)) {
            showMainActivity();
        }
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);
        tvHack = findViewById(R.id.tvHack);

        btnLogin.setOnClickListener(btnLoginListener);
        tvRegister.setOnClickListener(registerListener);
        tvHack.setOnClickListener(hackListener);
    }

    {
        btnLoginListener = new Button.OnClickListener() {
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(R.string.invalidUser)
                                    .setNegativeButton(R.string.loginRetry, null)
                                    .create()
                                    .show();
                        } else {
                            setUserToken(response);
                            showMainActivity();
                        }
                    }
                };

                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                        Toast.makeText(getApplicationContext(), R.string.vLoginError, Toast.LENGTH_LONG).show();
                    }
                };

                APIRequests.RequestHandler rh = APIRequests.formPOSTRequest(true, jsonPayload, null,
                        Config.URL_LOGIN, respListener, errListener, new WeakReference<>((Context) LoginActivity.this));
                rh.launch();
            }
        };
    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        }
    };

    private View.OnClickListener hackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(i, 0);
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

    private String getUserToken() {
        return sharedPrefs.getString(Config.KEY_TOKEN, Config.DEF_NO_TOKEN);
    }

    private void setUserToken(String userToken) {
        SharedPreferences.Editor e = sharedPrefs.edit();
        e.putString(Config.KEY_TOKEN, userToken);
        e.apply();
    }

    private void showMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            finish();
        }
    }
}


// EOF
