package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirlineDumper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.util.*;

/**
 * <h1>PrettyPrint Class</h1>
 * The PrettyPrint Class includes methods and parameters to print formatted Airline
 * and Flight data to either stdout or a text file.
 *
 * @author Jason Graalum
 * @version 1.0
 *
 */
public class PrettyPrinter implements AirlineDumper {

    private String fileName;
    Integer openTags = 0;

    void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void dump(AbstractAirline airline) throws IOException {
        Formatter formattedOutput = null;

        boolean stdOutput = false;
        try {
            if(fileName.equals("-"))
            {
                formattedOutput = new Formatter(System.out);
                stdOutput = true;
            }
            else {
                formattedOutput = new Formatter(new FileOutputStream(fileName));
            }
            writePrettyHeader(formattedOutput, airline.getName());

            List<Flight> flights = (List<Flight>) airline.getFlights();
            Comparator<Flight> comparator = Flight::compareTo;

            flights.sort(comparator);

            for (Flight flight : flights) {
                writePrettyFlight(formattedOutput, flight);
            }

        } catch (IOException ioe) {
            System.out.println("Error writing to file: " + fileName);
            System.exit(-1);
        }
        finally
            {
                try{
                    if(formattedOutput!=null && !stdOutput)
                        formattedOutput.close();
                }catch(Exception ex){
                    System.out.println("Error in closing the BufferedWriter" + ex);
                }
            }

    }

    public String toString(Airline airline, String source, String destination) throws IOException {

        StringBuilder prettyBuffer = new StringBuilder();

        boolean stdOutput = false;

        Formatter formattedOutput = new Formatter(prettyBuffer);
        writePrettyHeader(formattedOutput, airline.getName());

        List<Flight> flights = (List<Flight>) airline.getFlightsFromTo(source, destination);

        Comparator<Flight> comparator = Flight::compareTo;

        flights.sort(comparator);

        for (Flight flight : flights) {
            writePrettyFlight(formattedOutput, flight);
        }

        return formattedOutput.toString();
    }

    public String toString(Airline airline) throws IOException {

        StringBuilder prettyBuffer = new StringBuilder();
        Formatter formattedOutput = new Formatter(prettyBuffer);

        writePrettyHeader(formattedOutput, airline.getName());

        Collection<Flight> flights =  airline.getFlights();
        List<Flight> listOfFlights = new ArrayList(flights);
        Collections.sort(listOfFlights,Flight::compareTo);
        flights = listOfFlights;

        for (Flight flight : flights) {
            System.out.println("Flight: " + flight.getNumber());
            writePrettyFlight(formattedOutput, flight);
        }
        formattedOutput.format("\n");

        //System.out.print(prettyBuffer);
        formattedOutput.flush();
        return prettyBuffer.toString();
    }

    private void writePrettyHeader(Formatter formatter, String name) throws IOException {
        formatter.format("Flight Schedule for Airline: " + name + "\n");
        formatter.format("========================================================================================================================================\n");
        formatter.format(" %20s | %20s | %20s | %20s | %20s | %20s\n", "Flight #", "Source", "Departure Time", "Destination", "Arrival Time ", "Flight Duration");
        formatter.format("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void writePrettyFlight(Formatter formatter, AbstractFlight f) throws IOException {

        formatter.format("\n %20d | %20s | %20s | %20s | %20s | %d minutes",
                f.getNumber(),
                ((Flight) f).getSourceName(),
                f.getDepartureString(),
                ((Flight) f).getDestinationName(),
                f.getArrivalString(),
                ((Flight) f).getFlightDuration()
        );
    }
}
