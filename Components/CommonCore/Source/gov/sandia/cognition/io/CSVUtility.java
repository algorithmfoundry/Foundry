/*
 * File:                CSVUtility.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 2, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * The <code>CSVUtility</code> class implements some utility functions for
 * dealing with comma-separated value (CSV) file types.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=false,
            comments="Minor spacing changes to while statement. Otherwise, looks fine."
        ),
        
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=true,
            comments="This class should make the comma a parameter (in the splitCommas() method) so that it can be used on all character-delimited parsing schemes.",
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2008-02-18",
                moreChangesNeeded=false,
                comments="Added the split() method"
            )
        )
    }
)
public class CSVUtility
    extends java.lang.Object
{

    /**
     * Returns the next non-empty line from the given BufferedReader as an
     * array of the CSV entries. If there is no more data, null is returned.
     *
     * @param  r The BufferedReader to read from.
     * @return An array of the comma-separated values on the next line or null
     *         if the end of the file has been reached.
     * @throws IOException If there is an exception in reading from the 
     *         BufferedReader.
     */
    public static String[] nextNonEmptyLine(
        BufferedReader r)
        throws IOException
    {
        return nextNonEmptyLine(r, ',');
    }

    /**
     * Returns the next non-empty line from the given BufferedReader as an
     * array of the CSV entries. If there is no more data, null is returned.
     *
     * @param  r The BufferedReader to read from.
     * @param delimiter Delimiter to use.
     * @return An array of the comma-separated values on the next line or null
     *         if the end of the file has been reached.
     * @throws IOException If there is an exception in reading from the
     *         BufferedReader.
     */
    public static String[] nextNonEmptyLine(
        BufferedReader r,
        char delimiter )
        throws IOException
    {

        // Loop over the lines.
        String line = null;

        while ((line = r.readLine()) != null)
        {
            line = line.trim();

            if (line.length() <= 0)
            {
                continue;
            }

            // Split the commas.
            String[] entries = CSVUtility.split(line,delimiter);

            if (entries != null && entries.length > 0)
            {
                // Return the line since it had data on it.
                return entries;
            }
        // else - No data on the line.
        }

        // Nothing found before the end of the reader.
        return null;
    }

    /**
     * Splits the given line into the array of comma-separated values. If the
     * given line is null, then the value returned is null. If there is no
     * data on the line, then an empty array is returned. Otherwise, an array
     * containing at least one value is returned.
     *
     * @param  line The line to split on the commas.
     * @return The array of comma-separated values from the given line.
     */
    public static String[] splitCommas(
        final String line)
    {
        return split(line, ',');
    }

    /**
     * Splits the given line into the array of character-separated values. If 
     * the given line is null, then the value returned is null. If there is no
     * data on the line, then an empty array is returned. Otherwise, an array
     * containing at least one value is returned.
     *
     * @param  line The line to split on the given character.
     * @param  c The character to use to split the line.
     * @return The array of comma-separated values from the given line.
     */
    public static String[] split(
        final String line,
        final char c)
    {
        if (line == null)
        {
            // Error: Bad line.
            return null;
        }

        // Get the length of the line.
        int length = line.length();
        if (length <= 0)
        {
            // The line has no data.
            return new String[0];
        }
        
        // We build a list of the comma-separated values and at the end turn
        // it into an array.
        LinkedList<String> list = new LinkedList<String>();

        // Keep track of where the current value starts and ends.
        int start = 0;
        int end = 0;

        // Loop over all the commas in the string until we get to the last one.
        while ((start < length) &&
            ((end = line.indexOf(c, start)) >= 0))
        {
            // Get this value.
            String entry = line.substring(start, end);
            list.add(entry);
            start = end + 1;
        }

        if (start < length)
        {
            // Add the last value
            String last = line.substring(start);
            list.add(last);
        }
        else
        {
            // The last thing was a comma.
            list.add("");
        }

        // Convert the list to an array.
        return list.toArray(new String[list.size()]);
    }

    /**
     * Reads a CSV file into a list of arrays of string values.
     *
     * @param   fileName The file to read in.
     * @return  A list of arrays of strings that contain the comma-separated
     *      values in the given CSV file.
     * @throws java.io.IOException  If there is an error reading the file.
     */
    public static LinkedList<String[]> readFile(
        final String fileName )
        throws IOException
    {
        return readFile( fileName, ',' );
    }

    /**
     * Reads a CSV file into a list of arrays of string values.
     * 
     * @param   fileName The file to read in.
     * @param delimiter Delimiter to use.
     * @return  A list of arrays of strings that contain the comma-separated
     *      values in the given CSV file.
     * @throws java.io.IOException  If there is an error reading the file.
     */
    public static LinkedList<String[]> readFile(
        final String fileName,
        final char delimiter )
        throws IOException
    {
        final LinkedList<String[]> result = new LinkedList<String[]>();
        final BufferedReader reader = 
            new BufferedReader(new FileReader(fileName));
        
        try
        {
            String[] line = null;
            while ((line = nextNonEmptyLine(reader,delimiter)) != null)
            {
                result.add(line);
            }
        }
        finally
        {
            reader.close();
        }
        
        return result;
    }
}
