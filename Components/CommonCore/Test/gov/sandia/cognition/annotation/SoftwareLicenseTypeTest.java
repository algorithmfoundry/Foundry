/*
 * File:                SoftwareLicenseTypeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.annotation;

import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for SoftwareLicenseTypeTest.
 *
 * @author krdixon
 */
public class SoftwareLicenseTypeTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class SoftwareLicenseTypeTest.
     * @param testName Name of the test.
     */
    public SoftwareLicenseTypeTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class SoftwareLicenseTypeTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        SoftwareLicenseType slt = SoftwareLicenseType.Apache;
        System.out.println( "SLT: " + slt.toString() );

    }

    /**
     * Test of values method, of class SoftwareLicenseType.
     */
    public void testValues()
    {
        System.out.println("values");
        SoftwareLicenseType[] result = SoftwareLicenseType.values();
        assertTrue( result.length > 0 );
    }

    /**
     * Test of valueOf method, of class SoftwareLicenseType.
     */
    public void testValueOf()
    {
        System.out.println("valueOf");
        String name = "Apache";
        SoftwareLicenseType result = SoftwareLicenseType.valueOf(name);
        assertEquals(SoftwareLicenseType.Apache, result);
        
    }

}
