package edu.pdx.cs410J.jgraalum.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * A GWT remote service that returns a dummy airline
 */
@RemoteServiceRelativePath("airline")
public interface AirlineService extends RemoteService {

  Airline getAirline(String airlineName) throws IllegalStateException;

  String addAirline(String airlineName) throws IllegalArgumentException;
  String deleteAirline(String airlineName);
  void deleteAllAirline();

  void addFlight(String airlineName,
                 String flightNumber,
                 String departureAirportCode,
                 String departureDate,
                 String departureTime,
                 String arrivalAirportCode,
                 String arrivalDate,
                 String arrivalTime) throws IllegalArgumentException;

  /**
   * Return an airline created on the server
   */
  ArrayList<String> getAirlineNames() throws IllegalStateException;
}
