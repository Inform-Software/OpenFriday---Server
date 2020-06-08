package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.PlanSlot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanSlotRepository extends CrudRepository<PlanSlot, Long> {
}
