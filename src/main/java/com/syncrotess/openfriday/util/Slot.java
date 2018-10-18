package com.syncrotess.openfriday.util;

import org.json.JSONObject;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Slot {

  private Workshop[] workshops; // Index = Room-Index

  public Slot (int roomAmount) {
    workshops = new Workshop[roomAmount];
    fillDummies ();
  }

  public void addWorkshop (Workshop workshop,
                           int roomIndex) {
    if (roomIndex < 0 || roomIndex >= workshops.length) {
      return;
    }

    workshops[roomIndex] = workshop;
  }

  private void fillDummies () {
    for (int i = 0; i < workshops.length; i++) {
      workshops[i] = Workshop.getDummy ();
    }
  }

  public Workshop getWorkshopInRoom (int roomIndex) {
    if (roomIndex < 0 || roomIndex >= workshops.length) {
      return null;
    }
    return workshops[roomIndex];
  }

  @Override
  public String toString () {
    return asJson ().toString ();
  }

  public JSONObject asJson () {
    JSONObject data = new JSONObject ();
    try {
      data.put ("workshops", workshops);
    } catch (Exception e) {
      e.printStackTrace ();
    }
    return data;
  }
}
