package com.syncrotess.openfriday.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Vote {

    @EmbeddedId
    VoteKey id;

    @ManyToOne
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @MapsId("workshop_id")
    private Workshop workshop;

    @Column(name = "prio")
    private int priority;

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

    @Embeddable
    private class VoteKey implements Serializable {

        @Column(name = "user_id")
        Long userId;

        @Column(name = "workshop_id")
        Long workshopId;

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