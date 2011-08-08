/*
 * File:                AbstractEntropyBasedGlobalTermWeighterTest.java
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

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractEntropyBasedGlobalTermWeighter.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractEntropyBasedGlobalTermWeighterTest
{

    /**
     * Creates a new unit test.
     */
    public AbstractEntropyBasedGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        AbstractEntropyBasedGlobalTermWeighter instance = new DummyEntropyBasedGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.getTermDocumentFrequencies());
        assertNull(instance.getTermGlobalFrequencies());
        assertNull(instance.getTermEntropiesSum());

        vectorFactory = VectorFactory.getDefault();
        instance = new DummyEntropyBasedGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.getTermDocumentFrequencies());
        assertNull(instance.getTermGlobalFrequencies());
        assertNull(instance.getTermEntropiesSum());
    }

    /**
     * Test of clone method, of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testClone()
    {
        AbstractEntropyBasedGlobalTermWeighter instance =
            new DummyEntropyBasedGlobalTermWeighter();
        AbstractEntropyBasedGlobalTermWeighter clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
        assertNotSame(instance.getTermEntropiesSum(), clone.getTermEntropiesSum());
    }

    /**
     * Test of add method, of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testAdd()
    {
        AbstractEntropyBasedGlobalTermWeighter instance =
            new DummyEntropyBasedGlobalTermWeighter();

        Vector expectedTermEntropiesSum = new Vector3();
        instance.add(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 0.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 0.0, 1.0), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.plusEquals(new Vector3(3.0 * Math.log(3.0), 0.0, 0.0));
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());

        instance.add(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 0.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 0.0, 1.0), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.plusEquals(new Vector3());
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());

        instance.add(new Vector3(0.0, 1.0, 1.0));
        assertEquals(3, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.plusEquals(new Vector3());
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());
    }

    /**
     * Test of remove method, of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testRemove()
    {
        AbstractEntropyBasedGlobalTermWeighter instance =
            new DummyEntropyBasedGlobalTermWeighter();

        Vector expectedTermEntropiesSum = new Vector3();
        instance.add(new Vector3(3.0, 0.0, 1.0));
        instance.add(new Vector3());
        instance.add(new Vector3(0.0, 1.0, 1.0));
        expectedTermEntropiesSum.plusEquals(new Vector3(3.0 * Math.log(3.0), 0.0, 0.0));

        assertEquals(3, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());

        instance.remove(new Vector3());
        assertEquals(2, instance.getDocumentCount());
        assertEquals(new Vector3(1.0, 1.0, 2.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(3.0, 1.0, 2.0), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.minusEquals(new Vector3());
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());

        instance.remove(new Vector3(3.0, 0.0, 1.0));
        assertEquals(1, instance.getDocumentCount());
        assertEquals(new Vector3(0.0, 1.0, 1.0), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(0.0, 1.0, 1.0), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.minusEquals(new Vector3(3.0 * Math.log(3.0), 0.0, 0.0));
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());

        instance.remove(new Vector3(0.0, 1.0, 1.0));
        assertEquals(0, instance.getDocumentCount());
        assertEquals(new Vector3(), instance.getTermDocumentFrequencies());
        assertEquals(new Vector3(), instance.getTermGlobalFrequencies());
        expectedTermEntropiesSum.minusEquals(new Vector3());
        assertEquals(expectedTermEntropiesSum, instance.getTermEntropiesSum());
    }

    /**
     * Test of getTermEntropiesSum method, of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testGetTermEntropiesSum()
    {
        this.testSetTermEntropiesSum();
    }

    /**
     * Test of setTermEntropiesSum method, of class AbstractEntropyBasedGlobalTermWeighter.
     */
    @Test
    public void testSetTermEntropiesSum()
    {
        Vector termEntropiesSum = null;
        AbstractEntropyBasedGlobalTermWeighter instance =
            new DummyEntropyBasedGlobalTermWeighter();
        assertSame(termEntropiesSum, instance.getTermEntropiesSum());

        termEntropiesSum = new Vector2();
        instance.setTermEntropiesSum(termEntropiesSum);
        assertSame(termEntropiesSum, instance.getTermEntropiesSum());

        termEntropiesSum = new Vector3();
        instance.setTermEntropiesSum(termEntropiesSum);
        assertSame(termEntropiesSum, instance.getTermEntropiesSum());

        termEntropiesSum = null;
        instance.setTermEntropiesSum(termEntropiesSum);
        assertSame(termEntropiesSum, instance.getTermEntropiesSum());
    }

    public static class DummyEntropyBasedGlobalTermWeighter
        extends AbstractEntropyBasedGlobalTermWeighter
    {

        public DummyEntropyBasedGlobalTermWeighter()
        {
            super();
        }

        public DummyEntropyBasedGlobalTermWeighter(
            final VectorFactory<? extends Vector> vectorFactory)
        {
            super(vectorFactory);
        }

        public int getDimensionality()
        {
            return VectorUtil.safeGetDimensionality(this.termGlobalFrequencies);
        }

        public Vector getGlobalWeights()
        {
            throw new UnsupportedOperationException("Not supported.");
        }

    }
}