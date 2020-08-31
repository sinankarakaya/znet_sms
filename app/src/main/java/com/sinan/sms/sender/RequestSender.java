package com.sinan.sms.sender;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sinan.sms.pojo.SmsPojo;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestSender {
    private final static String url = "https://poscihazi.com";



    public static void sendRequest(final Context context, final SmsPojo sms){
        String sendUrl = url+"/sms_v2.php?action=create";

        String secretKey="";
        try {
            secretKey = sha1(sms.getMessage()+sms.getDeviceID()+getDateNow()+"create");
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG);
        }

        sendUrl+="&deviceID="+sms.getDeviceID()+"&sender="+sms.getSender()+"&message="+sms.getMessage()+"&key="+secretKey;

        final RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, sendUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context,"Sunucuya Gönderildi",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context,"Gönderim Hatası"+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    static String getDateNow(){
        //SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'-'HH:mm:ss");
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'-'HH");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
