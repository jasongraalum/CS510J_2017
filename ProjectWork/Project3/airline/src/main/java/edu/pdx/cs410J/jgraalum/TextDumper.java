package edu.pdx.cs410J.jgraalum;

import java.io.*;
import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AirlineDumper;

import java.util.Collection;
import java.util.Iterator;

import static java.lang.String.*;

/**
 * Created by Jason Graalum on 7/15/17.
 */
public class TextDumper implements AirlineDumper {

    String fileName;
    FileWriter dumpFile = null;
    Integer openTags = 0;

    public void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void dump(AbstractAirline airline) throws IOException {

        if(airline == null)
            return;

        try {
            dumpFile = new FileWriter(fileName);

            writeHeader();

            writeTagOpen("airline");

            writeTagWithData("name",airline.getName());

            Collection flights = airline.getFlights();

            for(Iterator<Flight> iterator = flights.iterator(); iterator.hasNext();)
            {
                Flight flight = iterator.next();
                writeFlight(flight);
            }

            writeFinalTagClose("airline");

        } finally {
            if (dumpFile != null) {
                dumpFile.close();
            }
        }
    }

    private void writeFlight(AbstractFlight f) throws IOException {
        Flight flight = (Flight) f;

        writeTagOpen("flight");
        writeTagWithData("number",flight.getNumber());
        writeTagOpen("source");
        writeTagWithData("id",flight.getSource());
        writeTagWithData( "date", flight.getDepartureDate());
        writeTagWithData("time", flight.getDepartureTime());
        writeTagClose("source");
        writeTagOpen("destination");
        writeTagWithData("id",flight.getDestination());
        writeTagWithData( "date", flight.getArrivalDate());
        writeTagWithData("time", flight.getArrivalTime());
        writeTagClose("destination");
        writeTagClose("flight");


    }

    private void writeHeader() throws IOException {
        writeString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writeNewLine();
    }

    private void writeTagOpenInline(String name) throws IOException {
        writeString("<" + name + ">");
    }

    private void writeTagCloseInline(String name) throws IOException {
        writeString("</" + name + ">");
    }

    private void writeTagOpen(String name) throws IOException
    {
        writeIndent();
        writeTagOpenInline(name);
        writeNewLine();
        openTags++;
    }
    private void writeTagClose(String name) throws IOException
    {
        openTags--;
        writeIndent();
        writeTagCloseInline(name);
        writeNewLine();

    }

    private void writeFinalTagClose(String name) throws IOException
    {
        openTags--;
        writeIndent();
        writeTagCloseInline(name);

    }
    private void writeTagWithData(String tagName, String data) throws IOException
    {
        writeIndent();
        writeTagOpenInline(tagName);
        writeString(data);
        writeTagCloseInline(tagName);
        writeNewLine();
    }

    private void writeTagWithData(String tagName, Integer data) throws IOException
    {
        writeIndent();
        writeTagOpenInline(tagName);
        writeString(valueOf(data));
        writeTagCloseInline(tagName);
        writeNewLine();
    }

    private void writeNewLine() throws IOException
    {
        writeString("\n");
    }

    private void writeIndent() throws IOException
    {
        for(int i = 0; i < openTags; i++)
        {
            writeString("    ");
        }
    }

    private void writeString(String data) throws IOException
    {
        dumpFile.write(data);
    }
}
