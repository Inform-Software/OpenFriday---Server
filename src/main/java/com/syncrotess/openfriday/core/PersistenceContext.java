package com.syncrotess.openfriday.core;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.syncrotess.openfriday.util.User;

@Configuration
@EnableTransactionManagement
@ComponentScan ("com.syncrotess.openfriday*")
@EnableNeo4jRepositories ("com.syncrotess.openfriday.manager")
public class PersistenceContext {

  public PersistenceContext () {
    System.out.println ("Persistence initiated.");
  }

  @Bean (destroyMethod = "shutdown")
  public GraphDatabaseService graphDatabaseService () {
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory ().newEmbeddedDatabaseBuilder (new File ("target/graph.db"))
                                                                           .setConfig (GraphDatabaseSettings.forbid_shortestpath_common_nodes,
                                                                                       "false")
                                                                           .newGraphDatabase ();

    return graphDatabaseService;
  }

  @Bean
  public SessionFactory sessionFactory () {
    return new SessionFactory (new EmbeddedDriver (graphDatabaseService ()), User.class.getPackage ().getName ());
  }

  @Bean
  public Neo4jTransactionManager transactionManager () throws Exception {
    return new Neo4jTransactionManager (sessionFactory ());
  }

}
