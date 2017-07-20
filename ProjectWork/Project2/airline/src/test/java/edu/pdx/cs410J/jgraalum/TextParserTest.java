package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.ParserException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by jasongraalum on 7/15/17.
 */
public class TextParserTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public void parserTestExpected(String testInputFilename) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        String testInputFileWithPath = classLoader.getResource(testInputFilename).getPath();

        TextParser parser = new TextParser(testInputFileWithPath);


        Airline airline;
        try {
            airline = parser.parse();

            TextDumper dumper = new TextDumper();
            dumper.setFileName(testInputFilename + ".dump");
            dumper.dump(airline);

            final File source = new File(classLoader.getResource(testInputFilename).getFile());
            List<String> expectedStrings = FileUtils.readLines(source);

            final File output = new File(testInputFilename + ".dump");
            List<String> outputStrings = FileUtils.readLines(output);

            assertThat(expectedStrings, equalTo(outputStrings));

        } catch (ParserException e) {
            System.out.println("Unable to parse input file: " + testInputFilename);

        }


    }

    @Test
    public void testParserAirlineNoFlights() {

        String testInputFileName = "P2_No_Flights_Test.xml";
        try {
            parserTestExpected(testInputFileName);
        } catch (IOException e) {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }

    @Test
    public void testParserAirlineSingleFlights() {

        String testInputFileName = "P2_One_Flight_Test.xml";
        try {
            parserTestExpected(testInputFileName);
        } catch (IOException e) {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }

    @Test
    public void testDumperAirlineTwoFlights() {

        String testInputFileName = "P2_Two_Flights_Test.xml";
        try {
            parserTestExpected(testInputFileName);
        } catch (IOException e) {
            System.out.println("Exception thrown :" + e);
            fail("Exception thrown");
        }
    }
}
