/*
 * File:                VectorFunctionKernelTest.java
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

import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
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
public class VectorFunctionKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public VectorFunctionKernelTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        VectorFunctionKernel instance = new VectorFunctionKernel();
        assertNull(instance.getFunction());
        
        LinearVectorFunction function = new LinearVectorFunction(RANDOM.nextGaussian());
        instance = new VectorFunctionKernel(function);
        assertSame(function, instance.getFunction());
        
        PolynomialKernel kernel = new PolynomialKernel(4);
        instance = new VectorFunctionKernel(function, kernel);
        assertSame(function, instance.getFunction());
        assertSame(kernel, instance.getKernel());
    }
    
    public void testClone()
    {
        LinearVectorFunction function = new LinearVectorFunction(RANDOM.nextGaussian());
        PolynomialKernel kernel = new PolynomialKernel(4);
        VectorFunctionKernel instance = new VectorFunctionKernel(function, kernel);
        
        VectorFunctionKernel clone = instance.clone();
        assertNotNull( clone );
        assertNotSame(instance.getFunction(), clone.getFunction());
        assertNotSame(instance.getKernel(), clone.getKernel());
        assertEquals(4, ((PolynomialKernel) clone.getKernel()).getDegree());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.VectorFunctionKernel.
     */
    public void testEvaluate()
    {
        LinearVectorFunction function = new LinearVectorFunction(RANDOM.nextGaussian());
        VectorFunctionKernel instance = new VectorFunctionKernel(function);
        
        PolynomialKernel kernel = new PolynomialKernel(4, RANDOM.nextDouble());
        
        int count = 10;
        for (int i = 0; i < count; i++)
        {
            Vector x = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
            Vector y = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        
            Vector fx = function.evaluate(x);
            Vector fy = function.evaluate(y);
            
            instance.setKernel(null);
            double expected = fx.dotProduct(fy);
            assertEquals(expected, instance.evaluate(x, y));
            assertEquals(expected, instance.evaluate(y, x));
            
            instance.setKernel(kernel);
            expected = kernel.evaluate(fx, fy);
            
            assertEquals(expected, instance.evaluate(x, y));
            assertEquals(expected, instance.evaluate(y, x));
        }
    }

    /**
     * Test of getFunction method, of class gov.sandia.cognition.learning.kernel.VectorFunctionKernel.
     */
    public void testGetFunction()
    {
        this.testSetFunction();
    }

    /**
     * Test of setFunction method, of class gov.sandia.cognition.learning.kernel.VectorFunctionKernel.
     */
    public void testSetFunction()
    {
        VectorFunctionKernel instance = new VectorFunctionKernel();
        assertNull(instance.getFunction());
        
        
        LinearVectorFunction function = new LinearVectorFunction(RANDOM.nextGaussian());
        instance.setFunction(function);
        assertSame(function, instance.getFunction());
        
        instance.setFunction(null);
        assertNull(instance.getFunction());
    }
}
