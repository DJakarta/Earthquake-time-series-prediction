package eu.djakarta.tsEarthquake;

public class Event {
  public final double magnitude;
  public final double time;
  public final double latitude;
  public final double longitude;
  public final double depth;

  public Event(double magnitude, double time, double latitude, double longitude, double depth) {
    this.magnitude = magnitude;
    this.time = time;
    this.latitude = latitude;
    this.longitude = longitude;
    this.depth = depth;
  }
}
