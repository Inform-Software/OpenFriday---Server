package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.Workshop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopRepository extends CrudRepository<Workshop, Long> {
}
