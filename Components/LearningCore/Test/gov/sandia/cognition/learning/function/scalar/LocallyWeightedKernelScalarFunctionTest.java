/*
 * File:                LocallyWeightedKernelScalarFunctionTest.java
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
 * This class implements JUnit tests for the following classes:
 * LocallyWeightedKernelScalarFunctionTest
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LocallyWeightedKernelScalarFunctionTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public LocallyWeightedKernelScalarFunctionTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(0.0, LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT);
        assertEquals(0.0, LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_VALUE);
    }
    
    public void testConstructors()
    {
        LocallyWeightedKernelScalarFunction<Vector> instance = 
            new LocallyWeightedKernelScalarFunction<Vector>();
        assertNull(instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelScalarFunction.DEFAULT_BIAS, instance.getBias());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantWeight());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_VALUE,
            instance.getConstantValue());
        
        LinearKernel kernel = LinearKernel.getInstance();
        instance = new LocallyWeightedKernelScalarFunction<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(KernelScalarFunction.DEFAULT_BIAS, instance.getBias());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantWeight());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_VALUE,
            instance.getConstantValue());
        
        ArrayList<WeightedValue<Vector3>> examples = 
            new ArrayList<WeightedValue<Vector3>>();
        examples.add( new DefaultWeightedValue<Vector3>(Vector3.createRandom(RANDOM)) );
        
        instance = new LocallyWeightedKernelScalarFunction<Vector>(kernel, examples);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(KernelScalarFunction.DEFAULT_BIAS, instance.getBias());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantWeight());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_VALUE,
            instance.getConstantValue());
        
        
        double bias = RANDOM.nextDouble();
        instance = new LocallyWeightedKernelScalarFunction<Vector>(kernel, examples, bias);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(bias, instance.getBias());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantWeight());
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_VALUE,
            instance.getConstantValue());
        
        
        
        double constantWeight = RANDOM.nextDouble();
        double constantValue = RANDOM.nextDouble();
        
        instance = new LocallyWeightedKernelScalarFunction<Vector>(kernel, examples, bias, constantWeight, constantValue);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(bias, instance.getBias());
        assertEquals(constantWeight, instance.getConstantWeight());
        assertEquals(constantValue, instance.getConstantValue());
        
        LocallyWeightedKernelScalarFunction<Vector> copy = 
            new LocallyWeightedKernelScalarFunction<Vector>(instance);
        assertNotSame(kernel, copy.getKernel());
        assertEquals(1, copy.getExamples().size());
        assertNotSame(examples, copy.getExamples());
        assertEquals(bias, copy.getBias());
        assertEquals(constantWeight, copy.getConstantWeight());
        assertEquals(constantValue, copy.getConstantValue());
    }

    public void testClone()
    {
        System.out.println( "Clone" );

        LinearKernel kernel = LinearKernel.getInstance();
        ArrayList<WeightedValue<Vector3>> examples =
            new ArrayList<WeightedValue<Vector3>>();
        examples.add( new DefaultWeightedValue<Vector3>(Vector3.createRandom(RANDOM)) );
        double bias = RANDOM.nextDouble();
        LocallyWeightedKernelScalarFunction<Vector> instance =
            new LocallyWeightedKernelScalarFunction<Vector>(kernel, examples, bias);

        LocallyWeightedKernelScalarFunction<Vector> clone =
            (LocallyWeightedKernelScalarFunction<Vector>) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getKernel() );
        assertNotSame( instance.getKernel(), clone.getKernel() );
        assertNotNull( clone.getExamples() );
        assertSame( instance.getExamples(), clone.getExamples() );
        assertEquals( instance.getBias(), clone.getBias() );
        assertEquals( instance.getConstantValue(), clone.getConstantValue() );
        

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.LocallyWeightedKernelScalarFunction.
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
        
        LocallyWeightedKernelScalarFunction<Vector> instance = 
            new LocallyWeightedKernelScalarFunction<Vector>(kernel, examples, bias);
        
        double epsilon = 0.001;
        Vector input = new Vector3(1.0, 1.0, 1.0);
        double output = 3.66666;
        assertEquals(output, instance.evaluate(input), epsilon);

        input = new Vector3(0.0, 7.0, 3.0);
        output = 3.4;
        assertEquals(output, instance.evaluate(input), epsilon);

        input = new Vector3(0.0, 0.0, 0.0);
        output = 4.0;
        assertEquals(output, instance.evaluate(input), epsilon);

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
     * Test of getConstantWeight method, of class gov.sandia.cognition.learning.util.function.LocallyWeightedKernelScalarFunction.
     */
    public void testGetConstantWeight()
    {
        this.testSetConstantWeight();
    }

    /**
     * Test of setConstantWeight method, of class gov.sandia.cognition.learning.util.function.LocallyWeightedKernelScalarFunction.
     */
    public void testSetConstantWeight()
    {
        LocallyWeightedKernelScalarFunction<Vector> instance = 
            new LocallyWeightedKernelScalarFunction<Vector>();
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantWeight());
        
        double constantWeight = RANDOM.nextDouble();
        instance.setConstantWeight(constantWeight);
        assertEquals(constantWeight, instance.getConstantWeight());
        
        constantWeight = 0.0;
        instance.setConstantWeight(constantWeight);
        assertEquals(constantWeight, instance.getConstantWeight());
        
        
        constantWeight = -1.0;
        instance.setConstantWeight(constantWeight);
        assertEquals(constantWeight, instance.getConstantWeight());
    }

    /**
     * Test of getConstantValue method, of class gov.sandia.cognition.learning.util.function.LocallyWeightedKernelScalarFunction.
     */
    public void testGetConstantValue()
    {
        this.testSetConstantValue();
    }

    /**
     * Test of setConstantValue method, of class gov.sandia.cognition.learning.util.function.LocallyWeightedKernelScalarFunction.
     */
    public void testSetConstantValue()
    {
        LocallyWeightedKernelScalarFunction<Vector> instance = 
            new LocallyWeightedKernelScalarFunction<Vector>();
        assertEquals(LocallyWeightedKernelScalarFunction.DEFAULT_CONSTANT_WEIGHT,
            instance.getConstantValue());
        
        double constantValue = RANDOM.nextDouble();
        instance.setConstantValue(constantValue);
        assertEquals(constantValue, instance.getConstantValue());
        
        constantValue = 0.0;
        instance.setConstantValue(constantValue);
        assertEquals(constantValue, instance.getConstantValue());
        
        
        constantValue = -1.0;
        instance.setConstantValue(constantValue);
        assertEquals(constantValue, instance.getConstantValue());
    }
}
