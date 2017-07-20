package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractFlight;

/**
 * <h1>Flight Class</h1>
 * The Flight Class includes methods and parameters to build flight details for an airline.
 *
 * @author Jason Graalum
 * @version 1.0
 *
 */
public class Flight extends AbstractFlight {

    boolean isValidFlight;
    Integer number;
    String sourceAirport;
    String departureDate;
    String departureTime;
    String destinationAirport;
    String arrivalDate;
    String arrivalTime;

    /**
     * Creates a new <code>Flight</code>
     *
     * @param flightData
     *      List of Strings representing flight number, source, departure data and time, destination and arrival date and time.
     */
    Flight(String... flightData) {
        isValidFlight = true;
        setNumber(flightData[0].trim());
        setSource(flightData[1].trim());
        setDepartureString(flightData[2].trim(), flightData[3].trim());
        setDestination(flightData[4].trim());
        setArrivalString(flightData[5].trim(), flightData[6].trim());
    }

    /**
     * Creates a new empty <code>Flight</code>
     *
     */
    Flight() {
        this("", "", "", "", "", "", "");
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
     * Converts flight number string and stores as an integer
     * @param numberString      String of digits representing the flight number
     */
    public void setNumber(String numberString) {
        if (isValidFlightNumber(numberString))
            number = Integer.parseInt(numberString);
        else {
            System.out.println("Invalid flight number " + numberString + " Number between 1 and 99999 expected.");
            number = 0;
            isValidFlight = false;
        }
    }

    /**
     * Returns the source airport string
     * @return sourceAirport Three letter code of source airport
     */
    @Override
    public String getSource() {
        return sourceAirport;
    }

    /**
     * Sets the source airport code
     * @param airportCode   Three letter code of source airport
     */
    public void setSource(String airportCode) {

        if (isValidAirportCode(airportCode)) {
            sourceAirport = airportCode;
        } else {
            System.out.println("Invalid source airport code: " + airportCode + " A three letter code expected.");
            sourceAirport = "NAN";
            isValidFlight = false;
        }
    }

    /**
     * Returns the departure date and time
     * @return  String of departure data and time in mm/dd/yyyy hh:mm format
     */
    @Override
    public String getDepartureString() {
        return (departureDate + " " + departureTime);
    }

    public String getDepartureDate()
    {
        return(departureDate);
    }

    public String getDepartureTime()
    {
        return(departureTime);
    }
    /**
     * Sets the departure date and time after validating the format
     * @param date      String in mm/dd/yyyy format
     * @param time      String in hh:mm formate
     */
    public void setDepartureString(String date, String time) {
        if (isValidDateString(date)) {
            departureDate = date;
        } else {
            System.out.println("Invalid departure date: " + date + " A date of format dd/mm/yyyy expected.");
            departureDate = "00/00/0000";
            isValidFlight = false;
        }

        if (isValidTimeString(time)) {
            departureTime = time;
        } else {
            System.out.println("Invalid departure time: " + time + " A time of format hh:mm expected.");
            departureTime = "00:00";
            isValidFlight = false;
        }
    }

    /**
     * Retrieves the destination airport code
     * @return destinationAirport   Three letter airport code
     */
    @Override
    public String getDestination() {
        return destinationAirport;
    }

    /**
     * Set the destination airport code
     * @param airportCode   Three letter airport code
     */
    public void setDestination(String airportCode) {

        if (isValidAirportCode(airportCode)) {
            destinationAirport = airportCode;
        } else {
            System.out.println("Invalid destination airport code " + airportCode + " A three letter code expected.");
            destinationAirport = "NAN";
            isValidFlight = false;
        }
    }

    /**
     * Retrieve arrival date and time
     * @return  String of arrival data and time in mm/dd/yyyy hh:mm format
     */
    @Override
    public String getArrivalString() {
        return (arrivalDate + " " + arrivalTime);
    }


    public String getArrivalDate()
    {
        return(arrivalDate);
    }

    public String getArrivalTime()
    {
        return(arrivalTime);
    }

    /**
     * Sets the arrival date and time after validating the format
     * @param date      String in mm/dd/yyyy format
     * @param time      String in hh:mm formate
     */
    public void setArrivalString(String date, String time) {
        if (isValidDateString(date)) {
            arrivalDate = date;
        } else {
            System.out.println("Invalid arrival date " + date + " A date of format dd/mm/yyyy expected.");
            arrivalDate = "00/00/0000";
            isValidFlight = false;
        }

        if (isValidTimeString(time)) {
            arrivalTime = time;
        } else {
            System.out.println("Invalid arrival date " + date + " A time of format hh:mm expected.");
            arrivalTime = "00:00";
            isValidFlight = false;
        }
    }

    /**
     * Validates that the flightNumber is a string of numeric digits
     * @param flightNumber      Flight number string
     * @return
     */
    private boolean isValidFlightNumber(String flightNumber) {

        return (flightNumber.matches("[0-9]*"));
    }

    /**
     * Validate that the airportCode string is three letters.
     * @param airportCode
     * @return  Boolean
     */
    private boolean isValidAirportCode(String airportCode) {
        return airportCode.matches("[a-zA-Z][a-zA-Z][a-zA-Z]");
    }

    /**
     * Validate that the string is of mm/dd/yyyy format
     * @param dateString
     * @return Boolean
     */
    private boolean isValidDateString(String dateString) {
        String day, month, year;
        String[] dateFields = dateString.split("/");

        if (dateFields.length == 3) {
            month = dateFields[0];
            day = dateFields[1];
            year = dateFields[2];

            return ((day.matches("[0-9]") || day.matches("[0-2][0-9]") || day.matches("[3][0-1]"))
                    && (month.matches("[0-9]") || month.matches("0[0-9]") || month.matches("1[0-2]"))
                    && (year.matches("[1-2][0-9][0-9][0-9]")));
        } else {
            return false;
        }

    }

    /**
     * Validate that the string is of hh:mm format
     * @param timeString
     * @return Boolean
     */
    private boolean isValidTimeString(String timeString) {
        String hour, minutes;
        String[] dateFields = timeString.split(":");

        if (dateFields.length == 2) {
            hour = dateFields[0];
            minutes = dateFields[1];

            return ((hour.matches("[0-9]") || hour.matches("[0-1][0-9]")  || hour.matches("[2][0-3]"))
                    && (minutes.matches("[0-9]") || minutes.matches("[0-5][0-9]")));
        } else {
            return false;
        }

    }
}
