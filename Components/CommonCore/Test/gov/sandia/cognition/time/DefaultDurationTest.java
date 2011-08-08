/*
 * File:                DefaultDurationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 09, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.time;

import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class DefaultDuration.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultDurationTest
    extends TestCase
{
    /** Default value of epsilon for equality tests with doubles. */
    public static final double EPSILON = 1e-10;

    /** The random number generator to use in the tests. */
    private Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultDurationTest(
        String testName)
    {
        super(testName);
        this.random = new Random(1);
    }

    /**
     * Test of fromMilliseconds method, of class DefaultDuration.
     */
    public void testFromMilliseconds()
    {
        double milliseconds = 0.0;
        DefaultDuration result = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, result.getTotalMilliseconds());

        milliseconds = 1.0;
        result = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, result.getTotalMilliseconds());

        milliseconds = 1234.5;
        result = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, result.getTotalMilliseconds());

        milliseconds = random.nextDouble();
        result = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, result.getTotalMilliseconds());

        milliseconds = -random.nextDouble();
        result = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, result.getTotalMilliseconds());
    }

    /**
     * Test of fromSeconds method, of class DefaultDuration.
     */
    public void testFromSeconds()
    {
        double seconds = 0.0;
        DefaultDuration result = DefaultDuration.fromSeconds(seconds);
        assertEquals(1000 * seconds, result.getTotalMilliseconds());
        assertEquals(seconds, result.getTotalSeconds(), 0.0);

        seconds = 1.0;
        result = DefaultDuration.fromSeconds(seconds);
        assertEquals(1000 * seconds, result.getTotalMilliseconds());
        assertEquals(seconds, result.getTotalSeconds(), 0.0);

        seconds = 1234.5;
        result = DefaultDuration.fromSeconds(seconds);
        assertEquals(1000 * seconds, result.getTotalMilliseconds());
        assertEquals(seconds, result.getTotalSeconds(), EPSILON);

        seconds = random.nextDouble();
        result = DefaultDuration.fromSeconds(seconds);
        assertEquals(1000 * seconds, result.getTotalMilliseconds());
        assertEquals(seconds, result.getTotalSeconds(), EPSILON);
        
        seconds = -random.nextDouble();
        result = DefaultDuration.fromSeconds(seconds);
        assertEquals(1000 * seconds, result.getTotalMilliseconds());
        assertEquals(seconds, result.getTotalSeconds(), EPSILON);
    }

    /**
     * Test of fromMinutes method, of class DefaultDuration.
     */
    public void testFromMinutes()
    {
        double minutes = 0.0;
        DefaultDuration result = DefaultDuration.fromMinutes(minutes);
        assertEquals(60 * 1000 * minutes, result.getTotalMilliseconds());
        assertEquals(minutes, result.getTotalMinutes(), 0.0);

        minutes = 1.0;
        result = DefaultDuration.fromMinutes(minutes);
        assertEquals(60 * 1000 * minutes, result.getTotalMilliseconds());
        assertEquals(minutes, result.getTotalMinutes(), 0.0);

        minutes = 1234.5;
        result = DefaultDuration.fromMinutes(minutes);
        assertEquals(60 * 1000 * minutes, result.getTotalMilliseconds());
        assertEquals(minutes, result.getTotalMinutes(), EPSILON);

        minutes = random.nextDouble();
        result = DefaultDuration.fromMinutes(minutes);
        assertEquals(60 * 1000 * minutes, result.getTotalMilliseconds());
        assertEquals(minutes, result.getTotalMinutes(), EPSILON);
        
        minutes = -random.nextDouble();
        result = DefaultDuration.fromMinutes(minutes);
        assertEquals(60 * 1000 * minutes, result.getTotalMilliseconds());
        assertEquals(minutes, result.getTotalMinutes(), EPSILON);
    }

    /**
     * Test of fromHours method, of class DefaultDuration.
     */
    public void testFromHours()
    {
        double hours = 0.0;
        DefaultDuration result = DefaultDuration.fromHours(hours);
        assertEquals(60 * 60 * 1000 * hours, result.getTotalMilliseconds());
        assertEquals(hours, result.getTotalHours(), 0.0);

        hours = 1.0;
        result = DefaultDuration.fromHours(hours);
        assertEquals(60 * 60 * 1000 * hours, result.getTotalMilliseconds());
        assertEquals(hours, result.getTotalHours(), 0.0);

        hours = 1234.5;
        result = DefaultDuration.fromHours(hours);
        assertEquals(60 * 60 * 1000 * hours, result.getTotalMilliseconds());
        assertEquals(hours, result.getTotalHours(), EPSILON);

        hours = random.nextDouble();
        result = DefaultDuration.fromHours(hours);
        assertEquals(60 * 60 * 1000 * hours, result.getTotalMilliseconds());
        assertEquals(hours, result.getTotalHours(), EPSILON);

        hours = -random.nextDouble();
        result = DefaultDuration.fromHours(hours);
        assertEquals(60 * 60 * 1000 * hours, result.getTotalMilliseconds());
        assertEquals(hours, result.getTotalHours(), EPSILON);
    }

    /**
     * Test of fromDays method, of class DefaultDuration.
     */
    public void testFromDays()
    {
        double days = 0.0;
        DefaultDuration result = DefaultDuration.fromDays(days);
        assertEquals(24 * 60 * 60 * 1000 * days, result.getTotalMilliseconds());
        assertEquals(days, result.getTotalDays(), 0.0);

        days = 1.0;
        result = DefaultDuration.fromDays(days);
        assertEquals(24 * 60 * 60 * 1000 * days, result.getTotalMilliseconds());
        assertEquals(days, result.getTotalDays(), 0.0);

        days = 1234.5;
        result = DefaultDuration.fromDays(days);
        assertEquals(24 * 60 * 60 * 1000 * days, result.getTotalMilliseconds());
        assertEquals(days, result.getTotalDays(), EPSILON);

        days = random.nextDouble();
        result = DefaultDuration.fromDays(days);
        assertEquals(24 * 60 * 60 * 1000 * days, result.getTotalMilliseconds());
        assertEquals(days, result.getTotalDays(), EPSILON);

        days = -random.nextDouble();
        result = DefaultDuration.fromDays(days);
        assertEquals(24 * 60 * 60 * 1000 * days, result.getTotalMilliseconds());
        assertEquals(days, result.getTotalDays(), EPSILON);
    }

    /**
     * Test of equals method, of class DefaultDuration.
     */
    public void testEquals()
    {
        DefaultDuration zero = DefaultDuration.fromMilliseconds(0.0);
        DefaultDuration day = DefaultDuration.fromDays(1.0);
        DefaultDuration randomHours = DefaultDuration.fromHours(random.nextDouble());

        assertTrue(zero.equals(zero));
        assertTrue(zero.equals(DefaultDuration.fromMilliseconds(0.0)));
        assertTrue(zero.equals(zero.negative()));
        assertTrue(zero.equals(zero.negative().negative()));
        assertFalse(zero.equals(null));
        assertTrue(zero.equals((Object) DefaultDuration.ZERO));
        assertFalse(zero.equals((Object) day));
        assertFalse(zero.equals(randomHours));

        assertTrue(day.equals(day));
        assertTrue(day.equals(DefaultDuration.fromDays(1.0)));
        assertFalse(day.equals(day.negative()));
        assertTrue(day.equals(day.negative().negative()));
        assertFalse(day.equals(null));
        assertTrue(day.equals(DefaultDuration.DAY));
        assertFalse(day.equals(zero));
        assertFalse(day.equals(randomHours));


        assertTrue(randomHours.equals(randomHours));
        assertTrue(randomHours.equals(DefaultDuration.fromHours(randomHours.getTotalHours())));
        assertFalse(randomHours.equals(randomHours.negative()));
        assertTrue(randomHours.equals(randomHours.negative().negative()));
        assertFalse(randomHours.equals(null));
        assertFalse(randomHours.equals(zero));
        assertFalse(randomHours.equals(day));
    }

    /**
     * Test of hashCode method, of class DefaultDuration.
     */
    public void testHashCode()
    {
        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        int hashCode = instance.hashCode();
        assertEquals(hashCode, instance.hashCode());
        assertEquals(hashCode, DefaultDuration.fromDays(days).hashCode());
        assertFalse(hashCode == DefaultDuration.ZERO.hashCode());
    }

    /**
     * Test of compareTo method, of class DefaultDuration.
     */
    public void testCompareTo()
    {
        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        assertEquals(0, instance.compareTo(instance));
        assertEquals(0, instance.compareTo(DefaultDuration.fromDays(days)));

        assertTrue(DefaultDuration.MILLISECOND.compareTo(DefaultDuration.MINUTE) < 0);
        assertTrue(DefaultDuration.MINUTE.compareTo(DefaultDuration.MILLISECOND) > 0);
    }

    /**
     * Test of negative method, of class DefaultDuration.
     */
    public void testNegative()
    {
        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        assertEquals(days, instance.getTotalDays(), EPSILON);
        assertEquals(-days, instance.negative().getTotalDays(), EPSILON);
        assertEquals(days, instance.negative().negative().getTotalDays(), EPSILON);
    }

    /**
     * Test of absoluteValue method, of class DefaultDuration.
     */
    public void testAbsoluteValue()
    {
        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        assertEquals(days, instance.absoluteValue().getTotalDays(), EPSILON);
        assertEquals(days, instance.negative().absoluteValue().getTotalDays(), EPSILON);
        assertEquals(days, instance.negative().negative().absoluteValue().getTotalDays(), EPSILON);

        instance = DefaultDuration.fromDays(-days);
        assertEquals(days, instance.absoluteValue().getTotalDays(), EPSILON);
        assertEquals(days, instance.negative().absoluteValue().getTotalDays(), EPSILON);
        assertEquals(days, instance.negative().negative().absoluteValue().getTotalDays(), EPSILON);
    }

    /**
     * Test of plus method, of class DefaultDuration.
     */
    public void testPlus()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, instance.plus(DefaultDuration.ZERO).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds + 1.0, instance.plus(DefaultDuration.MILLISECOND).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds + 1000.0, instance.plus(DefaultDuration.SECOND).getTotalMilliseconds(), 0.0);
        assertEquals(2.0 * milliseconds, instance.plus(instance).getTotalMilliseconds(), 0.0);
        assertEquals(3.0 * milliseconds, instance.plus(instance).plus(instance).getTotalMilliseconds(), 0.0);
    }

    /**
     * Test of minus method, of class DefaultDuration.
     */
    public void testMinus()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, instance.minus(DefaultDuration.ZERO).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds - 1.0, instance.minus(DefaultDuration.MILLISECOND).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds - 1000.0, instance.minus(DefaultDuration.SECOND).getTotalMilliseconds(), 0.0);
        assertEquals(0.0, instance.minus(instance).getTotalMilliseconds(), 0.0);
        assertEquals(-milliseconds, instance.minus(instance).minus(instance).getTotalMilliseconds(), 0.0);
    }

    /**
     * Test of times method, of class DefaultDuration.
     */
    public void testTimes()
    {
        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        double milliseconds = instance.getTotalMilliseconds();
        double scalar = random.nextDouble();
        assertEquals(scalar * milliseconds,
            instance.times(scalar).getTotalMilliseconds(), 0.0);
        assertEquals(0.0, instance.times(0.0).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds, instance.times(1.0).getTotalMilliseconds(), 0.0);
        assertEquals(-milliseconds, instance.times(-1.0).getTotalMilliseconds(), 0.0);
        assertEquals(DefaultDuration.fromDays(days * scalar).getTotalDays(),
            instance.times(scalar).getTotalDays(), EPSILON);
        assertEquals(days, instance.times(1.0).getTotalDays(), EPSILON);
        assertEquals(-days, instance.times(-1.0).getTotalDays(), EPSILON);
    }

    /**
     * Test of divide method, of class DefaultDuration.
     */
    public void testDivide()
    {
        double days1 = random.nextDouble();
        double days2 = random.nextDouble();
        DefaultDuration d1 = DefaultDuration.fromDays(days1);
        DefaultDuration d2 = DefaultDuration.fromDays(days2);
        assertEquals(1.0, d1.divide(d1));
        assertEquals(1.0, d2.divide(d2));
        assertEquals(days1 / days2, d1.divide(d2), EPSILON);
        assertEquals(days2 / days1, d2.divide(d1), EPSILON);

        assertEquals(0.0, DefaultDuration.ZERO.divide(d1));
        assertEquals(0.0, DefaultDuration.ZERO.divide(d2));

        double days = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromDays(days);
        double milliseconds = instance.getTotalMilliseconds();
        double scalar = random.nextDouble();
        assertEquals(milliseconds / scalar,
            instance.divide(scalar).getTotalMilliseconds(), 0.0);
        assertEquals(milliseconds, instance.divide(1.0).getTotalMilliseconds(), 0.0);
        assertEquals(-milliseconds, instance.divide(-1.0).getTotalMilliseconds(), 0.0);
        assertEquals(DefaultDuration.fromDays(days / scalar).getTotalDays(),
            instance.divide(scalar).getTotalDays(), EPSILON);
        assertEquals(days, instance.divide(1.0).getTotalDays(), EPSILON);
        assertEquals(-days, instance.divide(-1.0).getTotalDays(), EPSILON);
        
        assertEquals(10.0 * milliseconds, instance.divide(0.1).getTotalMilliseconds(), 0.0);
    }

    /**
     * Test of getTotalMilliseconds method, of class DefaultDuration.
     */
    public void testGetTotalMilliseconds()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds, instance.getTotalMilliseconds(), 0.0);
    }

    /**
     * Test of getTotalSeconds method, of class DefaultDuration.
     */
    public void testGetTotalSeconds()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds / 1000, instance.getTotalSeconds(), 0.0);
    }

    /**
     * Test of getTotalMinutes method, of class DefaultDuration.
     */
    public void testGetTotalMinutes()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds / (60 * 1000), instance.getTotalMinutes(), 0.0);
    }

    /**
     * Test of getTotalHours method, of class DefaultDuration.
     */
    public void testGetTotalHours()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds / (60 * 60 * 1000), instance.getTotalHours(), 0.0);
    }

    /**
     * Test of getTotalDays method, of class DefaultDuration.
     */
    public void testGetTotalDays()
    {
        double milliseconds = random.nextDouble();
        DefaultDuration instance = DefaultDuration.fromMilliseconds(milliseconds);
        assertEquals(milliseconds / (24 * 60 * 60 * 1000), instance.getTotalDays(), 0.0);
    }

    /**
     * Test of getMillisecondsPart method, of class DefaultDuration.
     */
    public void testGetMillisecondsPart()
    {
        int millisecondsPart = random.nextInt(1000);
        int secondsPart = random.nextInt(60);
        int minutesPart = random.nextInt(60);
        int hoursPart = random.nextInt(24);
        int daysPart = random.nextInt(100000);
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        assertEquals(millisecondsPart, instance.getMillisecondsPart());
        assertEquals(-millisecondsPart, instance.negative().getMillisecondsPart());
    }

    /**
     * Test of getSecondsPart method, of class DefaultDuration.
     */
    public void testGetSecondsPart()
    {
        int millisecondsPart = random.nextInt(1000);
        int secondsPart = random.nextInt(60);
        int minutesPart = random.nextInt(60);
        int hoursPart = random.nextInt(24);
        int daysPart = random.nextInt(100000);
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        assertEquals(secondsPart, instance.getSecondsPart());
        assertEquals(-secondsPart, instance.negative().getSecondsPart());
    }

    /**
     * Test of getMinutesPart method, of class DefaultDuration.
     */
    public void testGetMinutesPart()
    {
        int millisecondsPart = random.nextInt(1000);
        int secondsPart = random.nextInt(60);
        int minutesPart = random.nextInt(60);
        int hoursPart = random.nextInt(24);
        int daysPart = random.nextInt(100000);
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        assertEquals(minutesPart, instance.getMinutesPart());
        assertEquals(-minutesPart, instance.negative().getMinutesPart());
    }

    /**
     * Test of getHoursPart method, of class DefaultDuration.
     */
    public void testGetHoursPart()
    {
        int millisecondsPart = random.nextInt(1000);
        int secondsPart = random.nextInt(60);
        int minutesPart = random.nextInt(60);
        int hoursPart = random.nextInt(24);
        int daysPart = random.nextInt(100000);
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        assertEquals(hoursPart, instance.getHoursPart());
        assertEquals(-hoursPart, instance.negative().getHoursPart());
    }

    /**
     * Test of getDaysPart method, of class DefaultDuration.
     */
    public void testGetDaysPart()
    {
        int millisecondsPart = random.nextInt(1000);
        int secondsPart = random.nextInt(60);
        int minutesPart = random.nextInt(60);
        int hoursPart = random.nextInt(24);
        int daysPart = random.nextInt(100000);
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        assertEquals(daysPart, instance.getDaysPart());
        assertEquals(-daysPart, instance.negative().getDaysPart());
    }

    /**
     * Test of toString method, of class DefaultDuration.
     */
    public void testToString()
    {
        assertEquals("00:00:00", DefaultDuration.ZERO.toString());
        
        int millisecondsPart = 12;
        int secondsPart = 3;
        int minutesPart = 4;
        int hoursPart = 5;
        int daysPart = 6789;
        Duration instance = DefaultDuration.ZERO;
        instance = instance.plus(DefaultDuration.MILLISECOND.times(millisecondsPart));
        instance = instance.plus(DefaultDuration.SECOND.times(secondsPart));
        instance = instance.plus(DefaultDuration.MINUTE.times(minutesPart));
        instance = instance.plus(DefaultDuration.HOUR.times(hoursPart));
        instance = instance.plus(DefaultDuration.DAY.times(daysPart));

        String expected = "6789.05:04:03.012";
        assertEquals(expected, instance.toString());
        assertEquals("-" + expected, instance.negative().toString());
    }

}
