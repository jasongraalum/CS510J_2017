package edu.pdx.cs410J.jgraalum.client;

import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirportNames;

import static java.lang.Integer.compare;

/**
 * <h1>Flight Class</h1>
 * The Flight Class includes methods and parameters to build flight details for an airline.
 *
 * @author Jason Graalum
 * @version 1.0
 *
 */
public class Flight extends AbstractFlight implements Comparable<Flight> {

    boolean isValidFlight;
    private Integer number;
    private String sourceAirportName;
    private String sourceAirportCode;
    private String departureDate;
    private String departureTime;
    private String destinationAirportName;
    private String destinationAirportCode;
    private String arrivalDate;
    private String arrivalTime;
    private String airlineName;
    private String duration;

    //private transient DateTimeFormat flightDateTime = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");
    //private transient DateTimeFormat flightTime = DateTimeFormat.getFormat("hh:mm a");
    //private transient DateTimeFormat flightDate = DateTimeFormat.getFormat("MM/dd/yyyy");


    public Flight()
    {

    }

    /**
     * Creates a new <code>Flight</code>
     *
     * @param flightData
     *      List of Strings representing flight number, source, departure data and time, destination and arrival date and time.
     */
    public Flight(String... flightData) {

        sourceAirportCode = "XXX";
        departureDate = "01/01/1901";
        departureTime = "00:00 am";
        destinationAirportCode = "XXX";
        arrivalDate = "01/01/1901";
        arrivalTime = "00:00 am";
        airlineName = "Invalid Airline";
        duration = "-1";

        if(flightData.length != 9) return;


        System.out.print("In Flight Class");



        Integer i = 0;
        airlineName = flightData[i++];
        String flightNumberString = flightData[i++].trim();
        sourceAirportCode = flightData[i++].trim().toUpperCase();
        departureDate = flightData[i++];
        departureTime = flightData[i++];
        destinationAirportCode = flightData[i++].trim().toUpperCase();
        arrivalDate = flightData[i++];
        arrivalTime = flightData[i++];
        duration = flightData[i++];

        if(flightNumberString.matches("[0-9]*")) {
            number = Integer.parseInt(flightNumberString);
        }
        else {
            number = -9999;
        }

        sourceAirportName =  AirportNames.getName(sourceAirportCode);
        if(sourceAirportName == null) {
            sourceAirportName = "Not found";
        }

        destinationAirportName = AirportNames.getName(destinationAirportCode);
        if(destinationAirportName == null) {
            destinationAirportName = "Not found";
        }

    }


    /**
     * Returns current flight number
     * @return Flight number
     */
    @Override
    public int getNumber() {
        return this.number;
    }

    /**
     *
     * @return
     */
    public String getAirlineName() { return this.airlineName; }


    /**
     * Returns the source airport string
     * @return sourceAirport Three letter code of source airport
     */
    @Override
    public String getSource() { return sourceAirportCode; }

    /**
     * Returns the source airport string
     * @return sourceAirportName Validated name of airport
     */
    public String getSourceName() { return sourceAirportName; }

    /**
     * Returns the departure date and time
     * @return  String of departure data and time in mm/dd/yyyy hh:mm format
     */
    @Override
    public String getDepartureString() {
        return departureDate + " " + departureTime;
    }

    /**
     * Retrieves the destination airport code
     * @return destinationAirport   Three letter airport code
     */
    @Override
    public String getDestination() {
        return destinationAirportCode;
    }

    /**
     * Retrieves the destination airport code
     * @return destinationAirport   Three letter airport code
     */
    public String getDestinationName() {
        return destinationAirportName;
    }

    /**
     * Retrieve arrival date and time
     * @return  String of arrival data and time in mm/dd/yyyy hh:mm format
     */
    @Override
    public String getArrivalString() {
        return arrivalDate + " " + arrivalTime;
    }


    /**
     * Validate that the airportCode string is three letters.
     * @param airportCode   Airport code from Flight data
     * @return  Boolean     Return treu is fhte airportCode is three letters(upper or lower case)
     */
    private boolean isValidAirportCode(String airportCode) {
        return airportCode.matches("[a-zA-Z][a-zA-Z][a-zA-Z]");
    }


    public String getDepartureTimeString() { return (departureTime); }
    public String getArrivalTimeString() { return (arrivalTime); }
    public String getDepartureDateString() { return (departureDate); }
    public String getArrivalDateString() { return (arrivalDate); }

    @Override
    public int compareTo(Flight o) {
        if(!this.getSource().equals(o.getSource()))
            return(this.getSource().compareTo(o.getSource()));
        if(!this.getDestination().equals(o.getDestination()))
            return(this.getDestination().compareTo(o.getDestination()));
        return(compare(this.getNumber(), o.getNumber()));
    }

    public String getFlightDuration() {
        return(this.duration);
    }
}
