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
        ArgumentChecker.assertIsPositive(argument, 1);
        ArgumentChecker.assertIsPositive(argument, 2);
        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsPositive(argument, 0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
             assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsPositive(argument, -1);
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
    public void testAssertIsPositive_String_double()
    {
        String argument = "x";
        ArgumentChecker.assertIsPositive(argument, 1.0);
        ArgumentChecker.assertIsPositive(argument, 1.1);
        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsPositive(argument, 0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
             assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsPositive(argument, -1.0);
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
     * Test of assertIsNonNegative method, of class ArgumentChecker.
     */
    public void testAssertIsNonNegative_String_int()
    {
        String argument = "x";
        ArgumentChecker.assertIsNonNegative(argument, 0);
        ArgumentChecker.assertIsNonNegative(argument, 1);
        ArgumentChecker.assertIsNonNegative(argument, 2);
        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsNonNegative(argument, -1);
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
     * Test of assertIsNonNegative method, of class ArgumentChecker.
     */
    public void testAssertIsNonNegative_String_double()
    {
        String argument = "x";
        ArgumentChecker.assertIsNonNegative(argument, 0.0);
        ArgumentChecker.assertIsNonNegative(argument, 1.0);
        ArgumentChecker.assertIsNonNegative(argument, 1.1);
        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsNonNegative(argument, -0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
             assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsNonNegative(argument, -1.0);
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
     * Test of assertIsInRangeInclusive method, of class ArgumentChecker.
     */
    public void testAssertIsInRangeInclusive()
    {
        String argument = "x";
        ArgumentChecker.assertIsInRangeInclusive(argument, 0.0, 0.0, 0.0);
        ArgumentChecker.assertIsInRangeInclusive(argument, 1.1, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeInclusive(argument, 2.1, 1.1, 3.4);
        ArgumentChecker.assertIsInRangeInclusive(argument, 3.4, 1.1, 3.4);

        boolean exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsInRangeInclusive(argument, 0.1, 1.1, 3.4);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
             assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            ArgumentChecker.assertIsInRangeInclusive(argument, 3.5, 1.1, 3.4);
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
