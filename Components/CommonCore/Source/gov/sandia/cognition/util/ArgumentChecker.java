/*
 * File:                ArgumentChecker.java
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

/**
 * A utility class for checking arguments to a function. If the constraint is
 * violated, then an IllegalArgumentException is thrown.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class ArgumentChecker
    extends Object
{

    /**
     * Asserts that the given value is not null. If the assertion is violated,
     * an IllegalArgumentException is thrown.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     */
    public static void assertIsNotNull(
        final String argument,
        final Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(argument + " cannot be null.");
        }
    }

    /**
     * Asserts the given value is positive (> 0). If the assertion is violated,
     * an IllegalArgumentException is thrown.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     */
    public static void assertIsPositive(
        final String argument,
        final int value)
    {
        if (!(value > 0))
        {
            throw new IllegalArgumentException(argument + " must be positive "
                + "(was " + value + ").");
        }
    }

    /**
     * Asserts the given value is positive (> 0.0). If the assertion is
     * violated, an IllegalArgumentException is thrown. NaNs fail the assertion.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     */
    public static void assertIsPositive(
        final String argument,
        final double value)
    {
        if (!(value > 0.0))
        {
            throw new IllegalArgumentException(argument + " must be positive "
                + "(was " + value + ").");
        }
    }

    /**
     * Asserts that the given argument is non-negative (>=0.0). If the assertion
     * is violated, an IllegalArgumentException is thrown.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     */
    public static void assertIsNonNegative(
        final String argument,
        final int value)
    {
        if (!(value >= 0))
        {
            throw new IllegalArgumentException(argument + " cannot be negative "
                + "(was " + value + ").");
        }
    }

    /**
     * Asserts that the given argument is non-negative (>=0.0). If the assertion
     * is violated, an IllegalArgumentException is thrown. NaNs fail the
     * assertion.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     */
    public static void assertIsNonNegative(
        final String argument,
        final double value)
    {
        if (!(value >= 0.0))
        {
            throw new IllegalArgumentException(argument + " cannot be negative "
                + "(was " + value + ").");
        }
    }


    /**
     * Asserts that the given argument is in the given range, inclusive.
     * If the assertion is violated, an IllegalArgumentException is thrown.
     * NaNs are treated as outside of the range.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     * @param   lowerBound
     *      The lower bound of the value (inclusive).
     * @param   upperBound
     *      The upper bound of the value (inclusive).
     */
    public static void assertIsInRangeInclusive(
        final String argument,
        final double value,
        final double lowerBound,
        final double upperBound)
    {
        if (!(value >= lowerBound && value <= upperBound))
        {
            throw new IllegalArgumentException(
                "" + argument + " must be between "
                + lowerBound + " and " + upperBound + " (was " + value + ").");
        }
    }

    /**
     * Asserts that the given argument is in the given range, exclusive.
     * If the assertion is violated, an IllegalArgumentException is thrown.
     * NaNs are treated as outside of the range.
     *
     * @param   argument
     *      The name of the argument.
     * @param   value
     *      The value of the argument.
     * @param   lowerBound
     *      The lower bound of the value (exclusive).
     * @param   upperBound
     *      The upper bound of the value (exclusive).
     */
    public static void assertIsInRangeExclusive(
        final String argument,
        final double value,
        final double lowerBound,
        final double upperBound)
    {
        if (!(value > lowerBound && value < upperBound))
        {
            throw new IllegalArgumentException(
                "" + argument + " must be between "
                + lowerBound + " and " + upperBound + " (was " + value + ").");
        }
    }

}
