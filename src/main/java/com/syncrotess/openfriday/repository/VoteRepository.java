package com.syncrotess.openfriday.repository;

import com.syncrotess.openfriday.entities.Vote;
import com.syncrotess.openfriday.entities.Workshop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {
    public List<Vote> findAllByWorkshopAndPriority(Workshop workshop, int priority);
}
