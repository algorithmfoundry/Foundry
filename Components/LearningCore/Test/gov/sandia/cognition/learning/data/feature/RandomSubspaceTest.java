/*
 * File:            RandomSubspaceTest.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.function.vector.SubVectorEvaluator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class RandomSubspace.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class RandomSubspaceTest
{

    /** Random number generator. */
    protected Random random = new Random(4777);

    /**
     * Creates a new test.
     */
    public RandomSubspaceTest()
    {
        super();
    }

    /**
     * Test of constructors method, of class RandomSubspace.
     */
    @Test
    public void testConstructors()
    {
        int size = RandomSubspace.DEFAULT_SIZE;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        RandomSubspace instance = new RandomSubspace();
        assertEquals(size, instance.getSize());
        assertNotNull(instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
        
        size = this.random.nextInt(1000) + 1;
        instance = new RandomSubspace(size);
        assertNotNull(instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance = new RandomSubspace(size, random);
        assertNotNull(instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNotNull(instance.getRandom());

        vectorFactory = VectorFactory.getSparseDefault();
        instance = new RandomSubspace(size, random, vectorFactory);
        assertNotNull(instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNotNull(instance.getRandom());
    }

    /**
     * Test of learn method, of class RandomSubspace.
     */
    @Test
    public void testLearn()
    {
        int size = 3;
        RandomSubspace instance = new RandomSubspace(size, random);
        assertNull(instance.learn(null));
        assertNull(instance.learn(new LinkedList<Vector>()));

        int dimensionality = 7;
        Collection<Vector> data = new ArrayList<Vector>();
        data.add(VectorFactory.getDefault().createVector(dimensionality));
        data.add(VectorFactory.getDefault().createVector(dimensionality));

        SubVectorEvaluator result = instance.learn(data);
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(size, result.getOutputDimensionality());

        Set<Integer> indexSet = new LinkedHashSet<Integer>();
        for (int index : result.getSubIndices())
        {
            assertTrue(index >= 0);
            assertTrue(index < dimensionality);
            indexSet.add(index);
        }
        assertEquals(size, indexSet.size());

        size = dimensionality * 2;
        instance.setSize(size);
        result = instance.learn(data);
        size = dimensionality;
        assertEquals(dimensionality, result.getInputDimensionality());
        assertEquals(size, result.getOutputDimensionality());
        indexSet = new LinkedHashSet<Integer>();
        for (int index : result.getSubIndices())
        {
            assertTrue(index >= 0);
            assertTrue(index < dimensionality);
            indexSet.add(index);
        }
        assertEquals(size, indexSet.size());
    }

    /**
     * Test of getSize method, of class RandomSubspace.
     */
    @Test
    public void testGetSize()
    {
        this.testSetSize();
    }

    /**
     * Test of setSize method, of class RandomSubspace.
     */
    @Test
    public void testSetSize()
    {
        int size = RandomSubspace.DEFAULT_SIZE;
        RandomSubspace instance = new RandomSubspace();
        assertEquals(size, instance.getSize());

        int[] goodValues = {1, 2, 5, 1000, 4, random.nextInt(100) + 1};
        for (int goodValue : goodValues)
        {
            size = goodValue;
            instance.setSize(size);
        }

        int[] badValues = {0, -1, -2, -45, -random.nextInt(100)};
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setSize(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(size, instance.getSize());
        }
    }

    /**
     * Test of getVectorFactory method, of class RandomSubspace.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class RandomSubspace.
     */
    @Test
    public void testSetVectorFactory()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        RandomSubspace instance = new RandomSubspace();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getSparseDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        
        vectorFactory = VectorFactory.getDenseDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

}