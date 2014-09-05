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

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
//        assertEquals( 5.128623188405797, result.getTotalVariance(), TOLERANCE );
//        assertEquals( 2.497897384964844, result.getTestStatistic(0,1), TOLERANCE );


    }

    @Test
    public void testKnownValues3()
    {
        System.out.println( "known values 3" );

        // http://www.wavemetrics.com/products/igorpro/dataanalysis/statistics/tests/statistics_pxp29.htm
        // (0,1): 1.35108  (SE=2.94526)
        // (0,2): 1.10577  (SE=2.94526)
        // (0,3): 3.47157  (SE=3.04185)
        // (0,4): 0.479342 (SE=2.94526)
        // (0,5): 1.52398  (SE=3.04185)
        // (1,2): 0.253919 (SE=2.84539)
        // (1,3): 4.93651  (SE=2.94526)
        // (1,4): 0.902337 (SE=2.84539)
        // (1,5): 2.92504  (SE=2.94526)
        // (2,3): 4.69120  (SE=2.94526)
        // (2,4): 0.648414 (SE=2.84539)
        // (2,5): 2.67973  (SE=2.94526)
        // (3,4): 4.06477  (SE=2.94526)
        // (3,5): 1.94759  (SE=3.04185)
        // (4,5): 2.05330  (SE=2.94526)
        Collection<? extends Collection<Double>> data3 = createData3();

        TukeyKramerConfidence instance = this.createInstance();
        TukeyKramerConfidence.Statistic result =
            instance.evaluateNullHypotheses(data3);
        System.out.println( "Stat3:\n" + result );
        assertEquals( 2.9644980, result.getStandardErrors().getElement(0,1), TOLERANCE );
    }

    @Test
    public void testKnownValues4()
    {
        
        /**
         * 
         * From
         * 
         * Rasch, Herrendoerfer, Bock, Victor, Guiard
         * ISBN 3-486-23146-4
         * 
         * (In German)
         * 
         * Verfahrensbibliothek. Band 1.
         * Page 851
         * 
         * Verified with Statistica
         * 
         */        
        System.out.println( "known values 4" );
        
        @SuppressWarnings("unchecked")
        List<List<Double>> testData = Arrays.asList(
                Arrays.asList( 529d, 508d, 501d, 534d, 510d, 504d ),
                Arrays.asList( 505d, 521d, 560d, 516d, 598d, 552d ),
                Arrays.asList( 537d, 569d, 499d, 501d, 506d, 600d ),
                Arrays.asList( 619d, 632d, 644d, 638d, 623d ),
                Arrays.asList( 565d, 596d, 631d, 667d, 613d, 580d )
            );
        
        TukeyKramerConfidence t = new TukeyKramerConfidence();
        TukeyKramerConfidence.Statistic stat = t.evaluateNullHypotheses(testData);
        System.out.println( "Stat4:\n" + stat );
        int treatments = stat.getTreatmentCount();
        assertEquals( 5, stat.getTreatmentCount() );
        List<Double> means = stat.getTreatmentMeans();
        assertEquals( 514.33333, means.get(0), TOLERANCE );
        assertEquals( 542.00000, means.get(1), TOLERANCE );
        assertEquals( 535.33333, means.get(2), TOLERANCE );
        assertEquals( 631.20000, means.get(3), TOLERANCE );
        assertEquals( 608.66667, means.get(4), TOLERANCE );
        
        int subjects = (int) UnivariateStatisticsUtil.computeSum(stat.getSubjectCounts());
        assertEquals( 29, subjects );
        
        int dof = subjects - treatments;
        assertEquals( 24, dof );
        
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(0, 0)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(1, 1)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(2, 2)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(3, 3)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(4, 4)  , TOLERANCE );
        
        assertEquals( 0.541176,  stat.getNullHypothesisProbability(0, 1)  , TOLERANCE );
        assertEquals( 0.541176,  stat.getNullHypothesisProbability(1, 0)  , TOLERANCE );

        assertEquals( 0.763884,  stat.getNullHypothesisProbability(0, 2)  , TOLERANCE );
        assertEquals( 0.763884,  stat.getNullHypothesisProbability(2, 0)  , TOLERANCE );

        assertEquals( 0.000145,  stat.getNullHypothesisProbability(0, 3)  , TOLERANCE );
        assertEquals( 0.000145,  stat.getNullHypothesisProbability(3, 0)  , TOLERANCE );

        assertEquals( 0.000300,  stat.getNullHypothesisProbability(0, 4)  , TOLERANCE );
        assertEquals( 0.000300,  stat.getNullHypothesisProbability(4, 0)  , TOLERANCE );

        assertEquals( 0.995624,  stat.getNullHypothesisProbability(1, 2)  , TOLERANCE );
        assertEquals( 0.995624,  stat.getNullHypothesisProbability(2, 1)  , TOLERANCE );

        assertEquals( 0.000766,  stat.getNullHypothesisProbability(1, 3)  , TOLERANCE );
        assertEquals( 0.000766,  stat.getNullHypothesisProbability(3, 1)  , TOLERANCE );

        assertEquals( 0.008328,  stat.getNullHypothesisProbability(1, 4)  , TOLERANCE );
        assertEquals( 0.008328,  stat.getNullHypothesisProbability(4, 1)  , TOLERANCE );

        assertEquals( 0.000391,  stat.getNullHypothesisProbability(2, 3)  , TOLERANCE );
        assertEquals( 0.000391,  stat.getNullHypothesisProbability(3, 2)  , TOLERANCE );

        assertEquals( 0.748831,  stat.getNullHypothesisProbability(4, 3)  , TOLERANCE );
        assertEquals( 0.748831,  stat.getNullHypothesisProbability(3, 4)  , TOLERANCE );
        
    }

    @Test
    public void testKnownValues5()
    {

        // http://business.fullerton.edu/isds/zgoldstein/440/Contents/Course%2520Material%2FNotes/3.%2520Notes%2520on%2520ANOVA.docx
        System.out.println( "known values 5" );
        
        @SuppressWarnings("unchecked")
        List<List<Double>> testData = Arrays.asList(
                Arrays.asList( 31000.0, 25000.0, 28500.0, 29000.0, 32000.0, 27500.0 ),
                Arrays.asList( 24500.0, 25500.0, 27000.0, 26500.0, 25000.0, 28000.0, 27500.0 ),
                Arrays.asList( 30500.0, 28000.0, 32500.0, 28000.0, 31000.0 ),
                Arrays.asList( 24500.0, 27000.0, 26000.0, 21000.0, 25500.0, 26000.0 )
            );

        TukeyKramerConfidence t = new TukeyKramerConfidence();
        TukeyKramerConfidence.Statistic stat = t.evaluateNullHypotheses(testData);
        System.out.println( "Stat5:\n" + stat );
        int treatments = stat.getTreatmentCount();
        assertEquals( 4, stat.getTreatmentCount() );
        List<Double> means = stat.getTreatmentMeans();
        assertEquals( 28833.33333, means.get(0), TOLERANCE );
        assertEquals( 26285.71429, means.get(1), TOLERANCE );
        assertEquals( 30000.00000, means.get(2), TOLERANCE );
        assertEquals( 25000.00000, means.get(3), TOLERANCE );
        
        int subjects = (int) UnivariateStatisticsUtil.computeSum(stat.getSubjectCounts());
        assertEquals( 24, subjects );
        
        int dof = subjects - treatments;
        assertEquals( 20, dof );
        
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(0, 0)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(1, 1)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(2, 2)  , TOLERANCE );
        assertEquals( 1.000000,  stat.getNullHypothesisProbability(3, 3)  , TOLERANCE );

        
    }
    
}
