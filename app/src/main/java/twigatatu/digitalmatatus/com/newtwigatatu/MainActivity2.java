package twigatatu.digitalmatatus.com.newtwigatatu;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import twigatatu.digitalmatatus.com.newtwigatatu.R;

public class MainActivity2 extends AppCompatActivity {


    public static Boolean registered = true;
    public static Integer unitId = null;
    public static String userName = null;


    private SharedPreferences prefsManager = null;

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            if (key.equals("registered")) {

                updateRegistrationData();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SharedPreferences prefsManager = PreferenceManager.getDefaultSharedPreferences(MainActivity2.this);
        prefsManager.edit().putBoolean("registered", true).putString("unitId", "1234").putString("userName", "gtfs").commit();


        prefsManager = PreferenceManager.getDefaultSharedPreferences(this);
        prefsManager.registerOnSharedPreferenceChangeListener(prefListener);


        TextView userNameText = (TextView) findViewById(R.id.UserNameText);
        userNameText.setText("gtfs");

        TextView unitIdText = (TextView) findViewById(R.id.UnitIdText);
        unitIdText.setText("Unit1");


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.WandButton:

                        Intent settingsIntent = new Intent(MainActivity2.this, NewActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.uploadButton:

                        Intent mailIntent = new Intent(MainActivity2.this, ReviewActivity.class);
                        startActivity(mailIntent);
                        break;

                   /* case R.id.mapButton:

                        Intent uploadIntent = new Intent(MainActivity2.this, ShowMap.class);
                        startActivity(uploadIntent);

                        break;*/

                    default:
                        break;
                }
            }
        };

        ImageButton wandButton = (ImageButton) findViewById(R.id.WandButton);
        wandButton.setOnClickListener(listener);

        ImageButton reviewButton = (ImageButton) findViewById(R.id.uploadButton);
        reviewButton.setOnClickListener(listener);

        /*ImageButton uploadButton = (ImageButton) findViewById(R.id.UploadButton);
        uploadButton.setOnClickListener(listener);*/

    }


    public void updateRegistrationData() {

        if (prefsManager != null) {

            registered = prefsManager.getBoolean("registered", false);

            TextView userNameText = (TextView) findViewById(R.id.UserNameText);
            userNameText.setText(prefsManager.getString("userName", ""));

            TextView unitIdText = (TextView) findViewById(R.id.UnitIdText);
            unitIdText.setText("Unit " + prefsManager.getString("unitId", "unregistered"));
        }
    }

}
