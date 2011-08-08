/*
 * File:                KernelBinaryCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelBinaryCategorizerTest
    extends CategorizerTestHarness<Vector,Boolean>
{

    public final Random RANDOM = new Random(1);

    public KernelBinaryCategorizerTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(0.0, KernelBinaryCategorizer.DEFAULT_BIAS);
    }
    
    public void testConstructors()
    {
        KernelBinaryCategorizer<Vector> instance = 
            new KernelBinaryCategorizer<Vector>();
        assertNull(instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelBinaryCategorizer.DEFAULT_BIAS, instance.getBias());
        
        LinearKernel kernel = LinearKernel.getInstance();
        instance = new KernelBinaryCategorizer<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelBinaryCategorizer.DEFAULT_BIAS, instance.getBias());
        
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(
            new DefaultWeightedValue<Vector3>(new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian())) );
        double bias = RANDOM.nextGaussian();
        instance = new KernelBinaryCategorizer<Vector>(kernel, examples, bias);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(bias, instance.getBias());
        
        KernelBinaryCategorizer<Vector> copy = 
            new KernelBinaryCategorizer<Vector>(instance);
        assertNotSame(kernel, copy.getKernel());
        assertEquals(1, copy.getExamples().size());
        assertNotSame(examples, copy.getExamples());
        assertEquals(bias, copy.getBias());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testKnownValues()
    {
        LinearKernel kernel = LinearKernel.getInstance();
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(1.0, 0.0, 0.0),1.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 1.0, 0.0),0.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 0.0, 1.0),-2.0));
        double bias = 4.0;
        
        KernelBinaryCategorizer<Vector> instance = 
            new KernelBinaryCategorizer<Vector>(kernel, examples, bias);
        
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
     * Test of evaluateAsDouble method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testEvaluateAsDouble()
    {
        LinearKernel kernel = LinearKernel.getInstance();
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(1.0, 0.0, 0.0),1.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 1.0, 0.0),0.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 0.0, 1.0),-2.0));
        double bias = 4.0;
        
        KernelBinaryCategorizer<Vector> instance = 
            new KernelBinaryCategorizer<Vector>(kernel, examples, bias);
        
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
    }

    /**
     * Test of getExamples method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testGetExamples()
    {
        this.testSetExamples();
    }

    /**
     * Test of setExamples method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testSetExamples()
    {
        KernelBinaryCategorizer<Vector> instance = 
            new KernelBinaryCategorizer<Vector>();
        assertTrue(instance.getExamples().isEmpty());
        
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(
            new DefaultWeightedValue<Vector3>(new Vector3(RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian())) );
        
        instance.setExamples(examples);
        assertSame(examples, instance.getExamples());

        instance.setExamples(null);
        assertNull(instance.getExamples());
    }

    /**
     * Test of getBias method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testGetBias()
    {
        this.testSetBias();
    }

    /**
     * Test of setBias method, of class gov.sandia.cognition.learning.categorization.KernelBinaryCategorizer.
     */
    public void testSetBias()
    {
        KernelBinaryCategorizer<Vector> instance = 
            new KernelBinaryCategorizer<Vector>();
        assertEquals(0.0, KernelBinaryCategorizer.DEFAULT_BIAS);
        
        double bias = RANDOM.nextGaussian();
        instance.setBias(bias);
        assertEquals(bias, instance.getBias());
    }

    @Override
    public KernelBinaryCategorizer<Vector> createInstance()
    {
        return new KernelBinaryCategorizer<Vector>( new LinearKernel() );
    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }
    
}
