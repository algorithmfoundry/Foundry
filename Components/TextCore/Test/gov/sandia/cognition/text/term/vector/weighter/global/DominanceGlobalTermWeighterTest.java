/*
 * File:                DominanceGlobalTermWeighterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 30, 2009, Sandia Corporation.
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
 * Unit tests for class DominanceGlobalTermWeighter.
 *
 * @author Justin Basilico
 * @since   3.0
 */
public class DominanceGlobalTermWeighterTest
{

    /**
     * Creates a new test.
     */
    public DominanceGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getDominance());

        vectorFactory = VectorFactory.getDefault();
        instance = new DominanceGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        assertNull(instance.getDominance());
    }

    /**
     * Test of clone method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testClone()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        DominanceGlobalTermWeighter clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
        assertEquals(instance.getDominance(), clone.getDominance());

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
        assertEquals(instance.getDominance(), clone.getDominance());
        assertNotSame(instance.getDominance(), clone.getDominance());
    }

    /**
     * Test of add method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testAdd()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.dominance);

        instance.add(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertNull(instance.dominance);

        instance.add(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertNull(instance.dominance);

        instance.add(VectorFactory.getDefault().createVector(4));
        assertEquals(3, instance.getDocumentCount());
        assertNull(instance.dominance);
        
        assertNotNull(instance.getDominance());
        assertNotNull(instance.dominance);

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

        assertEquals(3, instance.getDocumentCount());
        assertNotNull(instance.dominance);
    }

    /**
     * Test of remove method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testRemove()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3());
        instance.add(new Vector3(0.0, 1.0, 1.0));
        assertNull(instance.dominance);

        assertEquals(3, instance.getDocumentCount());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());

        instance.remove(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());
        assertNull(instance.dominance);

        instance.remove(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertEquals(new Vector3(0.0, 1.0, 1.0), instance.getTermGlobalFrequencies());
        assertNull(instance.dominance);

        instance.remove(new Vector3(0.0, 1.0, 1.0));
        assertEquals(0, instance.getDocumentCount());
        assertEquals(new Vector3(), instance.getTermGlobalFrequencies());
        assertNull(instance.dominance);
    }

    /**
     * Test of getGlobalWeights method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testGetGlobalWeights()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        assertSame(instance.getDominance(), instance.getGlobalWeights());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));
        assertSame(instance.getDominance(), instance.getGlobalWeights());
    }

    /**
     * Test of getDimensionality method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testGetDimensionality()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        assertEquals(-1, instance.getDimensionality());

        instance.add(new Vector3());
        assertEquals(3, instance.getDimensionality());
    }

    /**
     * Test of getDominance method, of class DominanceGlobalTermWeighter.
     */
    @Test
    public void testGetDominance()
    {
        DominanceGlobalTermWeighter instance = new DominanceGlobalTermWeighter();
        assertNull(instance.getDominance());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3(4.0, 0.0, 0.0));
        instance.add(new Vector3(9.0, 0.0, 1.0));
        instance.add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.001;
        Vector expected = new Vector3(0.886, 0.0, 0.707);
        Vector actual = instance.getDominance();
        assertTrue(expected.equals(actual, epsilon));


        instance.add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.709, 0.2, 0.565);
        actual = instance.getDominance();
System.out.println("Actual: "+ actual);
        assertTrue(expected.equals(actual, epsilon));


        // Test handling of dense.
        instance.setVectorFactory(VectorFactory.getDefault());
        instance.setDominance(null);
        actual = instance.getDominance();
        assertTrue(expected.equals(actual, epsilon));
    }

}