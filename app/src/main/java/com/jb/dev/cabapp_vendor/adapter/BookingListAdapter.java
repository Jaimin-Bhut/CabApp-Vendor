package com.jb.dev.cabapp_vendor.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jb.dev.cabapp_vendor.R;
import com.jb.dev.cabapp_vendor.activites.DriverMapActivity;
import com.jb.dev.cabapp_vendor.activites.VerifyUserActivity;
import com.jb.dev.cabapp_vendor.model.BookingModel;

public class BookingListAdapter extends FirestoreRecyclerAdapter<BookingModel, BookingListAdapter.BookingListHolder> {
    int selectedPosition = -1;
    private BookingListAdapter.onItemClickListener listener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookingListAdapter(@NonNull FirestoreRecyclerOptions<BookingModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final BookingListHolder holder, int position, @NonNull final BookingModel model) {
        holder.textViewUserName.setText(model.getuser_name());
        holder.textViewPhone.setText(model.getUser_phone_number());
        holder.textViewDate.setText(model.getdate());
        holder.textViewTime.setText(model.gettime());
        holder.textViewTo.setText(model.getDestination_location());
        holder.textViewFrom.setText(model.getCurrent_location());
        final String cabNumber = model.getcab_number();

        final int finalPosition = position;
        holder.buttonJourneyComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = finalPosition;
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position, cabNumber);
                }
            }
        });
        holder.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VerifyUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("otp", model.getOtp());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DriverMapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("start_lat", model.getStart_lat());
                bundle.putDouble("start_long", model.getStart_long());
                bundle.putDouble("end_lat", model.getEnd_lat());
                bundle.putDouble("end_long", model.getEnd_long());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public BookingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_booking, parent, false);
        return new BookingListHolder(view);
    }

    class BookingListHolder extends RecyclerView.ViewHolder {
        public TextView textViewUserName, textViewPhone, textViewDate, textViewTime, textViewFrom, textViewTo;
        Button buttonVerify, buttonJourneyComplete;

        public BookingListHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.card_booking_user_name);
            textViewPhone = itemView.findViewById(R.id.card_booking_user_phone);
            textViewDate = itemView.findViewById(R.id.card_booking_date);
            textViewTime = itemView.findViewById(R.id.card_booking_time);
            textViewFrom = itemView.findViewById(R.id.card_booking_from);
            textViewTo = itemView.findViewById(R.id.card_booking_to);
            buttonVerify = itemView.findViewById(R.id.card_booking_btn_verify);
            buttonJourneyComplete = itemView.findViewById(R.id.card_booking_btn_journey_complete);
        }
    }

    public interface onItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String cabNumber);

    }

    public void setOnItemClickListener(BookingListAdapter.onItemClickListener listener) {
        this.listener = listener;
    }
}
