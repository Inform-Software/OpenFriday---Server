package com.syncrotess.openfriday.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.*;

public class Workshop implements Serializable {

    @JsonView(Views.Public.class)
    private UUID id;
    @JsonView(Views.Public.class)
    private String creator;
    @JsonView(Views.Public.class)
    private String name;
    @JsonView(Views.Public.class)
    private String description;
    @JsonView(Views.Public.class)
    private List<Slot> slots = new ArrayList<>();
    @JsonView(Views.Public.class)
    private int votesHigh;
    @JsonView(Views.Public.class)
    private int votesLow;
    @JsonView(Views.Internal.class)
    private boolean isLocked;           // used to lock the workshop during edit

    public Workshop() {}

    public void setId() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public Workshop setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getName() {
        return name;
    }

    public Workshop setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Workshop setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(Slot[] slots) {
        this.slots.clear();
        this.slots.addAll(Arrays.asList(slots));
        this.slots.sort(Comparator.comparing(Slot::getName));
    }

    public int getVotesHigh() {
        return votesHigh;
    }

    public void setVotesHigh(int votesHigh) {
        this.votesHigh = votesHigh;
    }

    public int getVotesLow() {
        return votesLow;
    }

    public void setVotesLow(int votesLow) {
        this.votesLow = votesLow;
    }

    @JsonIgnore
    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
