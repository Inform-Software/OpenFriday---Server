package com.syncrotess.openfriday.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import java.util.Set;

@Entity
public class Admin extends User {

    private String password;

    public Admin() {}

    public Admin(String name, String password, Set<Slot> slots) {
        super(name, slots);
        this.password = password;
    }

    public Admin(Long id, String name, String password, Set<Slot> slots, Set<Vote> votes) {
        super(id, name, slots, votes);
        this.password = password;
    }

    public boolean comparePassword(Admin otherAdmin) {
        return password.equals(otherAdmin.password);
    }

    public boolean comparePassword(String otherPassword) {
        return password.equals(otherPassword);
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isPasswordEmpty() {
        return password == null || password.equals("");
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // hides field password in websocket messages and http responses
    public String getPassword() {
        return password;
    }
}
