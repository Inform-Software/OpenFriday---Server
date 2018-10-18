package com.syncrotess.openfriday.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@ComponentScan ("com.syncrotess.openfriday*")
@EntityScan ("com.syncrotess.openfriday*")
@EnableNeo4jRepositories ("com.syncrotess.openfriday*")
public class Application {

  public static void main (String[] args) {
    // File databaseDirectory;
    // GraphDatabaseService graphDb = new GraphDatabaseFactory ().newEmbeddedDatabase (databaseDirectory);
    // registerShutdownHook (graphDb);
    SpringApplication.run (Application.class, args);
  }
}
