/*
 * File:                CSVParseExceptionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 13, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.io;

import junit.framework.TestCase;

/**
 * Unit tests for CSVParseExceptionTest.
 *
 * @author krdixon
 */
public class CSVParseExceptionTest
    extends TestCase
{

    /**
     * Tests for class CSVParseExceptionTest.
     * @param testName Name of the test.
     */
    public CSVParseExceptionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class CSVParseExceptionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        CSVParseException e = new CSVParseException();
        System.out.println( "String: " + e );
        e = new CSVParseException( "Test!" );
        System.out.println( "String: " + e );
        e = new CSVParseException( new NullPointerException() );
        System.out.println( "String: " + e );
        e = new CSVParseException( "Test!", new NullPointerException() );
        System.out.println( "String: " + e );

    }



}
