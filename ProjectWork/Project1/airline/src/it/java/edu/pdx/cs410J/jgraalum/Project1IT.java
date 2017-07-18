package edu.pdx.cs410J.jgraalum;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * An integration test for the {@link Project1} main class.
 */
public class Project1IT extends InvokeMainTestCase {

    /**
     * Invokes the main method of {@link Project1} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain( Project1.class, args );
    }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  public void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getExitCode(), equalTo(1));
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
        assertThat(result.getTextWrittenToStandardOut(), startsWith("Illegal Option given:"));
        assertThat(result.getExitCode(), equalTo(1));
    }
}