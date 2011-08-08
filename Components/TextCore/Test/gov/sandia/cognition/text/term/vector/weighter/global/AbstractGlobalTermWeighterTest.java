/*
 * File:                AbstractGlobalTermWeighterTest.java
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

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractGlobalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractGlobalTermWeighterTest
{
    /**
     * Creates a new test.
     */
    public AbstractGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class AbstractGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        AbstractGlobalTermWeighter instance = new DummyGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance = new DummyGlobalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of getVectorFactory method, of class AbstractGlobalTermWeighter.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class AbstractGlobalTermWeighter.
     */
    @Test
    public void testSetVectorFactory()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        AbstractGlobalTermWeighter instance = new DummyGlobalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    public static class DummyGlobalTermWeighter
        extends AbstractGlobalTermWeighter
    {
        public DummyGlobalTermWeighter()
        {
            super();
        }

        public DummyGlobalTermWeighter(
            final VectorFactory<? extends Vector> vectorFactory)
        {
            super(vectorFactory);
        }

        public void add(
            final Vector document)
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        public boolean remove(
            final Vector document)
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        public int getDocumentCount()
        {
            throw new UnsupportedOperationException("Not supported.");
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