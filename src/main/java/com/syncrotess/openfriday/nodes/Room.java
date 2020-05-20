package com.syncrotess.openfriday.nodes;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.UUID;

public class Room implements Serializable {

    @JsonView(Views.Public.class)
    private UUID id;
    @JsonView(Views.Public.class)
    private String name;
    @JsonView(Views.Public.class)
    private int size;
    @JsonView(Views.Public.class)
    private Slot[] slots;

    public Room() {}

    public void setId() {
        id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Slot[] getSlots() {
        return slots;
    }

    public void setSlots(Slot[] slots) {
        this.slots = slots;
    }
}
