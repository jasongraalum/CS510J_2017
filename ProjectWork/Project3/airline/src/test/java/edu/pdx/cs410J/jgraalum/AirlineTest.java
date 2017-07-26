package edu.pdx.cs410J.jgraalum;


import edu.pdx.cs410J.*;
import org.junit.Test;
import org.junit.internal.runners.statements.InvokeMethod;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

/**
 * Created by jasongraalum on 7/11/17.
 */
public class AirlineTest {

    @Test
    public void testGetAirlineName() {
        Airline airline = new Airline("Airline");
        assertThat(airline.getName(),equalTo("Airline"));
    }

    @Test
    public void testPrettyPrintAirline() throws ParserException {
        Airline airline = new Airline("Airline");
        Flight flight1 = new Flight("123","EVV","12/31/1901","12:30","AM", "CVG","01/01/1902","23:45", "PM");
        Flight flight2 = new Flight("123","PDX","12/31/1901", "12:30", "PM" ,"BOI","01/01/1902", "8:45", "AM");
        airline.addFlight(flight1);
        airline.addFlight(flight2);

    }

}
