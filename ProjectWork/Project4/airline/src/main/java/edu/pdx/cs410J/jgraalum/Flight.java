package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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
    private Date departureDateAndTime;
    private String destinationAirportName;
    private String destinationAirportCode;
    private Date arrivalDateAndTime;
    private DateFormat flightDateAndTime = new SimpleDateFormat("MM/dd/yyyy hh:mm a");


    private AirportNames airportNames;

    /**
     * Creates a new <code>Flight</code>
     *
     * @param flightData
     *      List of Strings representing flight number, source, departure data and time, destination and arrival date and time.
     */
    Flight(String... flightData) throws ParserException {

        isValidFlight = true;

        System.out.print(flightData.toString());
        if(flightData.length != 9 && flightData.length != 5) {
            throw new ParserException("Invalid number of flight arguments:  " + Arrays.toString(flightData));
        }

        String flightNumberString = flightData[0].trim();
        String departureDateTimeString;
        String arrivalDateTimeString;
        if(flightData.length == 5) {
            sourceAirportCode = flightData[1].trim().toUpperCase();
            departureDateTimeString = flightData[2];
            destinationAirportCode = flightData[3].trim().toUpperCase();
            arrivalDateTimeString = flightData[4];
        }
        else
        {
            sourceAirportCode = flightData[1].trim().toUpperCase();
            String departureDateString = flightData[2].trim();
            String departureTimeString = flightData[3].trim();
            String departureAMPMString = flightData[4].trim();
            departureDateTimeString = departureDateString + " " + departureTimeString + " " + departureAMPMString;

            destinationAirportCode = flightData[5].trim().toUpperCase();
            String arrivalDateString = flightData[6].trim();
            String arrivalTimeString = flightData[7].trim();
            String arrivalAMPMString = flightData[8].trim();
            arrivalDateTimeString = arrivalDateString + " " + arrivalTimeString + " " + arrivalAMPMString;

        }

        if(flightNumberString.matches("[0-9]*")) {
            number = Integer.parseInt(flightNumberString);
        }
        else {
            throw new ParserException("Invalid flight number: " + flightNumberString);
        }

        sourceAirportName = airportNames.getName(sourceAirportCode);
        if(sourceAirportName == null) {
            throw new ParserException("Invalid source airport code: " + sourceAirportCode);
        }

        destinationAirportName = airportNames.getName(destinationAirportCode);
        if(destinationAirportName == null) {
            throw new ParserException("Invalid destination airport code: " + destinationAirportCode);
        }

        try {
            departureDateAndTime = flightDateAndTime.parse(departureDateTimeString);
        } catch (ParseException e) {
            throw new ParserException("Invalid departure date/time format: " + departureDateTimeString);
        }

        try {
            arrivalDateAndTime = flightDateAndTime.parse(arrivalDateTimeString);
        } catch (ParseException e) {
            throw new ParserException("Invalid arrival date/time format: " + arrivalDateTimeString);
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
        return (flightDateAndTime.format(departureDateAndTime));
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
        return (flightDateAndTime.format(arrivalDateAndTime));
    }


    /**
     * Validate that the airportCode string is three letters.
     * @param airportCode   Airport code from Flight data
     * @return  Boolean     Return treu is fhte airportCode is three letters(upper or lower case)
     */
    private boolean isValidAirportCode(String airportCode) {
        return airportCode.matches("[a-zA-Z][a-zA-Z][a-zA-Z]");
    }

    public Long getFlightDuration() {

        return(arrivalDateAndTime.getTime()/60000 - departureDateAndTime.getTime()/60000);
    }

    @Override
    public int compareTo(Flight o) {
        if(!this.getSource().equals(o.getSource()))
            return(this.getSource().compareTo(o.getSource()));
        if(!this.getDestination().equals(o.getDestination()))
            return(this.getDestination().compareTo(o.getDestination()));
        return(compare(this.getNumber(), o.getNumber()));
    }
}
