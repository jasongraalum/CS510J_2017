package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.ParserException;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The main class for the CS410J airline Project
 */
public class Project2 {

    private static final ArrayList<String> ARGUMENT_NAMES;
    private static final ArrayList<String> ARGUMENT_DESCRIPTIONS;
    private static final ArrayList<String> OPTION_NAMES;
    private static final ArrayList<String> OPTION_DESCRIPTIONS;

    private static boolean printOption;
    private static boolean ReadMeOption;
    private static boolean fileOperation;
    private static boolean createNewFile;

    private static List<String> flightData;

    private static Airline airlineFromFile;
    private static String airlineFromFileName;

    private static Flight newFlight;
    private static String newAirlineName;

    private static String fileName;

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
                "Read Airline and Flight data from a file",
                "Prints a README for this project and exits")
        );
        OPTION_NAMES = new ArrayList<String>(Arrays.asList(
                "-print",
                "-fileName",
                "-README")
        );
    }


    public static void main(String[] args) {

        printOption = false;
        ReadMeOption = false;
        fileOperation = false;
        createNewFile = false;

        newAirlineName = "";
        airlineFromFileName = "";

        fileName = "";

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
        if(flightData.size() != 0 && flightData.size() != 7)
        {
            System.out.println("Incorrect Flight Data");
            System.out.println(flightData);
            printUsageMessageAndExit(2);
        }
        else if(flightData.size() == 7) {
            String[] flightDataArray = new String[flightData.size()];
            flightDataArray = flightData.toArray(flightDataArray);
            newFlight = new Flight(Arrays.copyOfRange(flightDataArray,0, flightDataArray.length));
            if(!newFlight.isValidFlight) {
                newFlight = null;
                System.out.println("Flight data is invalid. Flight not included.");
            }
        }
        else
            newFlight = null;

        // If -fileName was given, check if it exists and if so, parse it into airlineFromFile
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
                            System.exit(-3);;
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
                System.out.println("Unable to parse input file: " + fileName);
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

    private static void tryDumpAirline(Airline newAirline) {
        try {
            TextDumper dumper = new TextDumper();
            dumper.setFileName(fileName);
            dumper.dump(newAirline);
        } catch (IOException e) {
            System.out.println("Error dumping airline and flight data to: " + fileName);
            System.exit(-1);
        }
    }

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
    }

    public static void printUsageMessageAndExit(Integer e) {

        Integer NameToDescriptionsPadding = 22;

        String packageName = Project2.class.getCanonicalName();

        System.out.println("usage: java " + packageName + " [options] <args>");
        System.out.println("  args are (in this order):");
        Iterator<String> argNamesIter = Project2.ARGUMENT_NAMES.iterator();
        Iterator<String> argDescriptionsIter = Project2.ARGUMENT_DESCRIPTIONS.iterator();
        while(argNamesIter.hasNext())
        {
            System.out.println(
                    "    " +
                            argNamesIter.next() +
                            argDescriptionsIter.next()
            );
        }
        System.out.println("  options are (options may appear in any order):");
        Iterator<String> optionNameIter = Project2.OPTION_NAMES.iterator();
        Iterator<String> optionDescriptionIter = Project2.OPTION_DESCRIPTIONS.iterator();
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

  private static boolean isValidOption(String optionName)
  {
    return Project2.OPTION_NAMES.contains(optionName);
  }


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
              fileName = argListIterator.next();
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

   private static void checkForAndParseFile() throws ParserException{
       File inputFile = new File(fileName);
       if (inputFile.exists() && !inputFile.isDirectory()) {
           TextParser airlineParser = new TextParser(fileName);
           try {
               airlineFromFile = airlineParser.parse();
               airlineFromFileName = airlineFromFile.getName();
           } catch (ParserException e) {
               throw new ParserException("Unable to parse input file: " + fileName);
           }
       } else {
           createNewFile = true;
       }
   }

}