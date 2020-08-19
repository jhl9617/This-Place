package com.example.testrightnow;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;





public class MyFireBaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "FCM";

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
        Log.d(TAG,"token["+s+"]");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String token = remoteMessage.getData().get("token");
        String a=remoteMessage.getData().get("a");
        Integer b = Integer.parseInt(a);
        Log.d(TAG,"From: "+ remoteMessage.getFrom());
        Log.d(TAG,"Title: "+title);
        Log.d(TAG,"Message: "+message);
        Log.d(TAG,"token: "+ token);
        if(b==1){
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.createNotification(title,message,token);
        }else{
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.createNotification2(title,message,token);
        }

    }
    /*@Override
    public void onNewToken(String token){
        Log.d("FCM Log", "Refreshed token: "+ token);

    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        if (remoteMessage.getNotification() != null){
            Log.d("FCM Log", "알림 메시지: " + remoteMessage.getNotification().getBody());
            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Channel ID";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String channelName = "Channel Name";
                NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);

            }

            notificationManager.notify(0, notificationBuilder.build());


        }
    }*/
}

