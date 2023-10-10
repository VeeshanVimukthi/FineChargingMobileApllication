package com.example.vfms.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vfms.R;
import com.example.vfms.police_officer.PoliceOfficer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class OfficersRegisteredAdapter extends RecyclerView.Adapter<OfficersRegisteredAdapter.ViewHolder> {

    private List<PoliceOfficer> officerList;
    private Context context;

    public OfficersRegisteredAdapter(List<PoliceOfficer> officerList, Context context) {
        this.officerList = officerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.registerd_officer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PoliceOfficer officer = officerList.get(position);

        holder.textViewOfficerName.setText("Officer Name: " + officer.getName());
        holder.textViewOfficerNumber.setText("Officer Number: " + officer.getOfficerNumber());
        holder.textViewContact.setText("Contact: " + officer.getContact());
        holder.textViewNIC.setText("NIC: " + officer.getNic());
        holder.textViewEmail.setText("Email: " + officer.getEmail());

        // Decode and set the profile image
        String base64Image = officer.getprofileImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imageViewProfile.setImageBitmap(decodedBitmap);
        } else {
            // If no profile image is available, you can set a default image or hide the ImageView
            holder.imageViewProfile.setImageResource(R.drawable.profile);
        }

        // Delete button click listener
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePoliceOfficer(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return officerList.size();
    }

    public void deletePoliceOfficer(int position) {
        PoliceOfficer officerToDelete = officerList.get(position);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("police_officers"); // Change "police_officers" to your Firebase database node

        // Assuming you have a unique identifier for each officer, replace "uniqueId" with the actual field name.
        String officerIdToDelete = officerToDelete.getUserId();

        // Remove the officer from the Firebase database
        databaseRef.child(officerIdToDelete).removeValue();

        // Remove the officer from the local list as well (optional)
        officerList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOfficerName;
        TextView textViewOfficerNumber;
        TextView textViewContact;
        TextView textViewNIC;
        TextView textViewEmail;
        ImageView imageViewProfile;
        Button buttonDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewOfficerName = itemView.findViewById(R.id.textViewOfficerName);
            textViewOfficerNumber = itemView.findViewById(R.id.textViewOfficerNumber);
            textViewContact = itemView.findViewById(R.id.textViewContact);
            textViewNIC = itemView.findViewById(R.id.textViewNIC);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
