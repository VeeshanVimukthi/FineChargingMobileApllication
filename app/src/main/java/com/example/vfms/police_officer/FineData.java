package com.example.vfms.police_officer;

public class FineData {

    private String FineId;
    private String Address;
    private String Date;
    private String DriverName;
    private String FineAmount;
    private String LicenseNumber;
    private String NatureOfOffence;
    private String PolicemenId;
    private String Time;
    private String VehicleNumber;
    private boolean isPaid; // Add a field to store payment status

    private boolean paymentStatus; // Added boolean attribute

    private boolean updatedByPoliceOfficer; // Added field to track updates by police officer



    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getFineAmount() {
        return FineAmount;
    }

    public void setFineAmount(String fineAmount) {
        FineAmount = fineAmount;
    }

    public String getLicenseNumber() {
        return LicenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        LicenseNumber = licenseNumber;
    }

    public String getNatureOfOffence() {
        return NatureOfOffence;
    }

    public void setNatureOfOffence(String natureOfOffence) {
        NatureOfOffence = natureOfOffence;
    }

    public String getPolicemenId() {
        return PolicemenId;
    }

    public void setPolicemenId(String policemenId) {
        PolicemenId = policemenId;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getFineId() {
        return FineId;
    }

    public void setFineId(String FineId) {
        this.FineId = FineId;
    }



//    public boolean isPaid() {
//        return isPaid;
//    }
//
//    public void setPaid(boolean paid) {
//        isPaid = paid;
//    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isUpdatedByPoliceOfficer() {
        return updatedByPoliceOfficer;
    }

    public void setUpdatedByPoliceOfficer(boolean updatedByPoliceOfficer) {
        this.updatedByPoliceOfficer = updatedByPoliceOfficer;
    }

}
