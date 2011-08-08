/*
 * File:                TukeyKramerConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 16, 2011, Sandia Corporation.
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
 *
 * @author krdixon
 */
public class TukeyKramerConfidenceTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Default constructor
     */
    public TukeyKramerConfidenceTest()
    {
    }

    @Override
    public TukeyKramerConfidence createInstance()
    {
        return new TukeyKramerConfidence();
    }

    /**
     * Test of clone method, of class TukeyKramerConfidence.
     */
    @Test
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        TukeyKramerConfidence instance = new TukeyKramerConfidence();
        assertNotNull( instance );
    }

    /**
     * Test of evaluateNullHypothesis method, of class TukeyKramerConfidence.
     */
    @Test
    @Override
    public void testKnownValues()
    {
        System.out.println("evaluateNullHypothesis");
        
        @SuppressWarnings("unchecked")
        Collection<? extends Collection<Double>> experiment = createData1();

        TukeyKramerConfidence instance = this.createInstance();
        TukeyKramerConfidence.Statistic result =
            instance.evaluateNullHypotheses(experiment);
        System.out.println( "Result: " + result );

        assertEquals( 3, result.getTreatmentCount() );
        assertEquals( 10, result.getSubjectCounts().get(0).intValue() );
        assertEquals( 10, result.getSubjectCounts().get(1).intValue() );
        assertEquals( 10, result.getSubjectCounts().get(2).intValue() );
        assertEquals( 7.75, result.getTreatmentMeans().get(0), TOLERANCE );
        assertEquals( 6.75, result.getTreatmentMeans().get(1), TOLERANCE );
        assertEquals( 5.70, result.getTreatmentMeans().get(2), TOLERANCE );

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

        TukeyKramerConfidence.Statistic clone = result.clone();
        assertNotSame( result.getTreatmentMeans(), clone.getTreatmentMeans() );
        assertNotSame( result.getSubjectCounts(), clone.getSubjectCounts() );
        assertEquals( result.toString(), clone.toString() );

    }

    @Test
    public void testKnownValues2()
    {
        System.out.println( "known values 2" );

        Collection<? extends Collection<Integer>> data2 = createData2();

        AnalysisOfVarianceOneWay.Statistic ar =
            AnalysisOfVarianceOneWay.INSTANCE.evaluateNullHypothesis(data2);
        System.out.println( "ANOVA: " + ar );
        assertEquals( 3.59, ar.getF(), 0.01 );

        TukeyKramerConfidence instance = this.createInstance();
        TukeyKramerConfidence.Statistic result =
            instance.evaluateNullHypotheses(data2);
        System.out.println( "Result: " + result );

        // MSwithin = 5.128623188405797
        assertEquals( 5.128623188405797, result.getTotalVariance(), TOLERANCE );
        assertEquals( 2.497897384964844, result.getTestStatistic(0,1), TOLERANCE );


    }

    @Test
    public void testKnownValues3()
    {
        System.out.println( "known values 3" );

        // http://www.wavemetrics.com/products/igorpro/dataanalysis/statistics/tests/statistics_pxp29.htm
        Collection<? extends Collection<Double>> data3 = createData3();

        TukeyKramerConfidence instance = this.createInstance();
        TukeyKramerConfidence.Statistic result =
            instance.evaluateNullHypotheses(data3);
        System.out.println( "Result: " + result );
        

    }

}
