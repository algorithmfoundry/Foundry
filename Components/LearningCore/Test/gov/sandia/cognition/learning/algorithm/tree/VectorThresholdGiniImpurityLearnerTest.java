/*
 * File:                VectorThresholdGiniImpurityLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import junit.framework.TestCase;

/**
 * Unit tests for class VectorThresholdGiniImpurityLearner.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorThresholdGiniImpurityLearnerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public VectorThresholdGiniImpurityLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of computeSplitGain method, of class VectorThresholdGiniImpurityLearner.
     */
    public void testComputeSplitGain()
    {
        final double epsilon = 0.0001;
        VectorThresholdGiniImpurityLearner<String> instance =
            new VectorThresholdGiniImpurityLearner<String>();

        DefaultDataDistribution<String> empty = new DefaultDataDistribution<String>();
        DefaultDataDistribution<String> baseCounts = new DefaultDataDistribution<String>();
        baseCounts.increment("a", 50);
        baseCounts.increment("b", 50);


        assertEquals(0.0, instance.computeSplitGain(baseCounts, baseCounts, empty), epsilon);
        assertEquals(0.0, instance.computeSplitGain(baseCounts, empty, baseCounts), epsilon);

        DefaultDataDistribution<String> as = new DefaultDataDistribution<String>();
        as.increment("a", 50);
        DefaultDataDistribution<String> bs = new DefaultDataDistribution<String>();
        bs.increment("b", 50);
        assertEquals(0.5, instance.computeSplitGain(baseCounts, as, bs), epsilon);
        assertEquals(0.5, instance.computeSplitGain(baseCounts, bs, as), epsilon);

        DefaultDataDistribution<String> mixed = new DefaultDataDistribution<String>();
        mixed.increment("a", 25);
        mixed.increment("b", 25);
        assertEquals(0.0, instance.computeSplitGain(baseCounts, mixed, mixed), epsilon);

        DefaultDataDistribution<String> tenAs = new DefaultDataDistribution<String>();
        tenAs.increment("a", 10);


        DefaultDataDistribution<String> moreBs = new DefaultDataDistribution<String>();
        moreBs.increment("a", 15);
        moreBs.increment("b", 25);
        assertEquals(0.3125, instance.computeSplitGain(baseCounts, tenAs, moreBs), epsilon);
        assertEquals(0.3125, instance.computeSplitGain(baseCounts, moreBs, tenAs), epsilon);

    }

    /**
     * Test of giniImpurity method, of class VectorThresholdGiniImpurityLearner.
     */
    public void testGiniImpurity()
    {
        final double epsilon = 0.00001;
        DefaultDataDistribution<String> empty = new DefaultDataDistribution<String>();
        assertEquals(0.0, VectorThresholdGiniImpurityLearner.giniImpurity(empty), epsilon);
        DefaultDataDistribution<String> pure = new DefaultDataDistribution<String>();
        pure.increment("a", 100);
        assertEquals(0.0, VectorThresholdGiniImpurityLearner.giniImpurity(pure), epsilon);
        
        DefaultDataDistribution<String> impure = new DefaultDataDistribution<String>();
        impure.increment("a", 50);
        impure.increment("b", 50);
        assertEquals(0.5, VectorThresholdGiniImpurityLearner.giniImpurity(impure), epsilon);

        DefaultDataDistribution<String> almostPure = new DefaultDataDistribution<String>();
        almostPure.increment("a", 1);
        almostPure.increment("b", 99);
        assertEquals(0.0198, VectorThresholdGiniImpurityLearner.giniImpurity(almostPure), epsilon);
        
        
        DefaultDataDistribution<Integer> lots = new DefaultDataDistribution<Integer>();
        for (int i = 0; i < 100; i++)
        {
            lots.increment(i, 1);
        }
        assertEquals(0.99, VectorThresholdGiniImpurityLearner.giniImpurity(lots), epsilon);

    }

}
