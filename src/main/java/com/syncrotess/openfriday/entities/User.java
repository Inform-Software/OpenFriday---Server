package com.syncrotess.openfriday.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;

@Entity
@Table(name = "USER")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_IS_AVAILABLE_AT",
            inverseJoinColumns = @JoinColumn(name = "TIMESLOT_ID",
                    nullable = false,
                    updatable = false),
            joinColumns = @JoinColumn(name = "USER_ID",
                    nullable = false,
                    updatable = false),
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_USER_USERID", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE cascade"),
            inverseForeignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_USER_TIMESLOTID", foreignKeyDefinition = "FOREIGN KEY (timeslot_id) REFERENCES timeslot(id) ON DELETE cascade"))
    private Set<Timeslot> timeslots = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Vote> votes = new HashSet<>();

    public User() {}

    public User(String name, Set<Timeslot> timeslots) {
        this.name = name;
        this.timeslots = timeslots;
    }

    public User(Long id, String name, Set<Timeslot> timeslots, Set<Vote> votes) {
        this.id = id;
        this.name = name;
        this.timeslots = timeslots;
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

    public void setTimeslots(Set<Timeslot> newTimeslots) {
        this.timeslots = newTimeslots;
    }

    public Set<Timeslot> getTimeslots() {
        return timeslots;
    }

    /**
     * Votes for the workshop with the given id. Priorities are iterated.
     * @param workshop the workshop to vote for
     * @return the priority of the workshop after the vote (2 = high, 1 = low, 0 = not voted)
     */
    @Transactional
    public int vote(Workshop workshop) {
        // If the workshop is not included, it is added with value 2 (high priority)
        Optional<Vote> vote = votes.stream().filter(v -> v.getWorkshop().equals(workshop)).findFirst();
        if (!vote.isPresent()) {
            workshop.setTotalVotes(workshop.getTotalVotes() + 1);
            workshop.setVotesHigh(workshop.getVotesHigh() + 1);
            votes.add(new Vote(this, workshop, 2));
            return 2;
        }
        // If it is included and has value 2, the workshop is changed to value 1 (low priority)
        else if (vote.get().getPriority() == 2) {
            workshop.setVotesHigh(workshop.getVotesHigh() - 1);
            workshop.setVotesLow(workshop.getVotesLow() + 1);
            votes.remove(vote.get());
            votes.add(new Vote(this, workshop, 1));
            return 1;
        }
        // If it is included and has value 1, the workshop is removed
        else {
            workshop.setVotesLow(workshop.getVotesLow() - 1);
            workshop.setTotalVotes(workshop.getTotalVotes() - 1);
            votes.remove(vote.get());
            return 0;
        }
    }

    public Set<Vote> getVotes() {
        return votes;
    }
}