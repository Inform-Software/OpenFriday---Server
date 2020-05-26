package com.syncrotess.openfriday.nodes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Plan implements Serializable {
    public List<UUID> unusedWorkshops;          // list with the workshops not included in the table
    public Map<UUID, Map<UUID, UUID>> table;    // the timetable. Structure is Map<SlotID, Map<RoomID, WorkshopID>>
    public LocalDateTime timestamp;

    public Plan() {
        unusedWorkshops = new ArrayList<>();
        table = new HashMap<>();
        timestamp = LocalDateTime.now();
    }

    public void removeWorkshop(UUID workshopId) {
        unusedWorkshops.remove(workshopId);

        table.forEach((key, value) -> value.values().remove(workshopId));
    }
}
