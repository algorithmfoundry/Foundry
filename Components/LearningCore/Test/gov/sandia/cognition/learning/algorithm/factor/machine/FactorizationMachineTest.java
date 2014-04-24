/*
 * File:            FactorizationMachineTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector1;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link FactorizationMachine}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class FactorizationMachineTest
    extends Object
{
 
    protected Random random = new Random(4444);
    protected double epsilon = 1e-5;
    
    /**
     * Creates a new test.
     */
    public FactorizationMachineTest()
    {
    }

    /**
     * Test of constructors, of class FactorizationMachine.
     */
    @Test
    public void testConstructors()
    {
        double bias = 0.0;
        Vector weights = null;
        Matrix factors = null;
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(bias, instance.getBias(), 0.0);
        assertSame(weights, instance.getWeights());
        assertSame(factors, instance.getFactors());
        
        instance = new FactorizationMachine(12, 5);
        assertEquals(bias, instance.getBias(), 0.0);
        assertEquals(12, instance.getWeights().getDimensionality());
        assertEquals(5, instance.getFactors().getNumRows());
        assertEquals(12, instance.getFactors().getNumColumns());
        assertEquals(0.0, instance.getWeights().sum(), 0.0);
        assertEquals(0.0, instance.getFactors().sumOfRows().sum(), 0.0);
        
        bias = 0.4;
        weights = VectorFactory.getSparseDefault().createVector(11);
        factors = MatrixFactory.getSparseDefault().createMatrix(11, 6);
        instance = new FactorizationMachine(bias, weights, factors);
        assertEquals(bias, instance.getBias(), 0.0);
        assertSame(weights, instance.getWeights());
        assertSame(factors, instance.getFactors());
    }
    
    /**
     * Test of clone method, of class FactorizationMachine.
     */
    @Test
    public void testClone()
    {
        FactorizationMachine instance = new FactorizationMachine();
        FactorizationMachine clone = instance.clone();
       
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
        assertEquals(instance.getBias(), clone.getBias(), 0.0);
        assertNull(clone.getWeights());
        assertNull(clone.getFactors());
        
        instance.setBias(3.4);
        instance.setWeights(VectorFactory.getDefault().createUniformRandom(
            5, -1, 1, random));
        instance.setFactors(MatrixFactory.getDefault().createUniformRandom(
            3, 5, -1, 1, random));
        clone = instance.clone();
        
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
        assertEquals(instance.getBias(), clone.getBias(), 0.0);
        assertEquals(instance.getWeights(), clone.getWeights());
        assertEquals(instance.getFactors(), clone.getFactors());
        assertNotSame(instance.getWeights(), clone.getWeights());
        assertNotSame(instance.getFactors(), clone.getFactors());
    }

    /**
     * Test of evaluateAsDouble method, of class FactorizationMachine.
     */
    @Test
    public void testEvaluateAsDouble()
    {
        int d = 1 + this.random.nextInt(10);
        int k = 1 + this.random.nextInt(d - 1);
        Vector x1 = VectorFactory.getDefault().createUniformRandom(d, -10, 10, random);
        Vector x2 = VectorFactory.getDefault().createUniformRandom(d, -10, 10, random);
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(0.0, instance.evaluateAsDouble(null), 0.0);
        
        
        double b = this.random.nextGaussian();
        instance.setBias(b);
        assertEquals(b, instance.evaluateAsDouble(x1), 0.0);
        assertEquals(b, instance.evaluateAsDouble(x2), 0.0);
        
        
        Vector w = VectorFactory.getDefault().createVector(d);
        instance.setWeights(w);
        
        
        assertEquals(b, instance.evaluateAsDouble(x1), 0.0);
        assertEquals(b, instance.evaluateAsDouble(x2), 0.0);
        
        w = VectorFactory.getDefault().createUniformRandom(d, -1, 1, random);
        instance.setWeights(w);
        assertEquals(b + w.dotProduct(x1), instance.evaluateAsDouble(x1), 0.0);
        assertEquals(b + w.dotProduct(x2), instance.evaluateAsDouble(x2), 0.0);
        
        Matrix v = MatrixFactory.getDenseDefault().createMatrix(k, d);
        instance.setFactors(v);
        assertEquals(b + w.dotProduct(x1), instance.evaluateAsDouble(x1), 0.0);
        assertEquals(b + w.dotProduct(x2), instance.evaluateAsDouble(x2), 0.0);
        
        v = MatrixFactory.getDenseDefault().createUniformRandom(k, d, -1, 1,
            random);
        instance.setFactors(v);
        
        // This is the way in the base formula to compute it that is O(kn^2)
        // rather than the way it is really computed as O(kn).
        for (Vector x : Arrays.asList(x1, x2))
        {
            double expected = b + w.dotProduct(x);
            for (int i = 0; i < d; i++)
            {
                for (int j = i + 1; j < d; j++)
                {
                    expected += x.getElement(i) * x.getElement(j)
                        * v.getColumn(i).dotProduct(v.getColumn(j));
                }
            }
            
            assertEquals(expected, instance.evaluateAsDouble(x), epsilon);
        }
    }

    /**
     * Test of getInputDimensionality method, of class FactorizationMachine.
     */
    @Test
    public void testGetInputDimensionality()
    {
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(0, instance.getInputDimensionality());
        
        instance.setWeights(new Vector3());
        assertEquals(3, instance.getInputDimensionality());
        
        instance.setWeights(null);
        instance.setFactors(MatrixFactory.getDefault().createMatrix(12, 4));
        assertEquals(4, instance.getInputDimensionality());
        
        instance = new FactorizationMachine(4, 7);
        assertEquals(4, instance.getInputDimensionality());
    }

    /**
     * Test of getFactorCount method, of class FactorizationMachine.
     */
    @Test
    public void testGetFactorCount()
    {
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(0, instance.getFactorCount());
        
        instance.setFactors(MatrixFactory.getDefault().createMatrix(12, 4));
        assertEquals(12, instance.getFactorCount());
        
        instance = new FactorizationMachine(4, 7);
        assertEquals(7, instance.getFactorCount());
    }
    
    /**
     * Test of computeParameterGradient method, of class FactorizationMachine.
     */
    @Test
    public void testComputeParameterGradient()
    {
        VectorFactory<?> vf = VectorFactory.getSparseDefault();
        FactorizationMachine instance = new FactorizationMachine();
        Vector input = vf.createVector(0);
        Vector result = instance.computeParameterGradient(input);
        assertEquals(1, result.getDimensionality());
        assertEquals(1.0, result.getElement(0), 0.0);
        
        int d = 3;
        instance.setWeights(VectorFactory.getDenseDefault().createVector(d));
        input = vf.createUniformRandom(d, -10, 10, random);
        result = instance.computeParameterGradient(input);
        assertEquals(1 + d, result.getDimensionality());
        assertEquals(1.0, result.getElement(0), 0.0);
        assertEquals(input, result.subVector(1, d));
        
        int k = 2;
        instance.setFactors(MatrixFactory.getDenseDefault().createUniformRandom(k, d, -10, 10, random));
        input = vf.createUniformRandom(d, -10, 10, random);
        result = instance.computeParameterGradient(input);
        assertEquals(10, result.getDimensionality());
        assertEquals(1.0, result.getElement(0), 0.0);
        assertEquals(input, result.subVector(1, d));
        
        Vector factorGradients = result.subVector(d + 1, d + d * k);
        for (int f = 0; f < k; f++)
        {
            for (int l = 0; l < d; l++)
            {
                double actual = factorGradients.getElement(f * d + l);
                
                double expected = 0.0;
                for (int j = 0; j < d; j++)
                {
                    if (j != l)
                    {
                        double xl = input.getElement(l);
                        expected += xl * instance.getFactors().getElement(f, j) * input.getElement(j);
                    }
                }
                assertEquals(expected, actual, epsilon);
            }
        }
    }
    
    /**
     * Test of getParameterCount method, of class FactorizationMachine.
     */
    @Test
    public void testGetParameterCount()
    {
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(1, instance.getParameterCount());
        
        instance.setFactors(MatrixFactory.getDefault().createMatrix(12, 4));
        assertEquals(1 + 12 * 4, instance.getParameterCount());
        
        instance.setFactors(null);
        instance.setWeights(VectorFactory.getDefault().createVector(4));
        assertEquals(1 + 4, instance.getParameterCount());
        
        instance = new FactorizationMachine(4, 7);
        assertEquals(1 + 4 + 4 * 7, instance.getParameterCount());
    }
    
    
    /**
     * Test of convertToVector method, of class FactorizationMachine.
     */
    @Test
    public void testConvertToVector()
    {
        FactorizationMachine instance = new FactorizationMachine();
        Vector result = instance.convertToVector();
        assertEquals(instance.getParameterCount(), result.getDimensionality());
        assertTrue(result.isZero());
        
        int d = 7;
        int k = 4;
        instance = new FactorizationMachine(d, k);
        result = instance.convertToVector();
        assertEquals(instance.getParameterCount(), result.getDimensionality());
        assertTrue(result.isZero());
        
        double bias = this.random.nextGaussian();
        Vector weights = VectorFactory.getDefault().createUniformRandom(d, -1, 1, random);
        Matrix factors = MatrixFactory.getDefault().createUniformRandom(k, d, -1, 1, random);
        instance = new FactorizationMachine(bias, weights.clone(), factors.clone());
        result = instance.convertToVector();
        assertEquals(instance.getParameterCount(), result.getDimensionality());
        assertTrue(result.equals(new Vector1(bias).stack(weights).stack(factors.transpose().convertToVector())));
        
        // Try with weights disabled.
        instance.setWeights(null);
        result = instance.convertToVector();
        assertEquals(instance.getParameterCount(), result.getDimensionality());
        assertTrue(result.equals(new Vector1(bias).stack(factors.transpose().convertToVector())));
        
        // Try with factors disabled.
        instance.setWeights(weights.clone());
        instance.setFactors(null);
        result = instance.convertToVector();
        assertEquals(instance.getParameterCount(), result.getDimensionality());
        assertTrue(result.equals(new Vector1(bias).stack(weights)));
    }
    
    
    /**
     * Test of convertFromVector method, of class FactorizationMachine.
     */
    @Test
    public void testConvertFromVector()
    {
        FactorizationMachine instance = new FactorizationMachine();
        Vector converted = instance.convertToVector();
        Vector expected = converted.clone();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        
        int d = 7;
        int k = 4;
        instance = new FactorizationMachine(d, k);
        converted = instance.convertToVector();
        expected = converted.clone();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        
        double bias = this.random.nextGaussian();
        Vector weights = VectorFactory.getDefault().createUniformRandom(d, -1, 1, random);
        Matrix factors = MatrixFactory.getDefault().createUniformRandom(k, d, -1, 1, random);
        instance = new FactorizationMachine(bias, weights.clone(), factors.clone());
        converted = instance.convertToVector();
        expected = converted.clone();
        instance.convertFromVector(converted);
        assertEquals(expected, instance.convertToVector());
        assertEquals(bias, instance.getBias(), 0.0);
        assertEquals(weights, instance.getWeights());
        assertEquals(factors, instance.getFactors());
        
        instance = new FactorizationMachine(d, k);
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        assertEquals(bias, instance.getBias(), 0.0);
        assertEquals(weights, instance.getWeights());
        assertEquals(factors, instance.getFactors());
        
        // Try with weights disabled.
        instance.setWeights(null);
        converted = instance.convertToVector();
        expected = converted.clone();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        instance.setBias(0.0);
        instance.getFactors().zero();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        assertEquals(bias, instance.getBias(), 0.0);
        assertNull(instance.getWeights());
        assertEquals(factors, instance.getFactors());
        
        // Try with factors disabled.
        instance.setWeights(weights.clone());
        instance.setFactors(null);
        converted = instance.convertToVector();
        expected = converted.clone();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        instance.setBias(0.0);
        instance.getWeights().zero();
        instance.convertFromVector(converted);
        assertTrue(expected.equals(instance.convertToVector()));
        assertEquals(bias, instance.getBias(), 0.0);
        assertEquals(weights, instance.getWeights());
        assertNull(instance.getFactors());
    }

    /**
     * Test of hasWeights method, of class FactorizationMachine.
     */
    @Test
    public void testHasWeights()
    {
        FactorizationMachine instance = new FactorizationMachine();
        assertFalse(instance.hasWeights());
        
        instance.setWeights(new Vector3());
        assertTrue(instance.hasWeights());
        
        instance.setWeights(null);
        assertFalse(instance.hasWeights());
        
        instance = new FactorizationMachine(4, 7);
        assertTrue(instance.hasWeights());
    }

    /**
     * Test of hasFactors method, of class FactorizationMachine.
     */
    @Test
    public void testHasFactors()
    {
        FactorizationMachine instance = new FactorizationMachine();
        assertFalse(instance.hasFactors());
        
        instance.setFactors(MatrixFactory.getDefault().createMatrix(12, 4));
        assertTrue(instance.hasFactors());
        
        instance.setFactors(null);
        assertFalse(instance.hasFactors());
        
        instance = new FactorizationMachine(4, 7);
        assertTrue(instance.hasFactors());
    }

    /**
     * Test of getBias method, of class FactorizationMachine.
     */
    @Test
    public void testGetBias()
    {
        this.testSetBias();
    }

    /**
     * Test of setBias method, of class FactorizationMachine.
     */
    @Test
    public void testSetBias()
    {
        double bias = 0.0;
        FactorizationMachine instance = new FactorizationMachine();
        assertEquals(bias, instance.getBias(), 0.0);
        
        double[] values = {0.4, -0.4, 40, -40};
        for (double value : values)
        {
            bias = value;
            instance.setBias(bias);
            assertEquals(bias, instance.getBias(), 0.0);
        }
    }

    /**
     * Test of getWeights method, of class FactorizationMachine.
     */
    @Test
    public void testGetWeights()
    {
        this.testSetWeights();;
    }

    /**
     * Test of setWeights method, of class FactorizationMachine.
     */
    @Test
    public void testSetWeights()
    {
        Vector weights = null;
        FactorizationMachine instance = new FactorizationMachine();
        assertSame(weights, instance.getWeights());

        weights = VectorFactory.getSparseDefault().createVector(11);
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
        
        weights = null;
        instance.setWeights(weights);
        assertSame(weights, instance.getWeights());
    }

    /**
     * Test of getFactors method, of class FactorizationMachine.
     */
    @Test
    public void testGetFactors()
    {
        this.testSetFactors();
    }

    /**
     * Test of setFactors method, of class FactorizationMachine.
     */
    @Test
    public void testSetFactors()
    {
        Matrix factors = null;
        FactorizationMachine instance = new FactorizationMachine();
        assertSame(factors, instance.getFactors());

        factors = MatrixFactory.getSparseDefault().createMatrix(11, 6);
        instance.setFactors(factors);
        assertSame(factors, instance.getFactors());
        
        factors = null;
        instance.setFactors(factors);
        assertSame(factors, instance.getFactors());
    }
    
}