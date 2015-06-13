/*
 * File:            RandomForestFactoryTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.algorithm.ensemble.BaggingCategorizerLearner;
import gov.sandia.cognition.learning.algorithm.ensemble.BaggingRegressionLearner;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit tests for class {@link RandomForestFactory}.
 * 
 * @author  Justin Basilico
 * @since   3.4.2
 */
public class RandomForestFactoryTest
    extends Object
{
    protected Random random = new Random(3333);
    
    /**
     * Test of createCategorizationLearner method, of class RandomForestFactory.
     */
    @Test
    public void testCreateCategorizationLearner()
    {
        int ensembleSize = 3 + random.nextInt(1000);
        double baggingFraction = random.nextDouble();
        double dimensionsFraction = random.nextDouble();
        int maxTreeDepth = 3 + random.nextInt(10);
        int minLeafSize = 4 + random.nextInt(10);
        Random random = new Random();
        BaggingCategorizerLearner<Vector, String> result
            = RandomForestFactory.createCategorizationLearner(ensembleSize,
            baggingFraction, dimensionsFraction, maxTreeDepth, minLeafSize,
            random);
        assertEquals(ensembleSize, result.getMaxIterations());
        assertEquals(baggingFraction, result.getPercentToSample(), 0.0);
        assertSame(random, result.getRandom());
        @SuppressWarnings("rawtypes")
        CategorizationTreeLearner treeLearner = 
            (CategorizationTreeLearner) result.getLearner();
        assertEquals(maxTreeDepth, treeLearner.getMaxDepth());
        assertTrue(treeLearner.getLeafCountThreshold() >= 2 * minLeafSize);
        RandomSubVectorThresholdLearner<?> randomSubspace = (RandomSubVectorThresholdLearner<?>)
            treeLearner.getDeciderLearner();
        assertEquals(dimensionsFraction, randomSubspace.getPercentToSample(), 0.0);
        assertSame(random, randomSubspace.getRandom());
        VectorThresholdInformationGainLearner<?> splitLearner = (VectorThresholdInformationGainLearner<?>)
            randomSubspace.getSubLearner();
        assertEquals(minLeafSize, splitLearner.getMinSplitSize());
    }

    /**
     * Test of createRegressionLearner method, of class RandomForestFactory.
     */
    @Test
    public void testCreateRegressionLearner()
    {
        int ensembleSize = 3 + random.nextInt(1000);
        double baggingFraction = random.nextDouble();
        double dimensionsFraction = random.nextDouble();
        int maxTreeDepth = 3 + random.nextInt(10);
        int minLeafSize = 4 + random.nextInt(10);
        Random random = new Random();
        BaggingRegressionLearner<Vector> result
            = RandomForestFactory.createRegressionLearner(ensembleSize,
            baggingFraction, dimensionsFraction, maxTreeDepth, minLeafSize,
            random);
        assertEquals(ensembleSize, result.getMaxIterations());
        assertEquals(baggingFraction, result.getPercentToSample(), 0.0);
        assertSame(random, result.getRandom());
        @SuppressWarnings("rawtypes")
        RegressionTreeLearner treeLearner = 
            (RegressionTreeLearner) result.getLearner();
        assertEquals(maxTreeDepth, treeLearner.getMaxDepth());
        assertTrue(treeLearner.getLeafCountThreshold() >= 2 * minLeafSize);
        assertNull(treeLearner.getRegressionLearner());
        RandomSubVectorThresholdLearner<?> randomSubspace = (RandomSubVectorThresholdLearner<?>)
            treeLearner.getDeciderLearner();
        assertEquals(dimensionsFraction, randomSubspace.getPercentToSample(), 0.0);
        assertSame(random, randomSubspace.getRandom());
        VectorThresholdVarianceLearner splitLearner = (VectorThresholdVarianceLearner)
            randomSubspace.getSubLearner();
        assertEquals(minLeafSize, splitLearner.getMinSplitSize());
    }
    
}
