package com.aro.countdown;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {

    int numberOfNotificationCountdowns = 0;

    List<LocalDateTime> dateList;
    List<String> nameOfCountdownList;
    List<Boolean> booleanList;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.


        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dateList = new ArrayList<>();
        nameOfCountdownList = new ArrayList<>();
        booleanList = new ArrayList<>();

        getData();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        backgroundTimer();
    }


    private void getData(){

        SharedPreferences getData = getSharedPreferences("snapshot_notifications", MODE_PRIVATE);

        numberOfNotificationCountdowns = getData.getInt("countdownlistsize", 0);

        booleanList.clear();
        dateList.clear();
        nameOfCountdownList.clear();


        for (int i = 0; i < numberOfNotificationCountdowns; i++) {

            int notifNum = i + 1;

            //get the title of the countdown stored in sharedpref and add to list
            String nameOfCountdown = getData.getString("countdown" + notifNum + "name", "null");
            nameOfCountdownList.add(nameOfCountdown);

            //get the date info, construct the date, add it to the list



            int day = getData.getInt("timer" + notifNum + "day", 1);
            int month = getData.getInt("timer" + notifNum + "month", 1);
            int year = getData.getInt("timer" + notifNum + "year", 2020);
            int hour = getData.getInt("timer" + notifNum + "hour", 1);
            int min = getData.getInt("timer" + notifNum + "min", 1);
            int sec = getData.getInt("timer" + notifNum + "sec", 1);

            LocalDateTime storedDate = LocalDateTime.of(year, month, day, hour, min, sec);
            dateList.add(storedDate);


            //this adds a bool to the list to be used as a switch for each notification
            booleanList.add(false);

        }
    }

    private void backgroundTimer(){

        //update per tick and check if finished
        Handler handler = new Handler();
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < dateList.size(); i++) {

                            Boolean ranOnce = booleanList.get(i);

                            LocalDateTime countdownTimerLDT = dateList.get(i);

                            if(!ranOnce && numberOfNotificationCountdowns > 0){

                                LocalDateTime now = LocalDateTime.now();

                                //if the time passes fire the notification
                                if (countdownTimerLDT.isBefore(now)) {


                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "Aro Countdown Channel Id")
                                            .setSmallIcon(R.drawable.notification_icon)
                                            .setContentTitle("Countdown Complete!")
                                            .setContentText(nameOfCountdownList.get(i) + " countdown has finished.")
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                    int notificationId = 15015411;

                                    notificationManager.notify(notificationId, notificationBuilder.build());


                                    //flip the switch so that the notification will not run again
                                    booleanList.add(i, true);

                                    numberOfNotificationCountdowns = numberOfNotificationCountdowns -1;

                                }
                            }
                        }
                        handler.postDelayed(this, 500);
                    }
                });
        thread.start();
    }


}