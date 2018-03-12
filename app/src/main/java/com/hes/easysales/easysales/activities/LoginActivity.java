package com.hes.easysales.easysales.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.hes.easysales.easysales.AuthRequest;
import com.hes.easysales.easysales.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvRegister;
    private Button btnLogin;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(btnLoginListener);
    }

    Button.OnClickListener btnLoginListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkFields()) {
                return;
            }
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
//            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                }
//            };
//            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//            // Send request to the server, obtain the token, store it in local storage.
//            //
//            AuthRequest requests = new AuthRequest(username, password, listener, queue);
//            requests.login(username, password);
//
//            if (!token.contains("Wrong")) {
//                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(i);
//            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                builder.setMessage(R.string.invalidUser)
//                       .setNegativeButton(R.string.loginRetry, null)
//                       .create()
//                       .show();
//            }
        }
    };

    // If the result is false then there is an error in one of the fields,
    // and an error on that field was shown.
    //
    private boolean checkFields() {
        boolean noErrors = true;
        if (TextUtils.isEmpty(etUsername.getText())) {
            Toast.makeText(getApplicationContext(), R.string.emptyUsername, Toast.LENGTH_SHORT).show();
            etUsername.setError(getString(R.string.emptyUsername));
            noErrors = false;
        }

        if (TextUtils.isEmpty(etPassword.getText())) {
            Toast.makeText(getApplicationContext(), R.string.emptyPassword, Toast.LENGTH_SHORT).show();
            etPassword.setError(getString(R.string.emptyPassword));
            noErrors = false;
        }
        return noErrors;
    }
}


// EOF
