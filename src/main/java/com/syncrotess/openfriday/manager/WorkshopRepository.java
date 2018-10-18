package com.syncrotess.openfriday.manager;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.syncrotess.openfriday.util.Workshop;

public interface WorkshopRepository extends Neo4jRepository<Workshop, Long> {

  Workshop findByName (String name);

}
