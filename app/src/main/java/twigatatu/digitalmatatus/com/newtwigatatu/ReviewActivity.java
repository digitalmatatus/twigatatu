package twigatatu.digitalmatatus.com.newtwigatatu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Interface.ServerCallback;
import twigatatu.digitalmatatus.com.newtwigatatu.TransitWandProtos.Upload;
import twigatatu.digitalmatatus.com.newtwigatatu.controllers.PostData;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils;

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

public class ReviewActivity extends AppCompatActivity {

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
        setTitle("Twiga Tatu");
//        applyFontForToolbarTitle(this);

        if (getFilesDir().listFiles().length == 0) {
            Toast.makeText(ReviewActivity.this, "No data to review.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(ReviewActivity.DELETE_ITEM_ACTION));

//        updateList();
//        groupData();
        sendData();
    }

    private void sendData() {
        JSONArray ja = null;
        try {
            if (Utils.checkDefaults("data", getBaseContext())) {
                ja = new JSONArray(Utils.getDefaults("data", getBaseContext()));
                Log.e("jsonArray", ja.toString());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("data", ja);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("js", jsonObject.toString());
                PostData post = new PostData(getBaseContext());
                post.post("ride/", null, jsonObject, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
//				Log.e("ride result",result.toString());


                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.e("response result", response.toString());
                        Toast.makeText(ReviewActivity.this, "Data uploaded.", Toast.LENGTH_SHORT).show();
                        TextView tv = findViewById(R.id.descriptionText);
                        tv.setText("Data Uploaded");

                        for (File f : getFilesDir().listFiles()) {
                            f.delete();
                        }
                        Utils.Delete(getBaseContext());

//				TODO Do something here if it was successful
                    }
                });
            } else{
                Toast.makeText(getBaseContext(),"No data",Toast.LENGTH_LONG);
                TextView tv = findViewById(R.id.descriptionText);
                tv.setText("No data to upload");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateList() {

        ArrayList<RouteCapture> routes = new ArrayList<RouteCapture>();
        JSONArray ja = new JSONArray();

        for (File f : getFilesDir().listFiles()) {

            DataInputStream dataInputStream = null;

            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

                //	byte[] dataFrame = new byte[(int)f.length()];;

                //	dataInputStream.read(dataFrame);

                Upload.Route pbRouteData = Upload.Route.parseDelimitedFrom(dataInputStream);

//                RouteCapture rc = RouteCapture.deseralize(pbRouteData,getBaseContext());
                JSONObject jo = RouteCapture.jsonify(pbRouteData, getBaseContext());
                ja.put(jo);

//                routes.add(rc);

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
                startActivity(mapIntent);
*/            }


        });

    }
}
