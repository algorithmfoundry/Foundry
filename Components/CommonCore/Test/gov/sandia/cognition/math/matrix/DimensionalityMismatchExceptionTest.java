/*
 * File:                DimensionalityMismatchExceptionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DimensionalityMismatchExceptionTest.
 *
 * @author krdixon
 */
public class DimensionalityMismatchExceptionTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class DimensionalityMismatchExceptionTest.
     * @param testName Name of the test.
     */
    public DimensionalityMismatchExceptionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DimensionalityMismatchExceptionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        try
        {
            throw new DimensionalityMismatchException();
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            throw new DimensionalityMismatchException("Test, test, test");
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            throw new DimensionalityMismatchException(RANDOM.nextInt(), RANDOM.nextInt());
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }



}
