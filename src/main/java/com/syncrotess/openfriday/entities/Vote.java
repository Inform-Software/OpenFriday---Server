package com.syncrotess.openfriday.entities;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Vote {

    @EmbeddedId
    VoteKey id;

    @ManyToOne
    @JoinColumn(name = "USER_ID",
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VOTE_USER", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE cascade")
    )
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "WORKSHOP_ID",
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VOTE_WORKSHOP", foreignKeyDefinition = "FOREIGN KEY (workshop_id) REFERENCES workshop(id) ON DELETE cascade")
    )
    @MapsId("workshop_id")
    private Workshop workshop;

    @Column(name = "priority")
    private int priority;

    public Vote() {

    }

    public Vote(User user, Workshop workshop, int priority) {
        this.id = new VoteKey(user.getId(), workshop.getId());
        this.user = user;
        this.workshop = workshop;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Vote && ((Vote) other).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public User getUser() {
        return user;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    @Embeddable
    private static class VoteKey implements Serializable {

        @Column(name = "user_id")
        Long userId;

        @Column(name = "workshop_id")
        Long workshopId;

        public VoteKey() {}

        public VoteKey(Long userId, Long workshopId) {
            this.userId = userId;
            this.workshopId = workshopId;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof VoteKey && ((VoteKey) other).userId.equals(this.userId) && ((VoteKey) other).workshopId.equals(this.workshopId);
        }

        @Override
        public int hashCode() {
            return (int) (userId * workshopId);
        }
    }
}