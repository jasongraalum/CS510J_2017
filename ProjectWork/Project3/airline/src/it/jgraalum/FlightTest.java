package edu.pdx.cs410J.jgraalum;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link Flight} class.
 */
public class FlightTest {

  @Test
  public void testGetDepartureDateTime() {
    Flight flight = new Flight("123","SRC","12/31/1901", "12:30","DES","01/01/1902", "23:45");
    assertThat(flight.getDepartureString(),equalTo("12/31/1901 12:30"));
  }

  @Test
  public void testGetArrivalDateTime() {
    Flight flight = new Flight("123","SRC","12/31/1901", "12:30","DES","01/01/1902", "23:45");
    assertThat(flight.getArrivalString(),equalTo("01/01/1902 23:45"));
  }
  @Test
  public void testGetDestinationAirport() {
    Flight flight = new Flight("123","SRC","12/31/1901", "12:30","DES","01/01/1902", "23:45");
    assertThat(flight.getDestination(),equalTo("DES"));
  }
  @Test
  public void testGetSourceAirport() {
    Flight flight = new Flight("123","SRC","12/31/1901", "12:30","DES","01/01/1902", "23:45");
    assertThat(flight.getSource(),equalTo("SRC"));
  }

  @Test
  public void testGetFlightNumber() {
    Flight flight = new Flight("123","SRC","12/31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getNumber(), equalTo(123));
  }

  @Test
  public void testBadlyFormedFlightNumberAlphaChars() {
    Flight flight = new Flight("12B","SRC","12/31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getNumber(), equalTo(0));
  }

  @Test
  public void testBadlyShortSource() {
    Flight flight = new Flight("123","RC","12/31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getSource(), equalTo("NAN"));
  }

  @Test
  public void testBadlyLongSource() {
    Flight flight = new Flight("123","SRCC","12/31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getSource(), equalTo("NAN"));
  }

  @Test
  public void testMissDepartureDateField() {
    Flight flight = new Flight("123","SRC","31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getDepartureString(),equalTo("00/00/0000 12:30"));
  }

  @Test
  public void testMissingDepartureTimeField() {
    Flight flight = new Flight("123","SRC","12/31/1901",":30","DES","01/01/1902","23:45");
    assertThat(flight.getDepartureString(),equalTo("12/31/1901 00:00"));
  }

@Test
  public void testExtraDepartureDateField() {
    Flight flight = new Flight("123","SRC","12/12/31/1901","12:30","DES","01/01/1902","23:45");
    assertThat(flight.getDepartureString(),equalTo("00/00/0000 12:30"));
  }

    @Test
  public void testExtraDepartureTimeField() {
    Flight flight = new Flight("123","SRC","12/31/1901","12:12:30","DES","01/01/1902","23:45");
    assertThat(flight.getDepartureString(),equalTo("12/31/1901 00:00"));
  }


}
