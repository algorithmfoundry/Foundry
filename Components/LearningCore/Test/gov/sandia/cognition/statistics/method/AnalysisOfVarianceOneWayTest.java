/*
 * File:                AnalysisOfVarianceOneWayTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class AnalysisOfVarianceOneWayTest
    extends TestCase
{

    public AnalysisOfVarianceOneWayTest( String testName )
    {
        super( testName );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        AnalysisOfVarianceOneWay instance = new AnalysisOfVarianceOneWay();
        AnalysisOfVarianceOneWay clone = (AnalysisOfVarianceOneWay) instance.clone();
        assertNotSame( instance, clone );

    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.
     */
    public void testEvaluateNullHypothesis1()
    {
        System.out.println( "evaluateNullHypothesis1" );

        // I apologize for this clunky notation... it comes from a social
        // science text book: "Statistics for the Behavioral Sciences" by
        // Gravetter & Wallnau, Chapter 13.3, p. 406-412

        // I also double-checked the results with the anova() function in
        // octave
        Collection<Collection<Double>> data = new LinkedList<Collection<Double>>();

        data.add( Arrays.asList( 0.0, 1.0, 3.0, 1.0, 0.0 ) );
        data.add( Arrays.asList( 4.0, 3.0, 6.0, 3.0, 4.0 ) );
        data.add( Arrays.asList( 1.0, 2.0, 2.0, 0.0, 0.0 ) );

        AnalysisOfVarianceOneWay.Statistic stat =
            AnalysisOfVarianceOneWay.INSTANCE.evaluateNullHypothesis( data );

        final double EPS = 1e-4;
        assertEquals( 0.0017708, stat.getNullHypothesisProbability(), EPS );
        assertEquals( 11.25, stat.getF(), EPS );
        assertEquals( 2.0, stat.getDFbetween(), EPS );
        assertEquals( 12.0, stat.getDFwithin(), EPS );
        System.out.println( "p = " + stat.getNullHypothesisProbability() );
        System.out.println( "F = " + stat.getF() );
        System.out.println( "DFb = " + stat.getDFbetween() );
        System.out.println( "DFw = " + stat.getDFwithin() );
    }

    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.
     */
    public void testEvaluateNullHypothesis2()
    {
        System.out.println( "evaluateNullHypothesis2" );


        // I also double-checked the results with the anova() function in
        // octave
        Collection<Collection<Double>> data = new LinkedList<Collection<Double>>();

        data.add( Arrays.asList( 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ) );
        data.add( Arrays.asList( 1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0, 5.0, 5.0 ) );

        AnalysisOfVarianceOneWay.Statistic stat =
            AnalysisOfVarianceOneWay.INSTANCE.evaluateNullHypothesis( data );

        System.out.println( "p = " + stat.getNullHypothesisProbability() );
        System.out.println( "F = " + stat.getF() );
        System.out.println( "DFb = " + stat.getDFbetween() );
        System.out.println( "DFw = " + stat.getDFwithin() );

        final double EPS = 1e-4;
        assertEquals( 0.030842, stat.getNullHypothesisProbability(), EPS );
        assertEquals( 5.4878, stat.getF(), EPS );
        assertEquals( 1.0, stat.getDFbetween(), EPS );
        assertEquals( 18.0, stat.getDFwithin(), EPS );

    }

    public AnalysisOfVarianceOneWay.Statistic createStatisticInstance()
    {

        double F = Math.random() + 1.0;
        double dof1 = Math.random() * 10 + 1;
        double dof2 = Math.random() * 10 + 1;
        return new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println( "Statistic.clone" );

        AnalysisOfVarianceOneWay.Statistic instance = this.createStatisticInstance();
        AnalysisOfVarianceOneWay.Statistic clone = instance.clone();

        assertNotSame( instance, clone );
        assertEquals( instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability() );
        assertEquals( instance.getDFbetween(), clone.getDFbetween() );
        assertEquals( instance.getDFwithin(), clone.getDFwithin() );
        assertEquals( instance.getF(), clone.getF() );
    }

    /**
     * Test of getF method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticGetF()
    {
        System.out.println( "Statistic.getF" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );
        assertEquals( F, instance.getF() );
    }

    /**
     * Test of setF method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticSetF()
    {
        System.out.println( "Statistic.setF" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );

        assertEquals( F, instance.getF() );

        double F2 = F + 1.0;
        instance.setF( F2 );
        assertEquals( F2, instance.getF() );
    }

    /**
     * Test of getDFbetween method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticGetDFbetween()
    {
        System.out.println( "Statistic.getDFbetween" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );
        assertEquals( dof1, instance.getDFbetween() );

    }

    /**
     * Test of setDFbetween method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticSetDFbetween()
    {
        System.out.println( "Statistic.setDFbetween" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );
        assertEquals( dof1, instance.getDFbetween() );

        double dof12 = dof1 + 1.0;
        instance.setDFbetween( dof12 );
        assertEquals( dof12, instance.getDFbetween() );
    }

    /**
     * Test of getDFwithin method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticGetDFwithin()
    {
        System.out.println( "Statistic.getDFwithin" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );
        assertEquals( dof2, instance.getDFwithin() );

    }

    /**
     * Test of setDFwithin method, of class gov.sandia.cognition.learning.util.statistics.AnalysisOfVarianceOneWay.Statistic.
     */
    public void testStatisticSetDFwithin()
    {
        System.out.println( "Statistic.setDFwithin" );

        double F = Math.random();
        double dof1 = 10;
        double dof2 = 20;
        AnalysisOfVarianceOneWay.Statistic instance = new AnalysisOfVarianceOneWay.Statistic( F, dof1, dof2 );
        assertEquals( dof2, instance.getDFwithin() );

        double dof22 = dof2 + 1.0;
        instance.setDFwithin( dof22 );
        assertEquals( dof22, instance.getDFwithin() );
    }

    public void testMemberEvaluate()
    {
        System.out.println( "Member Evaluate" );

        // I also double-checked the results with the anova() function in
        // octave
        Collection<Double> data1 = Arrays.asList( 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 );
        Collection<Double> data2 = Arrays.asList( 1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0, 5.0, 5.0 );

        AnalysisOfVarianceOneWay anova = new AnalysisOfVarianceOneWay();

        AnalysisOfVarianceOneWay.Statistic stat =
            anova.evaluateNullHypothesis( data1, data2 );

        final double EPS = 1e-4;
        assertEquals( 0.030842, stat.getNullHypothesisProbability(), EPS );
        assertEquals( 5.4878, stat.getF(), EPS );
        assertEquals( 1.0, stat.getDFbetween(), EPS );
        assertEquals( 18.0, stat.getDFwithin(), EPS );

    }

}
