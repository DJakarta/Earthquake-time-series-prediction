package eu.djakarta.tsEarthquake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

public class MainWindow {
  public final ChartPanel mainChartPanel = new PannableZoomableChartPanel(null);
  public final ChartPanel predictionChartPanel = new PannableZoomableChartPanel(null);
  public final ChartPanel componentsChartPanel = new PannableZoomableChartPanel(null);
  public final JFrame frame = new JFrame("tsEarthquake");
  public final Container contentPane = this.frame.getContentPane();
  public final JComponent rightInstructionLabel = new JLabel(App.getInstructionsText());
  public final JLabel console = App.console;
  public boolean mainChartPanelPlotSynchronizationChange = false;
  public boolean predictionChartPanelPlotSynchronizationChange = false;
  public boolean shiftDownOnFrame = false;
  public final List<Prediction> predictionList = new LinkedList<>();
  public Color mainForegroundColor = new Color(70, 70, 70);
  public Color highlightsColor = new Color(40, 40, 40);
  public static Color barColor = new Color(50, 50, 30);

  public MainWindow() {
    this.addCloseBindings(frame);
    this.addKeyListeners();
    this.addPredictions();

    // Add the graphs
    JPanel graphContainer = new JPanel();
    graphContainer.setLayout(new GridLayout(3, 1));
    contentPane.add(graphContainer, BorderLayout.CENTER);
    this.addCharts(graphContainer);

    this.addRightSide();
  }

  public void addRightSide() {
    Box rightSide = new Box(BoxLayout.Y_AXIS);
    rightSide.add(this.rightInstructionLabel);
    rightSide.setBackground(new Color(0, 0, 0));
    rightSide.setOpaque(true);
    rightSide.setBorder(new LineBorder(this.highlightsColor, 2));
    this.rightInstructionLabel.setBackground(new Color(0, 0, 0));
    this.rightInstructionLabel.setOpaque(true);
    this.rightInstructionLabel.setPreferredSize(new Dimension(300, 150));
    this.rightInstructionLabel.setForeground(mainForegroundColor);
    this.rightInstructionLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

    this.console.setBackground(new Color(0, 0, 0));
    this.console.setOpaque(true);
    this.console.setPreferredSize(new Dimension(300, 150));
    this.console.setForeground(mainForegroundColor);
    this.console.setBorder(new EmptyBorder(5, 5, 5, 5));
    JScrollPane consoleScroller = new JScrollPane(this.console,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    rightSide.add(consoleScroller);

    this.contentPane.add(rightSide, BorderLayout.EAST);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private void addPredictions() {
    this.predictionList.add(new SimpleAutomaticPrediction(App.rPath));
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

    AbstractAction sDownAction = new MainWindow.SelectionDownAction(KeyEvent.VK_S);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "sDown");
    actionMap.put("sDown", sDownAction);

    AbstractAction sUpAction = new MainWindow.SelectionUpAction(KeyEvent.VK_S);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "sUp");
    actionMap.put("sUp", sUpAction);

    AbstractAction eDownAction = new MainWindow.SelectionDownAction(KeyEvent.VK_E);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "eDown");
    actionMap.put("eDown", eDownAction);

    AbstractAction eUpAction = new MainWindow.SelectionUpAction(KeyEvent.VK_E);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true), "eUp");
    actionMap.put("eUp", eUpAction);

    this.addPredictionKeyListeners();
  }

  private void addPredictionKeyListeners() {
    JComponent frame = this.frame.getRootPane();
    InputMap inputMap = frame.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = frame.getActionMap();

    /* TODO programmatically add for each key */
    AbstractAction predict1Action = new MainWindow.PredictAction(1);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "predict1");
    actionMap.put("predict1", predict1Action);

    AbstractAction predict2Action = new MainWindow.PredictAction(2);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "predict2");
    actionMap.put("predict2", predict2Action);

    AbstractAction predict3Action = new MainWindow.PredictAction(3);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "predict3");
    actionMap.put("predict3", predict3Action);
  }

  private void addCharts(Container graphContainer) {
    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
    XYBarDataset xyBarDataset = new XYBarDataset(timeSeriesCollection, App.barWidth);

    this.mainChartPanel.setChart(App.mainChart);
    graphContainer.add(this.mainChartPanel);
    this.mainChartPanel.setRangeZoomable(false);
    this.mainChartPanel.setDomainZoomable(true);
    this.setCommonChartPanelProperties(this.mainChartPanel);
    this.setCommonBarChartStyling(this.mainChartPanel);
    this.mainChartPanel.addChartMouseListener(new MouseRangeSelectionListener());
    /* TODO fix zoomable and pannable directions. Problem in listener */
    // this.mainChartPanel.getChart().getXYPlot().setDomainPannable(true);
    // this.mainChartPanel.getChart().getXYPlot().setRangePannable(true);
    this.mainChartPanel.getChart().addChangeListener(new MainWindow.SynchronizationListener());

    Range initialDomainRange =
        this.mainChartPanel.getChart().getXYPlot().getDomainAxis().getRange();
    Range initialRangeRange = this.mainChartPanel.getChart().getXYPlot().getRangeAxis().getRange();

    JFreeChart predictionChart = ChartFactory.createXYBarChart("Prediction", null, true, null,
        xyBarDataset, PlotOrientation.VERTICAL, false, true, false);
    predictionChart.getXYPlot().getDomainAxis().setRange(initialDomainRange);
    predictionChart.getXYPlot().getRangeAxis().setRange(initialRangeRange);
    this.predictionChartPanel.setChart(predictionChart);
    graphContainer.add(this.predictionChartPanel);
    this.setCommonChartPanelProperties(this.predictionChartPanel);
    this.setCommonBarChartStyling(this.predictionChartPanel);
    this.predictionChartPanel.getChart()
        .addChangeListener(new MainWindow.SynchronizationListener());

    JFreeChart componentsChart = ChartFactory.createTimeSeriesChart("Prediction components", null,
        null, timeSeriesCollection, true, true, false);
    componentsChart.getXYPlot().getDomainAxis().setRange(initialDomainRange);
    componentsChart.getXYPlot().getRangeAxis().setRange(initialRangeRange);
    this.componentsChartPanel.setChart(componentsChart);
    graphContainer.add(this.componentsChartPanel);
    this.setCommonChartPanelProperties(this.componentsChartPanel);
  }

  public void setCommonChartPanelProperties(ChartPanel panel) {
    this.setCommonStyling(panel);
    panel.setBorder(new LineBorder(this.highlightsColor, 2));
    panel.setPopupMenu(null);
  }

  public void setCommonStyling(ChartPanel panel) {
    Paint backgroundPaint = Color.BLACK;
    panel.getChart().setBackgroundPaint(backgroundPaint);
    Color titleColor = this.barColor;
    panel.getChart().getTitle().setPaint(titleColor);

    XYPlot plot = (XYPlot) panel.getChart().getPlot();
    plot.setBackgroundPaint(backgroundPaint);

    DateAxis axis = (DateAxis) plot.getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("YY-MM-dd"));
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
    renderer.setShadowVisible(false);
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
      App.log("Detected CTRL/CMD + W.");
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
      App.log("Closed window.");
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
            App.window.componentsChartPanel.getChart().getXYPlot().getDomainAxis().setRange(rangeX,
                true, true);
            Range rangeY = ev.getChart().getXYPlot().getRangeAxis().getRange();
            App.window.predictionChartPanel.getChart().getXYPlot().getRangeAxis().setRange(rangeY,
                true, true);
            Range componentsRangeY = new Range(-rangeY.getUpperBound(), rangeY.getUpperBound());
            App.window.componentsChartPanel.getChart().getXYPlot().getRangeAxis()
                .setRange(componentsRangeY, true, true);
            App.window.predictionChartPanelPlotSynchronizationChange = false;

          }
        }
        else if (ev.getChart() == App.window.predictionChartPanel.getChart()) {
          if (!App.window.mainChartPanelPlotSynchronizationChange) {
            App.window.mainChartPanelPlotSynchronizationChange = true;
            Range rangeX = ev.getChart().getXYPlot().getDomainAxis().getRange();
            App.window.mainChartPanel.getChart().getXYPlot().getDomainAxis().setRange(rangeX, true,
                true);
            App.window.componentsChartPanel.getChart().getXYPlot().getDomainAxis().setRange(rangeX,
                true, true);
            Range rangeY = ev.getChart().getXYPlot().getRangeAxis().getRange();
            App.window.mainChartPanel.getChart().getXYPlot().getRangeAxis().setRange(rangeY, true,
                true);
            Range componentsRangeY = new Range(-rangeY.getUpperBound(), rangeY.getUpperBound());
            App.window.componentsChartPanel.getChart().getXYPlot().getRangeAxis()
                .setRange(componentsRangeY, true, true);
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

  public static class PredictAction extends AbstractAction {
    private static final long serialVersionUID = -4109724432146726883L;
    public final int action;

    public PredictAction(int action) {
      this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      if (App.window.predictionList.size() > this.action - 1) {
        Prediction prediction = App.window.predictionList.get(this.action - 1);
        App.log("Called prediction number " + this.action + " ("
            + prediction.getClass().getSimpleName() + ").");
        App.predict(prediction);
      }
      else {
        App.log("No prediction set on number " + this.action + ".");
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

  public static class SelectionDownAction extends AbstractAction {
    private static final long serialVersionUID = 1535923848506198759L;
    private int key;

    public SelectionDownAction(int key) {
      this.key = key;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      if (this.key == KeyEvent.VK_S) {
        App.startSelection = true;
        App.endSelection = false;
      }
      else if (this.key == KeyEvent.VK_E) {
        App.startSelection = false;
        App.endSelection = true;
      }
    }
  }

  public static class SelectionUpAction extends AbstractAction {
    private static final long serialVersionUID = 1535923848506198759L;
    private int key;

    public SelectionUpAction(int key) {
      this.key = key;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      if (this.key == KeyEvent.VK_S) {
        App.startSelection = false;
        App.endSelection = false;
      }
      else if (this.key == KeyEvent.VK_E) {
        App.startSelection = false;
        App.endSelection = false;
      }
    }
  }

  public static class MouseRangeSelectionListener implements ChartMouseListener {

    @Override
    public void chartMouseClicked(ChartMouseEvent ev) {
      if (ev.getEntity() instanceof XYItemEntity) {
        XYItemEntity entity = (XYItemEntity) ev.getEntity();
        if (entity.getSeriesIndex() == 0) {
          entity.setItem(entity.getItem() + App.selectionEventSet.firstEvent()
              .daysDifference(App.databaseEventSet.firstEvent()));
        }
        if (App.startSelection && (App.selectionEndEntity == null
            || App.selectionEndEntity.getItem() > entity.getItem())) {
          App.selectionStartEntity = entity;
          App.select();
        }
        else if (App.endSelection && (App.selectionStartEntity == null
            || App.selectionStartEntity.getItem() < entity.getItem())) {
          App.selectionEndEntity = entity;
          App.select();
        }
      }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent ev) {
    }
  }
}