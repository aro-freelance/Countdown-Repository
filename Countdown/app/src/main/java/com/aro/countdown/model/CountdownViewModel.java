package com.aro.countdown.model;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aro.countdown.data.CountdownRepository;

import java.util.List;

public class CountdownViewModel extends AndroidViewModel {

    public static CountdownRepository repository;
    public final LiveData<List<Countdown>> allCountdowns;

    public CountdownViewModel(@NonNull Application application) {
        super(application);
        repository = new CountdownRepository(application);
        allCountdowns = repository.getCountdownList();
    }

    //create
    public static void insert(Countdown countdown) {repository.insert(countdown);}

    //read
    public LiveData<Countdown> get(long id) {return repository.get(id); }

    //read all
    public LiveData<List<Countdown>> getAllCountdowns() {return allCountdowns;}

    //update
    public static void update(Countdown countdown) {repository.update(countdown);}

    //delete
    public static void delete(Countdown countdown) {repository.delete(countdown);}


}
