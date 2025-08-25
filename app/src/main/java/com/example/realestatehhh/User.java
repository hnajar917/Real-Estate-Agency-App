package com.example.realestatehhh;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String gender;
    private String country;
    private String city;
    private String phone;
    private String role;

    public User() {}

    public User(String email, String firstName, String lastName, String password,
                String gender, String country, String city, String phone, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.role = role;
    }

    // Getters
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public String getGender() { return gender; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPassword(String password) { this.password = password; }
    public void setGender(String gender) { this.gender = gender; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.city = city; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
}