/*
 * File:            FeatureHashingTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */
package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.hash.Eva32Hash;
import gov.sandia.cognition.hash.HashFunction;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.statistics.distribution.PoissonDistribution;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link FeatureHashing}.
 * 
 * @author  Justin Basilico
 * @since   3.4.2
 */
public class FeatureHashingTest
    extends Object
{
    protected Random random = new Random(12333);
    
    /**
     * Creates a new test.
     */
    public FeatureHashingTest()
    {
        super();
    }
    
    /**
     * Test of constructors, of class FeatureHashing.
     */
    @Test
    public void testContructors()
    {
        int outputDimensionality = FeatureHashing.DEFAULT_OUTPUT_DIMENSIONALITY;
        VectorFactory<?> vectorFactory = VectorFactory.getSparseDefault();
        FeatureHashing instance = new FeatureHashing();
        assertEquals(outputDimensionality, instance.getOutputDimensionality());
        assertNotNull(instance.getHashFunction());
        assertSame(vectorFactory, instance.getVectorFactory());
        
        outputDimensionality = 12;
        instance = new FeatureHashing(outputDimensionality);
        assertEquals(outputDimensionality, instance.getOutputDimensionality());
        assertNotNull(instance.getHashFunction());
        assertSame(vectorFactory, instance.getVectorFactory());

        HashFunction hashFunction = new Eva32Hash();
        vectorFactory = new SparseVectorFactoryMTJ();
        instance = new FeatureHashing(outputDimensionality, hashFunction,
            vectorFactory);
        assertEquals(outputDimensionality, instance.getOutputDimensionality());
        assertSame(hashFunction, instance.getHashFunction());
        assertSame(vectorFactory, instance.getVectorFactory());
        
        boolean exeptionThrown = false;
        try
        {
            instance = new FeatureHashing(-1);
        }
        catch (IllegalArgumentException e)
        {
            exeptionThrown = true;
        }
        finally
        {
            assertTrue(exeptionThrown);
        }
    }

    /**
     * Test of evaluate method, of class FeatureHashing.
     */
    @Test
    public void testEvaluate()
    {
        System.out.println("evaluate");
        int n = 200;
        int d1 = 50;
        int d2 = 10;
        FeatureHashing instance = new FeatureHashing(d2);
        
        VectorFactory<?> vf = VectorFactory.getSparseDefault();
        List<Vector> inputs = new ArrayList<>(n);
        List<Vector> outputs = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
        {
            int capacity = new PoissonDistribution(10.0).sampleAsInt(random);
            Vector input = vf.createVectorCapacity(d1, capacity);

            for (int j = 0; j < capacity; j++)
            {
                input.setElement(random.nextInt(d1), random.nextGaussian());
            }

            Vector output = instance.evaluate(input);
            assertEquals(d2, output.getDimensionality());
            assertEquals(output, instance.evaluate(input));
            inputs.add(input);
            outputs.add(output);
        }

        double actualSum = 0.0;
        double hashedSum = 0.0;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                actualSum += inputs.get(i).dot(inputs.get(j));
                hashedSum += outputs.get(i).dot(outputs.get(j));
            }
        }
        
        double epsilon = 1 / Math.sqrt(d2);
        assertEquals(actualSum / (n * n), hashedSum / (n * n), epsilon);
    }

    /**
     * Test of getOutputDimensionality method, of class FeatureHashing.
     */
    @Test
    public void testGetOutputDimensionality()
    {
        this.testSetOutputDimensionality();
    }

    /**
     * Test of setOutputDimensionality method, of class FeatureHashing.
     */
    @Test
    public void testSetOutputDimensionality()
    {
        int outputDimensionality = FeatureHashing.DEFAULT_OUTPUT_DIMENSIONALITY;
        FeatureHashing instance = new FeatureHashing();
        assertEquals(outputDimensionality, instance.getOutputDimensionality());
        
        outputDimensionality = 3;
        instance.setOutputDimensionality(outputDimensionality);
        assertEquals(outputDimensionality, instance.getOutputDimensionality());

        boolean exeptionThrown = false;
        try
        {
            instance.setOutputDimensionality(-1);
        }
        catch (IllegalArgumentException e)
        {
            exeptionThrown = true;
        }
        finally
        {
            assertTrue(exeptionThrown);
        }
    }

    /**
     * Test of getHashFunction method, of class FeatureHashing.
     */
    @Test
    public void testGetHashFunction()
    {
        this.testSetHashFunction();
    }

    /**
     * Test of setHashFunction method, of class FeatureHashing.
     */
    @Test
    public void testSetHashFunction()
    {

        FeatureHashing instance = new FeatureHashing();
        assertNotNull(instance.getHashFunction());
        
        HashFunction hashFunction = new Eva32Hash();
        instance.setHashFunction(hashFunction);
        assertSame(hashFunction, instance.getHashFunction());
    }

    /**
     * Test of getVectorFactory method, of class FeatureHashing.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class FeatureHashing.
     */
    @Test
    public void testSetVectorFactory()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getSparseDefault();
        FeatureHashing instance = new FeatureHashing();
        assertSame(vectorFactory, instance.getVectorFactory());
        
        vectorFactory = new SparseVectorFactoryMTJ();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }
    
}
