package com.example.vfms.police_officer;

public class Officer {

    private String name;
    private String nic;
    private String contact;
    private String officerId;
    private String email;

    public Officer() {
        // Empty constructor required for Firestore
    }

    public Officer(String name, String nic, String contact, String officerId, String email) {
        this.name = name;
        this.nic = nic;
        this.contact = contact;
        this.officerId = officerId;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
