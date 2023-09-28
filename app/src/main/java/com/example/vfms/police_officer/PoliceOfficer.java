package com.example.vfms.police_officer;

import android.os.Parcel;
import android.os.Parcelable;

public class PoliceOfficer implements Parcelable {

    private String userId;
    private String name;
    private String nic;
    private String contact;
    private String officerId;
    private String email;
    private String profileImage; // Store the profile image as a Base64 encoded string

    public PoliceOfficer() {
        // Default constructor required for Firebase
    }

    public PoliceOfficer(String userId, String name, String nic, String contact, String email) {
        this.userId = userId;
        this.name = name;
        this.nic = nic;
        this.contact = contact;
        this.officerId = officerId;
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

    public String getOfficerId() {
        return officerId;
    }

    public String getEmail() {
        return email;
    }

    public String getprofileImage() {
        return profileImage;
    }

    public void setprofileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // Parcelable implementation for passing PoliceOfficer objects between activities
    protected PoliceOfficer(Parcel in) {
        userId = in.readString();
        name = in.readString();
        nic = in.readString();
        contact = in.readString();
        officerId = in.readString();
        email = in.readString();
        profileImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(nic);
        dest.writeString(contact);
        dest.writeString(officerId);
        dest.writeString(email);
        dest.writeString(profileImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PoliceOfficer> CREATOR = new Creator<PoliceOfficer>() {
        @Override
        public PoliceOfficer createFromParcel(Parcel in) {
            return new PoliceOfficer(in);
        }

        @Override
        public PoliceOfficer[] newArray(int size) {
            return new PoliceOfficer[size];
        }
    };
}
