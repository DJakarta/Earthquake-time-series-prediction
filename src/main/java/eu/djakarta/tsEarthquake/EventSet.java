package eu.djakarta.tsEarthquake;

import java.util.LinkedList;
import java.util.List;

public class EventSet {
  private List<Event> magnitudeAscending;
  private List<Event> dateAscending;
  
  public EventSet(List<Event> list) {
    this.magnitudeAscending = new LinkedList<>(list);
    this.magnitudeAscending.sort(new MagnitudeEventComparator());
    this.dateAscending = new LinkedList<>(list);
    this.dateAscending.sort(new MagnitudeEventComparator());
  }
}
