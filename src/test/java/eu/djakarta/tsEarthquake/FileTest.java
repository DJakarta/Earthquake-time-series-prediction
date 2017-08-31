package eu.djakarta.tsEarthquake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

public class FileTest {
  @Test
  public void fileDownloadTest() {
    // String server = "http://service.iris.edu/fdsnws/event/1/query?";
    String server = "http://www.isc.ac.uk/fdsnws/event/1/query?";
    String query = "minmagnitude=1&starttime=2015-01-01&endtime=2017-08-29";
    query += "&minlatitude=43&maxlatitude=49&minlongitude=19&maxlongitude=30";

    /* TODO implement response progress; the server returns the length of the
     * page in response headers */
    HttpRequest request = new HttpRequest(server + query, "GET");
    HttpResponse response = request.send();

    File outputFile = new File("db/database.xml");
    try {
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    try (PrintStream printStream = new PrintStream(outputFile)) {
      outputFile.createNewFile();
      printStream.print(response.response);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertTrue(outputFile.exists());
    assertEquals(200, response.code);
  }
}
