package edu.pdx.cs410J.jgraalum;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class MessagesTest {

  @Test
  public void malformedKeyAndPairReturnsNull() {
    assertThat(Messages.parseKeyValuePair("blah"), nullValue());
  }



}
