package com.syncrotess.openfriday.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "USER")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @ManyToMany(cascade =
            {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            })
    @JoinTable(name = "USER_IS_AVAILABLE_AT")
    private Set<Slot> slots = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Vote> votes = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private Set<Workshop> createdWorkshops = new HashSet<>();

    public User() {}

    public User(String name, Set<Slot> slots) {
        this.name = name;
        this.slots = slots;
    }

    public User(Long id, String name, Set<Slot> slots, Set<Vote> votes) {
        this.id = id;
        this.name = name;
        this.slots = slots;
        this.votes = votes;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setSlots(Set<Slot> newSlots) {
        this.slots = newSlots;
    }

    public Set<Slot> getSlots() {
        return slots;
    }

    /**
     * Votes for the workshop with the given id. Priorities are iterated.
     * @param workshopId the workshop to vote for
     * @return the priority of the workshop after the vote (2 = high, 1 = low, 0 = not voted)
     */
    public int vote(UUID workshopId) {
/*        // If the workshop is not included, it is added with value 2 (high priority)
        if (!votes.containsKey(workshopId)) {
            votes.put(workshopId, 2);
            return 2;
        }
        // If it is included and has value 2, the workshop is changed to value 1 (low priority)
        else if (votes.get(workshopId) == 2) {
            votes.replace(workshopId, 1);
            return 1;
        }
        // If it is included and has value 1, the workshop is removed
        else {
            votes.remove(workshopId);
            return 0;
        }*/
        return 0;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public Set<Workshop> getCreatedWorkshops() {
        return createdWorkshops;
    }

    public void setCreatedWorkshops(Set<Workshop> createdWorkshops) {
        this.createdWorkshops = createdWorkshops;
    }
}