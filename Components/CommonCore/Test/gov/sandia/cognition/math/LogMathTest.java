/*
 * File:                LogMathTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 13, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LogMath.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class LogMathTest
{
    protected Random random = new Random(311);
    protected double epsilon = 1e-10;

    /**
     * Creates a new test.
     */
    public LogMathTest()
    {
    }

    /**
     * Test of constants of class LogMath.
     */
    @Test
    public void testConstants()
    {
        assertEquals(Math.log(0.0), LogMath.LOG_0, 0.0);
        assertEquals(Math.log(1.0), LogMath.LOG_1, 0.0);
        assertEquals(Math.log(2.0), LogMath.LOG_2, 0.0);
        assertEquals(Math.log(10.0), LogMath.LOG_10, 0.0);
    }

    /**
     * Test of toLog method, of class LogMath.
     */
    @Test
    public void testToLog()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            double logX = Math.log(x);
            assertEquals(logX, LogMath.toLog(x), 0.0);
        }

        // Make sure negative logs are NaNs.
        for (double x : values)
        {
            if (x != 0.0)
            {
                assertTrue(Double.isNaN(LogMath.toLog(-x)));
            }
        }
    }

    /**
     * Test of fromLog method, of class LogMath.
     */
    @Test
    public void testFromLog()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            double logX = Math.log(x);
            assertEquals(Math.exp(logX), LogMath.fromLog(LogMath.toLog(x)), 0.0);
        }

        // Make sure any number can be converted back.
        for (int i = 0; i < 10; i++)
        {
            double logX = (random.nextDouble() - 0.5) * 100.0;
            assertEquals(Math.exp(logX), LogMath.fromLog(logX), 0.0);
        }
    }

    /**
     * Test of add method, of class LogMath.
     */
    @Test
    public void testAdd()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            for (double y : values)
            {
                double expected = Math.log(x + y);
                double logX = LogMath.toLog(x);
                double logY = LogMath.toLog(y);
                double actual = LogMath.add(logX, logY);
                assertEquals(expected, actual, epsilon);
            }
        }
    }

    /**
     * Test of subtract method, of class LogMath.
     */
    @Test
    public void testSubtract()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-200, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            for (double y : values)
            {
                double expected = Math.log(x - y);
                double logX = LogMath.toLog(x);
                double logY = LogMath.toLog(y);
                double actual = LogMath.subtract(logX, logY);
                assertEquals(expected, actual, epsilon);

                if (x < y)
                {
                    assertTrue(Double.isNaN(actual));
                }
            }
        }
    }

    /**
     * Test of multiply method, of class LogMath.
     */
    @Test
    public void testMultiply()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            for (double y : values)
            {
                double expected = Math.log(x * y);
                double logX = LogMath.toLog(x);
                double logY = LogMath.toLog(y);
                double actual = LogMath.multiply(logX, logY);
                assertEquals(expected, actual, epsilon);
                assertEquals(logX + logY, actual, 0.0);
            }
        }
    }

    /**
     * Test of divide method, of class LogMath.
     */
    @Test
    public void testDivide()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            for (double y : values)
            {
                double expected = Math.log(x / y);
                double logX = LogMath.toLog(x);
                double logY = LogMath.toLog(y);
                double actual = LogMath.divide(logX, logY);
                assertEquals(expected, actual, epsilon);
                assertEquals(logX - logY, actual, 0.0);
            }
        }
    }

    /**
     * Test of inverse method, of class LogMath.
     */
    @Test
    public void testInverse()
    {
        LinkedList<Double> values = new LinkedList<Double>();
        values.addAll(Arrays.asList(0.0, 0.1, 0.5, 1.0, 2.0, 4.7, 10.0, 1e-50, 1e100));
        for (int i = 0; i < 10; i++)
        {
            values.add(random.nextDouble() * 100000.0);
        }

        for (double x : values)
        {
            double logX = Math.log(x);
            double expected = Math.log(1.0 / x);
            double actual = LogMath.inverse(logX);
            assertEquals(expected, actual, epsilon);
        }
    }


}