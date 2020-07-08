package com.syncrotess.openfriday.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
public class Timetable {
    @ValueRangeProvider(id = "timeslotsRange")
    private List<Timeslot> timeslots;
    @ValueRangeProvider(id = "roomsRange")
    private List<Room> rooms;
    @ProblemFactCollectionProperty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Vote> votes;
    @PlanningEntityCollectionProperty
    private List<Workshop> workshops;

    @PlanningScore
    private HardMediumSoftScore score;

    private Timetable() {

    }

    public Timetable(List<Timeslot> timeslots, List<Room> rooms, List<Workshop> workshops, List<Vote> votes) {
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.workshops = workshops;
        this.votes = votes;
    }

    public Timetable(Iterable<Timeslot> timeslots, Iterable<Room> rooms, Iterable<Workshop> workshops, Iterable<Vote> votes) {
        this.timeslots = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.workshops = new ArrayList<>();
        this.votes = new ArrayList<>();

        timeslots.forEach(this.timeslots::add);
        rooms.forEach(this.rooms::add);
        workshops.forEach(this.workshops::add);
        votes.forEach(this.votes::add);
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Workshop ws: workshops) {
            builder.append(ws.getName()).append(" ").append(ws.getRoom() == null ? "null" : ws.getRoom().getName()).append(" ").append(ws.getTimeslot() == null ? "null" : ws.getTimeslot().getName()).append("              ").append(System.lineSeparator());
        }

        return builder.toString();
    }

    public List<Vote> getVotes() {
        return votes;
    }
}
