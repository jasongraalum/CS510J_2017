package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.ParserException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The main class for the CS410J airline Project
 */
public class Project3 {

    private static final ArrayList<String> ARGUMENT_NAMES;
    private static final ArrayList<String> ARGUMENT_DESCRIPTIONS;
    private static final ArrayList<String> OPTION_NAMES;
    private static final ArrayList<String> OPTION_DESCRIPTIONS;

    private static boolean printOption;
    private static boolean prettyPrintOption;
    private static boolean ReadMeOption;
    private static boolean fileOperation;
    private static boolean createNewFile;

    private static List<String> flightData;

    private static Airline airlineFromFile;
    private static String airlineFromFileName;

    private static Flight newFlight;
    private static String newAirlineName;

    private static String airlineFileName;
    private static String prettyPrintFileName;

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
                "Departure data and time (24-hour time)",
                "Three-letter code of arrival airport",
                "Arrival date and time (24-hour time)")
        );
        OPTION_DESCRIPTIONS = new ArrayList<String>(Arrays.asList(
                "Prints a description of the new flight",
                "Prints a pretty description of the new flight to either a file or standard output",
                "Read Airline and Flight data from a file",
                "Prints a README for this project and exits")
        );
        OPTION_NAMES = new ArrayList<String>(Arrays.asList(
                "-print",
                "-pretty",
                "-airlineFileName",
                "-README")
        );
    }

    /**
     * Main method for Project3.
     * Implements simple state machine to respond to command line arguments and input file.
     *
     * @param args     Command line arguments
     */
    public static void main(String[] args) throws ParserException {

        printOption = false;
        prettyPrintOption = false;
        ReadMeOption = false;
        fileOperation = false;
        createNewFile = false;

        newAirlineName = "";
        airlineFromFileName = "";

        airlineFileName = "";

        newFlight = null;
        airlineFromFile = null;

        flightData = new ArrayList<>();

        ArrayList<String> argsList;
        argsList = new ArrayList<>();
        Collections.addAll(argsList, args);

        // Parse command line arguments into options, new airline name, and new flight data.
        parseCmdLindOptions(argsList);

        // If -README given, simply display Usage and exit.
        if(ReadMeOption)
            printUsageMessageAndExit(1);

        // If there is "some" flight data, valid and create a new Flight.
        if(flightData.size() > 0)
        {
          String[] flightDataArray = new String[flightData.size()];
          flightDataArray = flightData.toArray(flightDataArray);
          try {
              newFlight = new Flight(Arrays.copyOfRange(flightDataArray, 0, flightDataArray.length));
          }
          catch (ParserException e)
          {
              System.err.println("ParserException: " + e.getMessage());
              System.exit(-1);
          }

        }
        else
            newFlight = null;

        // If -airlineFileName was given, check if it exists and if so, parse it into airlineFromFile
        if(fileOperation)
        {
            try {
                checkForAndParseFile();

                // File does not exist - needs to be created if possible
                if(createNewFile) {
                    if(newFlight != null) {
                        Airline newAirline = new Airline(newAirlineName);
                        newAirline.addFlight(newFlight);
                        printAirlineAndFlights(newAirline);
                        tryDumpAirline(newAirline);
                    }
                    else
                    {
                        System.exit(0);
                    }
                }
                // File exists and airline and flights have been parsed into airlineFromFile
                else {
                    if(newFlight != null) {
                        if(newAirlineName.equals(airlineFromFileName)) {
                            airlineFromFile.addFlight(newFlight);
                            printAirlineAndFlights(airlineFromFile);
                            tryDumpAirline(airlineFromFile);
                        }
                        else
                        {
                            System.out.println("The airline name in the input file, \"" +
                                    airlineFromFileName + "\"" +
                                    ", does not match the airline name on the command line, \"" +
                                    newAirlineName + "\"");
                            System.exit(-3);
                        }
                    }
                    // No new flight data, so just try to print
                    else
                    {
                        printAirlineAndFlights(airlineFromFile);
                    }
                }
            }
            catch (ParserException e) {
                System.out.println("Unable to parse input file: " + airlineFileName);
                System.exit(-2);
            }
        }

        // If no file operation or new flight data, print usage
        else if(flightData.size() == 0)
            printUsageMessageAndExit(3);


        // If we get here, there are 7 field in the flightData and no file operation
        else {
            String[] flightDataArray = new String[flightData.size()];
            flightDataArray = flightData.toArray(flightDataArray);
            Flight newFlight = new Flight(Arrays.copyOfRange(flightDataArray,0, flightDataArray.length));

            Airline newAirline = new Airline(newAirlineName);
            newAirline.addFlight(newFlight);

            printAirlineAndFlights(newAirline);
        }

    System.exit(0);

  }

    /**
     * Dumps the newAirline instance to airlineFileName
     *
     * @param newAirline
     */
    private static void tryDumpAirline(Airline newAirline) {
        try {
            TextDumper dumper = new TextDumper();
            dumper.setFileName(airlineFileName);
            dumper.dump(newAirline);
        } catch (IOException e) {
            System.out.println("Error dumping airline and flight data to: " + airlineFileName);
            System.exit(-1);
        }
    }

    /**
     * If the printOption is true, call toString on the airline and flights
     *
     * @param newAirline
     */
    private static void printAirlineAndFlights(Airline newAirline) {
        if(printOption) {
            System.out.println(newAirline.toString());
            Collection newAirlineFlights = newAirline.getFlights();
            Iterator<Flight> flightIter = newAirlineFlights.iterator();
            while(flightIter.hasNext())
            {
                System.out.println(flightIter.next().toString());
            }
        }
        if(prettyPrintOption) {
            try {
                PrettyPrinter prettyPrinter = new PrettyPrinter();
                prettyPrinter.setFileName(prettyPrintFileName);
                prettyPrinter.dump(newAirline);
            } catch (IOException e) {
                System.out.println("Error during print of airline and flight data to: " + prettyPrintFileName);
                System.exit(-1);
            }
        }
    }

    /**
     * Print the usage message and exit with status e
     *
     * @param e
     */
    public static void printUsageMessageAndExit(Integer e) {

        String packageName = Project3.class.getCanonicalName();

        System.out.println("usage: java " + packageName + " [options] <args>");
        System.out.println("  args are (in this order):");
        Iterator<String> argNamesIter = Project3.ARGUMENT_NAMES.iterator();
        Iterator<String> argDescriptionsIter = Project3.ARGUMENT_DESCRIPTIONS.iterator();
        while(argNamesIter.hasNext())
        {
            System.out.println(
                    "    " +
                            argNamesIter.next() +
                            argDescriptionsIter.next()
            );
        }
        System.out.println("  options are (options may appear in any order):");
        Iterator<String> optionNameIter = Project3.OPTION_NAMES.iterator();
        Iterator<String> optionDescriptionIter = Project3.OPTION_DESCRIPTIONS.iterator();
        while(optionNameIter.hasNext())
        {
            System.out.println(
                    "    " +
                            optionNameIter.next() +
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

      if(args.size() == 0) {
          return;
      }

      Iterator<String> argListIterator = args.iterator();


      while(argListIterator.hasNext())
      {
          String arg = argListIterator.next();
   //       System.out.println("arg = " + arg);

          if(arg.equals("-README"))
              ReadMeOption = true;
          else if(arg.equals("-print"))
              printOption = true;
          else if(arg.equals("-textFile"))
          {
              fileOperation = true;
              airlineFileName = argListIterator.next();
          }
          else if(arg.equals("-pretty"))
          {
              prettyPrintOption = true;
              prettyPrintFileName = argListIterator.next();
          }
          else if(arg.startsWith("-")) {
              System.out.println("Illegal option given: " + arg);
              System.exit(-2);
          }
          else if(newAirlineName.equals("")) {
              newAirlineName = arg;
          }
          else
          {
              flightData.add(arg);
              //System.out.println("Adding " + arg + " to flight data list");
          }

      }
  }

    /**
     * If the input file exists, try to parse it and create airlineFromFile.
     * If the input file does not exist, set createNewFile to true
     *
     * @throws ParserException
     */
   private static void checkForAndParseFile() throws ParserException{
       File inputFile = new File(airlineFileName);
       if (inputFile.exists() && !inputFile.isDirectory()) {
           TextParser airlineParser = new TextParser(airlineFileName);
           try {
               airlineFromFile = airlineParser.parse();
               airlineFromFileName = airlineFromFile.getName();
           } catch (ParserException e) {
               throw new ParserException("Unable to parse input file: " + airlineFileName);
           }
       } else {
           createNewFile = true;
       }
   }

}