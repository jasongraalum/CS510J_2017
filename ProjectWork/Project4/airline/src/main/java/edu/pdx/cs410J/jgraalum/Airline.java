package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.io.IOException;
import java.util.*;

/**
 * Airline Class
 * Created by Jason Graalum on 7/8/17.
 *
 *
 */
public class Airline extends AbstractAirline {

    /**
     */
    private String airlineName;
    //private ArrayList<Flight> flights = new ArrayList<Flight>();
    private final Map<Integer, Flight> flightData = new HashMap<>();


    /**
     * Return the airline name
     * @return      airlineName String
     */
    @Override
    public String getName() {
        return airlineName;
    }

    /**
     * Add a flight to the airline
     * @param flight    AbstractFlight instance
     */
    @Override
    public void addFlight(AbstractFlight flight) {
        this.flightData.put(flight.getNumber(), (Flight) flight);
    }

    /**
     *
     * @return      Collection of Flights
     */
    @Override
    public Collection<Flight> getFlights() {
        return flightData.values();
    }

    /**
     *
     * @return  Created string with airline details.
     */
    @Override
    public String toString() {

        return this.getName()
                + " with "
                + this.getFlights().size()
                + " flights";
    }

    /**
     *
     * @return      Collection of Flights
     */
    public Collection<Flight> getFlightsFromTo(String source, String destination) {

        Collection fromToFlights = new ArrayList<Flight>();

        Collection<Flight> flights = this.getFlights();
        for(Flight flight : flights)
        {
            if(flight.getSource() == source && flight.getDestination() == destination)
                fromToFlights.add(flight);
        }
        return fromToFlights;

    }

    /**
     * If the printOption is true, call toString on the airline and flights
     *
     */
    public void printAirlineAndFlights() {
            System.out.println(this.toString());
            Collection newAirlineFlights = this.getFlights();
            Iterator<Flight> flightIter = newAirlineFlights.iterator();
            while(flightIter.hasNext())
            {
                System.out.println(flightIter.next().toString());
            }
    }


    /**
     * If the printOption is true, call toString on the airline and flights
     *
     */
    public String prettyPrintAirlineAndFlights() throws IOException {
            PrettyPrinter prettyPrinter = new PrettyPrinter();
            return(prettyPrinter.toString(this));
    }

    /**
     * Create a new Airline instance with the airlineName: name
     * @param name  Airline name
     */
    Airline(String name) {
        airlineName = name;
    }
}
