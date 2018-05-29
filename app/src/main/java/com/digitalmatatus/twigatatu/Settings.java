package com.digitalmatatus.twigatatu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.digitalmatatus.twigatatu.utils.Utils;
import com.digitalmatatus.twigatatu.views.MainActivity;

import net.igenius.customcheckbox.CustomCheckBox;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final CustomCheckBox scb = (CustomCheckBox) findViewById(R.id.scb);
        scb.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                Log.d("CustomCheckBox", String.valueOf(isChecked));
                Utils.setDefaults("data_collection","enabled",getBaseContext());
                Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                startActivity(intent);

            }
        });


    }

}
