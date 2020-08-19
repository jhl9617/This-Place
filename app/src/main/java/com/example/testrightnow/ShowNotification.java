package com.example.testrightnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class ShowNotification extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);
        context = getBaseContext();

        TextView textView = (TextView) findViewById(R.id.txt_notiview);
        textView.setText("Notification replyview");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.cancelNotification(context,0);
    }
}
