package peticionesHTTP;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by julio on 07/11/2017.
 */

// paara datos por post
public    class LoginRequest extends StringRequest {
    private static final  String LOGIN_REQUEST_URL="";
    public LoginRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }
}
