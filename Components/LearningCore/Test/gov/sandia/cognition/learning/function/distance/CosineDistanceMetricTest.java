/*
 * File:                CosineDistanceMetricTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 *
 * @author Justin Basilico
 */
public class CosineDistanceMetricTest
    extends SemimetricTestHarness<Vectorizable>
{
    public CosineDistanceMetricTest(String testName)
    {
        super(testName);
    }

    /**
     * Test of evaluate method, of class CosineDistanceMetric.
     */
    public void testKnownValues()
    {
        CosineDistanceMetric instance = this.createInstance();
        int n = 10;
        for (int i = 0; i < n; i++)
        {
            Vector x = this.generateRandomFirstType();
            Vector y = this.generateRandomFirstType();
            
            double expected = 1.0 - x.cosine(y);
            double result = instance.evaluate(x, y);
            assertEquals(expected, result);
            assertTrue(result >= 0.0);
            assertEquals(0.0, instance.evaluate(x, x), TOLERANCE);
            assertEquals(0.0, instance.evaluate(y, y), TOLERANCE);
        }

        Vector v0 = VectorFactory.getDefault().copyValues(0);
        Vector v1 = VectorFactory.getDefault().copyValues(1.0);
        assertEquals( 0.0, instance.evaluate(v0, v0));
        assertEquals( 1.0, instance.evaluate(v0, v1));
        assertEquals( 1.0, instance.evaluate(v1, v0));
            
    }

    @Override
    public CosineDistanceMetric createInstance()
    {
        return CosineDistanceMetric.INSTANCE;
    }

    @Override
    public Vector generateRandomFirstType()
    {
        return Vector3.createRandom(RANDOM);
    }

}
