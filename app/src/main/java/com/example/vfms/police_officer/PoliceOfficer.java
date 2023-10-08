package com.example.vfms.police_officer;

import android.os.Parcel;
import android.os.Parcelable;

public class PoliceOfficer  {

    private String userId;

    private String officerNumber;
    private String name;
    private String nic;
    private String contact;
    private String email;
    private String profileImage; // Store the profile image as a Base64 encoded string

    public PoliceOfficer() {
        // Default constructor required for Firebase
    }

    public PoliceOfficer(String userId, String officerNumber, String name, String nic, String contact, String email) {
        this.userId = userId;
        this.name = name;
        this.nic = nic;
        this.contact = contact;
        this.officerNumber = officerNumber;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getNic() {
        return nic;
    }

    public String getContact() {
        return contact;
    }



    public String getEmail() {
        return email;
    }
    public String getprofileImage() {
        return profileImage;
    }




    public String getOfficerNumber() {
        return officerNumber;
    }

    public void setOfficerNumber(String officerNumber) {
        this.officerNumber = officerNumber;
    }

    // Setter methods for the remaining fields
    public void setName(String name) {
        this.name = name;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setprofileImage(String profileImage) {
        this.profileImage = profileImage;
    }



}
