package eu.djakarta.tsEarthquake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class MainWindow {
  public final ChartPanel mainChartPanel = new PannableZoomableChartPanel(null);
  public final ChartPanel predictionChartPanel = new PannableZoomableChartPanel(null);
  public final ChartPanel componentsChartPanel = new PannableZoomableChartPanel(null);
  public final JFrame frame = new JFrame("tsEarthquake");
  public final Container contentPane = this.frame.getContentPane();
  public final JComponent rightSide = new JLabel("test right side");
  public boolean mainChartPanelPlotSynchronizationChange = false;
  public boolean predictionChartPanelPlotSynchronizationChange = false;
  public boolean shiftDownOnFrame = false;

  public MainWindow() {
    this.addCloseBindings(frame);
    this.addKeyListeners();

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

  private void addKeyListeners() {
    JComponent frame = this.frame.getRootPane();
    InputMap inputMap = frame.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = frame.getActionMap();

    AbstractAction shiftDownAction = new MainWindow.ShiftDownAction();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK),
        "shiftDown");
    actionMap.put("shiftDown", shiftDownAction);

    AbstractAction shiftUpAction = new MainWindow.ShiftUpAction();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), "shiftUp");
    actionMap.put("shiftUp", shiftUpAction);
  }

  private void addCharts(Container graphContainer) {
    DefaultXYDataset defaultXYDataset = new DefaultXYDataset();
    XYBarDataset xyBarDataset = new XYBarDataset(defaultXYDataset, 0.9);

    this.mainChartPanel.setChart(App.mainChart);
    graphContainer.add(this.mainChartPanel);
    this.mainChartPanel.setRangeZoomable(true);
    this.mainChartPanel.setDomainZoomable(false);
    this.setCommonChartPanelProperties(this.mainChartPanel);
    this.setCommonBarChartStyling(this.mainChartPanel);
    /* TODO fix zoomable and pannable directions. Problem in listener */
    // this.mainChartPanel.getChart().getXYPlot().setDomainPannable(true);
    // this.mainChartPanel.getChart().getXYPlot().setRangePannable(true);
    this.mainChartPanel.getChart().addChangeListener(new MainWindow.SynchronizationListener());

    JFreeChart predictionChart = ChartFactory.createXYBarChart("Prediction", null, true, null,
        new XYBarDataset(App.databaseEventSet.getXYDataset(), 24 * 60 * 60 * 1000 * 0.8),
        PlotOrientation.VERTICAL, false, true, false);
    this.predictionChartPanel.setChart(predictionChart);
    graphContainer.add(this.predictionChartPanel);
    this.setCommonChartPanelProperties(this.predictionChartPanel);
    this.setCommonBarChartStyling(this.predictionChartPanel);
    this.predictionChartPanel.getChart()
        .addChangeListener(new MainWindow.SynchronizationListener());

    JFreeChart componentsChart = ChartFactory.createXYBarChart("Prediction components", null, true,
        null, xyBarDataset, PlotOrientation.VERTICAL, false, true, false);
    this.componentsChartPanel.setChart(componentsChart);
    graphContainer.add(this.componentsChartPanel);
    this.setCommonChartPanelProperties(this.componentsChartPanel);
  }

  public void setCommonChartPanelProperties(ChartPanel panel) {
    this.setCommonStyling(panel);
    panel.setPopupMenu(null);
  }

  public void setCommonStyling(ChartPanel panel) {
    Paint backgroundPaint = Color.BLACK;
    panel.getChart().setBackgroundPaint(backgroundPaint);
    Color titleColor = new Color(50, 50, 30);
    panel.getChart().getTitle().setPaint(titleColor);

    XYPlot plot = (XYPlot) panel.getChart().getPlot();
    plot.setBackgroundPaint(backgroundPaint);
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setShadowVisible(false);

    DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("YY-MM-dd-hh-mm"));
    plot.getRenderer().setBaseToolTipGenerator(new MainWindow.MainChartToolTipGenerator());
    plot.setDomainPannable(true);
  }

  private void setCommonBarChartStyling(ChartPanel panel) {
    XYPlot plot = (XYPlot) panel.getChart().getPlot();
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    StandardXYBarPainter barPainter = new StandardXYBarPainter();
    Color barPaint = new Color(100, 100, 60);
    renderer.setSeriesPaint(0, barPaint);
    renderer.setBarPainter(barPainter);
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
    @Override
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

    @Override
    public void mousePressed(MouseEvent e) {
      int mods = e.getModifiers();
      int panMask = InputEvent.BUTTON1_MASK;
      if (mods == InputEvent.BUTTON1_MASK + InputEvent.CTRL_MASK) {
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

  public static class SynchronizationListener implements ChartChangeListener {
    @Override
    public void chartChanged(ChartChangeEvent ev) {
      if (ev instanceof PlotChangeEvent) {
        if (ev.getChart() == App.window.mainChartPanel.getChart()) {
          /* TODO fix zooming to both directions */
          if (!App.window.predictionChartPanelPlotSynchronizationChange) {
            App.window.predictionChartPanelPlotSynchronizationChange = true;
            Range rangeX = ev.getChart().getXYPlot().getDomainAxis().getRange();
            App.window.predictionChartPanel.getChart().getXYPlot().getDomainAxis().setRange(rangeX,
                true, true);
            Range rangeY = ev.getChart().getXYPlot().getRangeAxis().getRange();
            App.window.predictionChartPanel.getChart().getXYPlot().getRangeAxis().setRange(rangeY,
                true, true);
            App.window.predictionChartPanelPlotSynchronizationChange = false;

          }
        }
        else if (ev.getChart() == App.window.predictionChartPanel.getChart()) {
          if (!App.window.mainChartPanelPlotSynchronizationChange) {
            App.window.mainChartPanelPlotSynchronizationChange = true;
            Range rangeX = ev.getChart().getXYPlot().getDomainAxis().getRange();
            App.window.mainChartPanel.getChart().getXYPlot().getDomainAxis().setRange(rangeX, true,
                true);
            Range rangeY = ev.getChart().getXYPlot().getRangeAxis().getRange();
            App.window.mainChartPanel.getChart().getXYPlot().getRangeAxis().setRange(rangeY, true,
                true);
            App.window.mainChartPanelPlotSynchronizationChange = false;
          }
        }
        else {
          throw new RuntimeException("Recieved unexpected event " + ev + " on chart titled "
              + ev.getChart().getTitle().getText());
        }
      }
    }
  }

  public static class ShiftDownAction extends AbstractAction {
    private static final long serialVersionUID = 7117098039299563693L;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      /* TODO change zoom axis on mouse scroll */
      if (!App.window.shiftDownOnFrame) {
        App.window.shiftDownOnFrame = true;
        // App.window.mainChartPanel.getChart().getXYPlot().setDomainPannable(false);
        // App.window.mainChartPanel.getChart().getXYPlot().setRangePannable(true);
      }
    }
  }

  public static class ShiftUpAction extends AbstractAction {
    private static final long serialVersionUID = 7117098039299563693L;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      /* TODO change zoom axis on mouse scroll */
      if (App.window.shiftDownOnFrame) {
        App.window.shiftDownOnFrame = false;
        // App.window.mainChartPanel.getChart().getXYPlot().setDomainPannable(true);
        // App.window.mainChartPanel.getChart().getXYPlot().setRangePannable(false);
      }
    }
  }
}