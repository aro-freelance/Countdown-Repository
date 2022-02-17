package com.aro.countdown.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aro.countdown.model.Countdown;

import java.util.List;

@Dao
public interface CountdownDao {


    //create
    @Insert
    void insertCountdown(Countdown countdown);

    //read
    @Query("SELECT * FROM countdown_table WHERE countdown_table.countdown_id == :id")
    LiveData<Countdown> get(long id);

    //read all
    @Query("SELECT * FROM countdown_table")
    LiveData<List<Countdown>> getAllCountdowns();

    //update
    @Update
    void update(Countdown countdown);

    //delete
    @Delete
    void delete(Countdown countdown);

    //delete all
    @Query("DELETE FROM countdown_table")
    void deleteAll();


}
