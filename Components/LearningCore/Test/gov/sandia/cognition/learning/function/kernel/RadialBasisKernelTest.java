/*
 * File:                RadialBasisKernelTest.java
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
 * RadialBasisKernel
 * @author Justin Basilico
 * @since  2.0
 */
public class RadialBasisKernelTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public RadialBasisKernelTest(
        String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertEquals(RadialBasisKernel.DEFAULT_SIGMA, 1.0);
    }
    
    public void testConstructors()
    {
        RadialBasisKernel instance = new RadialBasisKernel();
        assertEquals(RadialBasisKernel.DEFAULT_SIGMA, instance.getSigma());
        
        double sigma = Math.random();
        instance = new RadialBasisKernel(sigma);
        assertEquals(sigma, instance.getSigma());
        
        RadialBasisKernel copy = new RadialBasisKernel(instance);
        assertEquals(sigma, copy.getSigma());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.RadialBasisKernel.
     */
    public void testClone()
    {
        double sigma = RANDOM.nextDouble();
        RadialBasisKernel instance = new RadialBasisKernel(sigma);
        assertEquals(sigma, instance.getSigma());
        
        RadialBasisKernel clone = instance.clone();
        assertNotNull( clone );
        assertNotSame(instance, clone);
        assertEquals(sigma, instance.getSigma());
        
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.RadialBasisKernel.
     */
    public void testEvaluate()
    {
        double sigma = RANDOM.nextDouble();
        RadialBasisKernel instance = new RadialBasisKernel(sigma);
        
        Vector zero = new Vector3();
        Vector x = Vector3.createRandom(RANDOM);
        Vector y = Vector3.createRandom(RANDOM);
            
        assertEquals(
            Math.exp(x.euclideanDistanceSquared(y) / (-2.0 * sigma * sigma)), 
            instance.evaluate(x, y));
        assertEquals(
            Math.exp(x.euclideanDistanceSquared(y) / (-2.0 * sigma * sigma)), 
            instance.evaluate(y, x));
        assertEquals(
            Math.exp(x.euclideanDistanceSquared(zero) / (-2.0 * sigma * sigma)), 
            instance.evaluate(x, zero));
        assertEquals(
            Math.exp(y.euclideanDistanceSquared(zero) / (-2.0 * sigma * sigma)),  
            instance.evaluate(y, zero));
        assertEquals(
            Math.exp(zero.euclideanDistanceSquared(zero) / (-2.0 * sigma * sigma)), 
            instance.evaluate(zero, zero));
    }

    /**
     * Test of getSigma method, of class gov.sandia.cognition.learning.kernel.RadialBasisKernel.
     */
    public void testGetSigma()
    {
        this.testSetSigma();
    }

    /**
     * Test of setSigma method, of class gov.sandia.cognition.learning.kernel.RadialBasisKernel.
     */
    public void testSetSigma()
    {
        RadialBasisKernel instance = new RadialBasisKernel();
        assertEquals(RadialBasisKernel.DEFAULT_SIGMA, instance.getSigma());
        
        double sigma = RANDOM.nextDouble();
        instance.setSigma(sigma);
        assertEquals(sigma, instance.getSigma());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setSigma(0.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}
