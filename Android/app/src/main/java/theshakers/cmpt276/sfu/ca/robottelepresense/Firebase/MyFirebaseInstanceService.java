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
    private final String TAG = "MyFirebaseInstanceS";

    public static void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FBToken", "Refreshed token: " + refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("MsgRec","From: " + remoteMessage.getFrom());
        //getForegroundActivity();
       //showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {
            Log.d("MsgRec", "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                String path = jsonObject.getString("path");
                Log.d("MsgRec", "path: " + path);
                if(getForegroundActivity().equals("theshakers.cmpt276.sfu.ca.robottelepresense.RequestGameActivity"))
                    if (path.equals("acceptgame"))
                        acceptGame(jsonObject.getString("pepper_username"), jsonObject.getString("hint"), jsonObject.getString("word"));
                if(getForegroundActivity().equals("theshakers.cmpt276.sfu.ca.robottelepresense.GameActivity")) {
                    if (path.equals("vibration"))
                        vibrate();
                    else if (path.equals("song"))
                        song();
                    else if (path.equals("endgame"))
                        endGame(jsonObject.getInt("victory"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private  void song() {
        MediaPlayer soundForWrong = MediaPlayer.create(getApplicationContext(), R.raw.loserbell);
        soundForWrong.start();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(35000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(35000);
        }
    }

    private void endGame(int victory) {
        Log.d(TAG, "endGame victory: "+victory);
        Intent intent = new Intent(getApplicationContext(), GameResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("victory", victory);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void acceptGame(String pepper_username, String hint, String word) {
        Log.d(TAG, "acceptGame pepper: "+pepper_username+", hint: "+hint+", word: "+word);
        word = word.toUpperCase();
        Log.d(TAG, "change to upper case" + word);
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

    private String retriveNewApp() {
        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (applist != null && applist.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : applist) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            Log.e(TAG, "Current App in foreground is: " + currentApp);

            return currentApp;

        }
        else {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            Log.e(TAG, "Current App in foreground is: " + mm);
            return mm;
        }
    }
}
