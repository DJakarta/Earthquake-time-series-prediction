package eu.djakarta.tsEarthquake;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;

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
  public static boolean startSelection = false;
  public static boolean endSelection = false;
  public static XYItemEntity selectionStartEntity = null;
  public static XYItemEntity selectionEndEntity = null;
  public static EventSet selectionEventSet;
  public static JLabel console = new JLabel();
  public static String consoleString = "";

  public static void main(String[] args) {
    App.database = new XmlEventDatabase("db/database.xml");
    List<Event> eventList = database.getEventList();
    App.log("Loaded " + eventList.size() + " events from the database.");
    App.log("Date range of loaded events: " + eventList.get(eventList.size() - 1).time
        + " -> " + eventList.get(0).time + ".");
    App.log("");

    App.databaseEventSet = new EventSet(eventList);
    App.loadDatabaseEventsInMainChart();

    App.initSelection();

    App.window = new MainWindow();
    App.window.display();
  }

  private static void initSelection() {
    App.selectionEventSet = App.databaseEventSet;
    App.selectionStartEntity = new XYItemEntity(new Rectangle(0, 0, 0, 0), null, 1, 0, "", "");
    App.selectionEndEntity = new XYItemEntity(new Rectangle(0, 0, 0, 0), null, 1,
        App.selectionEventSet.getNormalizedTimeAscendingSortedList().size() - 1, "", "");
    App.select();
  }

  private static void loadDatabaseEventsInMainChart() {
    XYBarDataset databaseDataset =
        new XYBarDataset(App.databaseEventSet.getXYBarDataset(), barWidth);

    App.mainChart = ChartFactory.createXYBarChart("Events in the database", null, true, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);
  }

  public static void predict(Prediction prediction) {
    prediction.predict(App.selectionEventSet, 3 * 365);
  }

  public static String getInstructionsText() {
    String str = "";
    str += "<html>";
    str += "To select a starting time for prediction hold the (S) key and click an event.<br />";
    str += "To select a ending time for prediction hold the (E) key and click an event.<br />";
    str += "To predict a certain period of time using a registered prediction method, press the "
        + "prediction keys (1-9).";
    str += "</html>";
    return str;
  }

  public static Event getEventFromEntity(XYItemEntity entity) {
    return (entity != null)
        ? databaseEventSet.getNormalizedTimeAscendingSortedList().get(entity.getItem())
        : null;
  }

  public static Event getEventFromEntityInSelection(XYItemEntity entity) {
    return (entity != null)
        ? selectionEventSet.getNormalizedTimeAscendingSortedList().get(entity.getItem())
        : null;
  }

  public static TimeSeriesCollection databaseAndSelectionTimeSeriesCollection() {
    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
    timeSeriesCollection.addSeries(App.selectionEventSet.getTimeSeries());
    timeSeriesCollection.addSeries(App.databaseEventSet.getTimeSeries());
    return timeSeriesCollection;
  }

  public static void select() {
    if (App.selectionStartEntity != null && App.selectionEndEntity != null) {
      App.selectionEventSet = new EventSet(databaseEventSet.getNormalizedTimeAscendingSortedList()
          .subList(App.selectionStartEntity.getItem(), App.selectionEndEntity.getItem() + 1));
      App.mainChart.getXYPlot().setDataset(
          new XYBarDataset(App.databaseAndSelectionTimeSeriesCollection(), App.barWidth));
      App.mainChart.getXYPlot().getRenderer().setSeriesPaint(1, Color.DARK_GRAY);
      App.mainChart.getXYPlot().getRenderer().setSeriesPaint(0, Color.YELLOW);
      App.log("Selected events from " + App.selectionEventSet.firstTime() + " to "
          + App.selectionEventSet.lastTime() + ".");
    }
  }
  
  public static void log(Object object) {
    System.out.println(object);
    App.consoleString = App.consoleString + "<br />" + object.toString().replace("\n", "<br />");
    App.console.setText("<html>" + App.consoleString + "</html>");
  }
}
