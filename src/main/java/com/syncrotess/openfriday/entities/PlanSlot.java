package com.syncrotess.openfriday.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents the ternery relation between workshop, room and (time) slot.
 * "Workshop takes place in Room at Slot"
 */

@Entity
public class PlanSlot {

    @EmbeddedId
    Key id;

    @ManyToOne
    @MapsId("slot_id")
    Slot slot;

    @ManyToOne
    @MapsId("room_id")
    Room room;

    @ManyToOne
    Workshop workshop;

    @Embeddable
    private class Key implements Serializable {
        @Column(name = "slot_id")
        Long slotId;

        @Column(name = "room_id")
        Long roomId;

        @Override
        public boolean equals (Object other) {
            return other instanceof Key && ((Key) other).roomId.equals(this.roomId) && ((Key) other).slotId.equals(this.slotId);
        }

        @Override
        public int hashCode() {
            return (int) (slotId * roomId);
        }
    }
}
