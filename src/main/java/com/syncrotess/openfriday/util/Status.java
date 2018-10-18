package com.syncrotess.openfriday.util;

public class Status {

  String[] rooms    = {"R33", "R55", "R77"};                                    // Available rooms
  String[] slots    = {"9 Uhr - 10 Uhr", "10 Uhr - 11 Uhr", "11 Uhr - 12 Uhr"}; // Slots
  int      statusId = 0;                                                        // Current Status
  String   comment  = "Test";                                                   // Comment

  public Status () {

  }

  public Status (String[] rooms,
                 int statusId,
                 String comment) {
    this.rooms = rooms;
    this.statusId = statusId;
    this.comment = comment;
  }

  public String[] getRooms () {
    return rooms;
  }

  public void setRooms (String[] räume) {
    this.rooms = räume;
  }

  public String[] getSlots () {
    return slots;
  }

  public void setSlots (String[] slots) {
    this.slots = slots;
  }

  public int getStatusId () {
    return statusId;
  }

  public void setStatusId (int statusId) {
    this.statusId = statusId;
  }

  public String getComment () {
    return comment;
  }

  public void setComment (String comment) {
    this.comment = comment;
  }
}
