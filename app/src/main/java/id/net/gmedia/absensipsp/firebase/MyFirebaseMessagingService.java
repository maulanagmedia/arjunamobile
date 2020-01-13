package id.net.gmedia.absensipsp.firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import id.net.gmedia.absensipsp.Constant;
import id.net.gmedia.absensipsp.MainActivity;
import id.net.gmedia.absensipsp.R;
import id.net.gmedia.absensipsp.SessionManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> isiData = new HashMap<>();

        String title = getString(R.string.app_name);
        String body = "anda mendapat notifikasi";

        if(remoteMessage.getData() != null){
            /*title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            if(remoteMessage.getData().containsKey("type")){
                type = remoteMessage.getData().get("type");
            }*/

            Log.d(Constant.TAG, "data : " + remoteMessage.getData());
            isiData = remoteMessage.getData();
        }

        if(remoteMessage.getNotification() != null){
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        sendNotification(title, body, new HashMap<>(isiData));
    }

    private void sendNotification(String title, String messageBody, Map<String, String> isiData) {
        Intent intent = new Intent(this, MainActivity.class);
        /*if(isiData.containsKey("tipe")){
            intent.putExtra(Constant.EXTRA_APPROVAL, isiData.get("tipe"));
        }*/

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    "Arjuna Mobile",
                    NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
            channel.setDescription("Arjuna Mobile");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(Constant.TAG, "Refreshed token: " + s);

        SessionManager.setFcm(this, s);
    }
}
