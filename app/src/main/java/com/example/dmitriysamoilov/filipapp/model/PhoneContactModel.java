package com.example.dmitriysamoilov.filipapp.model;


public class PhoneContactModel {
    public String name;
    public String email;
    public String number;
    public String lastname;
    public boolean isChecked;


    public PhoneContactModel(String name, String email, String phoneNumber, String lastname, boolean isChecked) {
        this.name = name;
        this.email = email;
        this.number = phoneNumber;
        this.isChecked = isChecked;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
