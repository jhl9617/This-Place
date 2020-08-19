package com.example.testrightnow;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class howaboutthere extends StringRequest {
    final static private String URL = "http://27.96.134.241/htdocs/howaboutthere.php";
    private Map<String,String>parameters;

    public howaboutthere(String userid, String latitude, String longtitude, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
            parameters = new HashMap<>();
            parameters.put("userid",userid);
            parameters.put("latitude",latitude);
            parameters.put("longtitude",longtitude);
    }
    @Override
    protected Map<String,String>getParams() throws AuthFailureError{
        return parameters;
    }
}
