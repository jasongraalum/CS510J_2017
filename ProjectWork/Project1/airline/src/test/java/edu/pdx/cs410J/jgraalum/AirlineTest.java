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

}
