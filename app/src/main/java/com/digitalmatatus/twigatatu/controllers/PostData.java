package com.digitalmatatus.twigatatu.controllers;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import Interface.ServerCallback;
import Interface.VolleyCallback;
import com.digitalmatatus.twigatatu.model.Post;
import com.digitalmatatus.twigatatu.utils.Utils;

/**
 * Created by stephineosoro on 08/05/2018.
 */

public class PostData {
    Context context;

    public PostData(Context context) {
        this.context = context;
    }

    public void post(final String t_url, final Map<String, String> parameters, final JSONObject params, final ServerCallback callback) {

        if (parameters != null) {
            Post post = new Post(context);
            post.PostString(Utils.baseURL() + t_url, parameters, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    callback.onSuccess(result);
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                }
            });
        } else {

            Post post = new Post(context);
            post.PostJSON(Utils.baseURL() +t_url, params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                    callback.onSuccess(response);
                }
            });
        }
    }

    public void post2(final String t_url, final Map<String, String> parameters, final JSONObject params, final Map<String, String> headers, final ServerCallback callback) {

        if (parameters != null) {
            Post post = new Post(context);
            post.PostStringJWT("http://41.89.64.3/ma3tycoon/" + t_url, parameters,headers, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    callback.onSuccess(result);
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                }
            });
        } else {

            Post post = new Post(context);
            post.PostJSONJWT("http://41.89.64.3/ma3tycoon/" +t_url, params,headers, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                }

                @Override
                public void onSuccessResponse(JSONObject response) {
                    callback.onSuccess(response);
                }
            });
        }
    }


}
