/*
 * File:                DefaultKernelsContainerTest.java
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

import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: DefaultKernelsContainer
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class DefaultKernelsContainerTest
    extends TestCase
{
    public DefaultKernelsContainerTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        DefaultKernelsContainer<Vectorizable> instance = 
            new DefaultKernelsContainer<Vectorizable>();
        assertTrue(instance.getKernels().isEmpty());
        
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(LinearKernel.getInstance());
        kernels.add(new PolynomialKernel(2));
        
        instance = new DefaultKernelsContainer<Vectorizable>(kernels);
        assertSame(kernels, instance.getKernels());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.DefaultKernelsContainer.
     */
    public void testClone()
    {
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(LinearKernel.getInstance());
        kernels.add(new PolynomialKernel(2));
        
        DefaultKernelsContainer<Vectorizable> instance = 
            new DefaultKernelsContainer<Vectorizable>(kernels);
        assertSame(kernels, instance.getKernels());
        
        
        DefaultKernelsContainer<Vectorizable> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(kernels, clone.getKernels());
        
        for ( Kernel<Vectorizable> kernel : kernels )
        {
            assertFalse(clone.getKernels().contains(kernel));
        }
    }

    /**
     * Test of getKernels method, of class gov.sandia.cognition.learning.kernel.DefaultKernelsContainer.
     */
    public void testGetKernels()
    {
        this.testSetKernels();
    }

    /**
     * Test of setKernels method, of class gov.sandia.cognition.learning.kernel.DefaultKernelsContainer.
     */
    public void testSetKernels()
    {
        DefaultKernelsContainer<Vectorizable> instance = 
            new DefaultKernelsContainer<Vectorizable>();
        assertTrue(instance.getKernels().isEmpty());
        
        
        Collection<Kernel<Vectorizable>> kernels = 
            new LinkedList<Kernel<Vectorizable>>();
        kernels.add(LinearKernel.getInstance());
        kernels.add(new PolynomialKernel(2));
        
        instance.setKernels(kernels);
        assertSame(kernels, instance.getKernels());
        
        instance.setKernels(null);
        assertNull(instance.getKernels());
    }
}
