package eu.djakarta.tsEarthquake;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

public class EventSet {
  private List<Event> magnitudeAscendingList;
  private List<Event> timeAscendingList;
  private List<Event> normalizedMagnitudeAscendingList;
  private List<Event> normalizedTimeAscendingList;

  public EventSet(List<Event> list) {
    this.magnitudeAscendingList = new LinkedList<>(list);
    this.magnitudeAscendingList.sort(new MagnitudeEventComparator());
    this.timeAscendingList = new LinkedList<>(list);
    this.timeAscendingList.sort(new TimeEventComparator());
    this.normalizedMagnitudeAscendingList = EventSet.normalizeList(this.timeAscendingList);
    this.normalizedTimeAscendingList = new LinkedList<>(this.normalizedMagnitudeAscendingList);
    this.normalizedTimeAscendingList.sort(new TimeEventComparator());
  }

  public EventSet(double[] magnitudes, Day firstDay) {
    this(EventSet.seriesToEventList(magnitudes, firstDay));
  }

  private static List<Event> seriesToEventList(double[] magnitudes, Day firstDay) {
    LinkedList<Event> list = new LinkedList<>();
    Day currentDay = firstDay;
    for (int i = 0; i < magnitudes.length; i++) {
      Event event = new Event(magnitudes[i], Event.dayToDate(currentDay), 0, 0, 0);
      currentDay = (Day) currentDay.next();
      list.add(event);
    }
    return list;
  }

  public static List<Event> normalizeList(List<Event> timeAscendingList) {
    List<Event> list = new LinkedList<>();
    for (int i = 0; i < timeAscendingList.size(); i++) {
      int j = i;
      Event greatestMagnitudeOnDay = timeAscendingList.get(i);
      while (j + 1 < timeAscendingList.size()
          && timeAscendingList.get(i).sameDay(timeAscendingList.get(j + 1))) {
        j++;
        if (timeAscendingList.get(j).magnitude >= greatestMagnitudeOnDay.magnitude) {
          greatestMagnitudeOnDay = timeAscendingList.get(j);
        }
      }
      list.add(greatestMagnitudeOnDay);
      i = j;
    }

    return list;
  }

  public List<Event> getMagnitudeAscendingSortedList() {
    return Collections.unmodifiableList(this.magnitudeAscendingList);
  }

  public List<Event> getTimeAscendingSortedList() {
    return Collections.unmodifiableList(this.timeAscendingList);
  }

  public List<Event> getNormalizedMagnitudeAscendingSortedList() {
    return Collections.unmodifiableList(this.normalizedMagnitudeAscendingList);
  }

  public List<Event> getNormalizedTimeAscendingSortedList() {
    return Collections.unmodifiableList(this.normalizedTimeAscendingList);
  }

  public XYDataset getXYBarDataset() {
    TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(this.getTimeSeries());
    XYBarDataset barDataset = new XYBarDataset(dataset, App.barWidth);
    return barDataset;
  }

  public TimeSeries getTimeSeries() {
    TimeSeries timeSeries = new TimeSeries("databaseEvents");
    for (Event event : this.normalizedTimeAscendingList) {
      timeSeries.addOrUpdate(new Day(event.time), event.magnitude);
    }
    return timeSeries;
  }

  public Event firstEvent() {
    /* TODO fix empty event set problems */
    return this.timeAscendingList.get(0);
  }

  public Event lastEvent() {
    return this.timeAscendingList.get(this.timeAscendingList.size() - 1);
  }

  public Date firstTime() {
    return this.firstEvent().time;
  }

  public Date lastTime() {
    return this.lastEvent().time;
  }

  public double[] getNormalizeMagnitudeSeries() {
    double[] magnitudeSeries = new double[this.lastEvent().daysDifference(this.firstEvent()) + 1];
    for (int i = 0; i < normalizedTimeAscendingList.size(); i++) {
      int pos = this.normalizedTimeAscendingList.get(i).daysDifference(this.firstEvent());
      magnitudeSeries[pos] = normalizedTimeAscendingList.get(i).magnitude;
    }
    return magnitudeSeries;
  }
}
