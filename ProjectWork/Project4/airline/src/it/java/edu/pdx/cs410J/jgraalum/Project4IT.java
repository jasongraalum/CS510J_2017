package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * An integration test for {@link Project4} that invokes its main method with
 * various arguments
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

    private MainMethodResult invokeMain(String... args) {
        return invokeMain( Project4.class, args );
    }
    @Test
    public void test0RemoveAllMappings() throws IOException {
      AirlineRestClient client = new AirlineRestClient(HOSTNAME, Integer.parseInt(PORT));
        client.removeAllMappings();
    }

    @Test
    public void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }



    @Test
    public void testAddFlight() {
        String airlineName = "TEST Airline";
        String fligthNumber = "12345";
        String sourceAirportCode = "PDX";
        String departureString = "1/1/2001 1:00 AM";
        String destinationAirportCode = "BOS";
        String arrivalString = "1/2/2002 2:00 PM";

        MainMethodResult result = invokeMain( "-host", HOSTNAME, "-port", PORT, airlineName, fligthNumber, sourceAirportCode, departureString, destinationAirportCode, arrivalString);
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        System.out.print(out);

        result = invokeMain( Project4.class, HOSTNAME, PORT, airlineName );
        out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(" "));


    }
}