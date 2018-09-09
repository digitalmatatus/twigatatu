package com.digitalmatatus.twigatatu.controllers;

import android.content.Context;

import com.android.volley.Request;

import org.json.JSONObject;

import Interface.ServerCallback;
import Interface.VolleyCallback;

import com.digitalmatatus.twigatatu.model.Get;
import com.digitalmatatus.twigatatu.utils.Utils;

import java.util.Map;

/**
 * Created by stephineosoro on 29/03/2018.
 */

public class GetData {

    Context context;

    public GetData(Context context) {
        this.context = context;
    }

    public void get(final String t_url, final ServerCallback callback) {
        Get.getResponse(Request.Method.GET, Utils.baseURL() + t_url, null, context, null,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {

                        String res = result;
                        callback.onSuccess(res);
                    }

                    @Override
                    public void onSuccessResponse(JSONObject response) {

                    }
                });
    }

    public void getLogin(final String t_url, final ServerCallback callback) {
        Get.getResponse(Request.Method.GET, "http://41.89.64.3/ma3tycoon/" + t_url, null, context, null,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {

                        String res = result;
                        callback.onSuccess(res);
                    }

                    @Override
                    public void onSuccessResponse(JSONObject response) {

                    }
                });
    }

    public void online_data(final String t_url, final Map<String, String> parameters, final Map<String, String> headers, final ServerCallback callback) {
        Get.getResponse(Request.Method.GET, "http://41.89.64.3/ma3tycoon/" + t_url, null, context, parameters, headers,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {

                        String res = result;
                        callback.onSuccess(res);
                    }

                    @Override
                    public void onSuccessResponse(JSONObject response) {

                    }
                });
    }

}
