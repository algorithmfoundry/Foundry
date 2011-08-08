/*
 * File:                AbstractTermWeightNormalizerTest.java
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

package gov.sandia.cognition.text.term.vector.weighter.normalize;

import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractTermWeightNormalizer.
 *
 * @author Justin Basilico
 */
public class AbstractTermWeightNormalizerTest
{

    /**
     * Creates a new unit test.
     */
    public AbstractTermWeightNormalizerTest()
    {
    }

    /**
     * Test of constructors of class AbstractTermWeightNormalizer.
     */
    @Test
    public void testConstructors()
    {
        AbstractTermWeightNormalizer result = new DummyTermWeightNormalizer();
        assertNotNull(result);
    }

    public static class DummyTermWeightNormalizer
        extends AbstractTermWeightNormalizer
    {

        public DummyTermWeightNormalizer()
        {
            super();
        }

        public void normalizeWeights(Vector weights,
            Vector counts,
            Vector globalWeights)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}