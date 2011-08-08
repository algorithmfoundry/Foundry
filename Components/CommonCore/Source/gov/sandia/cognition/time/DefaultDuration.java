/*
 * File:                DefaultDuration.java
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

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.text.DecimalFormat;

/**
 * A default implementation of the {@code Duration} interface. It is implemented
 * storing milliseconds as a double value.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultDuration
    extends AbstractCloneableSerializable
    implements Duration
{
    /** There are {@value} milliseconds per second. */
    public static final int MILLISECONDS_PER_SECOND = 1000;

    /** There are {@value} seconds per minute. */
    public static final int SECONDS_PER_MINUTE = 60;

    /** There are {@value} minutes per hour. */
    public static final int MINUTES_PER_HOUR = 60;

    /** There are {@value} hours per day. */
    public static final int HOURS_PER_DAY = 24;

    /** There are {@value} milliseconds per minute. */
    public static final int MILLISECONDS_PER_MINUTE =
        SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

    /** There are {@value} milliseconds per hour. */
    public static final int MILLISECONDS_PER_HOUR =
        MINUTES_PER_HOUR * MILLISECONDS_PER_MINUTE;

    /** There are {@value} milliseoncds per day. */
    public static final int MILLISECONDS_PER_DAY =
        HOURS_PER_DAY * MILLISECONDS_PER_HOUR;

    /** A zero duration. */
    public static final DefaultDuration ZERO = new DefaultDuration(0.0);

    /** A millisecond in duration. */
    public static final DefaultDuration MILLISECOND =
        new DefaultDuration(1.0);

    /** A second in duration. */
    public static final DefaultDuration SECOND =
        new DefaultDuration(MILLISECONDS_PER_SECOND);

    /** A minute in duration. */
    public static final DefaultDuration MINUTE =
        new DefaultDuration(MILLISECONDS_PER_MINUTE);

    /** An hour in duration. */
    public static final DefaultDuration HOUR =
        new DefaultDuration(MILLISECONDS_PER_HOUR);

    /** A day in duration. */
    public static final DefaultDuration DAY =
        new DefaultDuration(MILLISECONDS_PER_DAY);

    /** The minimum value of a duration. */
    public static final DefaultDuration MIN_VALUE =
        new DefaultDuration(Long.MIN_VALUE);

    /** The maximum value of a duration. */
    public static final DefaultDuration MAX_VALUE =
        new DefaultDuration(Long.MAX_VALUE);

    /** Formatting used for the toString method that creates numbers of two
     *  digits. */
    private static final DecimalFormat TWO_DIGIT_FORMAT =
        new DecimalFormat("00");


    /** Formatting used for the toString method that creates numbers of three
     *  digits. */
    public static final DecimalFormat THREE_DIGIT_FORMAT =
        new DecimalFormat("000");

    /** The default duration keeps track of milliseconds. */
    private double milliseconds;

    /**
     * Creates a new {@code DefaultDuration} representing the given number of
     * milliseconds. To create instances of this class, the factory methods
     * (from*) should be used instead of this constructor, to avoid confusion
     * with the types of units used to construct the class.
     *
     * @param   milliseconds
     *      The duration in milliseconds.
     */
    protected DefaultDuration(
        final double milliseconds)
    {
        super();

        this.milliseconds = milliseconds;
    }

    /**
     * Creates a new {@code DefaultDuration} from the given number of
     * milliseconds.
     *
     * @param   milliseconds
     *      The number of milliseconds.
     * @return
     *      The duration representing that number of milliseconds, rounded down
     *      to the nearest millisecond.
     */
    public static DefaultDuration fromMilliseconds(
        final double milliseconds)
    {
        return new DefaultDuration(milliseconds);
    }

    /**
     * Creates a new {@code DefaultDuration} from the given number of
     * seconds.
     *
     * @param   seconds
     *      The number of seconds.
     * @return
     *      The duration representing that number of seconds, rounded down to
     *      the nearest millisecond.
     */
    public static DefaultDuration fromSeconds(
        final double seconds)
    {
        return new DefaultDuration(seconds * MILLISECONDS_PER_SECOND);
    }

    /**
     * Creates a new {@code DefaultDuration} from the given number of
     * minutes.
     *
     * @param   minutes
     *      The number of minutes.
     * @return
     *      The duration representing that number of minutes, rounded down to
     *      the nearest millisecond.
     */
    public static DefaultDuration fromMinutes(
        final double minutes)
    {
        return new DefaultDuration(minutes * MILLISECONDS_PER_MINUTE);
    }

    /**
     * Creates a new {@code DefaultDuration} from the given number of
     * hours.
     *
     * @param   hours
     *      The number of hours.
     * @return
     *      The duration representing that number of hours, rounded down to the
     *      nearest millisecond.
     */
    public static DefaultDuration fromHours(
        final double hours)
    {
        return new DefaultDuration(hours * MILLISECONDS_PER_HOUR);
    }

    /**
     * Creates a new {@code DefaultDuration} from the given number of
     * days.
     *
     * @param   days
     *      The number of days.
     * @return
     *      The duration representing that number of days, rounded down to the
     *      nearest millisecond.
     */
    public static DefaultDuration fromDays(
        final double days)
    {
        return new DefaultDuration(days * MILLISECONDS_PER_DAY);
    }
    
    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof Duration && this.equals((Duration) other);
    }

    public boolean equals(
        final Duration other)
    {
        return other != null && this.milliseconds == other.getTotalMilliseconds();
    }

    @Override
    public int hashCode()
    {
        final long bits = Double.doubleToLongBits(this.milliseconds);
        
        return (int) (bits ^ (bits >>> 32));
    }

    public int compareTo(
        final Duration other)
    {
        return Double.compare(this.milliseconds, other.getTotalMilliseconds());
    }

    public Duration negative()
    {
        return new DefaultDuration(-this.milliseconds);
    }

    public Duration absoluteValue()
    {
        return new DefaultDuration(Math.abs(this.milliseconds));
    }
    
    public Duration plus(
        final Duration other)
    {
        return new DefaultDuration(this.milliseconds + other.getTotalMilliseconds());
    }

    public Duration minus(
        final Duration other)
    {
        return new DefaultDuration(this.milliseconds - other.getTotalMilliseconds());
    }

    public Duration times(
        final double scalar)
    {
        return new DefaultDuration(this.milliseconds * scalar);
    }

    public double divide(
        final Duration other)
    {
        return this.milliseconds / other.getTotalMilliseconds();
    }

    public Duration divide(
        final double scalar)
    {
        return new DefaultDuration(this.milliseconds / scalar);
    }

    public double getTotalMilliseconds()
    {
        return this.milliseconds;
    }

    public double getTotalSeconds()
    {
        return (this.milliseconds / MILLISECONDS_PER_SECOND);
    }

    public double getTotalMinutes()
    {
        return (this.milliseconds / MILLISECONDS_PER_MINUTE);
    }

    public double getTotalHours()
    {
        return (this.milliseconds / MILLISECONDS_PER_HOUR);
    }

    public double getTotalDays()
    {
        return (this.milliseconds / MILLISECONDS_PER_DAY);
    }

    public int getMillisecondsPart()
    {
        return (int) (this.milliseconds % MILLISECONDS_PER_SECOND);
    }

    public int getSecondsPart()
    {
        return (int) ((this.milliseconds / MILLISECONDS_PER_SECOND) % SECONDS_PER_MINUTE);
    }

    public int getMinutesPart()
    {
        return (int) ((this.milliseconds / MILLISECONDS_PER_MINUTE) % MINUTES_PER_HOUR);
    }

    public int getHoursPart()
    {
        return (int) ((this.milliseconds / MILLISECONDS_PER_HOUR) % HOURS_PER_DAY);
    }

    public long getDaysPart()
    {
        return (long) (this.milliseconds / MILLISECONDS_PER_DAY);
    }

    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();

        if (this.milliseconds < 0)
        {
            result.append("-");
        }

        final long daysPart = this.getDaysPart();
        if (daysPart != 0)
        {
            result.append(Math.abs(daysPart));
            result.append(".");
        }

        result.append(TWO_DIGIT_FORMAT.format(Math.abs(this.getHoursPart())));
        result.append(":");
        result.append(TWO_DIGIT_FORMAT.format(Math.abs(this.getMinutesPart())));
        result.append(":");
        result.append(TWO_DIGIT_FORMAT.format(Math.abs(this.getSecondsPart())));
                
        final int millisecondsPart = this.getMillisecondsPart();
        if (millisecondsPart != 0)
        {
            result.append(".");
            result.append(THREE_DIGIT_FORMAT.format(Math.abs(millisecondsPart)));
        }

        return result.toString();
    }

}
