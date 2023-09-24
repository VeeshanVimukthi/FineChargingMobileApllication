package com.example.vfms.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;

import java.util.List;

public class FineDataAdapter extends RecyclerView.Adapter<FineDataAdapter.FineDataViewHolder> {
    private List<FineData> fineDataList;

    public FineDataAdapter(List<FineData> fineDataList) {
        this.fineDataList = fineDataList;
    }

    @NonNull
    @Override
    public FineDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fine, parent, false);
        return new FineDataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FineDataViewHolder holder, int position) {
        FineData fineData = fineDataList.get(position);

        holder.textViewDriverName.setText("Driver Name: " + fineData.getDriverName());
        holder.textViewLicenseNumber.setText("License Number: " + fineData.getLicenseNumber());
        holder.textViewFineAmount.setText("Fine Amount: " + fineData.getFineAmount());
        holder.textViewNatureOfOffence.setText("Nature of Offence: " + fineData.getNatureOfOffence());
        holder.textViewDate.setText("Date: " + fineData.getDate());
        holder.textViewTime.setText("Time: " + fineData.getTime());
        holder.textViewAddress.setText("Address: " + fineData.getAddress());
        holder.textViewVehicleNumber.setText("Vehicle Number: " + fineData.getVehicleNumber());

        // Handle "Pay" button click
        holder.buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PayFine.class);

                // Pass the necessary details to the PayFine activity
                intent.putExtra("driverName", fineData.getDriverName());
                intent.putExtra("licenseNumber", fineData.getLicenseNumber());
                intent.putExtra("fineAmount", fineData.getFineAmount());
                intent.putExtra("natureOfOffence", fineData.getNatureOfOffence());
                intent.putExtra("date", fineData.getDate());
                intent.putExtra("time", fineData.getTime());
                intent.putExtra("address", fineData.getAddress());
                intent.putExtra("vehicleNumber", fineData.getVehicleNumber());

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fineDataList.size();
    }

    public void setData(List<FineData> fineDataList) {
        this.fineDataList = fineDataList;
        notifyDataSetChanged();
    }

    public static class FineDataViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDriverName;
        public TextView textViewLicenseNumber;
        public TextView textViewFineAmount;
        public TextView textViewNatureOfOffence;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewAddress;
        public TextView textViewVehicleNumber;
        public Button buttonPay;

        public FineDataViewHolder(View itemView) {
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
        }
    }
}
