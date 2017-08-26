package eu.djakarta.tsEarthquake;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
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
    App.loadDatabaseEvents();
    App.styleMainChart();

    App.window = new MainWindow();
    App.window.display();
  }

  private static void loadDatabaseEvents() {
    XYBarDataset databaseDataset = new XYBarDataset(databaseEventSet.getXYDataset(), 0.9);

    App.mainChart = ChartFactory.createXYBarChart("Events in database", null, false, null,
        databaseDataset, PlotOrientation.VERTICAL, false, true, false);

    XYPlot mainPlot = (XYPlot) App.mainChart.getPlot();
    /* DateAxis axis = (DateAxis) mainPlot.getDomainAxis();
     * axis.setDateFormatOverride(new SimpleDateFormat("ddhhmmss")); */
    mainPlot.getRenderer().setBaseToolTipGenerator(new MainWindow.MainChartToolTipGenerator());
  }

  private static void styleMainChart() {
    XYBarRenderer barRenderer = new XYBarRenderer();
    barRenderer.setShadowVisible(false);
    XYPlot plot = (XYPlot) mainChart.getPlot();
    /* barRenderer.setSeriesPaint(0, Color.blue); */
    plot.setRenderer(0, barRenderer);
  }
}
