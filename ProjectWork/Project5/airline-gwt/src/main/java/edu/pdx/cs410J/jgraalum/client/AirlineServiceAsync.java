package edu.pdx.cs410J.jgraalum.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.ParserException;


import java.sql.Time;
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


  void addFlight(String airlineName,
                 String flightNumber,
                 String departureAirportCode,
                 String departureDate,
                 String departureTime,
                 String arrivalAirportCode,
                 String arrivalDate,
                 String arrivalTime,
                 AsyncCallback<Void> async);

  void addAirline(String airlineName, AsyncCallback<String> async);
  void deleteAirline(String airlineName, AsyncCallback<String> async);
  void deleteAllAirline(AsyncCallback<Void> async);


}
