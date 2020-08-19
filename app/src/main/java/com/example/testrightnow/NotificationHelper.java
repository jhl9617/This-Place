package com.example.testrightnow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.io.File;

public class NotificationHelper {
    private Context mContext;
    private NotificationManager notificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message, String token) {

        Intent resultIntent = new Intent(mContext , CameraActivity.class);
        resultIntent.putExtra("token",token);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false) //클릭하게 되면 사라지도록...
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000, 1000 }) //노티가 등록될 때 진동 패턴
                .setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){ // 8.0 이상
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelName = "NOTIFICATION_CHANNEL_NAME";
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
    public void createNotification2(String title, String message, String token) {

        Intent resultIntent = new Intent(mContext , TalkActivity.class);
        resultIntent.putExtra("token",token);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false) //클릭하게 되면 사라지도록...
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[] { 1000, 1000, 1000 }) //노티가 등록될 때 진동 패턴
                .setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){ // 8.0 이상
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelName = "NOTIFICATION_CHANNEL_NAME";
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
    public void callSettingNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE,mContext.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID,NOTIFICATION_CHANNEL_ID);
            mContext.startActivity(intent);
        }
    }

    public void cancelNotification(Context ctx, int notifyId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationManager mNotificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
        } else {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notifyId);
        }
    }
}
