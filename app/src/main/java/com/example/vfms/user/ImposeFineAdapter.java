package com.example.vfms.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;

import java.util.ArrayList;
import java.util.List;

public class ImposeFineAdapter extends RecyclerView.Adapter<ImposeFineAdapter.FineViewHolder> {

    private List<FineData> fineDataList = new ArrayList<>();

    FineData fineData = new FineData();

    public void addData(FineData fineData) {
        fineDataList.add(fineData);
        notifyDataSetChanged();
    }

    public void clearData() {
        fineDataList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_list_item_fine, parent, false);
        return new FineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineViewHolder holder, int position) {
        FineData FineData = fineDataList.get(position);
        // Bind your data to the views in your ViewHolder


        holder.textViewFineAmount.setText("Driver Name: " + FineData.getDriverName());
        holder.textViewLicenseNumber.setText("License Number: " + FineData.getLicenseNumber());
        holder.textViewDriverName.setText("Fine Amount: " + FineData.getFineAmount());
        holder.textViewVehicleNumber.setText("Vehicle Number: " + FineData.getVehicleNumber());
        holder.textViewNatureOfOffence.setText("Nature of Offence: " + FineData.getNatureOfOffence());

        holder.buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FineData.isPaymentStatus()) {
                    // Payment has not been made, allow the payment process

                    Intent intent = new Intent(v.getContext(), PayFine.class);

                    // Pass the necessary details to the PayFine activity, including fineId
                    intent.putExtra("driverName", FineData.getDriverName());
                    intent.putExtra("licenseNumber", FineData.getLicenseNumber());
                    intent.putExtra("fineAmount", FineData.getFineAmount());
                    intent.putExtra("natureOfOffence", FineData.getNatureOfOffence());
                    intent.putExtra("date", FineData.getDate());
                    intent.putExtra("time", FineData.getTime());
                    intent.putExtra("address", FineData.getAddress());
                    intent.putExtra("vehicleNumber", FineData.getVehicleNumber());
                    intent.putExtra("fineId", FineData.getFineId()); // Include fineId

                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Payment has already been Done.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check the paymentStatus and set the background color accordingly
        if (FineData.isPaymentStatus()) {
            // Payment was successful (true), set a green background color
            holder.CardviewOfficer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.successfulPaymentColor));
        } else {
            // Payment was not successful (false), set a different background color
            holder.CardviewOfficer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.unsuccessfulPaymentColor));
        }
    }

    @Override
    public int getItemCount() {
        return fineDataList.size();
    }

    public static class FineViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewDriverName;
        public TextView textViewLicenseNumber;
        public TextView textViewFineAmount;
        public TextView textViewNatureOfOffence;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewAddress;
        public TextView textViewVehicleNumber;
        public Button buttonPay;

        public androidx.cardview.widget.CardView CardviewOfficer; // Added for CardView reference

        public FineViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDriverName = itemView.findViewById(R.id.textViewDriverName);
            textViewLicenseNumber = itemView.findViewById(R.id.textViewLicenseNumber);
            textViewFineAmount = itemView.findViewById(R.id.textViewFineAmount);
            textViewNatureOfOffence = itemView.findViewById(R.id.textViewNatureOfOffence);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewVehicleNumber = itemView.findViewById(R.id.textViewVehicleNumber);
            buttonPay = itemView.findViewById(R.id.buttonPay);
            CardviewOfficer = itemView.findViewById(R.id.newCardView1); // Initialize CardView reference
        }
    }
}
