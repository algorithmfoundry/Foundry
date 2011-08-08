/*
 * File:                AtanFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;

/**
 *
 * @author jdbasil
 */
public class AtanFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{

    public AtanFunctionTest(String testName)
    {
        super(testName);
    }

    @Override
    public AtanFunction createInstance()
    {
        return new AtanFunction(RANDOM.nextDouble());
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AtanFunction f = new AtanFunction();
        assertEquals( AtanFunction.DEFAULT_MAX_MAGNITUDE, f.getMaxMagnitude() );

        double mag = RANDOM.nextDouble();
        f = new AtanFunction( mag );
        assertEquals( mag, f.getMaxMagnitude() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.AtanFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate (Scalar)");

        double maxMag = RANDOM.nextDouble();
        AtanFunction f1 = new AtanFunction(maxMag);

        assertEquals(0.0, f1.evaluate(0.0));

        double x1 = 1e7;
        double y1 = maxMag;

        assertEquals(y1, f1.evaluate(x1), TOLERANCE);
        assertEquals(-y1, f1.evaluate(-x1), TOLERANCE);

        double x2 = 1.0;
        double y2 = 0.5 * maxMag;
        assertEquals(y2, f1.evaluate(x2), TOLERANCE);
        assertEquals(-y2, f1.evaluate(-x2), TOLERANCE);

        double x3 = 0.1;
        double y3 = 0.063451 * maxMag;
        assertEquals(y3, f1.evaluate(x3), TOLERANCE);
        assertEquals(-y3, f1.evaluate(-x3), TOLERANCE);
    }

    /**
     * Test of getMaxMagnitude method, of class gov.sandia.isrc.learning.util.function.AtanFunction.
     */
    public void testGetMaxMagnitude()
    {
        System.out.println("getMaxMagnitude");
        AtanFunction instance = new AtanFunction();
        assertEquals(AtanFunction.DEFAULT_MAX_MAGNITUDE, instance.getMaxMagnitude());
    }

    /**
     * Test of setMaxMagnitude method, of class gov.sandia.isrc.learning.util.function.AtanFunction.
     */
    public void testSetMaxMagnitude()
    {
        System.out.println("setMaxMagnitude");

        double maxMagnitude = RANDOM.nextDouble();
        AtanFunction instance =
            new AtanFunction(maxMagnitude);
        assertEquals(maxMagnitude, instance.getMaxMagnitude());

        double newMaxMagnitude = RANDOM.nextDouble();
        instance.setMaxMagnitude(newMaxMagnitude);
        assertEquals(newMaxMagnitude, instance.getMaxMagnitude());

    }

    /**
     * Test of differentiate method, of class gov.sandia.cognition.learning.util.function.AtanFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");

        for (int i = 0; i < 100; i++)
        {
            AtanFunction instance = this.createInstance();
            double x = RANDOM.nextGaussian();
            double y = instance.getMaxMagnitude() * 2.0 / Math.PI / (1.0 + x * x);
            assertEquals(y, instance.differentiate(x), TOLERANCE);

        }
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.AtanFunction.
     */
    public void testClone()
    {
        System.out.println("clone");

        AtanFunction instance = this.createInstance();
        AtanFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getMaxMagnitude(), clone.getMaxMagnitude());
    }

    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.util.function.AtanFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        AtanFunction instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals(1, p.getDimensionality());
        assertEquals(instance.getMaxMagnitude(), p.getElement(0));
    }

    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.util.function.AtanFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        Vector parameters = VectorFactory.getDefault().copyValues(RANDOM.nextDouble());
        AtanFunction instance = this.createInstance();
        assertFalse(parameters.equals(instance.convertToVector()));
        instance.convertFromVector(parameters);
        assertTrue(parameters.equals(instance.convertToVector(), 1e-5));
        assertEquals(parameters.getElement(0), instance.getMaxMagnitude(), 1e-5);

    }

}
