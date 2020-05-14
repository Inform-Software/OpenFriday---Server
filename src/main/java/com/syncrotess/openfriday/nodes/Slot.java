package com.syncrotess.openfriday.nodes;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.UUID;

public class Slot implements Serializable {

    @JsonView(Views.Public.class)
    private UUID id;
    @JsonView(Views.Public.class)
    private String name;

    public Slot() {}

    public Slot(String name) {
        id = UUID.randomUUID();
        this.name = name;
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

    public Slot setName(String name) {
        this.name = name;
        return this;
    }

}
