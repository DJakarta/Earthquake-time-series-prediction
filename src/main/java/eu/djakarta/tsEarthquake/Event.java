package eu.djakarta.tsEarthquake;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

public class Event {
  public final double magnitude;
  public final Date time;
  public final double latitude;
  public final double longitude;
  public final double depth;

  public Event(double magnitude, Date time, double latitude, double longitude, double depth) {
    this.magnitude = magnitude;
    this.time = time;
    this.latitude = latitude;
    this.longitude = longitude;
    this.depth = depth;
  }

  @Override
  public String toString() {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(time);
    return "Event [magnitude=" + magnitude + ", time=" + DatatypeConverter.printDateTime(calendar)
        + ", latitude=" + latitude + ", longitude=" + longitude + ", depth=" + depth + "]";
  }
}
