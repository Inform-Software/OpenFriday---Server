package com.syncrotess.openfriday.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int size;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROOM_AVAILABLE_AT",
        inverseJoinColumns = @JoinColumn(name = "TIMESLOT_ID",
                nullable = false,
                updatable = false),
        joinColumns = @JoinColumn(name = "ROOM_ID",
                nullable = false,
                updatable = false),
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ROOM_ROOMID", foreignKeyDefinition = "FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE cascade"),
            inverseForeignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ROOM_TIMESLOTID", foreignKeyDefinition = "FOREIGN KEY (timeslot_id) REFERENCES timeslot(id) ON DELETE cascade"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Timeslot> timeslots;

    public Room() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(Set<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }
}
