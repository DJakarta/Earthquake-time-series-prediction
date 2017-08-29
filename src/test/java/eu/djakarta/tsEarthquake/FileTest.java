package eu.djakarta.tsEarthquake;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.junit.Test;

public class FileTest {
  @Test
  public void fileDownloadTest() {
    String server = "http://service.iris.edu/fdsnws/event/1/query?";
    String query = "minmagnitude=4.5&starttime=2015-01-01&endtime=2017-08-29";

    /* TODO implement response progress; the server returns the length of the
     * page in response headers */
    HttpRequest request = new HttpRequest(server + query, "GET");
    HttpResponse response = request.send();

    File outputFile = new File("src/main/resources/database.xml");
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
