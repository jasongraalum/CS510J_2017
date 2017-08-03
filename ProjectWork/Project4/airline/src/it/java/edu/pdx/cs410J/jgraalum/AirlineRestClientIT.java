package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.web.HttpRequestHelper.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test that tests the REST calls made by {@link AirlineRestClient}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AirlineRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private AirlineRestClient newAirlineRestClient() {
    int port = Integer.parseInt(PORT);
    return new AirlineRestClient(HOSTNAME, port);
  }


  @Test
  public void testAddAirlineFlight() throws IOException {
    AirlineRestClient client = newAirlineRestClient();
    String airlineName = "TEST Airline";
    String fligthNumber = "12345";
    String sourceAirportCode = "PDX";
    String departureString = "1/1/2001 1:00 AM";
    String destinationAirportCode = "BOS";
    String arrivalString = "1/2/2002 2:00 PM";
    client.addFlightToAirline(airlineName,fligthNumber,sourceAirportCode,departureString,destinationAirportCode,departureString);
    client.getAllFlights(airlineName);
  }

  @Test
  public void test4MissingRequiredParameterReturnsPreconditionFailed() throws IOException {
    AirlineRestClient client = newAirlineRestClient();
    Response response = client.postToMyURL();
    assertThat(response.getContent(), containsString(Messages.missingRequiredParameter("key")));
    assertThat(response.getCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
  }
}
