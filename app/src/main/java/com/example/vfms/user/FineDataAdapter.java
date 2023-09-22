package com.example.vfms.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vfms.R;
import com.example.vfms.police_officer.FineData;
import java.util.List;

public class FineDataAdapter extends RecyclerView.Adapter<FineDataAdapter.ViewHolder> {
    private final List<FineData> fineDataList;

    public FineDataAdapter(List<FineData> fineDataList) {
        this.fineDataList = fineDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_fine, parent, false); // Replace 'your_item_layout' with your actual item layout resource
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FineData fineData = fineDataList.get(position);
        // Bind data from fineData to your views in the ViewHolder
        holder.licenseTextView.setText("license NO : " + fineData.getLicenseNumber());
        // Bind other data as needed
    }

    @Override
    public int getItemCount() {
        return fineDataList.size();
    }

    public void setData(List<FineData> newData) {
        fineDataList.clear();
        fineDataList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView licenseTextView;
        // Define your other views here

        public ViewHolder(View itemView) {
            super(itemView);
            licenseTextView = itemView.findViewById(R.id.textViewLicenseNumber); // Replace with your actual view ID
            // Initialize your other views here
        }
    }
}
