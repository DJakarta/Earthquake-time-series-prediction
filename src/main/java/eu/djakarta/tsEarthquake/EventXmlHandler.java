package eu.djakarta.tsEarthquake;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EventXmlHandler extends DefaultHandler {
  private List<Event> list = new LinkedList<Event>();
  private double magnitude;
  private String time;
  private double latitude;
  private double longitude;
  private double depth;

  private boolean magnitudeFound;
  private boolean timeFound;
  private boolean latitudeFound;
  private boolean longitudeFound;
  private boolean depthFound;

  private boolean onMagnitude;
  private boolean onTime;
  private boolean onLatitude;
  private boolean onLongitude;
  private boolean onDepth;

  private boolean onValue;

  private void resetFound() {
    this.magnitudeFound = false;
    this.timeFound = false;
    this.latitudeFound = false;
    this.longitudeFound = false;
    this.depthFound = false;
  }

  private void resetOn() {
    this.onMagnitude = false;
    this.onTime = false;
    this.onLatitude = false;
    this.onLongitude = false;
    this.onDepth = false;
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equalsIgnoreCase("event")) {
      this.resetFound();
    }
    else if (qName.equalsIgnoreCase("value")) {
      this.onValue = true;
    }
    else {
      this.resetOn();
      if (qName.equalsIgnoreCase("mag")) {
        this.onMagnitude = true;
      }
      else if (qName.equalsIgnoreCase("time")) {
        this.onTime = true;
      }
      else if (qName.equalsIgnoreCase("latitude")) {
        this.onLatitude = true;
      }
      else if (qName.equalsIgnoreCase("longitude")) {
        this.onLongitude = true;
      }
      else if (qName.equalsIgnoreCase("depth")) {
        this.onDepth = true;
      }
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase("event")) {
      if (this.magnitudeFound && this.timeFound && this.latitudeFound && this.longitudeFound
          && this.depthFound) {
        Date time = DatatypeConverter.parseDateTime(this.time).getTime();
        this.list.add(new Event(this.magnitude, time, this.latitude, this.longitude, this.depth));
      }
      else {
        App.log("Skipping encountered event with attribute missing.");
      }
    }
    else if (qName.equalsIgnoreCase("value")) {
      this.onValue = false;
    }
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    if (this.onValue) {
      if (this.onMagnitude) {
        this.magnitude = Double.parseDouble(new String(ch, start, length));
        this.magnitudeFound = true;
      }
      else if (this.onTime) {
        this.time = new String(ch, start, length);
        this.timeFound = true;
      }
      else if (this.onLatitude) {
        this.latitude = Double.parseDouble(new String(ch, start, length));
        this.latitudeFound = true;
      }
      else if (this.onLongitude) {
        this.longitude = Double.parseDouble(new String(ch, start, length));
        this.longitudeFound = true;
      }
      else if (this.onDepth) {
        this.depth = Double.parseDouble(new String(ch, start, length));
        this.depthFound = true;
      }
    }
  }

  public List<Event> getEventList() {
    return this.list;
  }
}