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

import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
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

        MapBasedDataHistogram<String> empty = new MapBasedDataHistogram<String>();
        MapBasedDataHistogram<String> baseCounts = new MapBasedDataHistogram<String>();
        baseCounts.add("a", 50);
        baseCounts.add("b", 50);


        assertEquals(0.0, instance.computeSplitGain(baseCounts, baseCounts, empty), epsilon);
        assertEquals(0.0, instance.computeSplitGain(baseCounts, empty, baseCounts), epsilon);

        MapBasedDataHistogram<String> as = new MapBasedDataHistogram<String>();
        as.add("a", 50);
        MapBasedDataHistogram<String> bs = new MapBasedDataHistogram<String>();
        bs.add("b", 50);
        assertEquals(0.5, instance.computeSplitGain(baseCounts, as, bs), epsilon);
        assertEquals(0.5, instance.computeSplitGain(baseCounts, bs, as), epsilon);

        MapBasedDataHistogram<String> mixed = new MapBasedDataHistogram<String>();
        mixed.add("a", 25);
        mixed.add("b", 25);
        assertEquals(0.0, instance.computeSplitGain(baseCounts, mixed, mixed), epsilon);

        MapBasedDataHistogram<String> tenAs = new MapBasedDataHistogram<String>();
        tenAs.add("a", 10);


        MapBasedDataHistogram<String> moreBs = new MapBasedDataHistogram<String>();
        moreBs.add("a", 15);
        moreBs.add("b", 25);
        assertEquals(0.3125, instance.computeSplitGain(baseCounts, tenAs, moreBs), epsilon);
        assertEquals(0.3125, instance.computeSplitGain(baseCounts, moreBs, tenAs), epsilon);

    }

    /**
     * Test of giniImpurity method, of class VectorThresholdGiniImpurityLearner.
     */
    public void testGiniImpurity()
    {
        final double epsilon = 0.00001;
        MapBasedDataHistogram<String> empty = new MapBasedDataHistogram<String>();
        assertEquals(0.0, VectorThresholdGiniImpurityLearner.giniImpurity(empty), epsilon);
        MapBasedDataHistogram<String> pure = new MapBasedDataHistogram<String>();
        pure.add("a", 100);
        assertEquals(0.0, VectorThresholdGiniImpurityLearner.giniImpurity(pure), epsilon);
        
        MapBasedDataHistogram<String> impure = new MapBasedDataHistogram<String>();
        impure.add("a", 50);
        impure.add("b", 50);
        assertEquals(0.5, VectorThresholdGiniImpurityLearner.giniImpurity(impure), epsilon);

        MapBasedDataHistogram<String> almostPure = new MapBasedDataHistogram<String>();
        almostPure.add("a", 1);
        almostPure.add("b", 99);
        assertEquals(0.0198, VectorThresholdGiniImpurityLearner.giniImpurity(almostPure), epsilon);
        
        
        MapBasedDataHistogram<Integer> lots = new MapBasedDataHistogram<Integer>();
        for (int i = 0; i < 100; i++)
        {
            lots.add(i, 1);
        }
        assertEquals(0.99, VectorThresholdGiniImpurityLearner.giniImpurity(lots), epsilon);

    }

}
