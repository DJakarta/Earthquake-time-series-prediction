package eu.djakarta.tsEarthquake;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventSet {
  private List<Event> magnitudeAscending;
  private List<Event> timeAscending;
  
  public EventSet(List<Event> list) {
    this.magnitudeAscending = new LinkedList<>(list);
    this.magnitudeAscending.sort(new MagnitudeEventComparator());
    this.timeAscending = new LinkedList<>(list);
    this.timeAscending.sort(new TimeEventComparator());
  }
  
  public List<Event> magnitudeAscendingSortedList() {
    return Collections.unmodifiableList(this.magnitudeAscending);
  }
  
  public List<Event> timeAscendingSortedList() {
    return Collections.unmodifiableList(this.timeAscending);
  }
}
