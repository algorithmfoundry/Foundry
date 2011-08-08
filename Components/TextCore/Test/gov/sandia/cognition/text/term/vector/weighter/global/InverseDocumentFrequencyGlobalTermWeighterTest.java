/*
 * File:                InverseDocumentFrequencyGlobalTermWeighterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 29, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.global;

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class InverseDocumentFrequencyGlobalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class InverseDocumentFrequencyGlobalTermWeighterTest
{

    /**
     * Creates a new test.
     */
    public InverseDocumentFrequencyGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        InverseDocumentFrequencyGlobalTermWeighter instance = new InverseDocumentFrequencyGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getInverseDocumentFrequency());

        vectorFactory = VectorFactory.getDefault();
        instance = new InverseDocumentFrequencyGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getInverseDocumentFrequency());
    }

    /**
     * Test of clone method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testClone()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance = new InverseDocumentFrequencyGlobalTermWeighter();
        InverseDocumentFrequencyGlobalTermWeighter clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getInverseDocumentFrequency(), clone.getInverseDocumentFrequency());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertNotSame(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
        assertNotSame(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
        assertEquals(instance.getInverseDocumentFrequency(), clone.getInverseDocumentFrequency());
        assertNotSame(instance.getInverseDocumentFrequency(), clone.getInverseDocumentFrequency());
    }

    /**
     * Test of add method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testAdd()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance =
            new InverseDocumentFrequencyGlobalTermWeighter();
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.getTermDocumentFrequencies());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 0.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 0.0, 1.0), instance.getTermGlobalFrequencies());

        instance.add(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 0.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 0.0, 1.0), instance.getTermGlobalFrequencies());

        instance.add(new Vector3(0.0, 1.0, 1.0));
        assertEquals(3, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());

        VectorFactory f = VectorFactory.getDefault();
        instance.add(f.copyValues(0.0, 0.0, 0.0, 2.0));
        assertEquals(4, instance.getDocumentCount());
        assertEquals(f.copyValues(1.0, 1.0, 2.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(f.copyValues(3.0, 1.0, 2.0, 2.0), instance.getTermGlobalFrequencies());

        boolean exceptionThrown = false;
        try
        {
            instance.add(new Vector3());
        }
        catch (DimensionalityMismatchException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        assertEquals(4, instance.getDocumentCount());
    }

    /**
     * Test of remove method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testRemove()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance =
            new InverseDocumentFrequencyGlobalTermWeighter();
        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3());
        instance.add(new Vector3(0.0, 1.0, 1.0));

        assertEquals(3, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());

        instance.remove(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());

        instance.remove(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertEquals(new Vector3(0.0, 1.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(0.0, 1.0, 1.0), instance.getTermGlobalFrequencies());

        instance.remove(new Vector3(0.0, 1.0, 1.0));
        assertEquals(0, instance.getDocumentCount());
        assertEquals(new Vector3(), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(), instance.getTermGlobalFrequencies());
    }

    /**
     * Test of getDimensionality method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testGetDimensionality()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance = new InverseDocumentFrequencyGlobalTermWeighter();
        assertEquals(-1, instance.getDimensionality());

        instance.add(new Vector3());
        assertEquals(3, instance.getDimensionality());
    }

    /**
     * Test of getGlobalWeights method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testGetGlobalWeights()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance = new InverseDocumentFrequencyGlobalTermWeighter();
        assertSame(instance.getInverseDocumentFrequency(), instance.getGlobalWeights());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));
        assertSame(instance.getInverseDocumentFrequency(), instance.getGlobalWeights());
    }

    /**
     * Test of getInverseDocumentFrequency method, of class InverseDocumentFrequencyGlobalTermWeighter.
     */
    @Test
    public void testGetInverseDocumentFrequency()
    {
        InverseDocumentFrequencyGlobalTermWeighter instance =
            new InverseDocumentFrequencyGlobalTermWeighter();
        assertNull(instance.getInverseDocumentFrequency());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.001;
        Vector expected = new Vector3(0.0, 0.0, 0.287);
        Vector actual = instance.getInverseDocumentFrequency();
        assertTrue(expected.equals(actual, epsilon));
        assertTrue(expected.equals(instance.getGlobalWeights(), epsilon));

        instance.add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.223, 1.609, 0.510);
        actual = instance.getInverseDocumentFrequency();
        assertTrue(expected.equals(actual, epsilon));
        assertTrue(expected.equals(instance.getGlobalWeights(), epsilon));

        // Test handling of dense.
        instance.setVectorFactory(VectorFactory.getDefault());
        instance.setInverseDocumentFrequency(null);
        actual = instance.getInverseDocumentFrequency();
        assertTrue(expected.equals(actual, epsilon));
        assertTrue(expected.equals(instance.getGlobalWeights(), epsilon));
    }

}