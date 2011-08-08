/*
 * File:                SubVectorEvaluatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import junit.framework.TestCase;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class SubVectorEvaluatorTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public SubVectorEvaluatorTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    /**
     * Test of constructors of class SubVectorEvaluator.
     */
    public void testConstructors()
    {
        int inputDimensionality = -1;
        int[] subIndices = null;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        SubVectorEvaluator instance = new SubVectorEvaluator();
        assertEquals(inputDimensionality, instance.getInputDimensionality());
        assertSame(subIndices, instance.getSubIndices());
        assertSame(vectorFactory, instance.getVectorFactory());

        inputDimensionality = 23;
        subIndices = new int[] { 3, 4, 9 };
        instance = new SubVectorEvaluator(inputDimensionality, subIndices);
        assertEquals(inputDimensionality, instance.getInputDimensionality());
        assertSame(subIndices, instance.getSubIndices());
        assertSame(vectorFactory, instance.getVectorFactory());


        inputDimensionality = 23;
        subIndices = new int[] { 3, 4, 9 };
        vectorFactory = VectorFactory.getSparseDefault();
        instance = new SubVectorEvaluator(inputDimensionality, subIndices, vectorFactory);
        assertEquals(inputDimensionality, instance.getInputDimensionality());
        assertSame(subIndices, instance.getSubIndices());
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of evaluate method, of class SubVectorEvaluator.
     */
    public void testEvaluate()
    {
        SubVectorEvaluator instance = new SubVectorEvaluator();
        assertNull(instance.evaluate(null));

        // This is a basic functionality test.
        instance.setSubIndices(new int[] { 4, 7 });
        instance.setInputDimensionality(10);
        Vector input = VectorFactory.getDefault().createUniformRandom(
            10, -1.0, 1.0, random);
        Vector result = instance.evaluate(input);
        assertEquals(input.getElement(4), result.getElement(0));
        assertEquals(input.getElement(7), result.getElement(1));

        // This tests some funny cases, such as having duplicate indices and
        // non-monotonic indices.
        instance.setSubIndices(new int[] { 4, 7, 3, 3, 6 });
        result = instance.evaluate(input);
        assertEquals(input.getElement(4), result.getElement(0));
        assertEquals(input.getElement(7), result.getElement(1));
        assertEquals(input.getElement(3), result.getElement(2));
        assertEquals(input.getElement(3), result.getElement(3));
        assertEquals(input.getElement(6), result.getElement(4));
    }

    /**
     * Test of getInputDimensionality method, of class SubVectorEvaluator.
     */
    public void testGetInputDimensionality()
    {
        this.testSetInputDimensionality();
    }

    /**
     * Test of setInputDimensionality method, of class SubVectorEvaluator.
     */
    public void testSetInputDimensionality()
    {
        int inputDimensionality = -1;
        SubVectorEvaluator instance = new SubVectorEvaluator();
        assertEquals(inputDimensionality, instance.getInputDimensionality());

        inputDimensionality = 0;
        instance.setInputDimensionality(inputDimensionality);
        assertEquals(inputDimensionality, instance.getInputDimensionality());

        inputDimensionality = 1;
        instance.setInputDimensionality(inputDimensionality);
        assertEquals(inputDimensionality, instance.getInputDimensionality());

        inputDimensionality = 1010109;
        instance.setInputDimensionality(inputDimensionality);
        assertEquals(inputDimensionality, instance.getInputDimensionality());
        
        inputDimensionality = -1;
        instance.setInputDimensionality(inputDimensionality);
        assertEquals(inputDimensionality, instance.getInputDimensionality());
    }

    /**
     * Test of getOutputDimensionality method, of class SubVectorEvaluator.
     */
    public void testGetOutputDimensionality()
    {
        SubVectorEvaluator instance = new SubVectorEvaluator();
        instance.setSubIndices(new int[] { 2, 4, 9 });
        assertEquals(3, instance.getOutputDimensionality());
    }

    /**
     * Test of getSubIndices method, of class SubVectorEvaluator.
     */
    public void testGetSubIndices()
    {
        this.testSetSubIndices();
    }

    /**
     * Test of setSubIndices method, of class SubVectorEvaluator.
     */
    public void testSetSubIndices()
    {
        int[] subIndices = null;
        SubVectorEvaluator instance = new SubVectorEvaluator();
        assertSame(subIndices, instance.getSubIndices());

        subIndices = new int[] { };
        instance.setSubIndices(subIndices);
        assertSame(subIndices, instance.getSubIndices());

        subIndices = new int[] { 4, 5, 9 };
        instance.setSubIndices(subIndices);
        assertSame(subIndices, instance.getSubIndices());

        subIndices = null;
        instance.setSubIndices(subIndices);
        assertSame(subIndices, instance.getSubIndices());
    }

}
