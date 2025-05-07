package tn.esprit.entities;

import java.sql.Date;

public class User {

    private int id;
    private String email;
    private Roles roles;
    private String password;
    private String first_name, last_name;
    private String authCode;
    private boolean isBanned;
    private boolean enabled; // 🔥 ajouté ici
    private Date registration_date;
    private String resetToken;

    public User() {
    }

    public User(int id, String email, Roles roles, String password, String name, String lastName, boolean isBanned, boolean enabled, Date registration_date, String resetToken) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.first_name = name;
        this.last_name = lastName;
        this.isBanned = isBanned;
        this.enabled = enabled;
        this.registration_date = registration_date;
        this.resetToken = resetToken;
    }

    public User(String name, String lastName, String email) {
        this.email = email;
        this.first_name = name;
        this.last_name = lastName;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public void setRoles(String roleString) {
        roleString = roleString.replaceAll("[\\[\\]\"]", "");
        Roles roles = Roles.valueOf(roleString);
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getfirst_name() {
        return first_name;
    }

    public void setfirst_name(String name) {
        this.first_name = name;
    }

    public String getlast_name() {
        return last_name;
    }

    public void setlast_name(String lastName) {
        this.last_name = lastName;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isEnabled() {   // 🔥 ajouté ici
        return enabled;
    }

    public void setEnabled(boolean enabled) {  // 🔥 ajouté ici
        this.enabled = enabled;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                ", name='" + first_name + '\'' +
                ", lastName='" + last_name + '\'' +
                ", authCode='" + authCode + '\'' +
                ", isBanned=" + isBanned +
                ", enabled=" + enabled +  // 🔥 affichage dans toString
                ", registration_date=" + registration_date +
                '}';
    }
}
