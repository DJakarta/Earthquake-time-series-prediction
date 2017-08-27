package eu.djakarta.tsEarthquake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

public class MainWindow {
  public final ChartPanel mainChartPanel = new PannableZoomableChartPanel(null);
  public final ChartPanel predictionChartPanel = new ChartPanel(null);
  public final ChartPanel componentsChartPanel = new ChartPanel(null);
  public final JFrame frame = new JFrame("tsEarthquake");
  public final Container contentPane = this.frame.getContentPane();
  public final JComponent rightSide = new JLabel("test right side");

  public MainWindow() {
    this.addCloseBindings(frame);

    /* this.frame.addMouseListener(new MainWindow.InstantPopupMouseAdapter()); */

    // Add the graphs
    Container graphContainer = new Container();
    graphContainer.setLayout(new GridLayout(3, 1));
    contentPane.add(graphContainer, BorderLayout.CENTER);
    this.addCharts(graphContainer);

    // Add the right side
    this.contentPane.add(this.rightSide, BorderLayout.EAST);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private void addCharts(Container graphContainer) {
    DefaultXYDataset defaultXYDataset = new DefaultXYDataset();
    XYBarDataset xyBarDataset = new XYBarDataset(defaultXYDataset, 0.9);

    this.mainChartPanel.setChart(App.mainChart);
    graphContainer.add(this.mainChartPanel);
    this.mainChartPanel.setRangeZoomable(false);
    this.mainChartPanel.setDomainZoomable(true);
    this.setCommonChartPanelProperties(this.mainChartPanel);

    JFreeChart predictionChart = ChartFactory.createXYBarChart("Prediction", null, false, null,
        new XYBarDataset(App.databaseEventSet.getXYDataset(), 24 * 60 * 60 * 1000),
        PlotOrientation.VERTICAL, false, true, false);
    this.predictionChartPanel.setChart(predictionChart);
    graphContainer.add(this.predictionChartPanel);
    this.setCommonChartPanelProperties(this.predictionChartPanel);

    JFreeChart componentsChart = ChartFactory.createXYBarChart("Prediction components", null, false,
        null, xyBarDataset, PlotOrientation.VERTICAL, false, true, false);
    this.componentsChartPanel.setChart(componentsChart);
    graphContainer.add(this.componentsChartPanel);
    this.setCommonChartPanelProperties(this.componentsChartPanel);
  }

  private void setCommonChartPanelProperties(ChartPanel panel) {
    panel.setPopupMenu(null);
  }

  public void display() {
    frame.setVisible(true);
  }

  private void addCloseBindings(JFrame frame) {
    frame.getRootPane().getActionMap().put("close-window", new MainWindow.CloseAction(frame));
    frame.getRootPane().getInputMap(JComponent.WHEN_FOCUSED)
        .put(KeyStroke.getKeyStroke("control W"), "close-window");
    frame.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("meta W"),
        "close-window");

    frame.addWindowListener(new MainWindow.WindowClosedListener());
  }

  public void setToolTipRecursively(JComponent component, String text) {
    component.setToolTipText(text);
    for (Component child : component.getComponents()) {
      if (child instanceof JComponent) {
        setToolTipRecursively((JComponent) child, text);
      }
    }
  }

  public static class CloseAction extends AbstractAction {
    private static final long serialVersionUID = -2124532310470337508L;
    private Window window;

    public CloseAction(Window window) {
      this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      System.out.println("Detected CTRL/CMD + W.");
      if (window != null) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
      }
    }
  }

  public static class MainChartToolTipGenerator extends StandardXYToolTipGenerator {
    private static final long serialVersionUID = 8497826675138472136L;

    @Override
    public String generateToolTip(XYDataset dataset, int series, int item) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM YYYY");
      Date eventDate = new Date((long) dataset.getX(series, item));
      String toolTipString = "<html>";
      toolTipString += "<p>mag: " + dataset.getY(series, item) + "</p>";
      toolTipString += "<p>date: " + dateFormat.format(eventDate) + "</p>";
      toolTipString += "<p>depth: </p>";
      toolTipString += "<p>lat: </p>";
      toolTipString += "<p>long: </p>";
      toolTipString += "</html>";
      return toolTipString;
    }
  }

  public static class WindowClosedListener extends WindowAdapter {
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
      System.out.println("Closed window.");
    }
  }

  public static class PannableZoomableChartPanel extends ChartPanel {
    private static final long serialVersionUID = -3012263738404823127L;

    public PannableZoomableChartPanel(JFreeChart chart) {
      super(chart);
      this.setMouseWheelEnabled(true);
    }

    public void mousePressed(MouseEvent e) {
      int mods = e.getModifiers();
      int panMask = MouseEvent.BUTTON1_MASK;
      if (mods == MouseEvent.BUTTON1_MASK + MouseEvent.SHIFT_MASK) {
        panMask = 255;
      }
      try {
        Field mask = ChartPanel.class.getDeclaredField("panMask");
        mask.setAccessible(true);
        mask.set(this, panMask);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      super.mousePressed(e);
    }
  }
}