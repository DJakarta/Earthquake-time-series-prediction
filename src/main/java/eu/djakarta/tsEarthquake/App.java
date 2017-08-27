package eu.djakarta.tsEarthquake;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYBarDataset;

public class App {
  public static MainWindow window;
  public static EventSet databaseEventSet;
  public static EventDatabase database;
  public static JFreeChart mainChart;

  public static void main(String[] args) {
    App.database = new XmlEventDatabase("src/main/resources/database.xml");
    List<Event> eventList = database.getEventList();
    System.out.println("Loaded " + eventList.size() + " events from the database.");

    App.databaseEventSet = new EventSet(eventList);
    App.loadDatabaseEventsInMainChart();
    App.styleMainChart();

    App.window = new MainWindow();
    App.window.display();
  }

  private static void loadDatabaseEventsInMainChart() {
    XYBarDataset databaseDataset =
        new XYBarDataset(App.databaseEventSet.getXYDataset(), 24 * 60 * 60 * 1000 * 0.8);

    App.mainChart = ChartFactory.createXYBarChart("Events in the database", null, true, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);
  }

  private static void styleMainChart() {
  }
}
