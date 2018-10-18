package com.syncrotess.openfriday.manager;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.syncrotess.openfriday.util.User;

public interface UserRepository extends Neo4jRepository<User, Long> {

  // derived finder
  Optional<User> findByName (String name);

}
