package com.example.trimob;

public class HelperClassGoogle {
    private String username;
    private String name;
    private String email;
    private String phone;
    private String authenticationType;

    // Empty constructor required for Firebase
    public HelperClassGoogle() {
    }

    // Constructor with parameters
    public HelperClassGoogle(String username, String name, String email, String phone, String authenticationType) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.authenticationType = authenticationType;
    }

    // Getter and Setter methods for all fields
    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
