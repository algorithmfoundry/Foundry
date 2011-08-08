/*
 * File:                NormalizedKernelTest.java
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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements JUnit tests for the following classes: Normalized Kernel
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class NormalizedKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public NormalizedKernelTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(NormalizedKernelTest.class);
        
        return suite;
    }
    
    public void testConstructors()
    {
        NormalizedKernel<Vector> instance = new NormalizedKernel<Vector>();
        assertNull(instance.getKernel());
        
        PolynomialKernel kernel = new PolynomialKernel(2);
        instance = new NormalizedKernel<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        
        NormalizedKernel<Vector> copy = new NormalizedKernel<Vector>(instance);
        assertNotSame(kernel, copy.getKernel());
        assertEquals(2, ((PolynomialKernel) copy.getKernel()).getDegree());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.NormalizedKernel.
     */
    public void testClone()
    {
        PolynomialKernel kernel = new PolynomialKernel(2);
        NormalizedKernel<Vector> instance = new NormalizedKernel<Vector>(
            kernel);
        NormalizedKernel<Vector> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(instance.getKernel(), clone.getKernel());
        assertEquals(2, ((PolynomialKernel) clone.getKernel()).getDegree());
        
        instance.setKernel(null);
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertNull(clone.getKernel());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.NormalizedKernel.
     */
    public void testEvaluate()
    {
        final double epsilon = 0.00001;
        LinearKernel kernel = LinearKernel.getInstance();
        NormalizedKernel<Vector> instance = new NormalizedKernel<Vector>(
            kernel);

        Vector zero = new Vector3();
        Vector x = Vector3.createRandom(RANDOM);
        Vector y = Vector3.createRandom(RANDOM);
        
        assertEquals(x.cosine(y), instance.evaluate(x, y), epsilon);
        assertEquals(x.cosine(y), instance.evaluate(y, x), epsilon);
        assertEquals(0.0, instance.evaluate(x, zero));
        assertEquals(0.0, instance.evaluate(y, zero));
        assertEquals(0.0, instance.evaluate(zero, x));
        assertEquals(0.0, instance.evaluate(zero, y));
        assertEquals(0.0, instance.evaluate(zero, zero));
        
    }
}
