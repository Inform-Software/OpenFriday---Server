package com.syncrotess.openfriday.entities;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
public class Timetable {
    @ValueRangeProvider(id = "timeslotsRange")
    private List<Timeslot> timeslots;
    @ValueRangeProvider(id = "roomsRange")
    private List<Room> rooms;
    @PlanningEntityCollectionProperty
    private List<Workshop> workshops;

    @PlanningScore
    private HardSoftScore score;

    private Timetable() {

    }

    public Timetable(List<Timeslot> timeslots, List<Room> rooms, List<Workshop> workshops) {
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.workshops = workshops;
    }

    public Timetable(Iterable<Timeslot> timeslots, Iterable<Room> rooms, Iterable<Workshop> workshops) {
        this.timeslots = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.workshops = new ArrayList<>();

        timeslots.forEach(this.timeslots::add);
        rooms.forEach(this.rooms::add);
        workshops.forEach(this.workshops::add);
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

    public HardSoftScore getScore() {
        return score;
    }
}
