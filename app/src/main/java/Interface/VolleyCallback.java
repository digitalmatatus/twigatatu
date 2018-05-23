package Interface;

import org.json.JSONObject;

/**
 * Created by stephineosoro on 30/03/2018.
 */

public interface VolleyCallback {
    void onSuccessResponse(String result);
    void onSuccessResponse(JSONObject response);
}
