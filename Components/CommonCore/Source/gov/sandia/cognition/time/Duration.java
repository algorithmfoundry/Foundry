/*
 * File:                Duration.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 11, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.time;

import java.io.Serializable;

/**
 * Represents a duration of time. A duration is a standard span of time that
 * does not depend on any specific date or calendar representation. It has
 * methods for getting the standard time components of the span for standard
 * milliseconds (1ms), seconds(1s=1000ms), minutes (1m=60s), hours (1h=60m),
 * and days (1d=24h). Durations can be either positive or negative.
 *
 * The reason this interface exists is that date/time API designs have advanced
 * much beyond the original Java date/time classes. Java may revise these at
 * some point, but we have a need for interfacing with a lot of time-based data,
 * so we have created this interface. In the future, if Java changes its time
 * APIs, those can either be adapted to this interface or this interface could
 * be deprecated.
 *
 * The design of the interface is based on Joda time, .NET TimeSpan, and JavaFX
 * Duration.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Duration
    extends Serializable, Comparable<Duration>
{

    /**
     * Returns the negative of this duration.
     *
     * @return
     *      The negative of this duration.
     */
    public Duration negative();

    /**
     * Returns the absolute value of this duration.
     *
     * @return
     *      The absolute value of this duration.
     */
    public Duration absoluteValue();

    /**
     * Determines if this duration is equivalent to the given duration.
     *
     * @param   other
     *      The other duration.
     * @return
     *      True if this duration is equal to the given duration.
     */
    public boolean equals(
        final Duration other);

    /**
     * Adds this duration to the given duration and returns the sum.
     *
     * @param   other
     *      The other duration.
     * @return
     *      The sum of this duration and the other duration.
     */
    public Duration plus(
        final Duration other);

    /**
     * Subtracts the given duration from this duration and returns the
     * difference.
     *
     * @param   other
     *      The other duration.
     * @return
     *      The difference of this duration and the other duration.
     */
    public Duration minus(
        final Duration other);

    /**
     * Multiplies this duration by the given scalar value and returns the
     * product.
     *
     * @param   scalar
     *      The scalar value.
     * @return
     *      The product of this duration and the scalar value.
     */
    public Duration times(
        final double scalar);

    /**
     * Divides this duration by the given duration and returns the ratio.
     *
     * @param   other
     *      The other duration.
     * @return
     *      The result of dividing this duration by the given duration.
     */
    public double divide(
        final Duration other);


    /**
     * Divides this duration by the given scalar value and returns the result.
     *
     * @param   scalar
     *      The scalar value.
     * @return
     *      The result of dividing this duration by the given scalar value.
     */
    public Duration divide(
        final double scalar);

    /**
     * Gets the total number of standard milliseconds of this duration.
     *
     * @return
     *      The total number of standard milliseconds.
     */
    public double getTotalMilliseconds();

    /**
     * Gets the total number of standard seconds of this duration.
     *
     * @return
     *      The total number of standard seconds.
     */
    public double getTotalSeconds();

    /**
     * Gets the total number of standard minutes of this duration.
     *
     * @return
     *      The total number of standard minutes.
     */
    public double getTotalMinutes();

    /**
     * Gets the total number of standard hours of this duration.
     *
     * @return
     *      The total number of standard hours.
     */
    public double getTotalHours();

    /**
     * Gets the total number of standard days of this duration.
     *
     * @return
     *      The total number of standard days.
     */
    public double getTotalDays();

    /**
     * Gets the standard milliseconds part of the time. It is in the range
     * of -999 to 999, inclusive.
     *
     * @return
     *      The standard milliseconds part of the time.
     */
    public int getMillisecondsPart();

    /**
     * Gets the standard seconds part of the time. It is in the range
     * of -59 to 59, inclusive.
     *
     * @return
     *      The standard seconds part of the time.
     */
    public int getSecondsPart();

    /**
     * Gets the standard minutes part of the time. It is in the range
     * of -59 to 59, inclusive.
     *
     * @return
     *      The standard minutes part of the time.
     */
    public int getMinutesPart();


    /**
     * Gets the standard hours part of the time. It is in the range
     * of -23 to 23, inclusive.
     *
     * @return
     *      The standard hours part of the time.
     */
    public int getHoursPart();


    /**
     * Gets the standard minutes part of the time.
     *
     * @return
     *      The standard minutes part of the time.
     */
    public long getDaysPart();

}
