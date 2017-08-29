package eu.djakarta.tsEarthquake;

public interface Prediction {
  public void predict(EventSet eventSet, int periodLength);
}
