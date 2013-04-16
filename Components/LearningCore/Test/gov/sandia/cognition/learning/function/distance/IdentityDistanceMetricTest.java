/*
 * File:            IdentityDistanceMetricTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.mtj.Vector1;
import static org.junit.Assert.*;

/**
 * Unit tests for class IdentityDistanceMetric.
 * @author Justin Basilico
 * @since  3.3.3
 */
public class IdentityDistanceMetricTest 
    extends MetricTestHarness<Object>
{
    public IdentityDistanceMetricTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public IdentityDistanceMetric createInstance()
    {
        return new IdentityDistanceMetric();
    }

    @Override
    public Object generateRandomFirstType()
    {
        return RANDOM.nextDouble();
    }

    /**
     * Tests constructors of IdentityDistanceMetric.
     */
    public void testConstructors()
    {
        IdentityDistanceMetric instance = new IdentityDistanceMetric();
        assertNotNull(instance);
    }

    @Override
    public void testKnownValues()
    {
        IdentityDistanceMetric instance = this.createInstance();
        Object[] values = {"a", "b", "c", new Object(), RANDOM.nextDouble(), null};
        for (int i = 0; i < values.length; i++)
        {
            Object x = values[i];
            assertEquals(0.0, instance.evaluate(x, x), 0.0);

            for (int j = i + 1; j < values.length; j++)
            {
                Object y = values[j];
                assertEquals(1.0, instance.evaluate(x, y), 0.0);
                assertEquals(1.0, instance.evaluate(y, x), 0.0);
            }
        }

        // Guard against == versus equals.
        assertEquals(0.0, instance.evaluate(new Vector1(), new Vector1()), 0.0);
    }

}