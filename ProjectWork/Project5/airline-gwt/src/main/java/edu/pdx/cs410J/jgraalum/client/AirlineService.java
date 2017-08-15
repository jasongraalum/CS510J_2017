package edu.pdx.cs410J.jgraalum.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs410J.ParserException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;

/**
 * A GWT remote service that returns a dummy airline
 */
@RemoteServiceRelativePath("airline")
public interface AirlineService extends RemoteService {

  Airline getAirline(String airlineName);

  String addAirline(String airlineName);
  String deleteAirline(String airlineName);
  void deleteAllAirline();

  void addFlight(String airlineName,
                 String flightNumber,
                 String departureAirportCode,
                 String departureDate,
                 String departureTime,
                 String arrivalAirportCode,
                 String arrivalDate,
                 String arrivalTime);

  /**
   * Always throws an undeclared exception so that we can see GWT handles it.
   */
  void throwUndeclaredException();

  /**
   * Always throws a declared exception so that we can see GWT handles it.
   */
  void throwDeclaredException() throws IllegalStateException;

  /**
   * Return an airline created on the server
   */
  ArrayList<String> getAirlineNames();
}
