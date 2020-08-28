package com.sinan.sms.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.sinan.sms.MainActivity;
import com.sinan.sms.R;
import com.sinan.sms.pojo.SmsPojo;
import com.sinan.sms.sender.RequestSender;


public class ReceiveSmsControl extends Service {

    private final String TAG = this.getClass().getSimpleName();
    public static final String pdu_type = "pdus";
    private static BroadcastReceiver br_smsReceiver;
    private static final String CHANNEL_ID = "HEARTBEAT";
    private static final int SERVICE_NOTIFICATION_ID = 12345;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "HEARTBEAT", importance);
            channel.setDescription("CHANEL DESCRIPTION");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void registerSmsListener(){
        br_smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getSms(context,intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(2147483647);
        registerReceiver(br_smsReceiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getSms(Context context, Intent intent){
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                if (isVersionM) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                String sender = msgs[i].getOriginatingAddress();
                String message= msgs[i].getMessageBody();

                SmsPojo sms = new SmsPojo();
                sms.setDeviceID(MainActivity.deviceID);
                sms.setSender(sender);
                sms.setMessage(message);
                RequestSender.sendRequest(context,sms);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerSmsListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ZNET SMS Service")
                .setContentText("ÇALIŞIYOR...")
                .setSmallIcon(R.mipmap.sms_icon)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
