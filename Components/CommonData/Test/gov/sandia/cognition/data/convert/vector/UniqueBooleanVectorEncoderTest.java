/*
 * File:                UniqueBooleanVectorEncoderTest.java
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

package gov.sandia.cognition.data.convert.vector;

import gov.sandia.cognition.data.convert.number.DefaultBooleanToNumberConverter;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests of UniqueBooleanVectorEncoder
 * @author  Justin Basilico
 * @since   2.1
 */
public class UniqueBooleanVectorEncoderTest
{

    /**
     * Creates a new test.
     */
    public UniqueBooleanVectorEncoderTest()
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        String[] valuesArray =
        {
            "yes", "no", "maybe"
        };
        List<String> values = Arrays.asList(valuesArray);
        NumberConverterToVectorAdapter<Boolean> booleanConverter =
            new NumberConverterToVectorAdapter<Boolean>(
            new DefaultBooleanToNumberConverter());

        UniqueBooleanVectorEncoder<String> instance =
            new UniqueBooleanVectorEncoder<String>(values, booleanConverter);
        assertSame(values, instance.getValues());
        assertSame(booleanConverter, instance.getBooleanConverter());
    }

    /**
     * Test of evaluate method, of class UniqueBooleanVectorEncoder.
     */
    @Test
    public void testEvaluate()
    {
        LinkedList<String> values = new LinkedList<String>();
        values.add("a");
        values.add("b");
        
        UniqueBooleanVectorEncoder<String> instance =
            new UniqueBooleanVectorEncoder<String>(values, 
            new NumberConverterToVectorAdapter<Boolean>(
                new DefaultBooleanToNumberConverter()));
        
        assertEquals(new Vector2(+1.0, -1.0), instance.evaluate("a"));
        assertEquals(new Vector2(-1.0, +1.0), instance.evaluate("b"));
        assertEquals(new Vector2(-1.0, -1.0), instance.evaluate("c"));
        assertEquals(new Vector2(-1.0, -1.0), instance.evaluate("ab"));
        assertEquals(new Vector2(0.0, 0.0), instance.evaluate(null));
        
        values.add("a");
        assertEquals(new Vector3(+1.0, -1.0, +1.0), instance.evaluate("a"));
        assertEquals(new Vector3(-1.0, +1.0, -1.0), instance.evaluate("b"));
        assertEquals(new Vector3(-1.0, -1.0, -1.0), instance.evaluate("c"));
        assertEquals(new Vector3(-1.0, -1.0, -1.0), instance.evaluate("ab"));
        assertEquals(new Vector3(0.0, 0.0, 0.0), instance.evaluate(null));
    }

    /**
     * Test of getOutputDimensionality method, of class UniqueBooleanVectorEncoder.
     */
    @Test
    public void testGetOutputDimensionality()
    {
        LinkedList<String> values = new LinkedList<String>();
        UniqueBooleanVectorEncoder<String> instance =
            new UniqueBooleanVectorEncoder<String>(values, 
            new NumberConverterToVectorAdapter<Boolean>(
                new DefaultBooleanToNumberConverter()));
        assertEquals(0, instance.getOutputDimensionality());
        
        values.add("a");
        
        assertEquals(1, instance.getOutputDimensionality());
        
        values.add("b");
        assertEquals(2, instance.getOutputDimensionality());
    }

    /**
     * Test of getValues method, of class UniqueBooleanVectorEncoder.
     */
    @Test
    public void testGetValues()
    {
        String[] valuesArray =
        {
            "yes", "no", "maybe"
        };
        List<String> values = Arrays.asList(valuesArray);
        UniqueBooleanVectorEncoder<String> instance =
            new UniqueBooleanVectorEncoder<String>(values, null);
        assertSame(values, instance.getValues());
    }

    /**
     * Test of getBooleanConverter method, of class UniqueBooleanVectorEncoder.
     */
    @Test
    public void testGetBooleanConverter()
    {
        NumberConverterToVectorAdapter<Boolean> booleanConverter =
            new NumberConverterToVectorAdapter<Boolean>(
            new DefaultBooleanToNumberConverter());
        UniqueBooleanVectorEncoder<String> instance =
            new UniqueBooleanVectorEncoder<String>(null, booleanConverter);
        assertSame(booleanConverter, instance.getBooleanConverter());
    }

}