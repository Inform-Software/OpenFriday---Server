package com.syncrotess.openfriday.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Workshop {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Formula("(SELECT COUNT(*) FROM vote WHERE vote.workshop_id = id)") // calculates this value by sql statement
    private int totalVotes;

    @ManyToOne
    private User creator;

    @OneToMany(mappedBy = "workshop")
    private Set<Vote> votes = new HashSet<>();

    @ManyToMany
    private Set<Slot> possibleSlots = new HashSet<>();



}
