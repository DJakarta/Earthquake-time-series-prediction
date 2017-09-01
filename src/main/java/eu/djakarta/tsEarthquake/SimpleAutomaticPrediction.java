package eu.djakarta.tsEarthquake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class SimpleAutomaticPrediction implements Prediction {
  public final String rPath;
  private double[] series;
  private double[] residuals;

  public SimpleAutomaticPrediction(String rPath) {
    this.rPath = rPath;
  }

  @Override
  public void predict(EventSet eventSet, int predictionLength) {
    App.log("Attempting automatic prediction...");
    String info = "A model of (0, 0, 0) means that there is not enough correlation in the data. "
        + "This indicates no seasonality. A fairly straight line or big residuals also mean that"
        + "there is no seasonal trend.";
    App.log(info);
    String directoryName = "scripts";
    String inputFileName = "simpleAutomaticPrediction.in";
    File predictionInputFile = this.createScriptInputFile(directoryName, inputFileName);
    String outputFileName = "simpleAutomaticPrediction.out";
    File predictionOutputFile = new File(directoryName + "/" + outputFileName);
    this.writeToScriptInputFile(eventSet, predictionLength, predictionInputFile);

    this.callRScript(directoryName);

    this.readFromScriptOutputFile(predictionOutputFile);
    Day firstDay = new Day(new Date(eventSet.lastTime().getTime() + 24 * 60 * 60 * 1000));

    EventSet predicted = new EventSet(this.series, firstDay);

    App.window.predictionChartPanel.getChart().getXYPlot().setDataset(predicted.getXYBarDataset());
    App.window.componentsChartPanel.getChart().getXYPlot()
        .setDataset(new TimeSeriesCollection(this.residualsToTimeSeries(eventSet)));
    
    App.log("Warning: " + info);

    this.cleanup(predictionInputFile, predictionOutputFile);
  }

  private TimeSeries residualsToTimeSeries(EventSet eventSet) {
    TimeSeries timeSeries = new TimeSeries("ARIMA model residuals");
    Day currentDay = new Day(eventSet.firstTime());
    for (int i = 0; i < this.residuals.length; i++) {
      timeSeries.addOrUpdate(currentDay, residuals[i]);
      currentDay = (Day) currentDay.next();
    }
    return timeSeries;
  }

  private void callRScript(String directoryName) {
    try {
      String string =
          this.executeCommand("cmd /c Rscript simpleAutomaticPrediction.R", directoryName);
      App.log(string);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String executeCommand(String command, String workingDirectory) throws IOException {
    Runtime runtime = Runtime.getRuntime();
    Process process = runtime.exec(command, null, new File(workingDirectory));
    InputStream errorStream = process.getErrorStream();
    this.logErrorStream(errorStream);
    InputStream inputStream = process.getInputStream();
    Scanner scanner1 = new Scanner(inputStream);
    Scanner scanner2 = scanner1.useDelimiter("\\A");
    String output = scanner2.hasNext() ? scanner2.next() : "";
    scanner1.close();
    scanner2.close();
    return output;
  }

  public void logErrorStream(InputStream errorStream) {
    if (errorStream != null) {
      Scanner scanner1 = new Scanner(errorStream);
      Scanner scanner2 = scanner1.useDelimiter("\\A");
      String output = scanner2.hasNext() ? scanner2.next() : "";
      scanner1.close();
      scanner2.close();
      App.log(output);
    }
  }

  private void cleanup(File predictionInputFile, File predictionOutputFile) {
    if (predictionInputFile.exists()) {
      predictionInputFile.delete();
    }
    if (predictionOutputFile.exists()) {
      predictionOutputFile.delete();
    }
  }

  private void readFromScriptOutputFile(File predictionOutputFile) {
    try (Scanner predictionOutput = new Scanner(predictionOutputFile).useLocale(Locale.US)) {
      int seriesLength = predictionOutput.nextInt();
      this.series = new double[seriesLength];
      for (int i = 0; i < seriesLength; i++) {
        this.series[i] = predictionOutput.nextDouble();
      }

      int residualsLength = predictionOutput.nextInt();
      this.residuals = new double[residualsLength];
      for (int i = 0; i < residualsLength; i++) {
        this.residuals[i] = predictionOutput.nextDouble();
      }
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeToScriptInputFile(EventSet eventSet, int predictionLength,
      File predictionInputFile) {
    try (PrintStream predictionInput =
        new PrintStream(new FileOutputStream(predictionInputFile, false))) {
      predictionInput.println(predictionLength);
      double[] series = eventSet.getFilledNormalizedMagnitudeSeries();
      for (int i = 0; i < series.length; i++) {
        predictionInput.println(series[i]);
      }
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public File createScriptInputFile(String directoryName, String fileName) {
    File directory = new File(directoryName);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    File predictionInputFile = new File(directoryName + "/" + fileName);
    if (!predictionInputFile.exists()) {
      try {
        predictionInputFile.createNewFile();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return predictionInputFile;
  }
}
