package tn.esprit.service.session;



import tn.esprit.entities.Roles;

import java.sql.Date;

public class AuthDTO {

    private int id;
    private String email;
    private Roles roles;
    private String password;
    private String first_name;
    private String last_name;
    private boolean enabled; // 🔥 activation du compte
    private String authCode;
    private boolean isBanned;
    private Date registration_date;
    private String resetToken;

    // Getters et Setters

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

    // Ajout pour setRoles avec String (optionnel mais pratique)
    public void setRoles(String roleString) {
        if (roleString != null) {
            roleString = roleString.replaceAll("[\\[\\]\"]", ""); // Supprimer crochets et guillemets
            this.roles = Roles.valueOf(roleString);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getfirst_name() {
        return first_name;
    }

    public void setfirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getlast_name() {
        return last_name;
    }

    public void setlast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
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
        return "AuthDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", enabled=" + enabled +
                ", authCode='" + authCode + '\'' +
                ", isBanned=" + isBanned +
                ", registration_date=" + registration_date +
                ", resetToken='" + resetToken + '\'' +
                '}';
    }


}
