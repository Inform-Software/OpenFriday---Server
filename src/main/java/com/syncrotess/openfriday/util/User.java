package com.syncrotess.openfriday.util;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class User {

  @Id
  @GeneratedValue
  Long    id;            // The unique ID of the user
  String  name;          // The name of the user
  boolean admin = false; // Whether the user has administrator rights
  long[]  workshops;     // ID's of all Workshops

  public long[] getWorkshops () {
    if (workshops == null) {
      workshops = new long[0];
    }
    return workshops;
  }

  public User setWorkshops (long[] workids) {
    this.workshops = workids;
    return this;
  }

  public User setId (Long id) {
    this.id = id;
    return this;
  }

  public String getName () {
    return name;
  }

  public User setName (String name) {
    this.name = name;
    return this;
  }

  public boolean isAdmin () {
    return admin;
  }

  public User setAdmin (boolean admin) {
    this.admin = admin;
    return this;
  }

  public Long getId () {
    return id;
  }

  public boolean isInWorkshop (long workshopId) {
    for (int i = 0; i < workshops.length; i++) {
      if (workshopId == workshops[i]) {
        return true;
      }
    }
    return false;
  }

  public User addWorkshop (Workshop workshop) {
    clearInvalidWorkshops ();
    if (isInWorkshop (workshop.getId ())) {
      return this;
    }
    System.out.println ("Adding Workshop " + workshop.getName () + " to user " + getName ());
    long[] newWorkshops = new long[getWorkshops ().length + 1];
    for (int i = 0; i < workshops.length; i++) {
      System.out.println ("For: " + i + " (" + workshops[i] + ")");
      newWorkshops[i] = workshops[i];
    }
    newWorkshops[workshops.length] = workshop.getId (); // To last index
    workshops = newWorkshops;
    return this;
  }

  public void clearInvalidWorkshops () { // Entfernt alle Workshops, die 0 sind.
    int invalid = 0;
    for (int i = 0; i < workshops.length; i++) {
      if (workshops[i] <= 0) {
        invalid++;
      }
    }
    if (invalid == 0) {
      return;
    }
    long[] newWorkshops = new long[workshops.length - invalid];
    int index = 0;
    for (int i = 0; i < workshops.length; i++) {
      if (workshops[i] > 0) {
        newWorkshops[index] = workshops[i];
        index++;
      }
    }
    workshops = newWorkshops;

  }

}
