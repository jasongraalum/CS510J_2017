package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send key/value pairs.
 */
public class AirlineRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final String url;


    /**
     * Creates a client to the airline REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AirlineRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Returns all flights for given airline
     */
    public String getAllFlights(String airlineName) throws IOException {
        //System.out.println("Getting all flights for airline " + airlineName);

        Response response = get(this.url, "name", airlineName);
        throwExceptionIfNotOkayHttpStatus(response);
        String content = response.getContent();
        return content;
    }

    /**
     * Returns specific flights for given airline
     */
    public String getFromToFlights(String airlineName, String sourceAirport, String destinationAirport) throws IOException
    {
        //System.out.println("Getting flights for airline " + airlineName + " from " + sourceAirport + " to " + destinationAirport);
        Response response = get(this.url, "name", airlineName, "src", sourceAirport, "dest", destinationAirport);
        throwExceptionIfNotOkayHttpStatus(response);
        String content = response.getContent();
        return content;
    }

    /**
     * Returns specific flights for given airline
     */
    public String getFlightFromNumber(String airlineName, String flightNumberString) throws IOException
    {
        //System.out.println("Getting flights for airline " + airlineName + " from " + sourceAirport + " to " + destinationAirport);
        Response response = get(this.url, "name", airlineName, "flightNumber", flightNumberString);
        throwExceptionIfNotOkayHttpStatus(response);
        String content = response.getContent();
        return content;
    }

    public void addFlightToAirline(String airlineName,
                                   String flightNumber,
                                   String sourceAirportCode,
                                   String departureDateTimeAMPM,
                                   String destinationAirportCode,
                                   String arrivalDateTimeAMPM) throws IOException {
      Response response = post(this.url, "name", airlineName,
              "flightNumber", flightNumber,
              "src", sourceAirportCode,
              "departTime", departureDateTimeAMPM,
              "dest", destinationAirportCode,
              "arriveTime", arrivalDateTimeAMPM);

      throwExceptionIfNotOkayHttpStatus(response);
    }

    public void removeAllMappings() throws IOException {
      Response response = delete(this.url);
      throwExceptionIfNotOkayHttpStatus(response);
    }

    private Response throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getCode();
      String message = response.getContent();
      if (code != HTTP_OK) {
        System.out.println("HTTP Status Code: " + code);
        System.out.println(message);
      }
      return response;
    }
}
