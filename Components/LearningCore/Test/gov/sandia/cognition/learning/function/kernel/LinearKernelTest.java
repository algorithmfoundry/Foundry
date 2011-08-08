/*
 * File:                LinearKernelTest.java
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

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LinearKernelTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public LinearKernelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getInstance method, of class gov.sandia.cognition.learning.kernel.LinearKernel.
     */
    public void testGetInstance()
    {
        assertNotNull(LinearKernel.getInstance());
        assertSame(LinearKernel.getInstance(), LinearKernel.getInstance());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.LinearKernel.
     */
    public void testClone()
    {
        LinearKernel instance = new LinearKernel();
        LinearKernel clone = instance.clone();
        assertNotNull( clone );
        assertNotSame(instance, clone);
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.LinearKernel.
     */
    public void testEvaluate()
    {
        Vector zero = new Vector3();
        Vector x = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        Vector y = new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());
        double xDotY = x.dotProduct(y);
        
        LinearKernel instance = new LinearKernel();
        assertEquals(xDotY, instance.evaluate(x, y));
        assertEquals(xDotY, instance.evaluate(y, x));
        assertEquals(0.0, instance.evaluate(x, zero));
        assertEquals(0.0, instance.evaluate(y, zero));
        assertEquals(0.0, instance.evaluate(zero, zero));
        
        boolean exceptionThrown = false;
        try
        {
            instance.evaluate(x, null);
        }
        catch ( NullPointerException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance.evaluate(x, new Vector2(1.0, 2.0));
        }
        catch ( DimensionalityMismatchException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}
