/*
 * File:                SidakCorrectionTest.java
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
import static org.junit.Assert.*;

/**
 *
 * @author krdixon
 */
public class SidakCorrectionTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Test
     */
    public SidakCorrectionTest()
    {
    }

    @Override
    public SidakCorrection createInstance()
    {
        return new SidakCorrection();
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        SidakCorrection instance = new SidakCorrection();
        assertSame( SidakCorrection.DEFAULT_PAIRWISE_TEST, instance.getPairwiseTest() );

        instance = new SidakCorrection( WilcoxonSignedRankConfidence.INSTANCE );
        assertSame( WilcoxonSignedRankConfidence.INSTANCE, instance.getPairwiseTest() );
    }

    @Override
    public void testKnownValues()
    {
        Collection<? extends Collection<Double>> experiment = createData1();

        SidakCorrection instance = this.createInstance();
        AdjustedPValueStatistic result =
            instance.evaluateNullHypotheses( experiment );
        System.out.println( "Result: " + result );

        assertEquals( 3, result.getTreatmentCount() );
        final int N = 3*2/2;
        assertEquals( 1.0-Math.pow(1.0-result.getUncompensatedAlpha(),1.0/N),
            result.getAdjustedAlpha(), TOLERANCE );

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
