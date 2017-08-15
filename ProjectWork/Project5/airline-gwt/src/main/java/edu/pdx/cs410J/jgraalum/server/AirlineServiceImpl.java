package edu.pdx.cs410J.jgraalum.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.jgraalum.client.Airline;
import edu.pdx.cs410J.jgraalum.client.AirlineService;
import edu.pdx.cs410J.jgraalum.client.Flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The server-side implementation of the Airline service
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
    private Map<String, Airline> airlineData = new HashMap<>();

    SimpleDateFormat flightDateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

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
        System.out.println("Total airlines: " + airlineData.size());
        return(airline.getName());
    }

    @Override
    public String deleteAirline(String airlineName) {
        System.out.println("Removing: " + airlineName);
        airlineData.remove(airlineName);
        System.out.println("Total airlines: " + airlineData.size());
        return(airlineName);
    }

    @Override
    public void deleteAllAirline() {
        System.out.println("Removing all airlines");
        airlineData.clear();
        System.out.println("Total airlines: " + airlineData.size());
    }

    @Override
    public void addFlight(String airlineName,
                          String flightNumber,
                          String departureAirportCode,
                          String departureDate,
                          String departureTime,
                          String arrivalAirportCode,
                          String arrivalDate,
                          String arrivalTime){

        Long flightDuration = 0L;
        try {

            Date departureDateTime = flightDateTimeFormat.parse(departureDate + " " + departureTime);
            Date arrivalDateTime = flightDateTimeFormat.parse(arrivalDate + " " + arrivalTime);
            flightDuration = (arrivalDateTime.getTime() - departureDateTime.getTime())/60000;
            System.out.println("Calculating duration: " + String.valueOf(flightDuration));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Airline airline = airlineData.get(airlineName);
        System.out.println("AirlineServiceImpl - Adding flight to airline: " + airline.getName());
        System.out.println("Flight details: " + flightNumber + " " +
                departureAirportCode + " " +
                departureDate + " " +
                departureTime + " " +
                arrivalAirportCode + " " +
                arrivalDate + " " +
                arrivalTime + " " +
                String.valueOf(flightDuration)
               );
        //Flight flight = null;

        airline.addFlight(new Flight(airlineName, flightNumber, departureAirportCode, departureDate, departureTime, arrivalAirportCode, arrivalDate, arrivalTime, String.valueOf(flightDuration)));

        System.out.println("Total flights for airline: " + airline.getName() + " == " + airline.getFlights().size());

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
