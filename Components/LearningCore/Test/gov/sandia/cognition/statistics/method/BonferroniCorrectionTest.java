/*
 * File:                BonferroniCorrectionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 31, 2011, Sandia Corporation.
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
 * Tests for class BonferroniCorrectionTest.
 * @author krdixon
 */
public class BonferroniCorrectionTest
    extends MultipleHypothesisComparisonTestHarness
{


    /**
     * Default Constructor
     */
    public BonferroniCorrectionTest()
    {
    }

    /**
     * Tests the constructors of class BonferroniCorrectionTest.
     */
    @Test
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        BonferroniCorrection instance = new BonferroniCorrection();
        assertSame( BonferroniCorrection.DEFAULT_PAIRWISE_TEST, instance.getPairwiseTest() );

        instance = new BonferroniCorrection( WilcoxonSignedRankConfidence.INSTANCE );
        assertSame( WilcoxonSignedRankConfidence.INSTANCE, instance.getPairwiseTest() );
    }


    /**
     * Test of getPairwiseTest method, of class BonferroniCorrection.
     */
    @Test
    public void testGetPairwiseTest()
    {
        System.out.println("getPairwiseTest");
        BonferroniCorrection instance = new BonferroniCorrection( WilcoxonSignedRankConfidence.INSTANCE );
        assertSame( WilcoxonSignedRankConfidence.INSTANCE, instance.getPairwiseTest() );
    }

    /**
     * Test of setPairwiseTest method, of class BonferroniCorrection.
     */
    @Test
    public void testSetPairwiseTest()
    {
        System.out.println("setPairwiseTest");
        NullHypothesisEvaluator<Collection<? extends Number>> pairwiseTest = WilcoxonSignedRankConfidence.INSTANCE;
        BonferroniCorrection instance = new BonferroniCorrection( pairwiseTest );
        assertSame( pairwiseTest, instance.getPairwiseTest() );

        instance.setPairwiseTest(null);
        assertNull( instance.getPairwiseTest() );
        instance.setPairwiseTest(pairwiseTest);
        assertSame( pairwiseTest, instance.getPairwiseTest() );

    }

    @Override
    public BonferroniCorrection createInstance()
    {
        return new BonferroniCorrection();
    }

    @Override
    public void testKnownValues()
    {
        Collection<? extends Collection<Double>> experiment = createData1();

        BonferroniCorrection instance = this.createInstance();
        AdjustedPValueStatistic result =
            instance.evaluateNullHypotheses( experiment );
        System.out.println( "Result: " + result );

        assertEquals( 3, result.getTreatmentCount() );
        assertEquals( result.getUncompensatedAlpha()/3, result.getAdjustedAlpha(), TOLERANCE );

        for( int i = 0; i < result.getTreatmentCount(); i++ )
        {
            for( int j = 0; j < result.getTreatmentCount(); j++ )
            {
                System.out.println( "(" + i + "," + j + "): " +
                    "z = " + result.getTestStatistic(i, j) +
                    ", p = " + result.getNullHypothesisProbability(i, j) +
                    ", NH = " + result.acceptNullHypothesis(i, j) );

            }
        }

        AdjustedPValueStatistic clone = result.clone();
        assertNotSame( result.getPairwiseTestStatistics(), clone.getPairwiseTestStatistics() );
        assertEquals( result.toString(), clone.toString() );
    }

}
