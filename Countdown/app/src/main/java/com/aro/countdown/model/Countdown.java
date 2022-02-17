package com.aro.countdown.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;


@Entity(tableName = "countdown_table")
public class Countdown {


    @ColumnInfo(name="countdown_id")
    @PrimaryKey(autoGenerate = true)
    public long countdownId;


    @ColumnInfo(name="countdown_title")
    public String countdownTitle;

    @ColumnInfo(name="countdown_description")
    public String countdownDescription;


    @ColumnInfo(name="countdown_date")
    public LocalDateTime countdownDate;

    @ColumnInfo(name="countdown_created_date")
    public LocalDateTime dateCreated;

    @ColumnInfo(name="is_done")
    public Boolean isDone;

    @ColumnInfo(name="notification_on")
    public Boolean notificationOn;



    public Countdown(){}

    public Countdown(String countdownTitle, String countdownDescription,
                     LocalDateTime countdownDate, LocalDateTime dateCreated, Boolean isDone, Boolean notificationOn) {
        this.countdownTitle = countdownTitle;
        this.countdownDescription = countdownDescription;
        this.countdownDate = countdownDate;
        this.dateCreated = dateCreated;
        this.isDone = isDone;
        this.notificationOn = notificationOn;
    }


    public long getCountdownId() {
        return countdownId;
    }

    public void setCountdownId(long countdownId) {
        this.countdownId = countdownId;
    }

    public String getCountdownTitle() {
        return countdownTitle;
    }

    public void setCountdownTitle(String countdownTitle) {
        this.countdownTitle = countdownTitle;
    }

    public String getCountdownDescription() {
        return countdownDescription;
    }

    public void setCountdownDescription(String countdownDescription) {
        this.countdownDescription = countdownDescription;
    }

    public LocalDateTime getCountdownDate() {
        return countdownDate;
    }

    public void setCountdownDate(LocalDateTime countdownDate) {
        this.countdownDate = countdownDate;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Boolean getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(Boolean notificationOn) {
        this.notificationOn = notificationOn;
    }

    @NonNull
    @Override
    public String toString() {
        return "Countdown{" +
                "countdownId=" + countdownId +
                ", countdownTitle='" + countdownTitle + '\'' +
                ", countdownDescription='" + countdownDescription + '\'' +
                ", countdownDate=" + countdownDate +
                ", dateCreated=" + dateCreated +
                ", isDone=" + isDone +
                '}';
    }

}
