package com.aro.countdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import com.aro.countdown.adapter.CountdownAdapter;
import com.aro.countdown.adapter.OnItemClickedListener;
import com.aro.countdown.model.Countdown;
import com.aro.countdown.model.CountdownViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnItemClickedListener {

    /*

    This activity will show the countdowns in a recyclerview.

     */

    private RecyclerView recyclerView;
    private CountdownAdapter countdownAdapter;

    public List<Countdown> countdownList;

    private SoundPool soundPool;

    private int soundComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);


        FloatingActionButton addCountdownButton = findViewById(R.id.add_countdown_button);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        countdownList = new ArrayList<>();
        CountdownViewModel countdownViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CountdownViewModel.class);

        createNotificationChannel();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();

        soundComplete = soundPool.load(this, R.raw.endsound, 1);


        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        countdownViewModel.getAllCountdowns().observe(this, countdowns ->{

            countdownList = countdowns;

            for(Countdown countdown : countdowns){

                if(countdown.getCountdownDate().isBefore(LocalDateTime.now())){
                    countdown.setDone(true);
                }
            }

            //set recyclerview
            countdownAdapter = new CountdownAdapter(countdownList, this);
            recyclerView.setAdapter(countdownAdapter);


            //update per tick and check if finished
            Handler handler = new Handler();
            Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            for(Countdown countdown : countdowns){

                                if (countdown.getCountdownDate().isAfter(LocalDateTime.now())){
                                    countdown.setDone(false);
                                }

                                //countdown has ended
                                if(countdown.getCountdownDate().isBefore(LocalDateTime.now())
                                        || countdown.getCountdownDate() == LocalDateTime.now()){
                                    if(!countdown.isDone){
                                        //play sound
                                        soundPool.play(soundComplete, 1, 1, 0, 0, 1);


                                        //if the countdown has notifications turned on send notification that it is finished
                                        if(countdown.getNotificationOn()){
                                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this, "Aro Countdown Channel Id")
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setContentTitle("Countdown Complete!")
                                                    .setContentText(countdown.getCountdownTitle() + " countdown has finished.")
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

                                            int notificationId = 15015411;

                                            notificationManager.notify(notificationId, notificationBuilder.build());

                                        }
                                    }
                                    countdown.setDone(true);

                                    countdown.setNotificationOn(false);
                                }
                            }

                            countdownAdapter.notifyDataSetChanged();

                            handler.postDelayed(this, 500);
                        }
                    });
            thread.start();

            sharedPref();

        });

        // Start android service.
        Intent startServiceIntent = new Intent(MainActivity.this, MyService.class);
        startService(startServiceIntent);


        addCountdownButton.setOnClickListener(this::addCountdownButtonMethod);

    }





    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Aro Countdown Channel Id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sharedPref(){

        int numberWithNotifications = 0;


        SharedPreferences sharedPreferences = getSharedPreferences("snapshot_notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //for each countdown in the list pass info about it if notifications are on
        for (int i = 0; i < countdownList.size(); i++) {

            Countdown countdown = countdownList.get(i);

            //if it has notifications on send title and date information via sharedpreferences
            if(countdown.getNotificationOn() && !countdown.getDone()){


                numberWithNotifications = numberWithNotifications + 1;

                String timerName = "countdown" + numberWithNotifications + "name";

                String dayString = "timer" + numberWithNotifications + "day";
                String monthString = "timer" + numberWithNotifications + "month";
                String yearString = "timer" + numberWithNotifications + "year";
                String hourString = "timer" + numberWithNotifications + "hour";
                String minString = "timer" + numberWithNotifications + "min";
                String secString = "timer" + numberWithNotifications + "sec";


                LocalDateTime countdownEnd = countdown.getCountdownDate();


                int day = countdownEnd.getDayOfMonth();
                int month = countdownEnd.getMonthValue();
                int year = countdownEnd.getYear();
                int hour = countdownEnd.getHour();
                int min = countdownEnd.getMinute();
                int sec = countdownEnd.getSecond();

                editor.putString(timerName, countdown.getCountdownTitle());
                editor.putInt(dayString, day);
                editor.putInt(monthString, month);
                editor.putInt(yearString, year);
                editor.putInt(hourString, hour);
                editor.putInt(minString, min);
                editor.putInt(secString, sec);

            }

        }


        editor.putInt("countdownlistsize", numberWithNotifications);

        editor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(soundPool != null){
            soundPool.release();
            soundPool = null;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(countdownAdapter != null){
            countdownAdapter.notifyDataSetChanged();
        }
    }



    private void addCountdownButtonMethod(View view) {

        Intent intent = new Intent(MainActivity.this, AddCountdownActivity.class);
        startActivity(intent);

    }


    @Override
    public void OnCountdownClickedListener(int position, Countdown countdown) {

        //open the countdown to edit and pass the id of the countdown clicked
        long id = countdown.getCountdownId();
        Intent intent = new Intent(MainActivity.this, AddCountdownActivity.class);
        intent.putExtra("countdown id", id);
        startActivity(intent);

    }


}