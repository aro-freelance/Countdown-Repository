package com.aro.countdown.util;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aro.countdown.data.CountdownDao;
import com.aro.countdown.model.Countdown;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        version = 2,
        entities = {Countdown.class}

)
@TypeConverters({Converter.class})
public abstract class CountdownRoomDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "countdown_database";
    public static final int NUMBER_OF_THREADS = 4;
    public static volatile CountdownRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract CountdownDao countdownDao();


    //this is called by addCallback when the database is first created
    public static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriterExecutor.execute(()->{
                        // invoke Dao and write
                        CountdownDao countdownDao = INSTANCE.countdownDao();
                        countdownDao.deleteAll(); //clear the database when it is initially created

                    });
                }
            };

    //constructor (singleton)
    public static CountdownRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (CountdownRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CountdownRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }

        return INSTANCE;
    }


    //migrate to add notification parameter to countdowns
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE countdown_table ADD COLUMN notification_on INTEGER DEFAULT 0");
        }
    };



}
