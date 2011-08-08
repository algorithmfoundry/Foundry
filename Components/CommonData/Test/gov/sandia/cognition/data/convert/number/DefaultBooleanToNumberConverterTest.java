/*
 * File:                DefaultBooleanToNumberConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert.number;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests of DefaultBooleanToNumberConverter
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultBooleanToNumberConverterTest
{

    /** The random object to use. */
    protected Random random;

    /**
     * Creates a new test.
     */
    public DefaultBooleanToNumberConverterTest()
    {
        this.random = new Random();
    }

    /**
     * Tests the constants.
     */
    @Test
    public void testConstants()
    {
        assertEquals(1.0, DefaultBooleanToNumberConverter.DEFAULT_TRUE_VALUE, 0.0);
        assertEquals(-1.0, DefaultBooleanToNumberConverter.DEFAULT_FALSE_VALUE, 0.0);
        assertEquals(0.0, DefaultBooleanToNumberConverter.DEFAULT_NULL_VALUE, 0.0);
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        Number trueValue = DefaultBooleanToNumberConverter.DEFAULT_TRUE_VALUE;
        Number falseValue = DefaultBooleanToNumberConverter.DEFAULT_FALSE_VALUE;
        Number nullValue = DefaultBooleanToNumberConverter.DEFAULT_NULL_VALUE;

        DefaultBooleanToNumberConverter instance =
            new DefaultBooleanToNumberConverter();
        assertEquals(trueValue, instance.getTrueValue());
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(nullValue, instance.getNullValue());

        trueValue = 3.0;
        falseValue = 1.5;
        nullValue = -1.0;
        instance = new DefaultBooleanToNumberConverter(trueValue, falseValue, nullValue);
        assertEquals(trueValue, instance.getTrueValue());
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(nullValue, instance.getNullValue());
    }

    /**
     * Test of evaluate method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testEvaluate()
    {
        DefaultBooleanToNumberConverter instance =
            new DefaultBooleanToNumberConverter();
        assertEquals(+1.0, instance.evaluate(true));
        assertEquals(-1.0, instance.evaluate(false));
        assertEquals(0.0, instance.evaluate(null));
    }

    /**
     * Test of convertToNumber method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testConvertToNumber()
    {
        Double trueValue = this.random.nextDouble();
        Double falseValue = this.random.nextDouble();
        Double nullValue = this.random.nextDouble();
        DefaultBooleanToNumberConverter instance =
            new DefaultBooleanToNumberConverter(trueValue, falseValue, nullValue);

        assertEquals(trueValue, instance.convertToNumber(true));
        assertEquals(falseValue, instance.convertToNumber(false));
        assertEquals(nullValue, instance.convertToNumber(null));
    }

    /**
     * Test of convertToBoolean method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testConvertToBoolean()
    {
        DefaultBooleanToNumberConverter instance =
            new DefaultBooleanToNumberConverter();

        assertEquals(true, instance.convertToBoolean(+1.0));
        assertEquals(false, instance.convertToBoolean(-1.0));
        assertEquals(null, instance.convertToBoolean(0.0));
        assertEquals(null, instance.convertToBoolean(null));
        assertEquals(true, instance.convertToBoolean(0.1));
        assertEquals(false, instance.convertToBoolean(-0.1));
        assertEquals(true, instance.convertToBoolean(4.0));
        assertEquals(false, instance.convertToBoolean(-4.0));
    }

    /**
     * Test of getTrueValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testGetTrueValue()
    {
        this.testSetTrueValue();
    }

    /**
     * Test of setTrueValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testSetTrueValue()
    {
        Number trueValue = DefaultBooleanToNumberConverter.DEFAULT_TRUE_VALUE;
        DefaultBooleanToNumberConverter instance = new DefaultBooleanToNumberConverter();
        assertEquals(trueValue, instance.getTrueValue());
        assertEquals(trueValue, instance.evaluate(true));

        trueValue = 0.0;
        instance.setTrueValue(trueValue);
        assertEquals(trueValue, instance.getTrueValue());
        assertEquals(trueValue, instance.evaluate(true));

        trueValue = -1.0;
        instance.setTrueValue(trueValue);
        assertEquals(trueValue, instance.getTrueValue());
        assertEquals(trueValue, instance.evaluate(true));
    }

    /**
     * Test of getFalseValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testGetFalseValue()
    {
        this.testSetFalseValue();
    }

    /**
     * Test of setFalseValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testSetFalseValue()
    {
        Number falseValue = DefaultBooleanToNumberConverter.DEFAULT_FALSE_VALUE;
        DefaultBooleanToNumberConverter instance = new DefaultBooleanToNumberConverter();
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(falseValue, instance.evaluate(false));
        
        falseValue = 0.0;
        instance.setFalseValue(falseValue);
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(falseValue, instance.evaluate(false));
        
        falseValue = 1.0;
        instance.setFalseValue(falseValue);
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(falseValue, instance.evaluate(false));
        
        falseValue = this.random.nextDouble();
        instance.setFalseValue(falseValue);
        assertEquals(falseValue, instance.getFalseValue());
        assertEquals(falseValue, instance.evaluate(false));
    }

    /**
     * Test of getNullValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testGetNullValue()
    {
        this.testSetNullValue();
    }

    /**
     * Test of setNullValue method, of class DefaultBooleanToNumberConverter.
     */
    @Test
    public void testSetNullValue()
    {
        Number nullValue = DefaultBooleanToNumberConverter.DEFAULT_NULL_VALUE;
        DefaultBooleanToNumberConverter instance = new DefaultBooleanToNumberConverter();
        assertEquals(nullValue, instance.getNullValue());
        assertEquals(nullValue, instance.evaluate(null));
        
        nullValue = 1.0;
        instance.setNullValue(nullValue);
        assertEquals(nullValue, instance.getNullValue());
        assertEquals(nullValue, instance.evaluate(null));
        
        nullValue = -1.0;
        instance.setNullValue(nullValue);
        assertEquals(nullValue, instance.getNullValue());
        assertEquals(nullValue, instance.evaluate(null));
        
        nullValue = this.random.nextDouble();
        instance.setNullValue(nullValue);
        assertEquals(nullValue, instance.getNullValue());
        assertEquals(nullValue, instance.evaluate(null));
        
        nullValue = null;
        instance.setNullValue(nullValue);
        assertEquals(nullValue, instance.getNullValue());
        assertEquals(nullValue, instance.evaluate(null));
    }

}