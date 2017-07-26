package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;
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
    private Formatter formattedOutput = null;
    Integer openTags = 0;

    void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void dump(AbstractAirline airline) throws IOException {

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
            writePrettyHeader(airline.getName());

            List<Flight> flights = (List<Flight>) airline.getFlights();
            Comparator<Flight> comparator = Flight::compareTo;

            flights.sort(comparator);

            for (Flight flight : flights) {
                writePrettyFlight(flight);
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

    private void writePrettyHeader(String name) throws IOException {
        formattedOutput.format("Flight Schedule for Airline: " + name + "\n");
        formattedOutput.format("========================================================================================================================================\n");
        formattedOutput.format(" %20s | %20s | %20s | %20s | %20s | %20s\n", "Flight #", "Source", "Departure Time", "Destination", "Arrival Time ", "Flight Duration");
        formattedOutput.format("----------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void writePrettyFlight(AbstractFlight f) throws IOException {

        formattedOutput.format("\n %20d | %20s | %20s | %20s | %20s | %d minutes",
                f.getNumber(),
                ((Flight) f).getSourceName(),
                f.getDepartureString(),
                ((Flight) f).getDestinationName(),
                f.getArrivalString(),
                ((Flight) f).getFlightDuration()
        );
    }
}
