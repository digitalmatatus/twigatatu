package com.digitalmatatus.twigatatu.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import Interface.ServerCallback;

import static com.digitalmatatus.twigatatu.utils.Utils.checkDefaults;
import static com.digitalmatatus.twigatatu.utils.Utils.getToken;
import static com.digitalmatatus.twigatatu.utils.Utils.hasInternetConnected;
import static com.digitalmatatus.twigatatu.utils.Utils.jwtAuthHeaders;
import static com.digitalmatatus.twigatatu.utils.Utils.set;
import static com.digitalmatatus.twigatatu.utils.Utils.showToast;

public class Login extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    EditText _password, _username;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _password = findViewById(R.id.password);
        _username = findViewById(R.id.username);

        if (!hasInternetConnected(this)) {
            showToast("please turn on your internet connection to login", this);
            return;
        }

        // Automatically sign in if credentials are existing
        signin();

        Button btn2 = findViewById(R.id.btn_login);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {
                    showToast("Please correct the above errors", getBaseContext());
                    return;
                }

                set(_username.getText().toString(), _password.getText().toString(), getBaseContext());

                //TODO remove these hardcoded credentials used for coding tests
//                set("test", "Medic2018", getBaseContext());

                signin();


            }
        });

        Button btn = findViewById(R.id.register);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Register.class);
                startActivity(intent);
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String password = _password.getText().toString();
        String username = _username.getText().toString();


        if (username.isEmpty()) {
            _username.setError("Enter your username");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _password.setError("Enter your password");
            valid = false;
        } else {
            _password.setError(null);
        }


        return valid;
    }

    private void signin() {
        if (checkDefaults("password", getBaseContext())) {
            getToken(getBaseContext(), new ServerCallback() {
                @Override
                public void onSuccess(String result) {
//                    TODO remove this log - insecure
                    Log.e("jwt token", result);
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        String token = jsonObject.getString("token");

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getting token error", "token error");

                        showToast("Please enter the correct credentials", getBaseContext());

                        Intent intent = new Intent(getBaseContext(), Login.class);
                        intent.putExtra("error", e.toString());
                        startActivity(intent);
                    }

                }

                @Override
                public void onSuccess(JSONObject response) {

                }
            });
        }
    }
}
