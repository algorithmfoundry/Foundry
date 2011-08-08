/*
 * File:                KernelScalarFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: KernelScalarFunctionTest
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelScalarFunctionTest
    extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public KernelScalarFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(0.0, KernelScalarFunction.DEFAULT_BIAS);
    }
    
    public void testConstructors()
    {
        KernelScalarFunction<Vector> instance = 
            new KernelScalarFunction<Vector>();
        assertNull(instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelScalarFunction.DEFAULT_BIAS, instance.getBias());
        
        LinearKernel kernel = LinearKernel.getInstance();
        instance = new KernelScalarFunction<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelScalarFunction.DEFAULT_BIAS, instance.getBias());
        
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(
            new DefaultWeightedValue<Vector3>(Vector3.createRandom(RANDOM), RANDOM.nextDouble() ) );
        double bias = RANDOM.nextDouble();
        instance = new KernelScalarFunction<Vector>(kernel, examples, bias);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(bias, instance.getBias());
        
        KernelScalarFunction<Vector> copy = 
            new KernelScalarFunction<Vector>(instance);
        assertNotSame(kernel, copy.getKernel());
        assertEquals(1, copy.getExamples().size());
        assertNotSame(examples, copy.getExamples());
        assertEquals(bias, copy.getBias());

        instance.setExamples(null);
        copy = new KernelScalarFunction<Vector>(instance);
        assertNotNull( copy );
        assertNotSame( instance, copy );
        assertNull( copy.getExamples() );
    }

    public void testClone()
    {
        System.out.println( "Clone" );

        LinearKernel kernel = LinearKernel.getInstance();
        ArrayList<WeightedValue<Vector3>> examples =
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(
            new DefaultWeightedValue<Vector3>(Vector3.createRandom(RANDOM), RANDOM.nextDouble() ) );
        double bias = RANDOM.nextDouble();
        KernelScalarFunction<Vector> instance =
            new KernelScalarFunction<Vector>(kernel, examples, bias);

        KernelScalarFunction<Vector> clone =
            (KernelScalarFunction<Vector>) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getBias(), clone.getBias() );
        assertNotNull( clone.getExamples() );
        assertSame( instance.getExamples(), clone.getExamples() );
        assertNotNull( clone.getKernel() );
        assertNotSame( instance.getKernel(), clone.getKernel() );

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.KernelScalarFunction.
     */
    public void testEvaluate()
    {
        LinearKernel kernel = LinearKernel.getInstance();
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(1.0, 0.0, 0.0),1.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 1.0, 0.0),0.0));
        examples.add(new DefaultWeightedValue<Vector3>(new Vector3(0.0, 0.0, 1.0),-2.0));
        double bias = 4.0;
        
        KernelScalarFunction<Vector> instance = 
            new KernelScalarFunction<Vector>(kernel, examples, bias);
        
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
     * Test of getExamples method, of class gov.sandia.cognition.learning.util.function.KernelScalarFunction.
     */
    public void testGetExamples()
    {
        this.testSetExamples();
    }

    /**
     * Test of setExamples method, of class gov.sandia.cognition.learning.util.function.KernelScalarFunction.
     */
    public void testSetExamples()
    {
        KernelScalarFunction<Vector> instance = 
            new KernelScalarFunction<Vector>();
        assertTrue(instance.getExamples().isEmpty());
        
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add( new DefaultWeightedValue<Vector3>(
            Vector3.createRandom(RANDOM), RANDOM.nextDouble() ) );
        
        instance.setExamples(examples);
        assertSame(examples, instance.getExamples());

        instance.setExamples(null);
        assertNull(instance.getExamples());
    }

    /**
     * Test of getBias method, of class gov.sandia.cognition.learning.util.function.KernelScalarFunction.
     */
    public void testGetBias()
    {
        this.testSetBias();
    }

    /**
     * Test of setBias method, of class gov.sandia.cognition.learning.util.function.KernelScalarFunction.
     */
    public void testSetBias()
    {
        KernelScalarFunction<Vector> instance = 
            new KernelScalarFunction<Vector>();
        assertEquals(0.0, KernelScalarFunction.DEFAULT_BIAS);
        
        double bias = RANDOM.nextDouble();
        instance.setBias(bias);
        assertEquals(bias, instance.getBias());
    }
}
