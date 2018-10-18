package com.syncrotess.openfriday.manager;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.syncrotess.openfriday.util.Vote;

public interface VoteRepository extends Neo4jRepository<Vote, Long> {

  Vote findByName (String name);

}
