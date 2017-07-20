package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * An integration test for the {@link Project2} main class.
 */
public class Project2IT extends InvokeMainTestCase {

    private static String tempDirectoryName = "./Project2IT_TempFiles";
    private static File temporaryDirectory;
    @BeforeClass
    public static void setup() {
        temporaryDirectory = new File(tempDirectoryName);
        if(!temporaryDirectory.exists()){
            temporaryDirectory.mkdir();
        }
    }

    /**
     * Invokes the main method of {@link Project2} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain( Project2.class, args );
    }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  public void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getExitCode(), equalTo(3));
    assertThat(result.getTextWrittenToStandardOut(), startsWith("usage"));
  }
    @Test
    public void testReadMeArgument() {
        MainMethodResult result = invokeMain("-README");
        assertThat(result.getTextWrittenToStandardOut(), startsWith("usage:"));
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    public void testCorrectFlightArguments() {
        MainMethodResult result = invokeMain("AirlineName","123","SRC","12/31/1901","12:30","DES","01/01/1902","23:59");
        assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
        assertThat(result.getExitCode(), equalTo(0));
    }

    @Test
    public void testCorrectFlightArgumentsWithPrint() {
        MainMethodResult result = invokeMain("-print","AirlineName","123","SRC","12/31/1901","12:30","DES","01/01/1902","23:45");
        assertThat(result.getTextWrittenToStandardOut(), equalTo("Flight 123 departs SRC at 12/31/1901 12:30 arrives DES at 01/01/1902 23:45"));
        assertThat(result.getExitCode(), equalTo(0));
    }
    @Test
    public void testIllegalOptions() {
        MainMethodResult result = invokeMain("-badOption","AirlineName","123","SRC","12/31/1901","12:30","DES","01/01/1902","23:59");
        assertThat(result.getTextWrittenToStandardOut(), startsWith("Illegal option given:"));
        assertThat(result.getExitCode(), equalTo(-2));
    }

    @Test
    public void testCorrectFlightArgumentsWriteToFile() throws IOException {
        String testInputFileName = "P2_Int_Dump_One_Flight_Test.xml";
        String testOutputFileName = tempDirectoryName + "/" + testInputFileName + ".dump";

        MainMethodResult result = invokeMain("-textFile",testOutputFileName, "Airline with one flight","123","ABC","12/31/1901","12:30","DES","01/01/1902","23:30");

        ClassLoader classLoader = getClass().getClassLoader();
        String testInputFileWithPath = classLoader.getResource(testInputFileName).getPath();

        final File source = new File(classLoader.getResource(testInputFileName).getFile());
        List<String> expectedStrings = FileUtils.readLines(source);

        final File output = new File(testOutputFileName);
        List<String> outputStrings = FileUtils.readLines(output);

        assertThat(expectedStrings, equalTo(outputStrings));
    }

    @Test
    public void testCorrectFlightArgumentsWriteToFileWithPrint() throws IOException {
        String testInputFileName = "P2_Int_Dump_One_Flight_Test.xml";
        String testOutputFileName = tempDirectoryName + "/" + testInputFileName + ".dump";

        MainMethodResult result = invokeMain("-textFile",testOutputFileName, "-print", "Airline with one flight","123","ABC","12/31/1901","12:30","DES","01/01/1902","23:30");

        ClassLoader classLoader = getClass().getClassLoader();
        String testInputFileWithPath = classLoader.getResource(testInputFileName).getPath();

        final File source = new File(classLoader.getResource(testInputFileName).getFile());
        List<String> expectedStrings = FileUtils.readLines(source);

        final File output = new File(testOutputFileName);
        List<String> outputStrings = FileUtils.readLines(output);
        assertThat(result.getTextWrittenToStandardOut(), equalTo("Flight 123 departs ABC at 12/31/1901 12:30 arrives DES at 01/01/1902 23:30"));
        assertThat(expectedStrings, equalTo(outputStrings));
    }
    @Test
    public void testCorrectFlightArgumentsPrintWithWriteToFile() throws IOException {
        String testInputFileName = "P2_Int_Dump_One_Flight_Test.xml";
        String testOutputFileName = tempDirectoryName + "/" + testInputFileName + ".dump";

        MainMethodResult result = invokeMain("-print", "-textFile",testOutputFileName, "Airline with one flight","123","ABC","12/31/1901","12:30","DES","01/01/1902","23:30");

        ClassLoader classLoader = getClass().getClassLoader();
        String testInputFileWithPath = classLoader.getResource(testInputFileName).getPath();

        final File source = new File(classLoader.getResource(testInputFileName).getFile());
        List<String> expectedStrings = FileUtils.readLines(source);

        final File output = new File(testOutputFileName);
        List<String> outputStrings = FileUtils.readLines(output);
        assertThat(result.getTextWrittenToStandardOut(), equalTo("Flight 123 departs ABC at 12/31/1901 12:30 arrives DES at 01/01/1902 23:30"));
        assertThat(expectedStrings, equalTo(outputStrings));
    }

    @Test
    public void testReadMe2ArgumentWithOtherOptions() {
        String testInputFileName = "P2_Int_Dump_One_Flight_Test.xml";
        String testOutputFileName = tempDirectoryName + "/" + testInputFileName + ".dump";

        MainMethodResult result = invokeMain("-print", "-README", "-textFile",testOutputFileName,  "Airline with one flight","123","ABC","12/31/1901","12:30","DES","01/01/1902","23:30");

        assertThat(result.getTextWrittenToStandardOut(), startsWith("usage:"));
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    public void testReadMe3ArgumentWithOtherOptions() {
        String testInputFileName = "P2_Int_Dump_One_Flight_Test.xml";
        String testOutputFileName = tempDirectoryName + "/" + testInputFileName + ".dump";

        MainMethodResult result = invokeMain("-print", "-textFile",testOutputFileName, "-README", "Airline with one flight","123","ABC","12/31/1901","12:30","DES","01/01/1902","23:30");

        assertThat(result.getTextWrittenToStandardOut(), startsWith("usage:"));
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    public void testReadAndReWrite() throws IOException {
        String testInputFileName = "P2_Two_Flights_Test.xml";
        String testExpectedFileName = "P2_Int_Two_Plus_One_Flights_Test.xml";

        ClassLoader classLoader = getClass().getClassLoader();
        String testInputFileWithPath = classLoader.getResource(testInputFileName).getPath();
        String testExpectedFileWithPath = classLoader.getResource(testExpectedFileName).getPath();

        String testOutputFileWithPath = tempDirectoryName + "/" + "P2_Int_Two_Plus_One_Flights_Test.xml" + ".dump";

        copyTestFile(testInputFileWithPath,testOutputFileWithPath);

        MainMethodResult result = invokeMain("-textFile",testOutputFileWithPath, "Airline with two flights","456","ABC","12/31/1901","12:30","DST","01/01/1902","23:30");

        final File expected = new File(classLoader.getResource(testExpectedFileWithPath).getFile());
        List<String> expectedStrings = FileUtils.readLines(expected);

        final File output = new File(testOutputFileWithPath);
        List<String> outputStrings = FileUtils.readLines(output);

        assertThat(outputStrings, equalTo(expectedStrings));
    }



    public static void copyTestFile(String srcFile, String destFile) {

        FileInputStream ins = null;
        FileOutputStream outs = null;
        try {
            File infile =new File(srcFile);
            File outfile =new File(destFile);
            ins = new FileInputStream(infile);
            outs = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = ins.read(buffer)) > 0) {
                outs.write(buffer, 0, length);
            }
            ins.close();
            outs.close();

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

    }

    @AfterClass
    public static void teardown() throws IOException {
        if(temporaryDirectory.exists()){
            FileUtils.deleteDirectory(temporaryDirectory); //apache-commons-io
        }
    }
}