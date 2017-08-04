package edu.pdx.cs410J.jgraalum;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{



    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }


    public static String allMappingsDeleted() {
        return "All mappings have been deleted";
    }

    public static Map.Entry<String, String> parseKeyValuePair(String content) {
        Pattern pattern = Pattern.compile("\\s*(.*) -> (.*)");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return null;
        }

        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return matcher.group(1);
            }

            @Override
            public String getValue() {
                String value = matcher.group(2);
                if ("null".equals(value)) {
                    value = null;
                }
                return value;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException("This method is not implemented yet");
            }
        };
    }

    public static String airlineNotFound(String airlineName) {
        return "Data for airline " + airlineName + " not found.";
    }

    public static String noFlightsForAirline(String airlineName, String source, String destination) {
        return "No flights found for airline " + airlineName + " between " + source + " and " + destination;
    }

    public static String noFlightsForAirline(String airlineName) {
        return "No flights found for airline " + airlineName;
    }

    public static String invalidAirportCode(String code) {
        return code + " is not a valid airport code";
    }

    public static String invalidDateFormat(String dateString) {
        return dateString + " does not follow required the MM/DD/YYYY HH:MM AM/PM format";
    }
}
