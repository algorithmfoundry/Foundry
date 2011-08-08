/*
 * File:                UnitTermWeightNormalizerTest.java
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
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class UnitTermWeightNormalizer.
 *
 * @author Justin Basilico
 */
public class UnitTermWeightNormalizerTest
{

    /**
     * Creates a new unit test.
     */
    public UnitTermWeightNormalizerTest()
    {
    }

    /**
     * Test of constructors of class UnitTermWeightNormalizer.
     */
    @Test
    public void testConstructors()
    {
        UnitTermWeightNormalizer instance = new UnitTermWeightNormalizer();
        assertNotNull(instance);
    }

    /**
     * Test of normalizeWeights method, of class UnitTermWeightNormalizer.
     */
    @Test
    public void testNormalizeWeights()
    {
        Random random = new Random();
        UnitTermWeightNormalizer instance =
            new UnitTermWeightNormalizer();
        
        int dimensionality = 10;
        Vector weights = VectorFactory.getDefault().createUniformRandom(
            dimensionality, 0.0, 1.0, random);
        Vector expected = weights.unitVector();
        instance.normalizeWeights(weights, null, null);
        assertEquals(expected, weights);

        weights.zero();
        expected = weights.clone();
        instance.normalizeWeights(weights, null, null);
        assertEquals(expected, weights);

        weights.setElement(0, 1.0);
        expected = weights.clone();
        instance.normalizeWeights(weights, null, null);
        assertEquals(expected, weights);
    }

}