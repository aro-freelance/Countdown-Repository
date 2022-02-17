package com.aro.countdown.util;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Converter {

    //convert timestamp stored in Database back into a Date
    @TypeConverter
    public static Date timestampToDate (Long value){
        //if the value we received is null return null, else create a new Date with the value we are receiving
        return value == null ? null : new Date(value);

    }

    //convert Date to Timestamp to store in the db
    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }



    //convert timestamp stored in db back into LocalDateTime
    @TypeConverter
    public static LocalDateTime timestampToLDT (Long value){

        return value == null ? null : Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }


    //convert LocalDateTime to Timestamp to store in db
    @TypeConverter
    public static Long ldtToTimestamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


}
