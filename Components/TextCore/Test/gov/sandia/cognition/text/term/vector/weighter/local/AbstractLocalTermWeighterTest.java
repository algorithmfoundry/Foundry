/*
 * File:                AbstractLocalTermWeighterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 28, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.local;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractLocalTermWeighter.
 *
 * @author Justin Basilico
 */
public class AbstractLocalTermWeighterTest
{

    /**
     * Creates a new unit test.
     */
    public AbstractLocalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class AbstractLocalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<? extends Vector> vectorFactory =
            SparseVectorFactory.getDefault();
        AbstractLocalTermWeighter instance = new DummyLocalTermWeighter();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance = new DummyLocalTermWeighter(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of computeLocalWeights method, of class AbstractLocalTermWeighter.
     */
    @Test
    public void testComputeLocalWeights()
    {
        Random random = new Random();
        AbstractLocalTermWeighter instance = new DummyLocalTermWeighter();
        int dimensionality = 10;
        Vector document = VectorFactory.getDefault().createUniformRandom(
            dimensionality, 0.0, 1.0, random);
        Vector result = instance.computeLocalWeights(document);
        assertEquals(document.plus(document), result);
    }

    public static class DummyLocalTermWeighter
        extends AbstractLocalTermWeighter
    {

        public DummyLocalTermWeighter()
        {
            super();
        }

        public DummyLocalTermWeighter(
            final VectorFactory<? extends Vector> vectorFactory)
        {
            super(vectorFactory);
        }

        public Vector computeLocalWeights(
            final Vector document)
        {
            return document.plus(document);
        }

    }

}