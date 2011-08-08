/*
 * File:                ThresholdFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class ThresholdFunctionTest
    extends TestCase
{

    /** The random number generator for the tests. */
    public final Random random = new Random(1);
    
    /**
     * 
     * @param testName
     */
    public ThresholdFunctionTest(String testName)
    {
        super(testName);
    }

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ThresholdFunction instance = new ThresholdFunction();
        assertNotNull( instance );
        assertEquals( ThresholdFunction.DEFAULT_THRESHOLD, instance.getThreshold() );
        assertEquals( ThresholdFunction.DEFAULT_LOW_VALUE, instance.getLowValue() );
        assertEquals( ThresholdFunction.DEFAULT_HIGH_VALUE, instance.getHighValue() );

        double threshold = random.nextGaussian();
        instance = new ThresholdFunction( threshold );
        assertEquals( threshold, instance.getThreshold() );
        assertEquals( ThresholdFunction.DEFAULT_LOW_VALUE, instance.getLowValue() );
        assertEquals( ThresholdFunction.DEFAULT_HIGH_VALUE, instance.getHighValue() );

        double lowValue = random.nextGaussian();
        double highValue = random.nextGaussian();
        instance = new ThresholdFunction( threshold, lowValue, highValue );
        assertEquals( threshold, instance.getThreshold() );
        assertEquals( lowValue, instance.getLowValue() );
        assertEquals( highValue, instance.getHighValue() );


    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testClone()
    {
        System.out.println("clone");

        ThresholdFunction instance = new ThresholdFunction(random.nextGaussian());
        ThresholdFunction clone = instance.clone();
        assertEquals(instance.getThreshold(), clone.getThreshold());

    }

    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        ThresholdFunction instance = new ThresholdFunction(random.nextGaussian());
        Vector parameters = instance.convertToVector();
        assertEquals(1, parameters.getDimensionality());
        assertEquals(instance.getThreshold(), parameters.getElement(0));
    }

    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        double threshold = random.nextGaussian();
        ThresholdFunction instance = new ThresholdFunction(threshold);
        assertEquals(threshold, instance.getThreshold());

        Vector parameters = VectorFactory.getDefault().createUniformRandom(1, 10, 100, random);
        assertFalse(parameters.getElement(0) == instance.getThreshold());

        instance.convertFromVector(parameters);
        assertEquals(parameters.getElement(0), instance.getThreshold());


    }

    /**
     * Test of getThreshold method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testGetThreshold()
    {
        System.out.println("getThreshold");

        double threshold = random.nextGaussian();
        ThresholdFunction instance = new ThresholdFunction(threshold);
        assertEquals(threshold, instance.getThreshold());

    }

    /**
     * Test of setThreshold method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testSetThreshold()
    {
        System.out.println("setThreshold");

        double threshold = random.nextDouble();
        ThresholdFunction instance = new ThresholdFunction(threshold);
        assertEquals(threshold, instance.getThreshold());

        double t2 = threshold + 1.0;
        instance.setThreshold(t2);
        assertEquals(t2, instance.getThreshold());


    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        double threshold = random.nextGaussian();
        ThresholdFunction f = new ThresholdFunction(threshold);

        assertEquals(f.getHighValue(), f.evaluate(threshold));
        assertEquals(f.getHighValue(), f.evaluate(threshold + 1.0));
        assertEquals(f.getLowValue(), f.evaluate(threshold - 1.0));

        for (int n = 0; n < 100; n++)
        {
            Double x = random.nextGaussian();
            double y = (x >= threshold) ? f.getHighValue() : f.getLowValue();
            assertEquals(y, f.evaluate(x));
        }
    }

    /**
     * Test of getHighValue method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testGetHighValue()
    {
        System.out.println("getHighValue");

        double threshold = random.nextGaussian();
        double lowValue = random.nextGaussian();
        double highValue = random.nextGaussian();
        ThresholdFunction f = new ThresholdFunction(threshold, lowValue, highValue);

        assertEquals(highValue, f.getHighValue());

    }

    /**
     * Test of setHighValue method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testSetHighValue()
    {
        System.out.println("setHighValue");

        double threshold = random.nextGaussian();
        double lowValue = random.nextGaussian();
        double highValue = random.nextGaussian();
        ThresholdFunction f = new ThresholdFunction(threshold, lowValue, highValue);

        assertEquals(highValue, f.getHighValue());
        double h2 = highValue - 1.0;
        f.setHighValue(h2);
        assertEquals(h2, f.getHighValue());
    }

    /**
     * Test of getLowValue method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testGetLowValue()
    {
        System.out.println("getLowValue");

        double threshold = random.nextGaussian();
        double lowValue = random.nextGaussian();
        double highValue = random.nextGaussian();
        ThresholdFunction f = new ThresholdFunction(threshold, lowValue, highValue);

        assertEquals(lowValue, f.getLowValue());

    }

    /**
     * Test of setLowValue method, of class gov.sandia.cognition.learning.util.function.ThresholdFunction.
     */
    public void testSetLowValue()
    {
        System.out.println("setLowValue");

        double threshold = random.nextGaussian();
        double lowValue = random.nextGaussian();
        double highValue = random.nextGaussian();
        ThresholdFunction f = new ThresholdFunction(threshold, lowValue, highValue);

        assertEquals(lowValue, f.getLowValue());
        double l2 = lowValue - 1.0;
        f.setLowValue(l2);
        assertEquals(l2, f.getLowValue());
    }

}
