/*
 * File:                ValueClamperTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 03, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import junit.framework.TestCase;

/**
 * Tests of ValueClamper
 * @author  Justin Basilico
 * @since   3.0
 */
public class ValueClamperTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ValueClamperTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test of constructors of class ValueClamper.
     */
    public void testConstructors()
    {
        Double minimum = null;
        Double maximum = null;
        ValueClamper<Double> instance = new ValueClamper<Double>();
        assertSame(minimum, instance.getMinimum());
        assertSame(maximum, instance.getMaximum());
        
        minimum = 10.01;
        maximum = 20.02;
        instance = new ValueClamper<Double>(minimum, maximum);
        assertSame(minimum, instance.getMinimum());
        assertSame(maximum, instance.getMaximum());
    }

    public void testClone()
    {
        System.out.println( "Clone" );

        Double minimum = 10.0;
        Double maximum = 20.0;
        ValueClamper<Double> instance = new ValueClamper<Double>(minimum, maximum);
        ValueClamper<Double> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );


    }

    /**
     * Test of evaluate method, of class ValueClamper.
     */
    public void testEvaluate()
    {
        Double minimum = 10.01;
        Double maximum = 20.02;
        Double input = 0.0;
        ValueClamper<Double> instance = new ValueClamper<Double>(
            minimum, maximum);

        assertNull( instance.evaluate(null) );

        input = 15.51;
        assertSame(input, instance.evaluate(input));
        
        input = 10.00;
        assertSame(minimum, instance.evaluate(input));
        
        input = 20.03;
        assertSame(maximum, instance.evaluate(input));
        
        input = 0.0;
        instance.setMinimum(null);
        assertSame(input, instance.evaluate(input));
        
        input = 40.00;
        instance.setMaximum(null);
        assertSame(input, instance.evaluate(input));
    }

    /**
     * Test of getMinimum method, of class ValueClamper.
     */
    public void testGetMinimum()
    {
        this.testSetMinimum();
    }

    /**
     * Test of setMinimum method, of class ValueClamper.
     */
    public void testSetMinimum()
    {
        Double minimum = null;
        ValueClamper<Double> instance = new ValueClamper<Double>();
        assertSame(minimum, instance.getMinimum());
        
        minimum = 20.08;
        instance.setMinimum(minimum);
        assertSame(minimum, instance.getMinimum());
        
        minimum = 10.03;
        instance.setMinimum(minimum);
        assertSame(minimum, instance.getMinimum());
        
        minimum = null;
        instance.setMinimum(minimum);
        assertSame(minimum, instance.getMinimum());
    }

    /**
     * Test of getMaximum method, of class ValueClamper.
     */
    public void testGetMaximum()
    {
        this.testSetMaximum();
    }

    /**
     * Test of setMaximum method, of class ValueClamper.
     */
    public void testSetMaximum()
    {
        Double maximum = null;
        ValueClamper<Double> instance = new ValueClamper<Double>();
        assertSame(maximum, instance.getMaximum());
        
        maximum = 20.08;
        instance.setMaximum(maximum);
        assertSame(maximum, instance.getMaximum());
        
        maximum = 10.03;
        instance.setMaximum(maximum);
        assertSame(maximum, instance.getMaximum());
        
        maximum = null;
        instance.setMaximum(maximum);
        assertSame(maximum, instance.getMaximum());
    }

}
