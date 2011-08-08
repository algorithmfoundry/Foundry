/*
 * File:                WeightedKernelTest.java
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
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class WeightedKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);


    public WeightedKernelTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(WeightedKernelTest.class);
        
        return suite;
    }
    
    public void testStatics()
    {
        assertEquals(1.0, WeightedKernel.DEFAULT_WEIGHT);
    }
    
    public void testConstructors()
    {
        WeightedKernel<Vector> instance = new WeightedKernel<Vector>();
        assertEquals(WeightedKernel.DEFAULT_WEIGHT, instance.getWeight());
        assertNull(instance.getKernel());
        
        double weight = RANDOM.nextDouble();
        PolynomialKernel kernel = new PolynomialKernel(2);
        instance = new WeightedKernel<Vector>(weight, kernel);
        assertEquals(weight, instance.getWeight());
        assertSame(kernel, instance.getKernel());
        
        
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.WeightedKernel.
     */
    public void testClone()
    {
        double weight = RANDOM.nextDouble();
        PolynomialKernel kernel = new PolynomialKernel(2);
        WeightedKernel<Vector> instance = new WeightedKernel<Vector>(
            weight, kernel);
        
        WeightedKernel<Vector> clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(weight, clone.getWeight());
        assertNotSame(instance.getKernel(), clone.getKernel());
        assertEquals(2, ((PolynomialKernel) clone.getKernel()).getDegree());
        
        instance.setKernel(null);
        clone = instance.clone();
        assertNull(clone.getKernel());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.WeightedKernel.
     */
    public void testEvaluate()
    {
        Vector zero = new Vector3();
        Vector x = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        Vector y = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        
        double weight = RANDOM.nextDouble();
        WeightedKernel<Vector> instance = new WeightedKernel<Vector>(
            weight, LinearKernel.getInstance());
        
        double expected = weight * x.dotProduct(y);
        assertEquals(expected, instance.evaluate(x, y));
        assertEquals(expected, instance.evaluate(y, x));
        assertEquals(0.0, instance.evaluate(x, zero));
        assertEquals(0.0, instance.evaluate(y, zero));
        assertEquals(0.0, instance.evaluate(zero, zero));
    }

    /**
     * Test of getWeight method, of class gov.sandia.cognition.learning.kernel.WeightedKernel.
     */
    public void testGetWeight()
    {
        this.testSetWeight();
    }

    /**
     * Test of setWeight method, of class gov.sandia.cognition.learning.kernel.WeightedKernel.
     */
    public void testSetWeight()
    {
        WeightedKernel<Vector> instance = new WeightedKernel<Vector>();
        assertEquals(WeightedKernel.DEFAULT_WEIGHT, instance.getWeight());
        
        double weight = RANDOM.nextDouble();
        instance.setWeight(weight);
        assertEquals(weight, instance.getWeight());
        
        weight = 0.0;
        instance.setWeight(weight);
        assertEquals(weight, instance.getWeight());
        
        weight = 4.7;
        instance.setWeight(weight);
        assertEquals(weight, instance.getWeight());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setWeight(-1.0);
        }
        catch ( IllegalArgumentException ex )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
            
    }
}
