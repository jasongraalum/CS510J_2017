package edu.pdx.cs410J.jgraalum;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    static final String MISSING_ARGS = "Missing command line arguments";

    private static final ArrayList<String> ARGUMENT_NAMES;
    private static final ArrayList<String> ARGUMENT_DESCRIPTIONS;
    private static final ArrayList<String> OPTION_NAMES;
    private static final ArrayList<String> OPTION_DESCRIPTIONS;


    static {
        ARGUMENT_NAMES = new ArrayList<String>(Arrays.asList(
                "name",
                "flightNumber",
                "src",
                "departureTime",
                "dest",
                "arriveTime")
        );
        ARGUMENT_DESCRIPTIONS = new ArrayList<String>(Arrays.asList(
                "Then name of the airline",
                "The flight number",
                "Three-letter code of departure airport",
                "Departure data and time (mm/dd/yyyy hh:mm AM/PM)",
                "Three-letter code of arrival airport",
                "Arrival date and time (mm/dd/yyyy hh:mm AM/PM)")
        );
        OPTION_DESCRIPTIONS = new ArrayList<String>(Arrays.asList(
                "Host computer on which the server is running",
                "Port on which the server is listening",
                "Search for flights",
                "Prints a description of the new flight",
                "Prints a README for this project and exits")
        );
        OPTION_NAMES = new ArrayList<String>(Arrays.asList(
                "-host hostname",
                "-port port",
                "-search airline <source destination>",
                "-print",
                "-README")
        );
    }


    private static  String hostName;
    private static  String portString;

    private static boolean printOption;

    private static boolean ReadMeOption;

    private static boolean searchOption;
    private static String searchSource;
    private static String searchDestination;

    private static boolean createNewFlight;

    private static String airlineName;

    private static List<String> flightData;

    public static void main(String... args) {

        printOption = false;
        ReadMeOption = false;
        searchOption = false;
        createNewFlight = false;

        flightData = new ArrayList<>();

        ArrayList<String> argsList;
        argsList = new ArrayList<>();
        Collections.addAll(argsList, args);


        // Parse command line arguments into options, new airline name, and new flight data.
        parseCmdLindOptions(argsList);

        // If -README given, simply display Usage and exit.
        if(ReadMeOption)
            usage("ReadMe:", 0);

        if (airlineName == null)
            usage("Missing Airline Name", 1);

        if (hostName == null)
            usage("Missing hostname",1);

        if ((hostName != null) && (portString == null) )
            usage( "Missing port",1 );

        if(hostName != null && portString != null)
        {
            //System.out.println("Host: " + hostName + " Port: " + portString);
            int port;
            try {
                port = Integer.parseInt( portString );
            } catch (NumberFormatException ex) {
                usage("Port \"" + portString + "\" must be an integer",1);
                return;
            }

            AirlineRestClient client = new AirlineRestClient(hostName, port);

            String message;
            try {
                if(searchOption)
                {
                    if(searchSource == null || searchDestination == null) {
                        message = client.getAllFlights(airlineName);
                        //System.out.println("Search for all flights");
                    }
                    else {
                        //System.out.println("Searching for flights");
                        message = client.getFromToFlights(airlineName, searchSource, searchDestination);
                    }
                    System.out.println(message);
                }
                else if(createNewFlight)
                {
                    if (flightData.size() != 9) {
                        System.out.println(flightData.toString());
                        usage("Incomplete flight parameters provided", 1);
                    }

                    String[] flightDataArray = new String[flightData.size()];
                    flightDataArray = flightData.toArray(flightDataArray);

                    String flightNumberString = flightDataArray[0].trim();

                    String sourceAirportCode = flightDataArray[1].trim().toUpperCase();
                    String departureDateString = flightDataArray[2].trim();
                    String departureTimeString = flightDataArray[3].trim();
                    String departureAMPMString = flightDataArray[4].trim();
                    String departureDateTimeString = departureDateString + " " + departureTimeString + " " + departureAMPMString;

                    String destinationAirportCode = flightDataArray[5].trim().toUpperCase();
                    String arrivalDateString = flightDataArray[6].trim();
                    String arrivalTimeString = flightDataArray[7].trim();
                    String arrivalAMPMString = flightDataArray[8].trim();
                    String arrivalDateTimeString = arrivalDateString + " " + arrivalTimeString + " " + arrivalAMPMString;


                    client.addFlightToAirline(airlineName,
                            flightNumberString,
                            sourceAirportCode,
                            departureDateTimeString,
                            destinationAirportCode,
                            arrivalDateTimeString);

                    if(printOption)
                    {
                        message = client.getFlightFromNumber(airlineName, flightNumberString);
                        System.out.println(message);
                    }


                }
                else if(printOption)
                {
                    message = client.getAllFlights(airlineName);
                    System.out.println(message);
                }
            } catch (IOException ex) {

                error("Unable to connect to server: " + hostName + ":" + portString + "\nPlease verify that server has been started and is running");
            }
        }
        System.exit(0);
    }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Print the usage message and exit with status e
     *
     * @param e   return code
     */
    private static void usage(String message, Integer e) {

        String packageName = Project4.class.getCanonicalName();

        System.out.println("** " + message);
        System.out.println("usage: java " + packageName + " [options] <args>");
        System.out.println("  args are (in this order):");
        Iterator<String> argNamesIter = ARGUMENT_NAMES.iterator();
        Iterator<String> argDescriptionsIter = ARGUMENT_DESCRIPTIONS.iterator();
        while(argNamesIter.hasNext())
        {
            System.out.println(
                    "    " +
                            argNamesIter.next() + "\t" +
                            argDescriptionsIter.next()
            );
        }
        System.out.println("  options are (options may appear in any order):");
        Iterator<String> optionNameIter = OPTION_NAMES.iterator();
        Iterator<String> optionDescriptionIter = OPTION_DESCRIPTIONS.iterator();
        while(optionNameIter.hasNext())
        {
            System.out.println(
                    "    " +
                            optionNameIter.next() + "\t" +
                            optionDescriptionIter.next()
            );
        }
        System.out.println("Data and time should be in the format: mm/dd/yyyy hh:mm");
        System.exit(e);
    }
    /**
     * Set command option booleans based on command line args.  Remaining args are stored in the flight data.
     * @param args
     */
    private static void parseCmdLindOptions(List<String> args)
    {

        airlineName = null;
        StringBuilder airlineNameSB = new StringBuilder("");

        if(args.size() == 0) {
            return;
        }

        Iterator<String> argListIterator = args.iterator();
        boolean moreOptions = true;
        String arg = "";
        while(moreOptions) {

            arg = argListIterator.next();
            if (arg.equals("-README"))
                ReadMeOption = true;
            else if (arg.equals("-host"))
                hostName = argListIterator.next();
            else if (arg.equals("-port"))
                portString = argListIterator.next();
            else if (arg.equals("-print"))
                printOption = true;
            else if (arg.equals("-search")) {
                searchOption = true;
            } else {
                moreOptions = false;
            }
        }

        if(arg.length() > 0) {
            airlineNameSB.append(arg);
            if (airlineNameSB.charAt(0) == '\"')
                while (airlineNameSB.charAt(airlineNameSB.length() - 1) != '\"') {
                    airlineNameSB.append(" ");
                    airlineNameSB.append(argListIterator.next());
                }
        }

        if(searchOption)
        {
            if(argListIterator.hasNext())
                searchSource = argListIterator.next();
            else
                searchSource = null;
            if(argListIterator.hasNext())
                searchDestination = argListIterator.next();
            else
                searchDestination = null;
        }
        else
        {
            while(argListIterator.hasNext())
            {
                arg = argListIterator.next();
                createNewFlight = true;
                flightData.add(arg);
            }

        }

        if(airlineNameSB.length() > 0) {
            if (airlineNameSB.charAt(0) == '\"') {
                airlineNameSB.deleteCharAt(0);
                airlineNameSB.deleteCharAt(airlineNameSB.length() - 1);
            }
            airlineName = airlineNameSB.toString();
        }
        else {
            airlineName = null;
        }
    }
}