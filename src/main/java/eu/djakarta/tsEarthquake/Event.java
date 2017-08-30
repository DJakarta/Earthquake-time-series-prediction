package eu.djakarta.tsEarthquake;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.jfree.data.time.Day;

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

  public boolean sameDay(Event event) {
    return new Day(this.time).equals(new Day(event.time));
  }

  public int daysDifference(Event event) {
    Day dayThis = new Day(this.time);
    Day dayOther = new Day(event.time);
    long millisecondDifference = dayThis.getFirstMillisecond() - dayOther.getFirstMillisecond();
    int difference = (int) TimeUnit.DAYS.convert(millisecondDifference, TimeUnit.MILLISECONDS);
    return difference;
  }

  @Override
  public String toString() {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(time);
    return "Event [magnitude=" + magnitude + ", time=" + DatatypeConverter.printDateTime(calendar)
        + ", latitude=" + latitude + ", longitude=" + longitude + ", depth=" + depth + "]";
  }

  public static Date dayToDate(Day day) {
    Calendar calendar = new GregorianCalendar();
    calendar.set(day.getYear(), day.getMonth() - 1, day.getDayOfMonth());
    return calendar.getTime();
  }
}
