/*
 * File:                NumberComparatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 2, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.collection;

import junit.framework.TestCase;

/**
 * JUnit tests for class NumberComparatorTest
 * @author Kevin R. Dixon
 */
public class NumberComparatorTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class NumberComparatorTest
     * @param testName name of this test
     */
    public NumberComparatorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of compare method, of class NumberComparator.
     */
    public void testCompare()
    {
        System.out.println( "compare" );
        NumberComparator instance = new NumberComparator();
        assertEquals( 0, instance.compare( new Integer( 1 ), new Double( 1 ) ) );
        assertEquals( 0, instance.compare( new Double( -1.0 ), new Double( -1.0 ) ) );
        assertEquals( 1, instance.compare( new Double( 1.0 ), new Double( -1 ) ) );
        assertEquals( 1, instance.compare( new Integer( 1 ), new Double( -1.0 ) ) );
        assertEquals( 1, instance.compare( new Double( 1.0 ), new Integer( -1 ) ) );

        assertEquals( -1, instance.compare( new Integer( 10 ), new Double( 100 ) ) );
        assertEquals( -1, instance.compare( new Double( 10 ), new Integer( 100 ) ) );
        assertEquals( -1, instance.compare( new Double( 10 ), new Double( 100 ) ) );
        
    }

}
