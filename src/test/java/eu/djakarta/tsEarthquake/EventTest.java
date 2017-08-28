package eu.djakarta.tsEarthquake;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class EventTest {
  @Test
  public void sameDayTest() {
    Date date1 = new Date(1000 * 60 * 60 * 24 * 3 + 2);
    Date date2 = new Date(1000 * 60 * 60 * 24 * 3 + 20000);
    Date date3 = new Date(1000 * 60 * 60 * 24 * 3 + 20000 + 1000 * 60 * 60 * 24);
    Event event1 = new Event(0, date1, 0, 0, 0);
    Event event2 = new Event(0, date2, 0, 0, 0);
    Event event3 = new Event(0, date3, 0, 0, 0);

    assertTrue(event1.sameDay(event2));
    assertFalse(event1.sameDay(event3));
  }
  
  @Test
  public void daysDifferenceTest() {
    Date date1 = new Date(1000 * 60 * 60 * 24 * 3 + 2);
    Date date2 = new Date(1000 * 60 * 60 * 24 * 3 + 20000);
    Date date3 = new Date(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 24 + 100);
    Date date4 = new Date(1000 * 60 * 60 * 24 * 5 + 1000 * 60 * 60 * 24 + 1000);
    Event event1 = new Event(0, date1, 0, 0, 0);
    Event event2 = new Event(0, date2, 0, 0, 0);
    Event event3 = new Event(0, date3, 0, 0, 0);
    Event event4 = new Event(0, date4, 0, 0, 0);
    
    // System.out.println(date1);
    // System.out.println(date2);
    // System.out.println(date3);
    // System.out.println(date4);

    assertEquals(0, event1.daysDifference(event1));
    assertEquals(0, event2.daysDifference(event1));
    assertEquals(1, event3.daysDifference(event1));
    assertEquals(3, event4.daysDifference(event1));
  }
}
