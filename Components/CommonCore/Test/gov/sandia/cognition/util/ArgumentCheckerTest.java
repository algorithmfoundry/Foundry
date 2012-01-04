/*
 * File:                ArgumentCheckerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.util;

import junit.framework.TestCase;

/**
 * Unit tests for class ArgumentChecker.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class ArgumentCheckerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ArgumentCheckerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of assertIsNotNull method, of class ArgumentChecker.
     */
    public void testAssertIsNotNull()
    {
        String argument = "x";
        ArgumentChecker.assertIsNotNull(argument, new Object());
        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsNotNull(argument, null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
             assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of assertIsPositive method, of class ArgumentChecker.
     */
    public void testAssertIsPositive_String_int()
    {
        String argument = "x";
        int[] goodValues = {1, 2, 3, 5, 12, 103, 1000, Integer.MAX_VALUE};
        for (int value : goodValues)
        {
            ArgumentChecker.assertIsPositive(argument, value);
        }

        int[] badValues = {0, -1, -2, -3, -5, -12, -103, -100, Integer.MIN_VALUE};
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsPositive(argument, badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue("Should have been an exception: " + badValue,
                     exceptionThrown);
            }
        }
    }

    /**
     * Test of assertIsPositive method, of class ArgumentChecker.
     */
    public void testAssertIsPositive_String_double()
    {
        String argument = "x";
        double[] goodValues = {0.1, 1.0, 1.1, 2.3, 3.4, 12.13, 103, 1000,
            Math.PI, Math.E, Double.MIN_VALUE, Double.MIN_NORMAL,
            Double.POSITIVE_INFINITY};
        for (double value : goodValues)
        {
            ArgumentChecker.assertIsPositive(argument, value);
        }

        double[] badValues = {0.0, -0.0, -0.1, -1.0, -1.1, -2.3, -3.4, -12.13, -103, -1000,
            -Math.PI, -Math.E, -Double.MIN_VALUE, -Double.MIN_NORMAL,
            Double.NEGATIVE_INFINITY, Double.NaN};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsPositive(argument, badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue("Should have been an exception: " + badValue,
                     exceptionThrown);
            }
        }
    }

    /**
     * Test of assertIsNonNegative method, of class ArgumentChecker.
     */
    public void testAssertIsNonNegative_String_int()
    {
        String argument = "x";
        int[] goodValues = {0, 1, 2, 3, 5, 12, 103, 1000, Integer.MAX_VALUE};
        for (int value : goodValues)
        {
            ArgumentChecker.assertIsNonNegative(argument, value);
        }

        int[] badValues = {-1, -2, -3, -5, -12, -103, -100, Integer.MIN_VALUE};
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsNonNegative(argument, badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue("Should have been an exception: " + badValue,
                     exceptionThrown);
            }
        }
    }

    /**
     * Test of assertIsNonNegative method, of class ArgumentChecker.
     */
    public void testAssertIsNonNegative_String_double()
    {
        String argument = "x";
        double[] goodValues = {0.0, -0.0, 0.1, 1.0, 1.1, 2.3, 3.4, 12.13, 103, 1000,
            Math.PI, Math.E, Double.MIN_VALUE, Double.MIN_NORMAL,
            Double.POSITIVE_INFINITY};
        for (double value : goodValues)
        {
            ArgumentChecker.assertIsNonNegative(argument, value);
        }

        double[] badValues = {-0.1, -1.0, -1.1, -2.3, -3.4, -12.13, -103, -1000,
            -Math.PI, -Math.E, -Double.MIN_VALUE, -Double.MIN_NORMAL,
            Double.NEGATIVE_INFINITY, Double.NaN};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsNonNegative(argument, badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue("Should have been an exception: " + badValue,
                     exceptionThrown);
            }
        }
    }

    /**
     * Test of assertIsInRangeInclusive method, of class ArgumentChecker.
     */
    public void testAssertIsInRangeInclusive()
    {
        String argument = "x";
        ArgumentChecker.assertIsInRangeInclusive(argument, 0.0, 0.0, 0.0);
        ArgumentChecker.assertIsInRangeInclusive(argument, 1.1, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeInclusive(argument, 2.1, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeInclusive(argument, 3.4, 1.1, 3.4);

        double[] badValues = { -0.1, 0.0, 1.0, 1.09, 3.41, 3.5, 10.0,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsInRangeInclusive(argument, badValue, 1.1, 3.4);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue(exceptionThrown);
            }
        }
    }

    /**
     * Test of assertIsInRangeExclusive method, of class ArgumentChecker.
     */
    public void testAssertIsInRangeExclusive()
    {
        String argument = "x";
        ArgumentChecker.assertIsInRangeExclusive(argument, 1.2, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeExclusive(argument, 2.1, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeExclusive(argument, 3.1, 1.1, 3.4);

        double[] badValues = { -0.1, 0.0, 1.0, 1.1, 3.4, 3.5, 10.0,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN};
        for (double badValue : badValues)
            {
            boolean exceptionThrown = false;
            try
            {
                ArgumentChecker.assertIsInRangeExclusive(argument, badValue, 1.1, 3.4);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                 assertTrue(exceptionThrown);
            }
        }
    }

}
