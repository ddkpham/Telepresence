package theshakers.cmpt276.sfu.ca.robottelepresense.Firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import theshakers.cmpt276.sfu.ca.robottelepresense.GameActivity;
import theshakers.cmpt276.sfu.ca.robottelepresense.GameResultActivity;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;

//This class is for receiving notification or message from CloudServer using FCM
public class MyFirebaseInstanceService extends FirebaseMessagingService {
    private final static String TAG = "MyFirebaseInstanceS";

    public static void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.d(TAG,"From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "remoteMessage " + remoteMessage.getData());
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                String path = jsonObject.getString("path");
                Log.d(TAG, "path: " + path);
                if(getForegroundActivity().equals("theshakers.cmpt276.sfu.ca.robottelepresense.RequestGameActivity")) {
                    if (path.equals("acceptgame")) {
                        acceptGame(jsonObject.getString("pepper_username"), jsonObject.getString("hint"), jsonObject.getString("word"));
                    }
                } else if(getForegroundActivity().equals("theshakers.cmpt276.sfu.ca.robottelepresense.GameActivity")) {
                    if (path.equals("androidanimation")) {
                        String animation = jsonObject.getString("animation");
                        if(animation.equals("vibration"))
                            vibrate();
                        else if (animation.equals("song"))
                            song();
                    } else if (path.equals("endgame"))
                        endGame(jsonObject.getString("victory"));
                } else if (getForegroundActivity().equals("theshakers.cmpt276.sfu.ca.robottelepresense.ChatActivity")) {
                    if (path.equals("proactive")) {
                        String msg = jsonObject.getString("msg");
                        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private  void song() {
        MediaPlayer soundForDistraction = MediaPlayer.create(getApplicationContext(), R.raw.distract_song);
        soundForDistraction.start();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        v.vibrate(new long[]{0,1000,500,1000}, -1);
    }

    private void endGame(String victory) {
        Log.d(TAG, "endGame victory: "+victory);
        Intent intent = new Intent(getApplicationContext(), GameResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("victory", victory);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void acceptGame(String pepper_username, String hint, String word) {
        Log.d(TAG, "acceptGame pepper: "+pepper_username+", hint: "+hint+", word: "+word);
        word = word.toUpperCase();
        Log.d(TAG, "change to upper case " + word);
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("pepper_username", pepper_username);
        bundle.putString("hint", hint);
        bundle.putString("word", word);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showNotification(String title, String body){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "theshakers.cmpt276.sfu.ca.robottelepresense";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("TELE Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
    }

    private String getForegroundActivity() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        Log.d(TAG, "getForegroundActivity:" + taskInfo.get(0).topActivity.getClassName() + "   Package Name :  " + componentInfo.getPackageName());
        return taskInfo.get(0).topActivity.getClassName();
    }
}
