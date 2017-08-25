package eu.djakarta.tsEarthquake;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class HttpRequest {
  public final String url, method;
  public final Map<String, String> headers;
  public final String data;

  public HttpRequest(String url, String method, Map<String, String> headers, String data) {
    if (!method.equals("GET")) {
      throw new RuntimeException("Methods other than GET are temporarily forbidden.");
    }
    this.url = url;
    this.method = method;
    this.headers = Collections.unmodifiableMap(new Hashtable<String, String>(headers));
    this.data = data;
  }

  public HttpRequest(String url, String method, Map<String, String> headers) {
    this(url, method, headers, null);
  }
  
  public HttpRequest(String url, String method) {
    this(url, method, new Hashtable<String, String>());
  }

  public HttpResponse send() {
    HttpURLConnection connection = null;
    /* TODO better error handling */
    try {
      // Open connection
      URL url = new URL(this.url);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(this.method);
      for (String header : this.headers.keySet()) {
        connection.setRequestProperty(header, this.headers.get(header));
      }
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      // Send request
      if (this.data != null) {
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(this.data);
        wr.close();
      }

      // Get response
      InputStream is;
      /* TODO better stream checking */
      if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
        is = connection.getInputStream();
      } else {
        is = connection.getErrorStream();
      }
      StringBuilder response = new StringBuilder();
      if (is != null) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\n');
        }
        rd.close();
      }
      return new HttpResponse(response.toString(), connection.getResponseCode());
    } catch (Exception ex) {
      RuntimeException rex = new RuntimeException("Exception " + ex + " on request:\n" + this, ex);
      rex.printStackTrace();
      throw rex;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  public String toString() {
    return "HttpRequest [\n\turl=" + url + ", \n\tmethod=" + method + ", \n\theaders=" + headers
        + ", \n\tdata=" + data + "\n]";
  }
}