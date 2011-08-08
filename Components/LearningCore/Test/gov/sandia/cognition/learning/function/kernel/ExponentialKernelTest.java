/*
 * File:                ExponentialKernelTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 21, 2007, Sandia Corporation.  Under the terms of Contract
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
public class ExponentialKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public ExponentialKernelTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        ExponentialKernel<Vector> instance = new ExponentialKernel<Vector>();
        assertNull(instance.getKernel());
        
        LinearKernel kernel = LinearKernel.getInstance();
        instance = new ExponentialKernel<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.ExponentialKernel.
     */
    public void testClone()
    {
        PolynomialKernel kernel = new PolynomialKernel(2);
        ExponentialKernel<Vector> instance = 
            new ExponentialKernel<Vector>(kernel);
        
        
        ExponentialKernel<Vector> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(instance.getKernel(), clone.getKernel());
        assertEquals(2, ((PolynomialKernel) clone.getKernel()).getDegree());
        
        instance.setKernel(null);
        clone = instance.clone();
        assertNull(clone.getKernel());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.ExponentialKernel.
     */
    public void testEvaluate()
    {
        Vector zero = new Vector3();
        Vector x = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        Vector y = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        
        double weight = RANDOM.nextDouble();
        ExponentialKernel<Vector> instance = new ExponentialKernel<Vector>(
            LinearKernel.getInstance());
        
        double expected = Math.exp(x.dotProduct(y));
        assertEquals(expected, instance.evaluate(x, y));
        assertEquals(expected, instance.evaluate(y, x));
        assertEquals(1.0, instance.evaluate(x, zero));
        assertEquals(1.0, instance.evaluate(y, zero));
        assertEquals(1.0, instance.evaluate(zero, zero));
    }
}
