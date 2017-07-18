package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Airline Class
 * Created by jasongraalum on 7/8/17.
 */
public class Airline extends AbstractAirline {

    private String airlineName;
    private ArrayList<Flight> flights = new ArrayList<Flight>();

    @Override
    public String getName() {
        return airlineName;
    }

    @Override
    public void addFlight(AbstractFlight flight) {
        this.flights.add((Flight) flight);
    }

    @Override
    public Collection getFlights() {
        return flights;
    }

    @Override
    public String toString() {
        return this.getName() + " with " + this.getFlights().size() +
                " flights";
    }



        Airline() {
        this("Unknown");
    }

    Airline(String name)
    {
        airlineName = name;

    }
}
