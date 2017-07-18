package edu.pdx.cs410J.jgraalum;

import org.apache.commons.lang3.StringUtils;
import java.util.*;

/**
 * The main class for the CS410J airline Project
 */
public class Project1 {

    private static final ArrayList<String> ARGUMENT_NAMES;
    private static final ArrayList<String> ARGUMENT_DESCRIPTIONS;
    private static final ArrayList<String> OPTION_NAMES;
    private static final ArrayList<String> OPTION_DESCRIPTIONS;

    private static boolean[] optionValues = new boolean[2];

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
                "Prints a README for this project and exits")
        );
        OPTION_NAMES = new ArrayList<String>(Arrays.asList(
                "-print",
                "-README")
        );
    }


    public static void main(String[] args) {

        String[] flightData;

        Arrays.fill(optionValues,false);
        flightData = stripAndParseCmdLindOptions(args);


        if(optionValues[1] == true || flightData.length == 0) {
            printUsageMessageandExit();
        }

        Airline airline = new Airline(flightData[0]);
        Flight flight = new Flight(Arrays.copyOfRange(flightData,1, flightData.length));

        airline.addFlight(flight) ;

        if(optionValues[0] == true) {
            Collection flights = airline.getFlights();
            Iterator<Flight> flightIter = flights.iterator();
            while(flightIter.hasNext())
            {
                System.out.print(flightIter.next().toString());
            }
        }

        System.exit(0);
  }

  public static void printUsageMessageandExit() {

      Integer NameToDescriptionsPadding = 22;

      String packageName = Project1.class.getCanonicalName();

      System.out.println("usage: java " + packageName + " [options] <args>");
      System.out.println("  args are (in this order):");
      Iterator<String> argNamesIter = Project1.ARGUMENT_NAMES.iterator();
      Iterator<String> argDescriptionsIter = Project1.ARGUMENT_DESCRIPTIONS.iterator();
      while(argNamesIter.hasNext())
      {
          System.out.println(
                  "    " +
                  StringUtils.rightPad(argNamesIter.next(),NameToDescriptionsPadding) +
                  argDescriptionsIter.next()
          );
      }
      System.out.println("  options are (options may appear in any order):");
      Iterator<String> optionNameIter = Project1.OPTION_NAMES.iterator();
      Iterator<String> optionDescriptionIter = Project1.OPTION_DESCRIPTIONS.iterator();
      while(optionNameIter.hasNext())
      {
          System.out.println(
                  "    " +
                  StringUtils.rightPad( optionNameIter.next(), NameToDescriptionsPadding) +
                  optionDescriptionIter.next()
          );
      }
      System.out.println("Data and time should be in the format: mm/dd/yyyy hh:mm");
      System.exit(1);
  }

  private static boolean isValidOption(String optionName)
  {
    return Project1.OPTION_NAMES.contains(optionName);
  }


  private static String[] stripAndParseCmdLindOptions(String[] args)
  {
    int optionCount = 0;

    for(String arg: args) {
        if(arg.charAt(0) == '-')
        {
            if(isValidOption(arg)) {
                optionValues[OPTION_NAMES.indexOf(arg)] = true;
                optionCount++;
            }
            else {
                IllegalOption(arg);
            }
        }

    }
    return Arrays.copyOfRange(args,optionCount,args.length);
  }

  private static void IllegalOption(String optionString)
  {
    System.out.println("Illegal Option given: " + optionString);
    System.out.println("");
    printUsageMessageandExit();
  }


}