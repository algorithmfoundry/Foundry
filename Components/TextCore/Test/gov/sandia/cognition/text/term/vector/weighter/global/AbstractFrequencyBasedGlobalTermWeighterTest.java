/*
 * File:                AbstractFrequencyBasedGlobalTermWeighterTest.java
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
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractFrequencyBasedGlobalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractFrequencyBasedGlobalTermWeighterTest
{
    
    /**
     * Creates a new test.
     */
    public AbstractFrequencyBasedGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.getTermDocumentFrequencies());
        assertNull(instance.getTermGlobalFrequencies());

        vectorFactory = VectorFactory.getDefault();
        instance = new DummyFrequencyBasedGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        assertEquals(0, instance.getDocumentCount());
        assertNull(instance.getTermDocumentFrequencies());
        assertNull(instance.getTermGlobalFrequencies());
    }

    /**
     * Test of clone method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testClone()
    {
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
        AbstractFrequencyBasedGlobalTermWeighter clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermDocumentFrequencies());

        instance.add(new Vector3(3.0, 0.0, 1.0));
        clone = instance.clone();
        assertNotSame(clone, instance);
        assertEquals(instance.getDocumentCount(), clone.getDocumentCount());
        assertEquals(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertNotSame(instance.getTermDocumentFrequencies(), clone.getTermDocumentFrequencies());
        assertEquals(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
        assertNotSame(instance.getTermGlobalFrequencies(), clone.getTermGlobalFrequencies());
    }

    /**
     * Test of add method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testAdd()
    {
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();assertEquals(0, instance.getDocumentCount());
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
     * Test of remove method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testRemove()
    {
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
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
     * Test of getDocumentCount method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testGetDocumentCount()
    {
        int documentCount = 0;
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
        assertEquals(documentCount, instance.getDocumentCount());

        documentCount = 1;
        instance.add(new Vector3(1, 0, 0));
        assertEquals(documentCount, instance.getDocumentCount());

        documentCount = 2;
        instance.add(new Vector3(1, 0, 0));
        assertEquals(documentCount, instance.getDocumentCount());

        documentCount = 1;
        instance.remove(new Vector3(1, 0, 0));
        assertEquals(documentCount, instance.getDocumentCount());
    }

    /**
     * Test of getTermDocumentFrequencies method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testGetTermDocumentFrequencies()
    {
        this.testSetTermDocumentFrequencies();
    }

    /**
     * Test of setTermDocumentFrequencies method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testSetTermDocumentFrequencies()
    {
        Vector termDocumentFrequencies = null;
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
        assertSame(termDocumentFrequencies, instance.getTermDocumentFrequencies());

        termDocumentFrequencies = new Vector2();
        instance.setTermDocumentFrequencies(termDocumentFrequencies);
        assertSame(termDocumentFrequencies, instance.getTermDocumentFrequencies());
        
        termDocumentFrequencies = new Vector3();
        instance.setTermDocumentFrequencies(termDocumentFrequencies);
        assertSame(termDocumentFrequencies, instance.getTermDocumentFrequencies());

        termDocumentFrequencies = null;
        instance.setTermDocumentFrequencies(termDocumentFrequencies);
        assertSame(termDocumentFrequencies, instance.getTermDocumentFrequencies());
    }

    /**
     * Test of getTermGlobalFrequencies method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testGetTermGlobalFrequencies()
    {
        this.testSetTermGlobalFrequencies();
    }

    /**
     * Test of setTermGlobalFrequencies method, of class AbstractFrequencyBasedGlobalTermWeighter.
     */
    @Test
    public void testSetTermGlobalFrequencies()
    {
        Vector termGlobalFrequencies = null;
        AbstractFrequencyBasedGlobalTermWeighter instance = new DummyFrequencyBasedGlobalTermWeighter();
        assertSame(termGlobalFrequencies, instance.getTermGlobalFrequencies());

        termGlobalFrequencies = new Vector2();
        instance.setTermGlobalFrequencies(termGlobalFrequencies);
        assertSame(termGlobalFrequencies, instance.getTermGlobalFrequencies());

        termGlobalFrequencies = new Vector3();
        instance.setTermGlobalFrequencies(termGlobalFrequencies);
        assertSame(termGlobalFrequencies, instance.getTermGlobalFrequencies());

        termGlobalFrequencies = null;
        instance.setTermGlobalFrequencies(termGlobalFrequencies);
        assertSame(termGlobalFrequencies, instance.getTermGlobalFrequencies());
    }

    public static class DummyFrequencyBasedGlobalTermWeighter
        extends AbstractFrequencyBasedGlobalTermWeighter
    {

        public DummyFrequencyBasedGlobalTermWeighter()
        {
            super();
        }

        public DummyFrequencyBasedGlobalTermWeighter(
            final VectorFactory<? extends Vector> vectorFactory)
        {
            super(vectorFactory);
        }

        public int getDimensionality()
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        public Vector getGlobalWeights()
        {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}