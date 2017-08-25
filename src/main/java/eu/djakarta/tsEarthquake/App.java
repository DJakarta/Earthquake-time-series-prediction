package eu.djakarta.tsEarthquake;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;

public class App {
  public static void main(String[] args) {
    EventDatabase database = new XmlEventDatabase("src/main/resources/database.xml");
    List<Event> eventList = database.getEventList();
    System.out.println("Loaded " + eventList.size() + " events from the database.");
    EventSet set = new EventSet(eventList);
    App.example();
  }

  public static void example() {
    JFrame frame = new JFrame("Chart");

    double[][] data = App.sampleData();
    DefaultXYDataset defaultXYDataset = new DefaultXYDataset();
    defaultXYDataset.addSeries(0, data);
    XYBarDataset xyBarDataset = new XYBarDataset(defaultXYDataset, 0.5);

    JFreeChart chart1 = ChartFactory.createXYBarChart("Test Chart", "X", false, "Y", xyBarDataset,
        PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chart2 = ChartFactory.createXYBarChart("Test Chart E", "X", false, "Y", xyBarDataset,
        PlotOrientation.VERTICAL, false, false, false);
    frame.getContentPane().add(new ChartPanel(chart1), BorderLayout.WEST);
    frame.getContentPane().add(new ChartPanel(chart2), BorderLayout.EAST);
    frame.pack();
    frame.setVisible(true);
  }

  public static double[][] sampleData() {
    double[][] data = new double[2][50];
    for (int i = 0; i < 50; i++) {
      if (i < 20) {
        data[0][i] = 2 * i;
      } else {
        data[0][i] = i;
      }
      data[1][i] = i * i;
    }
    return data;
  }
}
