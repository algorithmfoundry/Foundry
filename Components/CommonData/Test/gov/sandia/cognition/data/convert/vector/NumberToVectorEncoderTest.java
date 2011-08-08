/*
 * File:                NumberToVectorEncoderTest.java
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

import gov.sandia.cognition.math.matrix.mtj.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class NumberToVectorEncoder.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class NumberToVectorEncoderTest
{

    /**
     * Creates a new test.
     */
    public NumberToVectorEncoderTest()
    {
    }

    /**
     * Tests the constructors.
     */
    @Test
    public void testConstructors()
    {
        NumberToVectorEncoder instance = new NumberToVectorEncoder();
        assertNotNull(instance);
    }

    /**
     * Test of encode method, of class NumberToVectorEncoder.
     */
    @Test
    public void testEncode()
    {
        NumberToVectorEncoder instance = new NumberToVectorEncoder();

        Vector2 vector = new Vector2();

        instance.encode(4.7, vector, 1);
        assertEquals(new Vector2(0.0, 4.7), vector);
        
        instance.encode(2, vector, 0);
        assertEquals(new Vector2(2.0, 4.7), vector);
        
        instance.encode(null, vector, 1);
        assertEquals(new Vector2(2.0, 0.0), vector);
    }

    /**
     * Test of getOutputDimensionality method, of class NumberToVectorEncoder.
     */
    @Test
    public void testGetDimensionality()
    {
        NumberToVectorEncoder instance = new NumberToVectorEncoder();
        assertEquals(1, instance.getOutputDimensionality());
    }

}