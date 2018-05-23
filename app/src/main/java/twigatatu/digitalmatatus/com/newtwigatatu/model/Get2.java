package twigatatu.digitalmatatus.com.newtwigatatu.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Interface.VolleyCallback;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.AppController;

//import org.apache.http.client.HttpClient;

/**
 * Created by stephineosoro on 30/03/2018.
 */

public class Get2 {

    private static String CSRFTOKEN;

    public static void getResponse(int method, final String url, JSONObject jsonValue, final Context mCtx, final Map<String, String> parameters, final VolleyCallback callback) {

//        queue = AppController.getInstance(mCtx).getRequestQueue();

        // Create a new HttpClient and Post Header HttpClient

// Get the CSRF token
/*
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    try {
                        DefaultHttpClient httpClient = new DefaultHttpClient();

                        httpClient.execute(new HttpGet("http://41.89.64.3"));
                        CookieStore cookieStore = httpClient.getCookieStore();
                        List<Cookie> cookies = cookieStore.getCookies();
                        for (Cookie cookie : cookies) {
                            Log.e("", cookie.toString());
                            if (cookie.getName().equals("XSRF-TOKEN")) {
                                CSRFTOKEN = cookie.getValue();

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();*/


        class RetrieveFeedTask extends AsyncTask<String, Void, String> {

            private Exception exception;

            protected String doInBackground(String... urls) {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    httpClient.execute(new HttpGet("http://41.89.64.3"));
                    CookieStore cookieStore = httpClient.getCookieStore();
                    List<Cookie> cookies = cookieStore.getCookies();
                    Log.e("all cookies", cookies.toString());
                    for (Cookie cookie : cookies) {
                        Log.e("cookies", cookie.toString());
                        if (cookie.getName().equals("csrftoken")) {
                            CSRFTOKEN = cookie.getValue();
                            Log.e("cookies found", CSRFTOKEN);


                        }
                    }


                    return CSRFTOKEN;
                } catch (Exception e) {
                    this.exception = e;
                    return null;
                }
            }

            protected void onPostExecute(String feed) {
                // TODO: check this.exception
                // TODO: do something with the feed

//                Log.e("CSRF", CSRFTOKEN);

                StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String Response) {
                        callback.onSuccessResponse(Response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        Toast.makeText(mCtx, e + "error", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", "gtfs");
                        params.put("password", "gtfs");
                        if (parameters != null) {
                            params = parameters;
                        }

//                params.put("categoryId", 2 + "");


                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                        setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                        headers.put("Content-Type", "application/json; charset=utf-8");

                        String creds = String.format("%s:%s", "gtfs", "gtfs");
                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                        headers.put("Authorization", auth);
                        headers.put("X-CSRF-TOKEN", CSRFTOKEN);
// csrfmiddlewaretoken
                        headers.put("csrftoken", CSRFTOKEN);
                        headers.put("csrfmiddlewaretoken", CSRFTOKEN);
                        headers.put("X-CSRF-Token", CSRFTOKEN);

                        return headers;
                    }


                };
                AppController.getInstance().addToRequestQueue(req);
                Log.e("request", req.toString());
            }

        }

        new RetrieveFeedTask().execute("");

    }
}
