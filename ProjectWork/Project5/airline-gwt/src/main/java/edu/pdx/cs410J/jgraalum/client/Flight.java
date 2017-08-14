package edu.pdx.cs410J.jgraalum.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

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
    private String airlineName;

    private transient DateTimeFormat flightDateTime = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");
    private transient DateTimeFormat flightTime = DateTimeFormat.getFormat("hh:mm a");
    private transient DateTimeFormat flightDate = DateTimeFormat.getFormat("MM/dd/yyyy");


    public Flight()
    {

    }
    /**
     * Creates a new <code>Flight</code>
     *
     * @param flightData
     *      List of Strings representing flight number, source, departure data and time, destination and arrival date and time.
     */
    public Flight(String... flightData) throws ParserException {

        isValidFlight = true;

        System.out.print("In Flight Class");
        if(flightData.length != 10 && flightData.length != 6) {
            throw new ParserException("Invalid number of flight arguments:  " + Arrays.toString(flightData));
        }


        String flightNumberString = flightData[0].trim();
        String departureDateTimeString;
        String arrivalDateTimeString;
        if(flightData.length == 6) {
            sourceAirportCode = flightData[1].trim().toUpperCase();
            departureDateTimeString = flightData[2];
            destinationAirportCode = flightData[3].trim().toUpperCase();
            arrivalDateTimeString = flightData[4];
            airlineName = flightData[5];
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
            airlineName = flightData[9];

        }

        if(flightNumberString.matches("[0-9]*")) {
            number = Integer.parseInt(flightNumberString);
        }
        else {
            throw new ParserException("Invalid flight number: " + flightNumberString);
        }

        sourceAirportName =  AirportNames.getName(sourceAirportCode);
        if(sourceAirportName == null) {
            throw new ParserException("Invalid source airport code: " + sourceAirportCode);
        }

        destinationAirportName = AirportNames.getName(destinationAirportCode);
        if(destinationAirportName == null) {
            throw new ParserException("Invalid destination airport code: " + destinationAirportCode);
        }

        departureDateAndTime = flightDateTime.parse(departureDateTimeString);

        arrivalDateAndTime = flightDateTime.parse(arrivalDateTimeString);

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
        return (flightDateTime.format(departureDateAndTime));
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
        return (flightDateTime.format(arrivalDateAndTime));
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

    public String getDepartureTimeString() { return (flightTime.format(departureDateAndTime)); }
    public String getArrivalTimeString() { return (flightTime.format(arrivalDateAndTime)); }
    public String getDepartureDateString() { return (flightDate.format(departureDateAndTime)); }
    public String getArrivalDateString() { return (flightDate.format(arrivalDateAndTime)); }

    @Override
    public int compareTo(Flight o) {
        if(!this.getSource().equals(o.getSource()))
            return(this.getSource().compareTo(o.getSource()));
        if(!this.getDestination().equals(o.getDestination()))
            return(this.getDestination().compareTo(o.getDestination()));
        return(compare(this.getNumber(), o.getNumber()));
    }
}
