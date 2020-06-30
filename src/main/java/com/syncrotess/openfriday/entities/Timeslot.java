package com.syncrotess.openfriday.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Timeslot {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Timeslot() {}

    public Timeslot(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timeslot setName(String name) {
        this.name = name;
        return this;
    }
}
