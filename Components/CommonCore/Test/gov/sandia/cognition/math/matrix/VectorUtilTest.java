/*
 * File:                VectorUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;

/**
 * Unit tests for class VectorUtil.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorUtilTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public VectorUtilTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructor()
    {
        System.out.println( "Constructor" );
        VectorUtil vu = new VectorUtil();
        assertNotNull( vu );
    }

    /**
     * Test of safeGetDimensionality method, of class VectorUtil.
     */
    public void testSafeGetDimensionality_Vectorizable()
    {
        Vectorizable vector = null;
        int dimensionality = -1;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));

        vector = new Vector2();
        dimensionality = 2;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));

        vector = new Vector3();
        dimensionality = 3;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));
    }

    /**
     * Test of safeGetDimensionality method, of class VectorUtil.
     */
    public void testSafeGetDimensionality_Vector()
    {
        Vector vector = null;
        int dimensionality = -1;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));
        
        vector = new Vector2();
        dimensionality = 2;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));

        vector = new Vector3();
        dimensionality = 3;
        assertEquals(dimensionality, VectorUtil.safeGetDimensionality(vector));
    }

    /**
     * Test of divideByNorm1 method, of class VectorUtil.
     */
    public void testDivideByNorm1()
    {
        Vector input = new Vector2();
        Vector inputCopy = input.clone();
        Vector result = VectorUtil.divideByNorm1(input);
        assertEquals(input, result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);

        input = new Vector2(1.0, 0.0);
        inputCopy = input.clone();
        result = VectorUtil.divideByNorm1(input);
        assertEquals(input, result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);

        input = new Vector3(1.0, 3.0, 0.0);
        inputCopy = input.clone();
        result = VectorUtil.divideByNorm1(input);
        assertEquals(new Vector3(0.25, 0.75, 0.0), result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);
    }
    
    /**
     * Test of divideByNorm1Equals method, of class VectorUtil.
     */
    public void testDivideByNorm1Equals()
    {
        Vector input = new Vector2();
        Vector expected = input.clone();
        VectorUtil.divideByNorm1Equals(input);
        assertEquals(expected, input);

        input = new Vector2(1.0, 0.0);
        expected = input.clone();
        VectorUtil.divideByNorm1Equals(input);
        assertEquals(expected, input);

        input = new Vector3(1.0, 3.0, 0.0);
        expected = new Vector3(0.25, 0.75, 0.0);
        VectorUtil.divideByNorm1Equals(input);
        assertEquals(expected, input);
    }
    
    /**
     * Test of divideByNorm1 method, of class VectorUtil.
     */
    public void testDivideByNorm2()
    {
        Vector input = new Vector2();
        Vector inputCopy = input.clone();
        Vector result = VectorUtil.divideByNorm2(input);
        assertEquals(input, result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);

        input = new Vector2(1.0, 0.0);
        inputCopy = input.clone();
        result = VectorUtil.divideByNorm2(input);
        assertEquals(input, result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);

        input = new Vector3(-1.0, 3.0, 0.0);
        inputCopy = input.clone();
        result = VectorUtil.divideByNorm2(input);
        assertEquals(new Vector3(-1 / Math.sqrt(10), 3 / Math.sqrt(10), 0.0), result);
        assertNotSame(input, result);
        assertEquals(inputCopy, input);
    }
    
    /**
     * Test of divideByNorm2Equals method, of class VectorUtil.
     */
    public void testDivideByNorm2Equals()
    {
        Vector input = new Vector2();
        Vector expected = input.clone();
        VectorUtil.divideByNorm2Equals(input);
        assertEquals(expected, input);

        input = new Vector2(1.0, 0.0);
        expected = input.clone();
        VectorUtil.divideByNorm2Equals(input);
        assertEquals(expected, input);

        input = new Vector3(-1.0, 3.0, 0.0);
        expected = new Vector3(-1 / Math.sqrt(10), 3 / Math.sqrt(10), 0.0);
        VectorUtil.divideByNorm2Equals(input);
        assertEquals(expected, input);
    }

    /**
     * Test of interpolateLinear method, of class VectorUtil.
     */
    public void testInterpolateLinear()
    {

        double epsilon = 1e-10;
        Vector first = new Vector3(3.0, 1.0, 0.0);
        Vector second = new Vector3(3.0, 2.0, -4.0);

        double alpha;
        Vector expected;
        Vector result;

        alpha = 0.0;
        expected = first.clone();
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 0.1;
        expected = new Vector3(3.0, 1.1, -0.4);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 0.2;
        expected = new Vector3(3.0, 1.2, -0.8);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 0.5;
        expected = new Vector3(3.0, 1.5, -2.0);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 0.8;
        expected = new Vector3(3.0, 1.8, -3.2);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 0.9;
        expected = new Vector3(3.0, 1.9, -3.6);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 1.0;
        expected = new Vector3(3.0, 2.0, -4.0);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));


        // Boundry cases.
        alpha = -0.1;
        expected = new Vector3(3.0, 1.0, 0.0);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));

        alpha = 1.1;
        expected = new Vector3(3.0, 2.0, -4.0);
        result = VectorUtil.interpolateLinear(first, second, alpha);
        assertTrue(expected.equals(result, epsilon));
    }

}
