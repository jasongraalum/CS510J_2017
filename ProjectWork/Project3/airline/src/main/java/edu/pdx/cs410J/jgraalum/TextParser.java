package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jasongraalum on 7/15/17.
 */
public class TextParser implements AirlineParser<Airline> {

    private String airlineXMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private String fileName = "";
    private Iterator<String> inputLinesIterator;
    private List<String> inputFileLines;

    TextParser(String file)
    {
        fileName = file;
        inputFileLines = getLinesFromFile(fileName);
        inputLinesIterator = inputFileLines.iterator();
    }

    private List<String> getLinesFromFile(String filename) {

        List<String> lines = new ArrayList<String>();
        try {
            InputStream inputFileStream = new FileInputStream(filename);
            InputStreamReader inputFileStreamReader = new InputStreamReader(inputFileStream,"UTF-8");
            BufferedReader bufferedInput = new BufferedReader(inputFileStreamReader);

            String line;
            while ((line = bufferedInput.readLine()) != null ) {
                lines.add(line);
            }

            bufferedInput.close();
            inputFileStream.close();
            inputFileStreamReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return null;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported file format: " + fileName);
            return null;
        } catch (IOException e) {
            System.out.println("Unable to read file: " + fileName);
            return null;
        }
        return lines;
    }
    @Override
    public Airline parse() throws ParserException {

        Airline airline= null;

        if(inputFileLines == null)
            throw new ParserException("Error reading data from file: " + fileName);
        else {

            if(inputFileLines.get(0).equals(airlineXMLHeader)) {
                airline = readAirlineXMLData();
            }
            return airline;
        }



    }

    /**
     * Method to parse Airline XML Data.  Nothing is assumed about the format.
     *
     * @return  Return an instance of Airline containing data read from input file
     */
    private Airline readAirlineXMLData() throws ParserException {

        Airline airline;

        String line = inputLinesIterator.next();
        while(!line.contains("<airline>"))
        {
            line = inputLinesIterator.next();
        }

        while(!line.contains("<name>"))
        {
            line = inputLinesIterator.next();
        }

        String airlineName = line.replaceAll("<name>","").replaceAll("</name>","").trim();
        airline = new Airline(airlineName);


        line = inputLinesIterator.next();
        while(!line.contains("</airline>"))
        {
            while(!line.contains("<flight>"))
                line = inputLinesIterator.next();

            while(!line.contains("<number>"))
                line = inputLinesIterator.next();
            String flightNumber = line.replaceAll("<number>","").replaceAll("</number>","").trim();

            while(!line.contains("<source>"))
                line = inputLinesIterator.next();

            while(!line.contains("<id>"))
                line = inputLinesIterator.next();
            String sourceID = line.replaceAll("<id>","").replaceAll("</id>","").trim();

            while(!line.contains("<date>"))
                line = inputLinesIterator.next();
            String[] departureDateTime = line.replaceAll("<date>","").replaceAll("</date>","").trim().split(" ");
            String departureDate = departureDateTime[0];
            String departureTime = departureDateTime[1];
            String departureAMPM= departureDateTime[2];


            while(!line.contains("</source>"))
                line = inputLinesIterator.next();

            while(!line.contains("<destination>"))
                line = inputLinesIterator.next();

            while(!line.contains("<id>"))
                line = inputLinesIterator.next();
            String destinationID = line.replaceAll("<id>","").replaceAll("</id>","").trim();

            while(!line.contains("<date>"))
                line = inputLinesIterator.next();
            String[] arrivalDateTime = line.replaceAll("<date>","").replaceAll("</date>","").trim().split(" ");
            String arrivalDate = arrivalDateTime[0];
            String arrivalTime = arrivalDateTime[1];
            String arrivalAMPM= arrivalDateTime[2];

            while(!line.contains("</destination>"))
                line = inputLinesIterator.next();

            while(!line.contains("</flight>"))
                line = inputLinesIterator.next();

            Flight flight = new Flight(flightNumber, sourceID, departureDate, departureTime, departureAMPM, destinationID, arrivalDate, arrivalTime, arrivalAMPM);
            airline.addFlight(flight);

            line = inputLinesIterator.next();
        }

        return airline;

    }

}
