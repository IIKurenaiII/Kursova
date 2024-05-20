package org.kursova;

import java.time.LocalDate;

public class User {
    private String firstname;
    private String lastname;
    private String password;
    private String username;
    private String phonenumber;
    private String email;
    private String role;

    public User(String firstname, String lastname, String password, String username, String phonenumber, String email,String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.username = username;
        this.phonenumber = phonenumber;
        this.email = email;
        this.role = role;
    }

    public User() {

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
