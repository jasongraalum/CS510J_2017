package edu.pdx.cs410J.jgraalum.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.ParserException;


import java.util.ArrayList;

/**
 * The client-side interface to the airline service
 */
public interface AirlineServiceAsync {

  /**
   * Return an airline created on the server
   */
  void getAirline(String airlineName, AsyncCallback<Airline> async);

  /**
   * Return an airline created on the server
   */
  void getAirlineNames(AsyncCallback<ArrayList<String>> async);

  /**
   * Always throws an exception so that we can see how to handle uncaught
   * exceptions in GWT.
   */
  void throwUndeclaredException(AsyncCallback<Void> async);

  /**
   * Always throws a declared exception so that we can see GWT handles it.
   */
  void throwDeclaredException(AsyncCallback<Void> async);

  void addFlight(String airlineName,
                 String flightNumber,
                 String departureAirportCode,
                 String departureDateTime,
                 String arrivalAirportCode,
                 String arrivalDateTime,
                 AsyncCallback<Void> async);

  void addAirline(String airlineName, AsyncCallback<String> async);
  void deleteAirline(String airlineName, AsyncCallback<String> async);
  void deleteAllAirline(AsyncCallback<Void> async);

}
