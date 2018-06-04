package com.digitalmatatus.twigatatu.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.utils.Utils;

import net.igenius.customcheckbox.CustomCheckBox;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);


        final CustomCheckBox scb = findViewById(R.id.scb);
        scb.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                Log.d("CustomCheckBox", String.valueOf(isChecked));
                Utils.setDefaults("data_collection", "enabled", getBaseContext());
                Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                startActivity(intent);

            }
        });


    }

}
