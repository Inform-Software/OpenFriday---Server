package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.Timeslot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeslotRepository extends CrudRepository<Timeslot, Long> {

}
