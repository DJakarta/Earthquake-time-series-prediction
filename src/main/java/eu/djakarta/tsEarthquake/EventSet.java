package eu.djakarta.tsEarthquake;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class EventSet {
  private List<Event> magnitudeAscendingList;
  private List<Event> timeAscendingList;

  public EventSet(List<Event> list) {
    this.magnitudeAscendingList = new LinkedList<>(list);
    this.magnitudeAscendingList.sort(new MagnitudeEventComparator());
    this.timeAscendingList = new LinkedList<>(list);
    this.timeAscendingList.sort(new TimeEventComparator());
  }

  public List<Event> magnitudeAscendingSortedList() {
    return Collections.unmodifiableList(this.magnitudeAscendingList);
  }

  public List<Event> timeAscendingSortedList() {
    return Collections.unmodifiableList(this.timeAscendingList);
  }

  public XYDataset getXYDataset() {
    /* TODO implement getXYDataset */
    TimeSeries timeSeries = new TimeSeries("databaseEvents");

    for (Event event : this.timeAscendingList) {
      timeSeries.add(new Day(event.time), event.magnitude);
    }
    TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(timeSeries);
    return dataset;
  }
}
