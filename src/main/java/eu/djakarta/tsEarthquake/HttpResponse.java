package eu.djakarta.tsEarthquake;

public class HttpResponse {
  public final String response;
  public final int code;

  public HttpResponse(String response, int code) {
    this.response = response;
    this.code = code;
  }

  @Override
  public String toString() {
    return "HttpResponse [\n\tresponse=" + response + ",\n\tcode=" + code + "\n]";
  }
}