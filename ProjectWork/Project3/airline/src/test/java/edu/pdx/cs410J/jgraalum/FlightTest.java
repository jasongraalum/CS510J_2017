package edu.pdx.cs410J.jgraalum;


import edu.pdx.cs410J.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link Flight} class.
 */
public class FlightTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testGetDepartureDateTime() throws ParserException {
        Flight flight = new Flight("123","DET","12/31/1901", "12:30","AM", "BOI","01/01/1902", "12:45","PM");
        assertThat(flight.getDepartureString(),equalTo("12/31/1901 12:30 AM"));
    }

    @Test
    public void testGetArrivalDateTime() throws ParserException {
        Flight flight = new Flight("123","DBQ","12/31/1901", "12:30", "AM", "MFR","01/01/1902", "10:45", "AM");
        assertThat(flight.getArrivalString(),equalTo("01/01/1902 10:45 AM"));
    }
    @Test
    public void testGetDestinationAirport() throws ParserException {
        Flight flight = new Flight("123","PDX","12/31/1901", "12:30", "PM" ,"BOI","01/01/1902", "8:45", "AM");
        assertThat(flight.getDestinationName(),equalTo("Boise, ID"));
    }
    @Test
    public void testGetSourceAirport() throws ParserException {
        Flight flight = new Flight("123","EGE","12/31/1901", "12:30", "PM", "SFO","01/01/1902", "3:45", "AM");
        assertThat(flight.getSource(),equalTo("EGE"));
    }

    @Test
    public void testGetFlightNumber() throws ParserException {
        Flight flight = new Flight("123","EVV","12/31/1901","12:30","AM", "CVG","01/01/1902","23:45", "PM");
        assertThat(flight.getNumber(), equalTo(123));
    }

    @Test
    public void testBadFormedFlightNumberAlphaChars() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid flight number: 12B");
        Flight flight = new Flight("12B","LCH","12/31/1901","12:30", "AM","LNK","01/01/1902","23:45", "PM");
    }

    @Test
    public void testBadShortSource() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid source airport code: RC");

        Flight flight = new Flight("123","RC","12/31/1901","12:30", "AM", "ANC","01/01/1902","23:45", "PM");
    }

    @Test
    public void testBadLongSource() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid source airport code: BOII");

        Flight flight = new Flight("123","BOII","12/31/1901","12:30", "AM", "BOS","01/01/1902","23:45", "PM");
    }

    @Test
    public void testMissDepartureDateField() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid departure date/time format: 31/1901 12:30 PM");

        Flight flight = new Flight("123","BGR","31/1901","12:30", "PM", "MSP","01/01/1902","23:45", "PM");
    }

    @Test
    public void testMissingDepartureTimeField() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid departure date/time format: 12/31/1901 :30 AM");

        Flight flight = new Flight("123","ABE","12/31/1901",":30","AM", "ONT","01/01/1902","23:45", "AM");
    }

    @Test
    public void testExtraDepartureDateField() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid departure date/time format: 12/12/31/1901 12:30 AM");
        Flight flight = new Flight("123","GUC","12/12/31/1901","12:30","AM", "HLN","01/01/1902","23:45", "AM");
    }

    @Test
    public void testExtraDepartureTimeField() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid departure date/time format: 12/31/1901 12:12:30 AM");

        Flight flight = new Flight("123","LGA","12/31/1901","12:12:30", "AM", "IND","01/01/1902","23:45", "PM");
    }
    @Test
    public void testMissingDepartureAMPM() throws ParserException {
        expectedEx.expect(ParserException.class);
        expectedEx.expectMessage("Invalid number of flight arguments:  [123, LGA, 12/31/1901, 12:12:30, IND, 01/01/1902, 23:45, PM]");

        Flight flight = new Flight("123","LGA","12/31/1901","12:12:30", "IND","01/01/1902","23:45", "PM");
    }

}
