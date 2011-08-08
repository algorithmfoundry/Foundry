/*
 * File:                SumKernelTest.java
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
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * SumKernel
 * @author Justin Basilico
 * @since  2.0
 */
public class SumKernelTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public SumKernelTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        SumKernel<Vectorizable> instance = 
            new SumKernel<Vectorizable>();
        assertTrue(instance.getKernels().isEmpty());
        
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(LinearKernel.getInstance());
        kernels.add(new PolynomialKernel(2));
        
        instance = new SumKernel<Vectorizable>(kernels);
        assertSame(kernels, instance.getKernels());
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.SumKernel.
     */
    public void testClone()
    {
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(LinearKernel.getInstance());
        kernels.add(new PolynomialKernel(2));
        
        SumKernel<Vectorizable> instance = 
            new SumKernel<Vectorizable>(kernels);
        assertSame(kernels, instance.getKernels());
        
        
        SumKernel<Vectorizable> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(kernels, clone.getKernels());
        assertEquals( kernels.size(), clone.getKernels().size() );
        for ( Kernel<Vectorizable> kernel : kernels )
        {
            assertFalse(clone.getKernels().contains(kernel));
        }
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.SumKernel.
     */
    public void testEvaluate()
    {
        LinearKernel linear = LinearKernel.getInstance();
        PolynomialKernel poly = new PolynomialKernel(2);
        RadialBasisKernel rbf = new RadialBasisKernel(RANDOM.nextDouble());
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(linear);
        kernels.add(poly);
        kernels.add(rbf);
        
        
        SumKernel<Vectorizable> instance = 
            new SumKernel<Vectorizable>(kernels);
        
        int count = 10;
        for (int i = 0; i < count; i++)
        {
            Vector x = Vector3.createRandom(RANDOM);
            Vector y = Vector3.createRandom(RANDOM);
            double expected = 
                  linear.evaluate(x, y) 
                + poly.evaluate(x, y)
                + rbf.evaluate(x, y);
            
            assertEquals(expected, instance.evaluate(x, y));
            assertEquals(expected, instance.evaluate(y, x));
        }
    }
}
