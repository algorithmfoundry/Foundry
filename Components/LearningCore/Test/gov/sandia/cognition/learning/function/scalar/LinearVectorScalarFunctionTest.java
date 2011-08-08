/*
 * File:                LinearVectorScalarFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright December 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     LinearVectorScalarFunction
 * 
 * @author Justin Basilico
 */
public class LinearVectorScalarFunctionTest 
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public LinearVectorScalarFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(0.0, LinearVectorScalarFunction.DEFAULT_BIAS);
    }
    
    public void testConstructors()
    {
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction();
        assertNull(instance.getWeights());
        assertEquals(0.0, instance.getBias());
        
        Vector weights = Vector3.createRandom(RANDOM);
        double bias = RANDOM.nextGaussian();
        instance = new LinearVectorScalarFunction(weights, bias);
        assertEquals(weights, instance.getWeights());
        assertEquals(bias, instance.getBias());
        
        LinearVectorScalarFunction copy = new LinearVectorScalarFunction(instance);
        assertEquals(instance.getWeights(), copy.getWeights());
        assertNotSame(instance.getWeights(), copy.getWeights());
        assertEquals(instance.getBias(), copy.getBias());
        
        instance = new LinearVectorScalarFunction();
        copy = new LinearVectorScalarFunction(instance);
        assertEquals(instance.getWeights(), copy.getWeights());
        assertNull(copy.getWeights());
        assertEquals(instance.getBias(), copy.getBias());
    }

    /**
     * Test of clone method, of class LinearVectorScalarFunction.
     */
    public void testClone()
    {
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction(
            new Vector3(1.0, 0.0, -2.0), 4.0);
        LinearVectorScalarFunction clone = instance.clone();
        
        assertNotSame(instance, clone);
        assertEquals(instance.getWeights(), clone.getWeights());
        assertEquals(instance.getBias(), clone.getBias());
        
        instance = new LinearVectorScalarFunction();
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getWeights(), clone.getWeights());
        assertEquals(instance.getBias(), clone.getBias());
    }

    /**
     * Test of evaluate method, of class LinearVectorScalarFunction.
     */
    public void testEvaluate()
    {
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction(
            new Vector3(1.0, 0.0, -2.0), 4.0);

        Vector input = new Vector3(1.0, 1.0, 1.0);
        double output = 3.0;
        assertEquals(output, instance.evaluate(input));

        input = new Vector3(0.0, 7.0, 3.0);
        output = -2.0;
        assertEquals(output, instance.evaluate(input));

        input = new Vector3(0.0, 0.0, 0.0);
        output = 4.0;
        assertEquals(output, instance.evaluate(input));

        boolean exceptionThrown = false;
        try
        {
            instance.evaluate(null);
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
            instance.evaluate(new Vector2(1.0, 2.0));
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

    /**
     * Test of evaluateAsDouble method, of class LinearVectorScalarFunction.
     */
    public void testEvaluateAsDouble()
    {
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction(
            new Vector3(1.0, 0.0, -2.0), 4.0);

        Vector input = new Vector3(1.0, 1.0, 1.0);
        double output = 3.0;
        assertEquals(output, instance.evaluateAsDouble(input));

        input = new Vector3(0.0, 7.0, 3.0);
        output = -2.0;
        assertEquals(output, instance.evaluateAsDouble(input));

        input = new Vector3(0.0, 0.0, 0.0);
        output = 4.0;
        assertEquals(output, instance.evaluateAsDouble(input));

        boolean exceptionThrown = false;
        try
        {
            instance.evaluateAsDouble(null);
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
            instance.evaluateAsDouble(new Vector2(1.0, 2.0));
        }
        catch ( DimensionalityMismatchException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        instance = new LinearVectorScalarFunction();
        assertEquals(0.0, instance.evaluateAsDouble(input), 0.0);
        instance.setBias(3.4);
        assertEquals(3.4, instance.evaluateAsDouble(input), 0.0);
    }

    /**
     * Test of getWeights method, of class LinearVectorScalarFunction.
     */
    public void testGetWeights()
    {
        this.testSetWeights();
    }

    /**
     * Test of setWeights method, of class LinearVectorScalarFunction.
     */
    public void testSetWeights()
    {
        Vector weights = null;
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction();
        assertNull(instance.getWeights());
        
        weights = new Vector3();
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
        
        
        weights = Vector3.createRandom(RANDOM);
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
        
        weights = null;
        instance.setWeights(weights);
        assertNull(weights);
    }

    /**
     * Test of getBias method, of class LinearVectorScalarFunction.
     */
    public void testGetBias()
    {
        this.testSetBias();
    }

    /**
     * Test of setBias method, of class LinearVectorScalarFunction.
     */
    public void testSetBias()
    {
        double bias = 0.0;
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction();
        assertEquals(bias, instance.getBias());
        
        bias = 4.0;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias());
        
        bias = -7.0;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias());
        
        bias = 0.0;
        instance.setBias(bias);
        assertEquals(bias, instance.getBias());
    }

    /**
     * Test of toString method, of class LinearVectorScalarFunction.
     */
    public void testToString()
    {
        LinearVectorScalarFunction instance = new LinearVectorScalarFunction();
        assertNotNull(instance.toString());
    }

}
