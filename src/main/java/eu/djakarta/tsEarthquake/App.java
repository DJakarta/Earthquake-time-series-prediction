package eu.djakarta.tsEarthquake;

import java.awt.Color;
import java.awt.Paint;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
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

    XYPlot mainPlot = (XYPlot) App.mainChart.getPlot();
    DateAxis axis = (DateAxis) mainPlot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("YY-MM-dd-hh-mm"));
    mainPlot.getRenderer().setBaseToolTipGenerator(new MainWindow.MainChartToolTipGenerator());
    mainPlot.setDomainPannable(true);
  }

  private static void styleMainChart() {
    Paint backgroundPaint = Color.BLACK;
    App.mainChart.setBackgroundPaint(backgroundPaint);
    Color titleColor = new Color(50, 50, 30);
    App.mainChart.getTitle().setPaint(titleColor);

    XYPlot plot = (XYPlot) mainChart.getPlot();
    plot.setBackgroundPaint(backgroundPaint);
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setShadowVisible(false);
    StandardXYBarPainter barPainter = new StandardXYBarPainter();
    Color barPaint = new Color(100, 100, 60);
    renderer.setSeriesPaint(0, barPaint);
    renderer.setBarPainter(barPainter);
  }
}
