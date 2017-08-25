package eu.djakarta.tsEarthquake;

public class TimeEventComparator implements EventComparator {
  public int compare(Event event1, Event event2) {
    if (event1.time.after(event2.time)) {
      return 1;
    }
    else if (event1.time.before(event2.time)) {
      return -1;
    }
    else {
      return 0;
    }
  }
}
