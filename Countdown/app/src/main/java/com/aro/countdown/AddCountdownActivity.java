package com.aro.countdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aro.countdown.model.Countdown;
import com.aro.countdown.model.CountdownViewModel;
import com.aro.countdown.util.Utils;

import java.time.LocalDateTime;

public class AddCountdownActivity extends AppCompatActivity {

      /*

    This activity will create new countdowns from user input and add them to the database.
    It will also be used to edit the countdowns.

     */



    private TextView countdownTitleTextView;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Switch notificationSwitch;

    private boolean isEdit = false;
    private long countdownId = -99;

    private Countdown countdownToEdit;

    Boolean notificationStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_countdown);

        countdownTitleTextView = findViewById(R.id.countdown_title);
        Button saveButton = findViewById(R.id.new_countdown_confirm_button);
        Button deleteButton = findViewById(R.id.delete_button);
        datePicker = findViewById(R.id.date_Picker);
        timePicker = findViewById(R.id.time_picker);
        notificationSwitch = findViewById(R.id.switch1);


        CountdownViewModel countdownViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CountdownViewModel.class);




        Intent mIntent = getIntent();

        countdownId = mIntent.getLongExtra("countdown id", -99);

        if(countdownId >= 0){
            isEdit = true;
        }


        if(isEdit){

            saveButton.setText(R.string.update);
            deleteButton.setVisibility(View.VISIBLE);


            countdownViewModel.getAllCountdowns().observe(this, countdowns -> {

                for(Countdown countdown : countdowns){

                    if(countdown.getCountdownId() == countdownId){
                        countdownToEdit = countdown;
                        //set views using stored data
                        //set title
                        countdownTitleTextView.setText(countdownToEdit.getCountdownTitle());

                        if(countdownToEdit.getCountdownDate() != null){
                            //set date
                            int storedYear = countdownToEdit.getCountdownDate().getYear();
                            int storedMonth = countdownToEdit.getCountdownDate().getMonthValue();
                            int storedDay = countdownToEdit.getCountdownDate().getDayOfMonth();

                            datePicker.updateDate(storedYear, storedMonth - 1, storedDay);

                            //set time

                            int storedHour = countdownToEdit.getCountdownDate().getHour();
                            int storedMin = countdownToEdit.getCountdownDate().getMinute();

                            timePicker.setHour(storedHour);
                            timePicker.setMinute(storedMin);

                        }

                        if(countdownToEdit.getNotificationOn() != null){
                            notificationSwitch.setChecked(countdownToEdit.getNotificationOn());
                        }



                    }
                }




            });


        }

        notificationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            if(isChecked){

                notificationStatus = true;

                Toast.makeText(this, "Notification on", Toast.LENGTH_SHORT).show();

            }
            else{

                notificationStatus = false;
                Toast.makeText(this, "Notification off", Toast.LENGTH_SHORT).show();

            }

        });

        countdownTitleTextView.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                switch(keyCode){
                    //if the enter key is pressed when the title text is being edited close the soft keyboard
                    case KeyEvent.KEYCODE_ENTER:
                        Utils.hideKeyboard(countdownTitleTextView);
                        //and remove the extra space... one line max
                        countdownTitleTextView.setText(countdownTitleTextView.getText().toString().trim());

                }
            }

            return false;
        });



        saveButton.setOnClickListener(this::saveButtonMethod);
        deleteButton.setOnClickListener(this::deleteButtonMethod);



    }

    private void deleteButtonMethod(View view) {

        CountdownViewModel.delete(countdownToEdit);
        finish();
    }


    private void saveButtonMethod(View view) {

        //get user input////////////////////

        String countdownTitle = countdownTitleTextView.getText().toString().trim();

        LocalDateTime countdownDate = LocalDateTime.of(datePicker.getYear(), datePicker.getMonth() + 1,
                datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());

        LocalDateTime nowDate = LocalDateTime.now();




        //////////////////////////////////////

        //save user input to a new countdown and store in the database////////////

        if(!TextUtils.isEmpty(countdownTitleTextView.getText().toString().trim())){

            Countdown countdown = new Countdown(countdownTitle, null,
                    countdownDate, nowDate, false, notificationStatus);

            if(isEdit){

                countdownToEdit.setCountdownTitle(countdownTitle);
                countdownToEdit.setCountdownDate(countdownDate);
                countdownToEdit.setNotificationOn(notificationStatus);
                CountdownViewModel.update(countdownToEdit);
                isEdit = false;

            }
            else{

                countdown.setNotificationOn(notificationStatus);

               //save the new countdown
                CountdownViewModel.insert(countdown);

          }
            this.finish();

        }
        else{
            Toast.makeText(this, "Cannot save. Title Field is blank.", Toast.LENGTH_LONG).show();
        }

    }

}