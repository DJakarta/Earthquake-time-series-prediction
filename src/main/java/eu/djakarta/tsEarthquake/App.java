package eu.djakarta.tsEarthquake;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;

public class App {
  public static MainWindow window;
  public static EventSet databaseEventSet;
  public static EventDatabase database;
  public static JFreeChart mainChart;
  public static double barWidth = 24 * 60 * 60 * 1000 * 0.8;

  public static void main(String[] args) {
    App.database = new XmlEventDatabase("src/main/resources/database.xml");
    List<Event> eventList = database.getEventList();
    System.out.println("Loaded " + eventList.size() + " events from the database.");
    System.out.println("Date range of loaded events: " + eventList.get(0).time + " -> "
        + eventList.get(eventList.size() - 1).time + ".");
    System.out.println("");

    App.databaseEventSet = new EventSet(eventList);
    App.loadDatabaseEventsInMainChart();

    App.window = new MainWindow();
    App.window.display();
  }

  private static void loadDatabaseEventsInMainChart() {
    XYBarDataset databaseDataset = new XYBarDataset(App.databaseEventSet.getXYDataset(), barWidth);

    App.mainChart = ChartFactory.createXYBarChart("Events in the database", null, true, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);
  }

  public static void predict(Prediction prediction) {
    prediction.predict(App.databaseEventSet);
  }
}
