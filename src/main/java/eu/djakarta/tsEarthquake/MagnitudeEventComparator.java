package eu.djakarta.tsEarthquake;

public class MagnitudeEventComparator implements EventComparator {
  public int compare(Event event1, Event event2) {
    if (event1.magnitude > event2.magnitude) {
      return 1;
    }
    else if (event1.magnitude < event2.magnitude) {
      return -1;
    }
    else {
      return 0;
    }
  }
}
