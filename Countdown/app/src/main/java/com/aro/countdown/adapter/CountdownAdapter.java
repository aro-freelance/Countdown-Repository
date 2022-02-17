package com.aro.countdown.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aro.countdown.R;
import com.aro.countdown.model.Countdown;
import com.aro.countdown.util.Utils;

import java.time.LocalDateTime;
import java.util.List;

public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.ViewHolder>{


    private final List<Countdown> countdownList;
    private final OnItemClickedListener itemClickedListener;

    private Context context;


    public CountdownAdapter(List<Countdown> countdownList, OnItemClickedListener itemClickedListener) {
        this.countdownList = countdownList;
        this.itemClickedListener = itemClickedListener;
    }

    @NonNull
    @Override
    public CountdownAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.countdown_row, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountdownAdapter.ViewHolder holder, int position) {

        //get the countdown at the current position
        Countdown countdown = countdownList.get(position);

        //get the date from the countdown

        String countdownString;

        if(countdown.getCountdownDate() != null){

            LocalDateTime countdownEnd = countdown.getCountdownDate();
            LocalDateTime now = LocalDateTime.now();


            long[] time = Utils.getTimeDifference(now, countdownEnd);


            String days = String.valueOf(time[0]);
            String hours = String.valueOf(time[1]);
            String minutes = String.valueOf(time[2]);
            String seconds = String.valueOf(time[3]);

            countdownString = "" + days + " Days " + hours + " Hours " + minutes + " Minutes " + seconds + " Seconds" ;



            //also if it is set to done the countdown is over
            if(countdown.getDone()){

                countdownString = "Countdown Completed!";

                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_green));

            }
            else{
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }



            if(countdown.getNotificationOn()){

                holder.notificationImage.setVisibility(View.VISIBLE);

            }
            else{
                holder.notificationImage.setVisibility(View.INVISIBLE);
            }






        }
        else{

            countdownString = "There is no date entered for this item";

        }


        //set the countdown to the views

        holder.titleText.setText(countdown.getCountdownTitle());
        holder.dateText.setText(countdownString);

    }



    @Override
    public int getItemCount() {
        return countdownList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //declare views from row layout

        public TextView titleText;
        public TextView dateText;

        public ImageView notificationImage;

        public CardView cardView;

        OnItemClickedListener onItemClickedListener;


        //constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //implement views

            titleText = itemView.findViewById(R.id.title_text_countdown_row);
            dateText = itemView.findViewById(R.id.countdown_update_textview);
            cardView = itemView.findViewById(R.id.row_cardview);

            notificationImage = itemView.findViewById(R.id.notification_image);

            //set up on click
            this.onItemClickedListener = itemClickedListener;

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {

            int id = view.getId();

            if(getAdapterPosition() >= 0 && getAdapterPosition() <= countdownList.size()){
                Countdown currentCountdown = countdownList.get(getAdapterPosition());
                if(id == R.id.row_parent){
                    onItemClickedListener.OnCountdownClickedListener(getAdapterPosition(), currentCountdown);
                }
            }

            //if other id (other view is clicked)
            //do other things



        }
    }
}
