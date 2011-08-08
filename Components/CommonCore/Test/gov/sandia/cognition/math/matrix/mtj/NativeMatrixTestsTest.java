/*
 * File:                NativeMatrixTestsTest.java
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

package gov.sandia.cognition.math.matrix.mtj;

import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for NativeMatrixTestsTest.
 *
 * @author krdixon
 */
public class NativeMatrixTestsTest
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
     * Tests for class NativeMatrixTestsTest.
     * @param testName Name of the test.
     */
    public NativeMatrixTestsTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class NativeMatrixTestsTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        NativeMatrixTests nmt = new NativeMatrixTests();
        assertNotNull( nmt );
    }

    /**
     * Test of isNativeLAPACK method, of class NativeMatrixTests.
     */
    public void testIsNativeLAPACK()
    {
        System.out.println("isNativeLAPACK");
        assertFalse( NativeMatrixTests.isNativeLAPACK() );
    }

    /**
     * Test of isNativeBLAS method, of class NativeMatrixTests.
     */
    public void testIsNativeBLAS()
    {
        System.out.println("isNativeBLAS");
        assertFalse( NativeMatrixTests.isNativeBLAS() );
    }

}
