package com.syncrotess.openfriday.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Timetable {

  Slot[] slots;
  int    slotAmount;
  int    roomAmount;

  public Timetable (int slotAmount,
                    int roomAmount) {
    this.slotAmount = slotAmount;
    this.roomAmount = roomAmount;
    slots = new Slot[slotAmount];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = new Slot (roomAmount);
    }
  }

  public Slot[] getSlots () {
    return slots;
  }

  public int getSlotAmount () {
    return slotAmount;
  }

  public int getRoomAmount () {
    return roomAmount;
  }

  public Slot getSlot (int slotIndex) {
    if (slotIndex < 0 || slotIndex >= slots.length) {
      return null;
    }
    return slots[slotIndex];
  }

  public void setSlot (int slotIndex,
                       Slot slot) {
    if (slotIndex < 0 || slotIndex >= slots.length) {
      return;
    }
    slots[slotIndex] = slot;
  }

  public Workshop[] getWorkshopsInRoom (int roomIndex) {
    Workshop[] roomWorkshops = new Workshop[slots.length]; // Pro Slot ein Workshop in Raum (bzw. Dummy)
    for (int i = 0; i < slots.length; i++) {
      roomWorkshops[i] = slots[i].getWorkshopInRoom (roomIndex);
    }
    return roomWorkshops;
  }

  public Timetable addAll (Iterable<Workshop> iterable) {
    List<Workshop> list = new ArrayList<> ();
    iterable.forEach (list::add);
    int added = 0;
    System.out.println ("List size: " + list.size ());
    for (int slot = 0; slot < slotAmount; slot++) {
      for (int room = 0; room < roomAmount; room++) {
        if (added == list.size ()) {
          break;
        }
        slots[slot].setWorkshopInRoom (room, list.get (added));
        added++;
      }
    }
    return this;
  }

  @Override
  public String toString () {
    JSONObject data = new JSONObject ();

    try {
      data.put ("slotAmount", this.slotAmount);
      data.put ("roomAmount", this.roomAmount);
      JSONArray array = new JSONArray ();
      for (int i = 0; i < slots.length; i++) {
        try {
          array.put (slots[i].asJson ());
        } catch (Exception e) {
          e.printStackTrace ();
        }
      }
      data.put ("slots", array);
    } catch (Exception e) {
      e.printStackTrace ();
    }
    return data.toString ();
  }

}
