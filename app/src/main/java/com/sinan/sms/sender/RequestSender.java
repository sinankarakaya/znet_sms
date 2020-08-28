package com.sinan.sms.sender;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sinan.sms.pojo.SmsPojo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestSender {
    static String url = "https://poscihazi.com/sms_v2.php?action=create";
    //private static String url = "https://sinan.free.beeceptor.com/sms_v2?action=create";

    public static void sendRequest(Context context, SmsPojo sms){
        String secretKey="";
        try {
            secretKey = sha1(sms.getMessage()+"."+sms.getDeviceID()+"."+getDateNow());
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG);
        }

        url+="&deviceID="+sms.getDeviceID()+"&sender="+sms.getSender()+"&message="+sms.getMessage()+"&key="+secretKey;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'-'HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
