package com.example.demoapp;

import java.io.Serializable;

public class Customer implements Serializable {
    private String name;
    private String email;
    private String mobile;
    private String imageUri;

    public Customer(String name, String email, String mobile, String imageUri) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.imageUri = imageUri;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getImageUri() { return imageUri; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}