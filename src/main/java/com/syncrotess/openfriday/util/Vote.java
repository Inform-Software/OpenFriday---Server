package com.syncrotess.openfriday.util;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Vote {

  @Id
  @GeneratedValue
  private Long   id;
  private String name;
  private int    interest;

  public String getName () {
    if (name == null) {
      name = "";
    }
    return name;
  }

  public int getInterest () {
    return interest;
  }

  public Vote setName (String name) {
    this.name = name;
    return this;
  }

  public Vote setInterest (int interest) {
    this.interest = interest;
    return this;
  }

}
