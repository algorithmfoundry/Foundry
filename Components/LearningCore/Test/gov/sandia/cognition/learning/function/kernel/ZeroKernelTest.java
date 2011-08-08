/*
 * File:                ZeroKernelTest.java
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

import junit.framework.TestCase;


/**
 * This class implements JUnit tests for the following classes: ZeroKernel
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ZeroKernelTest
    extends TestCase
{
    public ZeroKernelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getInstance method, of class gov.sandia.cognition.learning.kernel.ZeroKernel.
     */
    public void testGetInstance()
    {
        assertNotNull(ZeroKernel.getInstance());
        assertSame(ZeroKernel.getInstance(), ZeroKernel.getInstance());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.ZeroKernel.
     */
    public void testClone()
    {
        ZeroKernel instance = new ZeroKernel();
        ZeroKernel clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.ZeroKernel.
     */
    public void testEvaluate()
    {
        Object x = new Object();
        Object y = new Object();
        Kernel<Object> instance = ZeroKernel.getInstance();
        
        assertEquals(0.0, instance.evaluate(x, y));
        assertEquals(0.0, instance.evaluate(y, x));
        assertEquals(0.0, instance.evaluate(x, x));
        assertEquals(0.0, instance.evaluate(y, y));
        assertEquals(0.0, instance.evaluate(x, null));
        assertEquals(0.0, instance.evaluate(null, y));
        assertEquals(0.0, instance.evaluate(null, null));
    }
}
