package com.example.vfms.user;

public class UserData {

    public String expireDate;
    public String licenceFrontSide;
    public String licenceBackSide;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public UserData() {
    }

    public UserData(String expireDate, String licenceFrontSide, String licenceBackSide) {
        this.expireDate = expireDate;
        this.licenceFrontSide = licenceFrontSide;
        this.licenceBackSide = licenceBackSide;
    }

    // Getters and setters (optional, but recommended)
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getLicenceFrontSide() {
        return licenceFrontSide;
    }

    public void setLicenceFrontSide(String licenceFrontSide) {
        this.licenceFrontSide = licenceFrontSide;
    }

    public String getLicenceBackSide() {
        return licenceBackSide;
    }

    public void setLicenceBackSide(String licenceBackSide) {
        this.licenceBackSide = licenceBackSide;
    }
}