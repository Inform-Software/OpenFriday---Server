package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.Timetable;
import com.syncrotess.openfriday.entities.Workshop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimetableRepository {
    public static final Long SINGLETON_TIMETABLE_ID = 1L;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WorkshopRepository workshopRepository;

    public Timetable findById(Long id) {
        if (!SINGLETON_TIMETABLE_ID.equals(id)) {
            throw new IllegalStateException("There is no timeTable with id (" + id + ").");
        }
        // Occurs in a single transaction, so each initialized lesson references the same timeslot/room instance
        // that is contained by the timeTable's timeslotList/roomList.
        return new Timetable(
                timeslotRepository.findAll(),
                roomRepository.findAll(),
                workshopRepository.findAll());
    }

    public void save(Timetable timetable) {
        for (Workshop workshop : timetable.getWorkshops()) {
            workshopRepository.save(workshop);
        }
    }
}
