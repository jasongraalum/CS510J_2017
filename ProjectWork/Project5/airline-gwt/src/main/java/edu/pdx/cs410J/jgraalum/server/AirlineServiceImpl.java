package edu.pdx.cs410J.jgraalum.server;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.jgraalum.client.Airline;
import edu.pdx.cs410J.jgraalum.client.AirlineService;
import edu.pdx.cs410J.jgraalum.client.Flight;

import java.util.*;

/**
 * The server-side implementation of the Airline service
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
    private Map<String, Airline> airlineData = new HashMap<>();

    @Override
    public ArrayList<String> getAirlineNames() {

        ArrayList<String> airlineNames = new ArrayList<String>();

        for(Map.Entry<String, Airline> airline : airlineData.entrySet())
        {
            airlineNames.add(airline.getValue().getName());
        }

        return(airlineNames);
    }

    @Override
    public Airline getAirline(String airlineName) {
        Airline airline = airlineData.get(airlineName);
        return airline;
    }


    @Override
    public String addAirline(String airlineName) {
        Airline airline = new Airline(airlineName);
        if((airlineName.length() > 0) && (airlineData.get(airlineName) == null)) {
            airlineData.put(airlineName, airline);
            System.out.println("Added " + airlineName);
        }
        return(airline.getName());
    }

    @Override
    public String deleteAirline(String airlineName) {
        System.out.println("Removing: " + airlineName);
        airlineData.remove(airlineName);
        return(airlineName);
    }

    @Override
    public void deleteAllAirline() {
        System.out.println("Removing all airlines");
        airlineData.clear();
    }

    @Override
    public void addFlight(String airlineName,
                          String flightNumber,
                          String departureAirportCode,
                          String departureDateTime,
                          String arrivalAirportCode,
                          String arrivalDateTime){

        Airline airline = airlineData.get(airlineName);
        System.out.println("AirlineServiceImpl - Adding flight to airline: " + airline.getName());
        System.out.println("Flight details: " + flightNumber + " " +
                departureAirportCode + " " +
                departureDateTime + " " +
                arrivalAirportCode + " " +
                arrivalDateTime);
        Flight flight = null;

        try {
            flight = new Flight(airlineName, flightNumber, departureAirportCode, departureDateTime, arrivalAirportCode,arrivalDateTime);
        } catch (ParserException e) {

            e.printStackTrace();
        }

        System.out.println("New flight added: " + flight.getNumber());
        airline.addFlight(flight);

    }

    @Override
    public void throwUndeclaredException() {
        throw new IllegalStateException("Expected undeclared exception");
    }

    @Override
    public void throwDeclaredException() throws IllegalStateException {
        throw new IllegalStateException("Expected declared exception");
    }

    /**
     * Log unhandled exceptions to standard error
     *
     * @param unhandled
     *        The exception that wasn't handled
     */
    @Override
    protected void doUnexpectedFailure(Throwable unhandled) {
        unhandled.printStackTrace(System.err);
        super.doUnexpectedFailure(unhandled);
    }
}
