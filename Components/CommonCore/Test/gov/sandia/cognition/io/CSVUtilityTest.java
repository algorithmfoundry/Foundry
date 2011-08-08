/*
 * File:                CSVUtilityTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CSVUtility
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CSVUtilityTest
    extends TestCase
{
    public CSVUtilityTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        CSVUtility csv = new CSVUtility();
        assertNotNull( csv );
    }

    /**
     * Test of nextNonEmptyLine method, of class gov.sandia.isrc.cognition.io.CSVUtility.
     */
    public void testNextNonEmptyLine() 
        throws IOException
    {
        StringReader stringReader = null;
        BufferedReader br = null;   
        String[] entries = null;
        
        // Test the line "a".
        stringReader = new StringReader("a");
        br = new BufferedReader(stringReader);
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertTrue(Arrays.equals(new String[] { "a" }, entries));
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertNull(entries);
        
        // Test a complicated set.
        stringReader = new StringReader("\n\na,b,c\n\na\n");
        br = new BufferedReader(stringReader);
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertTrue(Arrays.equals(new String[] { "a", "b", "c" }, entries));
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertTrue(Arrays.equals(new String[] { "a" }, entries));
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertNull(entries);
        
        
        // Test with no data.
        stringReader = new StringReader("");
        br = new BufferedReader(stringReader);
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertNull(entries);
        
        // Test with just newlines.
        stringReader = new StringReader("\n\n\n");
        br = new BufferedReader(stringReader);
        
        entries = CSVUtility.nextNonEmptyLine(br);
        assertNull(entries);
        
    }

    /**
     * Test of splitCommas method, of class gov.sandia.isrc.cognition.io.CSVUtility.
     */
    public void testSplitCommas()
    {
        String[] result = CSVUtility.splitCommas("");
        assertEquals(0, result.length);
        
        result = CSVUtility.splitCommas("a");
        assertTrue(Arrays.equals(new String[] { "a" }, result));
        
        result = CSVUtility.splitCommas(",");
        assertTrue(Arrays.equals(new String[] { "", "" }, result));
        
        result = CSVUtility.splitCommas("a,");
        assertTrue(Arrays.equals(new String[] { "a", "" }, result));
        
        result = CSVUtility.splitCommas(",a");
        assertTrue(Arrays.equals(new String[] { "", "a" }, result));
        
        result = CSVUtility.splitCommas(",,");
        assertTrue(Arrays.equals(new String[] { "", "", "" }, result));
        
        result = CSVUtility.splitCommas("a,b,c");
        assertTrue(Arrays.equals(new String[] { "a", "b", "c" }, result));
        
        result = CSVUtility.splitCommas(" a , b , c ");
        assertTrue(Arrays.equals(new String[] { " a ", " b ", " c " }, result));
        
        result = CSVUtility.splitCommas(null);
        assertNull(result);
    }
}
