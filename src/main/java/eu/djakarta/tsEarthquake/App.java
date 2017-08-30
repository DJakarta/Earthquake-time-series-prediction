package eu.djakarta.tsEarthquake;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYBarDataset;

/* TODO add filtering subset of events and mouse interaction for filtering */
/* TODO add prediction summary */
/* TODO add trendline to simple auto prediction */
/* TODO add prediction loading and settings (rPath, etc) */
/* TODO add Romania earthquakes to database */
/* TODO add custom database download */
/* TODO package and test package */

public class App {
  public static MainWindow window;
  public static EventSet databaseEventSet;
  public static EventDatabase database;
  public static JFreeChart mainChart;
  public static double barWidth = 24 * 60 * 60 * 1000 * 0.8;
  public static String rPath = "";

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
    XYBarDataset databaseDataset =
        new XYBarDataset(App.databaseEventSet.getXYBarDataset(), barWidth);

    App.mainChart = ChartFactory.createXYBarChart("Events in the database", null, true, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);
  }

  public static void predict(Prediction prediction) {
    prediction.predict(App.databaseEventSet, 3 * 365);
  }

  public static String getInstructionsText() {
    String str = "";
    str += "<html>";
    str += "To predict a certain period of time using a registered prediction method, press the "
        + "prediction keys (1-9).";
    str += "</html>";
    return str;
  }
}
