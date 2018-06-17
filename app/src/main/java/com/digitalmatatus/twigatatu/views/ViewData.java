package com.digitalmatatus.twigatatu.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Interface.ServerCallback;

import com.digitalmatatus.twigatatu.R;

import com.digitalmatatus.twigatatu.controllers.PostData;
import com.digitalmatatus.twigatatu.model.TransitWandProtos;
import com.digitalmatatus.twigatatu.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;

public class ViewData extends AppCompatActivity {

    public static String DELETE_ITEM_ACTION = "com.conveyal.transitwand.DELETE_ITEM_ACTION";

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction().equals(ReviewActivity.DELETE_ITEM_ACTION)) {
//                updateList();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Captured Data");
        applyFontForToolbarTitle(this);//        applyFontForToolbarTitle(this);

        if (getFilesDir().listFiles().length == 0) {
            Toast.makeText(ViewData.this, "No data to review.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ViewData.this, MainActivity2.class);
            startActivity(intent);
            finish();
            return;
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(ReviewActivity.DELETE_ITEM_ACTION));

        updateList();


    }

    private void updateList() {

        ArrayList<RouteCapture> routes = new ArrayList<RouteCapture>();

        for (File f : getFilesDir().listFiles()) {

            DataInputStream dataInputStream = null;

            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                //	byte[] dataFrame = new byte[(int)f.length()];;

                //	dataInputStream.read(dataFrame);

                TransitWandProtos.Upload.Route pbRouteData = TransitWandProtos.Upload.Route.parseDelimitedFrom(dataInputStream);

                RouteCapture rc = RouteCapture.deseralize(pbRouteData, getBaseContext());

                routes.add(rc);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        ListView captureListView = (ListView) findViewById(R.id.captureList);
        captureListView.setAdapter(new CaptureListAdapter(this, routes));

        captureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

               /* MapActivity.itemPosition = position;

                Intent mapIntent = new Intent(ReviewActivity.this, MapActivity.class);
                startActivity(mapIntent);*/
            }


        });

    }

}
