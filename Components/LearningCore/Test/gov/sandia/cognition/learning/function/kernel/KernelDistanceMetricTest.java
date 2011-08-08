/*
 * File:                KernelDistanceMetricTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelDistanceMetricTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public KernelDistanceMetricTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        KernelDistanceMetric<Vector> instance = 
            new KernelDistanceMetric<Vector>();
        assertNull(instance.getKernel());
        
        
        LinearKernel kernel = LinearKernel.getInstance();
        instance = new KernelDistanceMetric<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        
        KernelDistanceMetric<Vector> copy = 
            new KernelDistanceMetric<Vector>(instance);
        assertNotNull( copy.getKernel() );
        assertNotSame(instance.getKernel(), copy.getKernel());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.KernelDistanceMetric.
     */
    public void testClone()
    {
        LinearKernel kernel = LinearKernel.getInstance();
        KernelDistanceMetric<Vector> instance = 
            new KernelDistanceMetric<Vector>(kernel);
        
        KernelDistanceMetric<Vector> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotNull( clone.getKernel() );
        assertNotSame(instance.getKernel(), clone.getKernel());
        
        instance.setKernel(null);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNull(instance.getKernel());
        assertNull(clone.getKernel());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.KernelDistanceMetric.
     */
    public void testEvaluate()
    {
        final double epsilon = 1e-5;
        LinearKernel kernel = LinearKernel.getInstance();
        KernelDistanceMetric<Vector> instance = 
            new KernelDistanceMetric<Vector>(kernel);
        
        Vector zero = new Vector3();
        Vector x = Vector3.createRandom(RANDOM);
        Vector y = Vector3.createRandom(RANDOM);
        
        assertEquals(x.euclideanDistanceSquared(y), instance.evaluate(x, y), epsilon);
        assertEquals(x.euclideanDistanceSquared(y), instance.evaluate(y, x), epsilon);
        assertEquals(x.euclideanDistanceSquared(zero), instance.evaluate(x, zero), epsilon);
        assertEquals(x.euclideanDistanceSquared(zero), instance.evaluate(zero, x), epsilon);
        assertEquals(y.euclideanDistanceSquared(zero), instance.evaluate(y, zero), epsilon);
        assertEquals(y.euclideanDistanceSquared(zero), instance.evaluate(zero, y), epsilon);
        assertEquals(0.0, instance.evaluate(zero, zero));
    }
}
