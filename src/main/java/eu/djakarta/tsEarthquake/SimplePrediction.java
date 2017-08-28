package eu.djakarta.tsEarthquake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SimplePrediction implements Prediction {

  @Override
  public void predict(EventSet eventSet) {
    App.window.predictionChartPanel.getChart().getXYPlot()
        .setDataset(App.databaseEventSet.getXYDataset());
    App.window.componentsChartPanel.getChart().getXYPlot()
        .setDataset(App.databaseEventSet.getXYDataset());

    File predictionInputFile = new File("simplePredictionInput.txt");
    if (!predictionInputFile.exists()) {
      try {
        predictionInputFile.createNewFile();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try (PrintStream predictionInput =
        new PrintStream(new FileOutputStream(predictionInputFile, false))) {
      double[] series = eventSet.getNormalizeMagnitudeSeries();
      for (int i = 0; i < series.length; i++) {
        predictionInput.println(series[i]);
      }
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
