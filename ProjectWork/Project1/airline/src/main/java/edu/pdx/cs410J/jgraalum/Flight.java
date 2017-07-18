package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AbstractFlight;
import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.StringUtils;

public class Flight extends AbstractFlight {

    boolean isValidFlight;
    Integer number;
    String sourceAirport;
    String departureDate;
    String departureTime;
    String destinationAirport;
    String arrivalDate;
    String arrivalTime;


    Flight(String... flightData) {
        isValidFlight = true;
        setNumber(flightData[0].trim());
        setSource(flightData[1].trim());
        setDepartureString(flightData[2].trim(), flightData[3].trim());
        setDestination(flightData[4].trim());
        setArrivalString(flightData[5].trim(), flightData[6].trim());
    }

    Flight() {
        this(new String[]{"", "", "", "", "", "", ""});
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    public void setNumber(String numberString) {
        if(isValidFlightNumber(numberString))
            number = Integer.parseInt(numberString);
        else {
            System.out.println("Invalid flight number " + numberString + " Number between 1 and 99999 expected.");
            number = 0;
            isValidFlight = false;
        }
    }

    @Override
    public String getSource() {
        return sourceAirport;
    }

    public void setSource(String airportCode) {

        if(isValidAirportCode(airportCode))
        {
            sourceAirport = airportCode;
        }
        else
        {
            System.out.println("Invalid source airport code: " + airportCode + " A three letter code expected.");
            sourceAirport = "NAN";
            isValidFlight = false;
        }
    }

    @Override
    public String getDepartureString() {
        return(departureDate + " " + departureTime);
    }

    public void setDepartureString(String date, String time)
    {
        if(isValidDateString(date))
            departureDate = date;
        else
        {
            System.out.println("Invalid departure date: " + date + " A date of format dd/mm/yyyy expected.");
            departureDate = "00/00/0000";
            isValidFlight = false;
        }

        if(isValidTimeString(time))
            departureTime = time;
        else
        {
            System.out.println("Invalid departure time: " + time + " A time of format hh:mm expected.");
            departureTime= "00:00";
            isValidFlight = false;
        }
    }

    @Override
    public String getDestination() {
        return destinationAirport;
    }

    public void setDestination(String airportCode) {

        if(isValidAirportCode(airportCode))
        {
            destinationAirport = airportCode;
        }
        else
        {
            System.out.println("Invalid destination airport code " + airportCode + " A three letter code expected.");
            destinationAirport = "NAN";
            isValidFlight = false;
        }
    }

    @Override
    public String getArrivalString() {
        return (arrivalDate + " " + arrivalTime);
    }

    public void setArrivalString(String date, String time)
    {
        if(isValidDateString(date))
            arrivalDate = date;
        else
        {
            System.out.println("Invalid arrival date " + date + " A date of format dd/mm/yyyy expected.");
            arrivalDate = "00/00/0000";
            isValidFlight = false;
        }

        if(isValidTimeString(time))
            arrivalTime = time;
        else
        {
            System.out.println("Invalid arrival date " + date + " A time of format hh:mm expected.");
            arrivalTime= "00:00";
            isValidFlight = false;
        }
    }

  private boolean isValidFlightNumber(String flightNumber)
  {
      return(StringUtils.isNumeric(flightNumber));
  }

  private boolean isValidAirportCode(String airportCode)
  {
      if(StringUtils.isAlpha(airportCode) && airportCode.length() == 3)
          return true;
      else
          return false;
  }

  private boolean isValidDateString(String dateString)
  {
      String day, month, year;
      String[] dateFields = dateString.split("/");

      if(dateFields.length == 3) {
          month = dateFields[0];
          day = dateFields[1];
          year = dateFields[2];

          return (StringUtils.isNumeric(day) && StringUtils.isNumeric(month) && StringUtils.isNumeric(year) &&
                  (Integer.parseInt(day) < 32) &&
                  (Integer.parseInt(month) < 13) &&
                  (Integer.parseInt(year) > 1900)
          );
      }
      else
          return false;

  }

  private boolean isValidTimeString(String timeString)  {
      String hour, minutes;
      String[] dateFields = timeString.split(":");

      if(dateFields.length == 2) {
          hour = dateFields[0];
          minutes = dateFields[1];

          return (StringUtils.isNumeric(hour) && StringUtils.isNumeric(minutes)  &&
                  (Integer.parseInt(hour) < 25) &&
                  (Integer.parseInt(minutes) < 61)
          );
      }
      else
          return false;

  }
}
