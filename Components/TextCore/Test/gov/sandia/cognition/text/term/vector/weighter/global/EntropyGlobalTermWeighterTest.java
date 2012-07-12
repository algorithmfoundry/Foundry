/*
 * File:                EntropyGlobalTermWeighterTest.java
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
 * Unit tests for class EntropyGlobalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class EntropyGlobalTermWeighterTest
{

    /**
     * Creates a new test.
     */
    public EntropyGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getEntropy());

        vectorFactory = VectorFactory.getDefault();
        instance = new EntropyGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getEntropy());
    }

    /**
     * Test of clone method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testClone()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
        EntropyGlobalTermWeighter clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
        assertEquals(instance.getEntropy(), clone.getEntropy());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertNotSame(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
        assertNotSame(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
        assertEquals(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
        assertNotSame(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
        assertEquals(instance.getEntropy(), clone.getEntropy());
        assertNotSame(instance.getEntropy(), clone.getEntropy());
    }

    /**
     * Test of add method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testAdd()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
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

        VectorFactory<?> f = VectorFactory.getDefault();
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
     * Test of remove method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testRemove()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
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
     * Test of getDimensionality method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testGetDimensionality()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
        assertEquals(-1, instance.getDimensionality());

        instance.add(new Vector3());
        assertEquals(3, instance.getDimensionality());
    }

    /**
     * Test of getGlobalWeights method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testGetGlobalWeights()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
        assertSame(instance.getEntropy(), instance.getGlobalWeights());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));
        assertSame(instance.getEntropy(), instance.getGlobalWeights());
    }

    /**
     * Test of getEntropy method, of class EntropyGlobalTermWeighter.
     */
    @Test
    public void testGetEntropy()
    {
        EntropyGlobalTermWeighter instance = new EntropyGlobalTermWeighter();
        assertNull(instance.getEntropy());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.001;
        Vector expected = new Vector3(0.087, 1.0, 0.25);
        Vector actual = instance.getEntropy();
        assertTrue(expected.equals(actual, epsilon));


        instance.add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.214, 1.0, 0.353);
        actual = instance.getEntropy();
        assertTrue(expected.equals(actual, epsilon));


        // Test handling of dense.
        instance.setVectorFactory(VectorFactory.getDefault());
        instance.setEntropy(null);
        actual = instance.getEntropy();
        assertTrue(expected.equals(actual, epsilon));
    }

}