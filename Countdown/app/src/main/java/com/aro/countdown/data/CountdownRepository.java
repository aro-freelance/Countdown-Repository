package com.aro.countdown.data;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.aro.countdown.model.Countdown;
import com.aro.countdown.util.CountdownRoomDatabase;

import java.util.List;

public class CountdownRepository {

    private final CountdownDao countdownDao;
    private final LiveData<List<Countdown>> allCountdowns;


    public CountdownRepository(Application application) {
        CountdownRoomDatabase database = CountdownRoomDatabase.getDatabase(application);
        this.countdownDao = database.countdownDao();
        this.allCountdowns = countdownDao.getAllCountdowns();
    }

    //create
    public void insert(Countdown countdown){
        CountdownRoomDatabase.databaseWriterExecutor.execute(()-> countdownDao.insertCountdown(countdown));
    }

    //read
    public LiveData<Countdown> get(long id){
        return countdownDao.get(id);
    }

    //read all
    public LiveData<List<Countdown>> getCountdownList(){
        return allCountdowns;
    }

    //update
    public void update(Countdown countdown){
        CountdownRoomDatabase.databaseWriterExecutor.execute(()-> countdownDao.update(countdown));
    }

    //delete
    public void delete(Countdown countdown){
        CountdownRoomDatabase.databaseWriterExecutor.execute(()-> countdownDao.delete(countdown));
    }

}
