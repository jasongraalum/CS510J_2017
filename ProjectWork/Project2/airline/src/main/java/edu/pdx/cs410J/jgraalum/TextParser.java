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
        return(lines);
    }
    @Override
    public Airline parse() throws ParserException {

        Airline airline= null;

        if(inputFileLines == null)
            throw new ParserException("Error reading data from file: " + fileName);
        else {

            if(inputFileLines.get(0).equals(airlineXMLHeader)) {
                airline = readAirlineXMLData(inputFileLines);
            }
            return(airline);
        }



    }

    private Airline readAirlineXMLData(List<String> inputFileLines) {

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
            String departureDate = line.replaceAll("<date>","").replaceAll("</date>","").trim();

            while(!line.contains("<time>"))
                line = inputLinesIterator.next();
            String departureTime = line.replaceAll("<time>","").replaceAll("</time>","").trim();

            while(!line.contains("</source>"))
                line = inputLinesIterator.next();

            while(!line.contains("<destination>"))
                line = inputLinesIterator.next();

            while(!line.contains("<id>"))
                line = inputLinesIterator.next();
            String destinationID = line.replaceAll("<id>","").replaceAll("</id>","").trim();

            while(!line.contains("<date>"))
                line = inputLinesIterator.next();
            String arrivalDate = line.replaceAll("<date>","").replaceAll("</date>","").trim();

            while(!line.contains("<time>"))
                line = inputLinesIterator.next();
            String arrivalTime = line.replaceAll("<time>","").replaceAll("</time>","").trim();

            while(!line.contains("</destination>"))
                line = inputLinesIterator.next();

            while(!line.contains("</flight>"))
                line = inputLinesIterator.next();

            Flight flight = new Flight(flightNumber, sourceID, departureDate, departureTime, destinationID, arrivalDate, arrivalTime);
            airline.addFlight(flight);

            line = inputLinesIterator.next();
        }

        return(airline);

    }

}
