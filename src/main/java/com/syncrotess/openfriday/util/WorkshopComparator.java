package com.syncrotess.openfriday.util;

import java.util.Comparator;

public class WorkshopComparator implements Comparator<Workshop> {

  @Override
  public int compare (Workshop first,
                      Workshop second) {
    return Integer.compare (first.getVotes ().size (), second.getVotes ().size ());
  }
}
