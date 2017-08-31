package eu.djakarta.tsEarthquake;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JTextPane;

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
  public static JTextPane console = new JTextPane();
  public static String consoleString = "";
  public static String serverAddress = "http://www.isc.ac.uk/fdsnws/event/1/query?";
  public static double minRomaniaLatitude = 43;
  public static double maxRomaniaLatitude = 49;
  public static double minRomaniaLongitude = 19;
  public static double maxRomaniaLongitude = 30;

  public static void main(String[] args) {
    App.database = new XmlEventDatabase("db/database.xml");
    App.initialLoadDatabase();

    App.window = new MainWindow();
    App.window.checkboxRomania.requestFocus();
    App.window.display();
  }

  private static void initSelection() {
    App.selectionEventSet = App.databaseEventSet;
    App.selectionStartEntity = new XYItemEntity(new Rectangle(0, 0, 0, 0), null, 1, 0, "", "");
    App.selectionEndEntity = new XYItemEntity(new Rectangle(0, 0, 0, 0), null, 1,
        App.selectionEventSet.getNormalizedTimeAscendingSortedList().size() - 1, "", "");
    App.select();
  }

  private static void createMainChart() {
    XYBarDataset databaseDataset =
        new XYBarDataset(App.databaseEventSet.getXYBarDataset(), barWidth);

    App.mainChart = ChartFactory.createXYBarChart("Events in the database", null, true, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);
  }

  private static void loadMainChart() {
    App.window.addMainChart(App.window.graphContainer);
  }

  public static void predict(Prediction prediction) {
    prediction.predict(App.selectionEventSet, (int) App.window.predictionLengthSpinner.getValue());
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
    App.consoleString =
        App.consoleString + "<br /><br />>> " + object.toString().replace("\n", "<br />");
    App.console.setText("<html><p style=\"color:#505050;font-family:Consolas\">" + App.consoleString
        + "</p></html>");
  }

  public static void requestDatabase() {
    String server = App.window.serverAddressTextField.getText();
    String query = "";
    boolean romania = App.window.checkboxRomania.getState();
    query += "minmagnitude=" + App.window.minMagnitudeSpinner.getValue();
    query += "&maxmagnitude=" + App.window.maxMagnitudeSpinner.getValue();
    query += "&minlatitude="
        + (romania ? App.minRomaniaLatitude : App.window.minLatitudeSpinner.getValue());
    query += "&maxlatitude="
        + (romania ? App.maxRomaniaLatitude : App.window.maxLatitudeSpinner.getValue());
    query += "&minlongitude="
        + (romania ? App.minRomaniaLongitude : App.window.minLongitudeSpinner.getValue());
    query += "&maxlongitude="
        + (romania ? App.maxRomaniaLongitude : App.window.maxLongitudeSpinner.getValue());
    DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
    Date startTime = (Date) App.window.startDateSpinner.getValue();
    query += "&starttime=" + format.format(startTime);
    Date endTime = (Date) App.window.endDateSpinner.getValue();
    query += "&endtime=" + format.format(endTime);

    /* TODO implement response progress; the server returns the length of the
     * page in response headers */
    HttpRequest request = new HttpRequest(server + query, "GET");
    App.log("Executing request to " + request.url);
    HttpResponse response = request.send();
    App.log(response.code);
    File outputFile = new File("db/database.xml");
    try {
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    try (PrintStream printStream = new PrintStream(outputFile)) {
      outputFile.createNewFile();
      printStream.print(response.response);
      if (response.code == 200) {
        App.log("Downloaded events from server. The updated database is ready to be loaded"
            + " into the program.");
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void initialLoadDatabase() {
    App.log("Loading events from the local database...");
    List<Event> eventList = database.getEventList();
    App.log("Loaded " + eventList.size() + " events from the database.");
    App.log("Date range of loaded events: " + eventList.get(eventList.size() - 1).time + " -> "
        + eventList.get(0).time + ".");

    App.databaseEventSet = new EventSet(eventList);
    App.createMainChart();

    App.initSelection();
  }

  public static void loadDatabase() {
    App.initialLoadDatabase();
    App.loadMainChart();
  }
}
