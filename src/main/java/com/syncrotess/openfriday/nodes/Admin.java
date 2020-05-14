package com.syncrotess.openfriday.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Admin extends User {

    @JsonView(Views.Internal.class) // hides field password in http responses if the method is annotated with Views.Public.class
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // hides field password in websocket messages
    private String password;

    public Admin() {}

    public Admin(String name, String password, List<Slot> slots) {
        super(name, slots);
        this.password = password;
    }

    public Admin(UUID id, String name, String password, List<Slot> slots, HashMap<UUID, Integer> votes) {
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

    public String getPassword() {
        return password;
    }
}
