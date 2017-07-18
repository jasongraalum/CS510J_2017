package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Airline Class
 * Created by Jason Graalum on 7/8/17.
 */
public class Airline extends AbstractAirline {

    private String airlineName;
    private ArrayList<Flight> flights = new ArrayList<Flight>();

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
        this.flights.add((Flight) flight);
    }

    /**
     *
     * @return      Collection of Flights
     */
    @Override
    public Collection getFlights() {

        return flights;
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
     * Create a new Airline instance with the airlineName: name
     * @param name
     */
    Airline(String name) {

        airlineName = name;
    }
}
