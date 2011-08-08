/*
 * File:                SigmoidKernelTest.java
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
 * @author Justin Basilico
 * @since  2.0
 */
public class SigmoidKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public SigmoidKernelTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SigmoidKernelTest.class);
        
        return suite;
    }
    
    public void testConstants()
    {
        assertEquals(1.0, SigmoidKernel.DEFAULT_KAPPA);
        assertEquals(0.0, SigmoidKernel.DEFAULT_CONSTANT);
    }
    
    public void testConstructors()
    {
        SigmoidKernel instance = new SigmoidKernel();
        assertEquals(SigmoidKernel.DEFAULT_KAPPA, instance.getKappa());
        assertEquals(SigmoidKernel.DEFAULT_CONSTANT, instance.getConstant());
        
        double kappa = RANDOM.nextDouble();
        double constant = RANDOM.nextDouble();
        instance = new SigmoidKernel(kappa, constant);
        assertEquals(kappa, instance.getKappa());
        assertEquals(constant, instance.getConstant());
        
        SigmoidKernel copy = new SigmoidKernel(instance);
        assertEquals(kappa, copy.getKappa());
        assertEquals(constant, copy.getConstant());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testClone()
    {
        double kappa = RANDOM.nextDouble();
        double constant = RANDOM.nextDouble();
        SigmoidKernel instance = new SigmoidKernel(kappa, constant);
        
        SigmoidKernel clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(kappa, clone.getKappa());
        assertEquals(constant, clone.getConstant());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testEvaluate()
    {
        double kappa = RANDOM.nextDouble();
        double constant = RANDOM.nextDouble();
        SigmoidKernel instance = new SigmoidKernel(kappa, constant);
        
        Vector zero = new Vector3();
        Vector x = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        Vector y = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        
        assertEquals(Math.tanh(kappa * x.dotProduct(y) + constant),
            instance.evaluate(x, y));
        assertEquals(Math.tanh(kappa * x.dotProduct(y) + constant),
            instance.evaluate(y, x));
        assertEquals(Math.tanh(kappa * x.dotProduct(zero) + constant),
            instance.evaluate(x, zero));
        assertEquals(Math.tanh(kappa * y.dotProduct(zero) + constant),
            instance.evaluate(y, zero));
        assertEquals(Math.tanh(kappa * zero.dotProduct(zero) + constant),
            instance.evaluate(zero, zero));
    }

    /**
     * Test of getKappa method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testGetKappa()
    {
        this.testSetKappa();
    }

    /**
     * Test of setKappa method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testSetKappa()
    {
        SigmoidKernel instance = new SigmoidKernel();
        assertEquals(SigmoidKernel.DEFAULT_KAPPA, instance.getKappa());
        
        double kappa = RANDOM.nextDouble();
        instance.setKappa(kappa);
        assertEquals(kappa, instance.getKappa());
    }

    /**
     * Test of getConstant method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testGetConstant()
    {
        this.testSetConstant();
    }

    /**
     * Test of setConstant method, of class gov.sandia.cognition.learning.kernel.SigmoidKernel.
     */
    public void testSetConstant()
    {
        SigmoidKernel instance = new SigmoidKernel();
        assertEquals(SigmoidKernel.DEFAULT_CONSTANT, instance.getConstant());
        
        double constant = RANDOM.nextDouble();
        instance.setConstant(constant);
        assertEquals(constant, instance.getConstant());
    }
}
