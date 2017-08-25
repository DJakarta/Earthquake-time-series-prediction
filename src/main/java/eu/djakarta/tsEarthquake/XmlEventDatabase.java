package eu.djakarta.tsEarthquake;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class XmlEventDatabase implements EventDatabase {
  private File file;

  public XmlEventDatabase(File file) {
    this.file = file;
  }

  public XmlEventDatabase(String fileName) {
    this(new File(fileName));
  }

  @Override
  public List<Event> getEventList() {
    /* TODO implement getEventList */
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser;
    try {
      saxParser = factory.newSAXParser();
    }
    catch (ParserConfigurationException | SAXException e) {
      throw new RuntimeException(e);
    }
    EventXmlHandler eventXmlHandler = new EventXmlHandler();
    try {
      saxParser.parse(this.file, eventXmlHandler);
    }
    catch (SAXException | IOException e) {
      throw new RuntimeException(e);
    }
    List<Event> list = eventXmlHandler.getEventList();
    return list;
  }
}
