package com.example.vfms.user;

public class Users {
    private String userId;
    private String username;
    private String nic;
    private String email;
    private String LicenseNumber;
    private String contact;
    private String profileImage; // Base64 encoded image string

    public Users() {
        // Default constructor required for Firebase
    }

    public Users(String userId, String username, String nic, String email, String LicenseNumber, String contact) {
        this.userId = userId;
        this.username = username;
        this.nic = nic;
        this.email = email;
        this.LicenseNumber = LicenseNumber;
        this.contact = contact;
        // Initialize profileImageBase64 to an empty string initially
        this.profileImage = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNumber() {
        return LicenseNumber;
    }

    public void setLicenseNumber(String LicenseNumber) {
        this.LicenseNumber = LicenseNumber;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getProfileImageBase64() {
        return profileImage;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImage = profileImageBase64;
    }


}
