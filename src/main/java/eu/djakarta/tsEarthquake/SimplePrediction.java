package eu.djakarta.tsEarthquake;

import java.io.File;
import java.io.IOException;

public class SimplePrediction implements Prediction {

  @Override
  public void predict(EventSet eventSet, int periodLength) {
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
    // try (PrintStream predictionInput =
    // new PrintStream(new FileOutputStream(predictionInputFile, false))) {
    // double[] series = eventSet.getNormalizeMagnitudeSeries();
    // for (int i = 0; i < series.length; i++) {
    // predictionInput.println(series[i]);
    // }
    // TimeSeries timeSeries = TestData.elecSales;
    // ArimaOrder modelOrder = ArimaOrder.order(0, 1, 1, 0, 1, 1);
    // Arima model = Arima.model(timeSeries, modelOrder);
    // System.out.println(model.aic());
    // System.out.println(model.coefficients());
    // System.out.println(java.util.Arrays.toString(model.stdErrors()));
    // Forecast forecast = model.forecast(12);
    // forecast.plot();
    // }
    // catch (FileNotFoundException e) {
    // throw new RuntimeException(e);
    // }
  }
}
