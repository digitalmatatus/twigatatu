package twigatatu.digitalmatatus.com.newtwigatatu.controllers;

import android.content.Context;

import com.android.volley.Request;

import org.json.JSONObject;

import Interface.ServerCallback;
import Interface.VolleyCallback;
import twigatatu.digitalmatatus.com.newtwigatatu.model.Get;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils;

/**
 * Created by stephineosoro on 29/03/2018.
 */

public class GetData {

    Context context;

    public GetData(Context context) {
        this.context = context;
    }

    public void online_stops(final String t_url, final ServerCallback callback) {
            Get.getResponse(Request.Method.GET, Utils.baseURL() + t_url, null, context, null,
                    new VolleyCallback() {
                        @Override
                        public void onSuccessResponse(String result) {

                                String res= result;
                                callback.onSuccess(res);
                        }

                        @Override
                        public void onSuccessResponse(JSONObject response) {

                        }
                    });
        }


}
