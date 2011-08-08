/*
 * File:                MultipleComparisonExperimentTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 2, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class MultipleComparisonExperimentTest.
 * @author krdixon
 */
public class MultipleComparisonExperimentTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Default Constructor
     */
    public MultipleComparisonExperimentTest()
    {
    }

    /**
     * Tests the constructors of class MultipleComparisonExperimentTest.
     */
    @Test
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MultipleComparisonExperiment instance = new MultipleComparisonExperiment();
        assertSame( MultipleComparisonExperiment.DEFAULT_BLOCK_EXPERIMENT_COMPARISON, instance.getBlockExperimentComparison() );
        assertSame( MultipleComparisonExperiment.DEFAULT_POST_HOC_TEST, instance.getPostHocTest() );
        assertEquals( MultipleComparisonExperiment.DEFAULT_ALPHA, instance.getAlpha(), TOLERANCE );
    }

    /**
     * Test of getBlockExperimentComparison method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testGetBlockExperimentComparison()
    {
        System.out.println("getBlockExperimentComparison");
        MultipleComparisonExperiment instance = this.createInstance();
        BlockExperimentComparison<Number> blockExperimentComparison =
            instance.getBlockExperimentComparison();
        assertNotNull( blockExperimentComparison );
    }

    /**
     * Test of setBlockExperimentComparison method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testSetBlockExperimentComparison()
    {
        System.out.println("setBlockExperimentComparison");
        MultipleComparisonExperiment instance = this.createInstance();
        BlockExperimentComparison<Number> blockExperimentComparison =
            instance.getBlockExperimentComparison();
        assertNotNull( blockExperimentComparison );
        instance.setBlockExperimentComparison(null);
        assertNull( instance.getBlockExperimentComparison() );
        instance.setBlockExperimentComparison(blockExperimentComparison);
        assertSame( blockExperimentComparison, instance.getBlockExperimentComparison() );
    }

    /**
     * Test of getPostHocTest method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testGetPostHocTest()
    {
        System.out.println("getPostHocTest");
        MultipleComparisonExperiment instance = this.createInstance();
        MultipleHypothesisComparison<Collection<? extends Number>> postHocTest =
            instance.getPostHocTest();
        assertNotNull( postHocTest );
    }

    /**
     * Test of setPostHocTest method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testSetPostHocTest()
    {
        System.out.println("setPostHocTest");
        MultipleComparisonExperiment instance = this.createInstance();
        MultipleHypothesisComparison<Collection<? extends Number>> postHocTest =
            instance.getPostHocTest();
        assertNotNull( postHocTest );
        instance.setPostHocTest(null);
        assertNull( instance.getPostHocTest() );
        instance.setPostHocTest(postHocTest);
        assertSame( postHocTest, instance.getPostHocTest() );
    }

    /**
     * Test of getAlpha method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testGetAlpha()
    {
        System.out.println("getAlpha");
        MultipleComparisonExperiment instance = this.createInstance();
        assertEquals( MultipleComparisonExperiment.DEFAULT_UNCOMPENSATED_ALPHA, instance.getAlpha(), TOLERANCE );
    }

    /**
     * Test of setAlpha method, of class MultipleComparisonExperiment.
     */
    @Test
    public void testSetAlpha()
    {
        System.out.println("setAlpha");
        double alpha = RANDOM.nextDouble();
        MultipleComparisonExperiment instance = this.createInstance();
        instance.setAlpha(alpha);
        assertEquals( alpha, instance.getAlpha(), TOLERANCE );
    }

    @Override
    public MultipleComparisonExperiment createInstance()
    {
        return new MultipleComparisonExperiment();
    }

    @Override
    public void testKnownValues()
    {
    }

}
