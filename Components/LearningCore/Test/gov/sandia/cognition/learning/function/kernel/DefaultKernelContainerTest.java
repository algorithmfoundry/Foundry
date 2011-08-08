/*
 * File:                DefaultKernelContainerTest.java
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
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class DefaultKernelContainerTest
    extends TestCase
{
    public DefaultKernelContainerTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        DefaultKernelContainer<Vector> instance = 
            new DefaultKernelContainer<Vector>();
        assertNull(instance.getKernel());
        
        int degree = 4;
        PolynomialKernel kernel = new PolynomialKernel(degree);
        instance = new DefaultKernelContainer<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        
        DefaultKernelContainer<Vector> copy = 
            new DefaultKernelContainer<Vector>(instance);
        assertNotSame(instance.getKernel(), copy.getKernel());
        assertEquals(degree, ((PolynomialKernel) copy.getKernel()).getDegree());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.DefaultKernelContainer.
     */
    public void testClone()
    {
        int degree = 4;
        PolynomialKernel kernel = new PolynomialKernel(degree);
        DefaultKernelContainer<Vector> instance = 
            new DefaultKernelContainer<Vector>(kernel);
        
        DefaultKernelContainer<Vector> clone = instance.clone();
        assertNotSame(instance.getKernel(), clone.getKernel());
        assertEquals(degree, ((PolynomialKernel) clone.getKernel()).getDegree());
    }

    /**
     * Test of getKernel method, of class gov.sandia.cognition.learning.kernel.DefaultKernelContainer.
     */
    public void testGetKernel()
    {
        this.testSetKernel();
    }

    /**
     * Test of setKernel method, of class gov.sandia.cognition.learning.kernel.DefaultKernelContainer.
     */
    public void testSetKernel()
    {
        DefaultKernelContainer<Vector> instance = 
            new DefaultKernelContainer<Vector>();
        assertNull(instance.getKernel());
        
        int degree = 4;
        PolynomialKernel kernel = new PolynomialKernel(degree);
        instance.setKernel(kernel);
        assertSame(kernel, instance.getKernel());
        
        instance.setKernel(null);
        assertNull(instance.getKernel());
    }
}
