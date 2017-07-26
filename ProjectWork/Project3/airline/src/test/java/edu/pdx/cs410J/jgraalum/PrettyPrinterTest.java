package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jason Graalum on 7/15/17.
 */
public class PrettyPrinterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private void dumperTestExpected(String testOutputFilename, String testExceptedFileName, Airline airline) throws IOException {
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        prettyPrinter.setFileName(testOutputFilename);

        prettyPrinter.dump(airline);

        ClassLoader classLoader = getClass().getClassLoader();

        final File expected = new File(classLoader.getResource(testExceptedFileName).getFile());
        //List<String> expectedStrings = FileUtils.readLines(expected);

        final File output = new File(testOutputFilename);
        //List<String> outputStrings = FileUtils.readLines(output);

        assertEquals("The files differ", FileUtils.readFileToString(expected, "utf-8"),FileUtils.readFileToString(output,"utf-8"));
    }

    @Test
    public void testDumperAirlineNoFlights() {

        String testExceptedFileName = "P3_No_Flights_Test.txt";
        String testOutputFilename = "testDumperAirlineNoFlights.out.txt";
        Airline airline = new Airline("Airline with no flights");

        try {
            dumperTestExpected(testOutputFilename, testExceptedFileName, airline);
        } catch (IOException e) {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }

    @Test
    public void testDumperAirlineSingleFlights() throws ParserException {

        String testExceptedFileName = "P3_One_Flight_Test.txt";
        String testOutputFilename = "testDumperAirlineOneFlight.out.txt";
        Airline airline = new Airline("Airline with one flight");
        Flight flight = new Flight("123","SFO","01/01/1901", "12:30", "PM", "LAX","01/01/1901", "2:30", "PM");
        airline.addFlight(flight);

        try {
            dumperTestExpected(testOutputFilename, testExceptedFileName, airline);
        } catch(IOException e)
        {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }

    @Test
    public void testDumperAirlineTwoFlights() throws ParserException {

        String testExceptedFileName = "P3_Two_Flights_Test.txt";
        String testOutputFilename = "testDumperAirlineTwoFlights.out.txt";
        Airline airline = new Airline("Airline with two flights");

        Flight flight2 = new Flight("123","BOI","01/01/1901", "12:30","AM", "PDX","01/02/1901", "1:30", "PM");
        Flight flight1 = new Flight("321","PDX","02/02/1902", "2:00", "PM", "BOI","1/5/2017", "3:30", "AM");
        airline.addFlight(flight1);
        airline.addFlight(flight2);

        try {
            dumperTestExpected(testOutputFilename, testExceptedFileName, airline);
        } catch(IOException e)
        {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }

    @Test
    public void testDumperAirlineTwoFlightsOrder2() throws ParserException {

        String testExceptedFileName = "P3_Two_Flights_Test.txt";
        String testOutputFilename = "testDumperAirlineTwoFlights.out.txt";
        Airline airline = new Airline("Airline with two flights");

        Flight flight1 = new Flight("123","BOI","01/01/1901", "12:30","AM", "PDX","01/02/1901", "1:30", "PM");
        Flight flight2 = new Flight("321","PDX","02/02/1902", "2:00", "PM", "BOI","1/5/2017", "3:30", "AM");
        airline.addFlight(flight1);
        airline.addFlight(flight2);

        try {
            dumperTestExpected(testOutputFilename, testExceptedFileName, airline);
        } catch(IOException e)
        {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }


}

