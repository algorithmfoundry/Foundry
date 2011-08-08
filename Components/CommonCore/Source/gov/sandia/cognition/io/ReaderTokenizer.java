/*
 * File:                ReaderTokenizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Reads each line from a Reader, and returns each line as a List of Strings
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=true,
    comments={
        "Should this class be merged with CSVUtility?",
        "To a large extend, this class seems like a generalization of CSV.",
        "Please review."
    },
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2008-02-18",
        moreChangesNeeded=false,
        comments="No, its different enough that it could exist on its own."
    )
)
public class ReaderTokenizer
{

    /**
     * BufferedReader from which we will read lines
     */
    private BufferedReader bufferedReader;

    /** status of the ReaderTokenizer */
    private boolean valid;

    /** Number of tokens read in the last call to readNextLine() */
    private int lastTokenNum;

    /**
     * Creates a new instance of ReaderTokenizer
     * @param reader 
     * Input stream from which to parse lines
     */
    public ReaderTokenizer(
        Reader reader )
    {
        if (reader != null)
        {
            this.setBufferedReader( new BufferedReader( reader ) );
            this.setValid( true );
        }
        else
        {
            this.setBufferedReader( null );
            this.setValid( false );
        }

        this.setLastTokenNum( 0 );
    }

    /**
     * Returns the status of the ReaderTokenizer
     * @return true if valid, false otherwise
     */
    public boolean isValid()
    {
        return this.valid;
    }

    /**
     * Returns an ArrayList with each of the tokens as an entry in the array
     * 
     * @param data 
     * String to tokenize 
     * @return ArrayList where the ith entry is the ith token on the line
     */
    public static ArrayList<String> tokenizeString(
        String data )
    {
        return ReaderTokenizer.tokenizeString( data, 1 );
    }

    /**
     * Returns an ArrayList with each of the tokens as an entry in the array
     * @param data
     * String to tokenize 
     * @param expectedTokenNum 
     * Expected number of tokens on the line, may be greater than or less than
     * actual amount, but algorithm will be faster if the number is correct.
     * Throws run-time exception if less than zero.
     * @return ArrayList where the ith entry is the ith token on the line
     */
    public static ArrayList<String> tokenizeString(
        String data,
        int expectedTokenNum )
    {

        ArrayList<String> tokens = new ArrayList<String>( expectedTokenNum );

        StringTokenizer parser = new StringTokenizer( data );
        while (parser.hasMoreElements())
        {
            tokens.add( parser.nextToken() );
        }

        return tokens;
    }

    /**
     * Reads the next line of the Reader, returning each token on the line as
     * an entry in an ArrayList
     * @throws java.io.IOException if the read fails
     * @return ArrayList where each element is a token on the line
     */
    public ArrayList<String> readNextLine()
        throws IOException
    {

        ArrayList<String> tokens;

        String nextLine = this.bufferedReader.readLine();
        if (nextLine == null)
        {
            this.setValid( false );
            tokens = null;
        }
        else
        {
            tokens = ReaderTokenizer.tokenizeString(
                nextLine, this.getLastTokenNum() );
            this.setLastTokenNum( tokens.size() );
        }

        return tokens;

    }

    /**
     * Setter for valid
     * @param valid status of the ReaderTokenizer
     */
    protected void setValid(
        boolean valid )
    {
        this.valid = valid;
    }

    /**
     * Getter for lastTokenNum
     * @return Number of tokens read in the last call to readNextLine()
     */
    protected int getLastTokenNum()
    {
        return this.lastTokenNum;
    }

    /**
     * Setter for lastTokenNum
     * @param lastTokenNum
     * Number of tokens read in the last call to readNextLine()
     */
    protected void setLastTokenNum(
        int lastTokenNum )
    {
        this.lastTokenNum = lastTokenNum;
    }

    /**
     * Getter for bufferedReader
     * @return 
     * BufferedReader from which we will read lines
     */
    public BufferedReader getBufferedReader()
    {
        return this.bufferedReader;
    }

    /**
     * Setter for bufferedReader
     * @param bufferedReader 
     * BufferedReader from which we will read lines
     */
    public void setBufferedReader(
        BufferedReader bufferedReader )
    {
        this.bufferedReader = bufferedReader;
    }

}
