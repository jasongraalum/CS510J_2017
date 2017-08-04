package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>Airline</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple key/value pairs.
 */
public class AirlineServlet extends HttpServlet {


    private final Map<String, Airline> airlineData = new HashMap<>();

    /**
     * Handles an HTTP GET request from a client by writing the value of the key
     * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
     * parameter is not specified, all of the key/value pairs are written to the
     * HTTP response.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException

    {
        response.setContentType("text/plain");

        String airlineName = getParameter("name", request);
        String sourceAirportCode = getParameter("src", request);
        String destinationAirportCode = getParameter("dest", request);
        String flightNumberString = getParameter("flightNumber", request);


        //System.out.println("Source = " + sourceAirportCode);
        //System.out.println("Destination = " + destinationAirportCode);

        if (airlineName == null) {
            missingRequiredParameter(response, "Airline Name");
            return;
        }

        Airline airline = airlineData.get(airlineName);
        if (airline == null) {
            airlineNotFound(response, airlineName);
            return;
        }


        String prettyOutput;
        PrettyPrinter prettyPrinter = new PrettyPrinter();

        if (flightNumberString != null)
        {
            //System.out.println("Searching for flight by number");
            prettyOutput = prettyPrinter.toString(airline,Integer.parseInt(flightNumberString));
            if (prettyOutput == null) {
                noFlightsForAirline(response, airlineName);
                response.sendError(HttpServletResponse.SC_OK, "No Flights Found");
            }
        }
        else if (sourceAirportCode == null || destinationAirportCode == null) {
            //System.out.println("Searching for all flights");
            prettyOutput = prettyPrinter.toString(airline);
            if (prettyOutput == null) {
                noFlightsForAirline(response, airlineName);
                response.sendError(HttpServletResponse.SC_OK, "No Flights Found");
            }
        } else {
            if(AirportNames.getName(sourceAirportCode) == null)
            {
                invalidAirportCode(response, sourceAirportCode);
                return;
            }

            if(AirportNames.getName(destinationAirportCode) == null)
            {
                invalidAirportCode(response, destinationAirportCode);
                return;
            }
            //System.out.println("Searching for flights between " + sourceAirportCode + " and " + destinationAirportCode);
            prettyOutput = prettyPrinter.toString(airline, sourceAirportCode, destinationAirportCode);
            System.out.print(prettyOutput);
            if (prettyOutput == null) {
                noFlightsForAirline(response, airlineName, sourceAirportCode, destinationAirportCode);
                response.sendError(HttpServletResponse.SC_OK, "No Flights Found");
            }

        }

        if (prettyOutput != null)
        {
            PrintWriter pw = response.getWriter();
            pw.print(prettyOutput);
            pw.flush();
            response.setStatus( HttpServletResponse.SC_OK );
        }
    }

    /**
     * Handles an HTTP POST request by storing the key/value pair specified by the
     * "key" and "value" request parameters.  It writes the key/value pair to the
     * HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        //System.out.println("Executing Post");
        response.setContentType( "text/plain" );

        String airlineName = getParameter("name",request);
        String flightNumber = getParameter("flightNumber", request);
        String sourceAirportCode = getParameter("src",request);
        String departureDateTime = getParameter("departTime",request);
        String destinationAirportCode = getParameter("dest",request);
        String arrivalDateTime = getParameter("arriveTime",request);

        if(airlineName == null)
        {
            missingRequiredParameter(response, "Airline Name");
            return;
        }

        if(flightNumber == null)
        {
            missingRequiredParameter(response, "Flight Number");
            return;
        }

        if(sourceAirportCode == null)
        {
            missingRequiredParameter(response, "Source Airport Code");
            return;
        }

        if(destinationAirportCode == null)
        {
            missingRequiredParameter(response, "Destination Airport Code");
            return;
        }

        if(departureDateTime == null)
        {
            missingRequiredParameter(response, "Departure Date and Time");
            return;
        }

        if(arrivalDateTime == null)
        {
            missingRequiredParameter(response, "Arrival Date and Time");
            return;
        }

        if(AirportNames.getName(sourceAirportCode) == null)
        {
            invalidAirportCode(response, sourceAirportCode);
            return;
        }

        if(AirportNames.getName(destinationAirportCode) == null)
        {
            invalidAirportCode(response, destinationAirportCode);
            return;
        }

        DateFormat flightDateAndTime = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        try {
            flightDateAndTime.parse(arrivalDateTime);
        } catch (ParseException e) {
            invalidDateFormat(response, arrivalDateTime);
            return;
        }
        try {
            flightDateAndTime.parse(departureDateTime);
        } catch (ParseException e) {
            invalidDateFormat(response, departureDateTime);
            return;
        }

        System.out.println(airlineName);

        Airline airline = airlineData.get(airlineName);

        if(airline == null) {
            airline = new Airline(airlineName);
        }

        Flight newFlight;

        try {
            //System.out.println("Adding Flight: " + flightNumber);
            newFlight = new Flight(new String[]{flightNumber, sourceAirportCode, departureDateTime, destinationAirportCode, arrivalDateTime});
            airline.addFlight(newFlight);
            airlineData.put(airlineName,airline);
        } catch (ParserException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Handles an HTTP DELETE request by removing all key/value pairs.  This
     * behavior is exposed for testing purposes only.  It's probably not
     * something that you'd want a real application to expose.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        this.airlineData.clear();

        PrintWriter pw = response.getWriter();
        pw.println(Messages.allMappingsDeleted());
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);

    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
            throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }
    /**
     * Writes an error message missing airline data to the HTTP response.
     *
     */
    private void airlineNotFound(HttpServletResponse response, String airlineName) throws IOException {
        String message = Messages.airlineNotFound(airlineName);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }
    /**
     * Writes an error message about no flights between a source and destination to the HTTP response.
     *
     */
    private void noFlightsForAirline(HttpServletResponse response, String airlineName, String source, String destination) throws IOException {
        String message = Messages.noFlightsForAirline(airlineName, source, destination);
        response.sendError(HttpServletResponse.SC_NO_CONTENT, message);
    }
    /**
     * Writes an error message about no flights included in airline to the HTTP response.
     *
     */
    private void noFlightsForAirline(HttpServletResponse response, String airlineName) throws IOException {
        String message = Messages.noFlightsForAirline(airlineName);
        response.sendError(HttpServletResponse.SC_NO_CONTENT, message);
    }
    /**
     * Writes an error message about invalid airport code to the HTTP response.
     *
     */
    private void invalidAirportCode(HttpServletResponse response, String code) throws IOException {
        String message = Messages.invalidAirportCode(code);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }
    /**
     * Writes an error message about incorrectly formatted date string to the HTTP response.
     *
     */
    private void invalidDateFormat(HttpServletResponse response, String datestring) throws IOException {
        String message = Messages.invalidDateFormat(datestring);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || "".equals(value)) {
            return null;

        } else {
            return value;
        }
    }

    /**
     * If the input file exists, try to parse it and create airlineFromFile.
     * If the input file does not exist, set createNewFile to true
     *
     * @throws ParserException,FileNotFoundException
     * @param airlineName - String containing name of airline.
     */
    private static Airline checkForAndParseFile(String airlineName) throws ParserException, FileNotFoundException {

        Airline airlineFromFile;
        String airlineFromFileName;

        String airlineFileName = airlineName + ".xml";
        File inputFile = new File(airlineFileName);

        if (inputFile.exists() && !inputFile.isDirectory()) {
            TextParser airlineParser = new TextParser(airlineFileName);
            try {
                airlineFromFile = airlineParser.parse();
                airlineFromFileName = airlineFromFile.getName();
                if(!airlineFromFileName.equals(airlineName))
                {
                    throw new ParserException("Airline name in " + airlineFromFileName + " does not match given name " + airlineName);
                }
            } catch (ParserException e) {
                throw new ParserException("Unable to parse input file: " + airlineFileName);
            }
        } else {
            return null;
        }
        return airlineFromFile;
    }

}
