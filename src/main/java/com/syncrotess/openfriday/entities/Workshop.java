package com.syncrotess.openfriday.entities;

import com.syncrotess.openfriday.repository.VoteRepository;
import org.hibernate.annotations.Formula;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("JpaDataSourceORMInspection")
@PlanningEntity
@Entity
public class Workshop {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    // @Formula(value = "(SELECT COUNT(*) FROM vote WHERE vote.workshop_id = id)") // calculates this value by sql statement
    @Column(name = "totalVotes", columnDefinition = "Integer(50) default '0'")
    private int totalVotes;

    // @Formula("(SELECT COUNT(*) FROM vote WHERE vote.workshop_id = id AND vote.priority = 2)")
    @Column(name = "votesHigh", columnDefinition = "Integer(50) default '0'")
    private int votesHigh;

    // @Formula("(SELECT COUNT(*) FROM vote WHERE vote.workshop_id = id AND vote.priority = 1)")
    @Column(name = "votesLow", columnDefinition = "Integer(50) default '0'")
    private int votesLow;

    @ManyToOne
    @JoinColumn(name = "CREATOR",
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_WORKSHOP_CREATOR", foreignKeyDefinition = "FOREIGN KEY (creator) REFERENCES user(id) ON DELETE set null"))
    private User creator;

    @PlanningVariable(valueRangeProviderRefs = "timeslotsRange", nullable = true)
    @ManyToOne
    @JoinColumn(name = "TIMESLOT",
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_WORKSHOP_TIMESLOT", foreignKeyDefinition = "FOREIGN KEY (timeslot) REFERENCES timeslot(id) ON DELETE set null"))
    // the timeslot the workshop takes place in (used by planner)
    private Timeslot timeslot;

    @PlanningVariable(valueRangeProviderRefs = "roomsRange", nullable = true)
    @ManyToOne
    @JoinColumn(name = "ROOM",
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_WORKSHOP_ROOM", foreignKeyDefinition = "FOREIGN KEY (room) REFERENCES room(id) ON DELETE set null"))
    // the room the workshop takes place in (used by planner)
    private Room room;

//    @OneToMany(mappedBy = "workshop")
//    private Set<Vote> votes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "WORKSHOP_AVAILABLE_AT",
            inverseJoinColumns = @JoinColumn(name = "TIMESLOT_ID",
                    nullable = false,
                    updatable = false),
            joinColumns = @JoinColumn(name = "WORKSHOP_ID",
                    nullable = false,
                    updatable = false),
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_WORKSHOP_WORKSHOPID", foreignKeyDefinition = "FOREIGN KEY (workshop_id) REFERENCES workshop(id) ON DELETE cascade"),
            inverseForeignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_WORKSHOP_TIMESLOTID", foreignKeyDefinition = "FOREIGN KEY (timeslot_id) REFERENCES timeslot(id) ON DELETE cascade"))
    private Set<Timeslot> possibleTimeslots = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<Timeslot> getPossibleTimeslots() {
        return possibleTimeslots;
    }

    public void setPossibleTimeslots(Set<Timeslot> possibleTimeslots) {
        this.possibleTimeslots = possibleTimeslots;
    }

    @Override
    public boolean equals (Object other) {
        return other instanceof Workshop && ((Workshop) other).getId().equals(this.id);
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public int getVotesHigh() {
        return votesHigh;
    }

    public int getVotesLow() {
        return votesLow;
    }

    public void setTotalVotes(int votes) {
        this.totalVotes = votes;
    }

    public void setVotesHigh(int votes) {
        this.votesHigh = votes;
    }

    public void setVotesLow(int votes) {
        this.votesLow = votes;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
