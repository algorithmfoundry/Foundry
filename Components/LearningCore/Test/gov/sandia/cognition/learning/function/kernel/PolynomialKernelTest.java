/*
 * File:                PolynomialKernelTest.java
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
 * PolynomialKernel
 * 
 * @author Justin Basilico
 * @since  2.0
 */
public class PolynomialKernelTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public PolynomialKernelTest(
        String testName)
    {
        super(testName);
    }

    public void testStatics()
    {
        assertEquals(2, PolynomialKernel.DEFAULT_DEGREE);
        assertEquals(1.0, PolynomialKernel.DEFAULT_CONSTANT);
    }
    
    public void testConstructors()
    {
        PolynomialKernel instance = new PolynomialKernel();
        assertEquals(PolynomialKernel.DEFAULT_DEGREE, instance.getDegree());
        assertEquals(PolynomialKernel.DEFAULT_CONSTANT, instance.getConstant());
        
        int degree = 47;
        instance = new PolynomialKernel(degree);
        assertEquals(degree, instance.getDegree());
        assertEquals(PolynomialKernel.DEFAULT_CONSTANT, instance.getConstant());
        
        double constant = RANDOM.nextDouble();
        instance = new PolynomialKernel(degree, constant);
        assertEquals(degree, instance.getDegree());
        assertEquals(constant, instance.getConstant());
        
        PolynomialKernel copy = new PolynomialKernel(instance);
        assertEquals(degree, copy.getDegree());
        assertEquals(constant, copy.getConstant());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testClone()
    {
        int degree = 47;
        double constant = RANDOM.nextDouble();
        PolynomialKernel instance = new PolynomialKernel(degree, constant);
        PolynomialKernel clone = new PolynomialKernel(instance);
        assertNotSame(instance, clone);
        assertEquals(degree, clone.getDegree());
        assertEquals(constant, clone.getConstant());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testEvaluate()
    {
        int d = 4;
        double c = RANDOM.nextDouble();
        PolynomialKernel instance = new PolynomialKernel(d, c);
        
        Vector zero = new Vector3();
        Vector x = Vector3.createRandom(RANDOM);
        Vector y = Vector3.createRandom(RANDOM);
        
        assertEquals(Math.pow((x.dotProduct(y) + c), d), instance.evaluate(x, y));
        assertEquals(Math.pow((x.dotProduct(y) + c), d), instance.evaluate(y, x));
        assertEquals(Math.pow((x.dotProduct(zero) + c), d), instance.evaluate(x, zero));
        assertEquals(Math.pow((y.dotProduct(zero) + c), d), instance.evaluate(y, zero));
        assertEquals(Math.pow((zero.dotProduct(zero) + c), d), instance.evaluate(zero, zero));
    }

    /**
     * Test of getDegree method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testGetDegree()
    {
        this.testSetDegree();
    }

    /**
     * Test of setDegree method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testSetDegree()
    {
        PolynomialKernel instance = new PolynomialKernel();
        assertEquals(PolynomialKernel.DEFAULT_DEGREE, instance.getDegree());
        
        int degree = 47;
        instance.setDegree(degree);
        assertEquals(degree, instance.getDegree());
        
        degree = 1;
        instance.setDegree(degree);
        assertEquals(degree, instance.getDegree());
        
        degree = 2;
        instance.setDegree(degree);
        assertEquals(degree, instance.getDegree());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setDegree(0);
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

    /**
     * Test of getConstant method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testGetConstant()
    {
        this.testSetConstant();
    }

    /**
     * Test of setConstant method, of class gov.sandia.cognition.learning.kernel.PolynomialKernel.
     */
    public void testSetConstant()
    {
        PolynomialKernel instance = new PolynomialKernel();
        assertEquals(PolynomialKernel.DEFAULT_CONSTANT, instance.getConstant());
        
        double constant = RANDOM.nextDouble();
        instance.setConstant(constant);
        assertEquals(constant, instance.getConstant());
        
        constant = 0.0;
        instance.setConstant(constant);
        assertEquals(constant, instance.getConstant());
        
        constant = 1.0;
        instance.setConstant(constant);
        assertEquals(constant, instance.getConstant());
        
        constant = 10.0;
        instance.setConstant(constant);
        assertEquals(constant, instance.getConstant());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setConstant(-0.5);
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
