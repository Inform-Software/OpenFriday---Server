package com.syncrotess.openfriday.nodes;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {

    @JsonView(Views.Public.class)
    private UUID id;
    @JsonView(Views.Public.class)
    private String name;
    @JsonView(Views.Public.class)
    private List<Slot> slots = new ArrayList<>();
    @JsonView(Views.Public.class)
    private HashMap<UUID, Integer> votes = new HashMap<>();           // Contains all workshop-ids a user has votes for. Value 2 means he really wants it, value 1 means he want to participate if it is possible

    public User() {}

    public User(String name, List<Slot> slots) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.slots = slots;
    }

    public User(UUID id, String name, List<Slot> slots, HashMap<UUID, Integer> votes) {
        this.id = id;
        this.name = name;
        this.slots = slots;
        this.votes = votes;
    }

    public void setId() {
        id = UUID.randomUUID();
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setSlots(Slot[] newSlots) {
        slots.clear();
        slots.addAll(Arrays.asList(newSlots));
    }

    public List<Slot> getSlots() {
        return slots;
    }

    /**
     * Votes for the workshop with the given id. Priorities are iterated.
     * @param workshopId the workshop to vote for
     * @return the priority of the workshop after the vote (2 = high, 1 = low, 0 = not voted)
     */
    public int vote(UUID workshopId) {
        // If the workshop is not included, it is added with value 2 (high priority)
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
        }
    }

    public HashMap<UUID, Integer> getVotes() {
        return votes;
    }

}