/*
 * File:                DefaultPrecisionRecallPairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.text.evaluation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultPrecisionRecallPair.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultPrecisionRecallPairTest
{

    public DefaultPrecisionRecallPairTest()
    {
    }

    /**
     * Test of constructors of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testConstructors()
    {
        double precision = DefaultPrecisionRecallPair.DEFAULT_PRECISION;
        double recall = DefaultPrecisionRecallPair.DEFAULT_RECALL;
        DefaultPrecisionRecallPair instance = new DefaultPrecisionRecallPair();
        assertEquals(precision, instance.getPrecision(), 0.0);
        assertEquals(recall, instance.getRecall(), 0.0);

        precision = 0.1;
        recall = 0.2;
        instance = new DefaultPrecisionRecallPair(precision, recall);
        assertEquals(precision, instance.getPrecision(), 0.0);
        assertEquals(recall, instance.getRecall(), 0.0);
    }

    /**
     * Test of getPrecision method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testGetPrecision()
    {
        this.testSetPrecision();
    }

    /**
     * Test of setPrecision method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testSetPrecision()
    {
        double precision = DefaultPrecisionRecallPair.DEFAULT_PRECISION;
        DefaultPrecisionRecallPair instance = new DefaultPrecisionRecallPair();
        assertEquals(precision, instance.getPrecision(), 0.0);

        precision = 0.1;
        instance.setPrecision(precision);
        assertEquals(precision, instance.getPrecision(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            precision = -0.1;
            instance.setPrecision(precision);
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
            precision = 1.1;
            instance.setPrecision(precision);
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
     * Test of getRecall method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testGetRecall()
    {
        this.testSetRecall();
    }

    /**
     * Test of setRecall method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testSetRecall()
    {
        double recall = DefaultPrecisionRecallPair.DEFAULT_RECALL;
        DefaultPrecisionRecallPair instance = new DefaultPrecisionRecallPair();
        assertEquals(recall, instance.getRecall(), 0.0);

        recall = 0.2;
        instance.setRecall(recall);
        assertEquals(recall, instance.getRecall(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            recall = -0.1;
            instance.setRecall(recall);
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
            recall = 1.1;
            instance.setRecall(recall);
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
     * Test of getFirst method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testGetFirst()
    {
        double precision = 0.1;
        DefaultPrecisionRecallPair instance = new DefaultPrecisionRecallPair();
        instance.setPrecision(precision);
        assertEquals(precision, instance.getFirst(), 0.0);
    }

    /**
     * Test of getSecond method, of class DefaultPrecisionRecallPair.
     */
    @Test
    public void testGetSecond()
    {
        double recall = 0.2;
        DefaultPrecisionRecallPair instance = new DefaultPrecisionRecallPair();
        instance.setRecall(recall);
        assertEquals(recall, instance.getSecond(), 0.0);
    }

}