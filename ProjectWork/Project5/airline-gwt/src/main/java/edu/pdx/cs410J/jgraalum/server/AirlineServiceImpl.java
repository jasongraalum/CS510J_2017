package edu.pdx.cs410J.jgraalum.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.jgraalum.client.Airline;
import edu.pdx.cs410J.jgraalum.client.AirlineService;
import edu.pdx.cs410J.jgraalum.client.Flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The server-side implementation of the Airline service
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
    private Map<String, Airline> airlineData = new HashMap<>();

    SimpleDateFormat flightDateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    @Override
    public ArrayList<String> getAirlineNames() throws IllegalStateException {

        ArrayList<String> airlineNames = new ArrayList<String>();

        for(Map.Entry<String, Airline> airline : airlineData.entrySet())
        {
            airlineNames.add(airline.getValue().getName());
        }

        return(airlineNames);
    }

    @Override
    public Airline getAirline(String airlineName) throws IllegalStateException {
        Airline airline = airlineData.get(airlineName);
        return airline;
    }

    @Override
    public String addAirline(String airlineName) throws IllegalArgumentException {
        Airline airline = null;
        try {
            airline = new Airline(airlineName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
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
                          String arrivalTime) throws IllegalArgumentException  {


        Long flightDuration = 0L;
        Date departureDateTime = null;
        Date arrivalDateTime = null;
        try {
            departureDateTime = flightDateTimeFormat.parse(departureDate + " " + departureTime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Incorrect Date/Time Format (departure Date/Time)" + departureDate + " " + departureTime);
        }
        try {
            arrivalDateTime = flightDateTimeFormat.parse(arrivalDate + " " + arrivalTime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Incorrect Date/Time Format (arrival Date/Time)" + arrivalDate + " " + arrivalTime);
        }
        flightDuration = (arrivalDateTime.getTime() - departureDateTime.getTime())/60000;

        Airline airline = airlineData.get(airlineName);
        if(airline == null)
            throw new IllegalArgumentException("Invalid airline. Unable to find: " + airlineName);
        else {
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
            Flight newFlight;
            try {
                newFlight = new Flight(airlineName, flightNumber, departureAirportCode, departureDate, departureTime, arrivalAirportCode, arrivalDate, arrivalTime, String.valueOf(flightDuration));
            } catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException(e);
            }
            System.out.println("Successfully created flight");

            airline.addFlight(newFlight);

            System.out.println("Flight added to airline");

        }

        System.out.println("Total flights for airline: " + airline.getName() + " == " + airline.getFlights().size());

    }


}
