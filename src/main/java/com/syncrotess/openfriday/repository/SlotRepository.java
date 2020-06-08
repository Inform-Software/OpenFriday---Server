package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.Slot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends CrudRepository<Slot, Long> {

}
