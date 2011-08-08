/*
 * File:                LinearBinaryCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 6, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     LinearBinaryCategorizer
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LinearBinaryCategorizerTest
    extends CategorizerTestHarness<Vectorizable,Boolean>
{
    public LinearBinaryCategorizerTest(
        String testName)
    {
        super(testName);
    }
    
    public void testStatics()
    {
        assertEquals(0.0, LinearBinaryCategorizer.DEFAULT_BIAS);
    }
    
    public void testConstructors()
    {
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer();
        assertNull(instance.getWeights());
        assertEquals(0.0, instance.getBias());
        
        Vector weights = this.createRandomInput();
        double bias = RANDOM.nextGaussian();
        instance = new LinearBinaryCategorizer(weights, bias);
        assertEquals(weights, instance.getWeights());
        assertEquals(bias, instance.getBias());
        
        LinearBinaryCategorizer copy = new LinearBinaryCategorizer(instance);
        assertEquals(instance.getWeights(), copy.getWeights());
        assertNotSame(instance.getWeights(), copy.getWeights());
        assertEquals(instance.getBias(), copy.getBias());
        
        instance = new LinearBinaryCategorizer();
        copy = new LinearBinaryCategorizer(instance);
        assertEquals(instance.getWeights(), copy.getWeights());
        assertNull(copy.getWeights());
        assertEquals(instance.getBias(), copy.getBias());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testCloneLocal()
    {
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer(
            new Vector3(1.0, 0.0, -2.0), 4.0);
        LinearBinaryCategorizer clone = instance.clone();
        
        assertNotSame(instance, clone);
        assertEquals(instance.getWeights(), clone.getWeights());
        assertEquals(instance.getBias(), clone.getBias());
        
        instance = new LinearBinaryCategorizer();
        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getWeights(), clone.getWeights());
        assertEquals(instance.getBias(), clone.getBias());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testKnownValues()
    {
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer(
            new Vector3(1.0, 0.0, -2.0), 4.0);

        Vector input = new Vector3(1.0, 1.0, 1.0);
        Boolean output = true;
        assertEquals(output, instance.evaluate(input));

        input = new Vector3(0.0, 7.0, 3.0);
        output = false;
        assertEquals(output, instance.evaluate(input));

        input = new Vector3(0.0, 0.0, 0.0);
        output = true;
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
     * Test of evaluateAsDouble method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testEvaluateAsDouble()
    {
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer(
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

        instance = new LinearBinaryCategorizer();
        assertEquals(0.0, instance.evaluateAsDouble(input), 0.0);
        instance.setBias(3.4);
        assertEquals(3.4, instance.evaluateAsDouble(input), 0.0);
    }

    /**
     * Test of getWeights method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testGetWeights()
    {
        this.testSetWeights();
    }

    /**
     * Test of setWeights method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testSetWeights()
    {
        Vector weights = null;
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer();
        assertNull(instance.getWeights());
        
        weights = new Vector3();
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
        
        
        weights = this.createRandomInput();
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
        
        weights = null;
        instance.setWeights(weights);
        assertNull(weights);
    }

    /**
     * Test of getBias method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testGetBias()
    {
        this.testSetBias();
    }

    /**
     * Test of setBias method, of class gov.sandia.cognition.learning.categorization.LinearBinaryCategorizer.
     */
    public void testSetBias()
    {
        double bias = 0.0;
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer();
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
    
    public void testToString()
    {
        LinearBinaryCategorizer instance = new LinearBinaryCategorizer();
        assertNotNull(instance.toString());
    }

    @Override
    public LinearBinaryCategorizer createInstance()
    {
        return new LinearBinaryCategorizer( this.createRandomInput(), RANDOM.nextGaussian() );
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }

}
