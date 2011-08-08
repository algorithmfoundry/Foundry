/*
 * File:                MannWhitneyUConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.method.MannWhitneyUConfidence.Statistic;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class MannWhitneyUConfidenceTest extends TestCase
{
    
    public static final Random random = new Random( 1 );
    
    public MannWhitneyUConfidenceTest(String testName)
    {
        super(testName);
    }
        
    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        MannWhitneyUConfidence instance = new MannWhitneyUConfidence();
        MannWhitneyUConfidence clone = (MannWhitneyUConfidence) instance.clone();
        assertNotSame( instance, clone );
    }
    
    /**
     * Test of evaluateNullHypothesis method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.
     */
    public void testEvaluateNullHypothesis()
    {
        System.out.println("evaluateNullHypothesis");
        
        Collection<InputOutputPair<Double,Boolean>> scoreClassPairs =
            new LinkedList<InputOutputPair<Double,Boolean>>();
        
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(18.0,true) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(21.0,true) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(23.0,true) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(25.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(26.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(29.0,true) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(34.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(37.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(39.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(42.0,true) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(48.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(56.0,false) );
        scoreClassPairs.add( new DefaultInputOutputPair<Double,Boolean>(104.0,false) );
        
        MannWhitneyUConfidence test = new MannWhitneyUConfidence();
        MannWhitneyUConfidence.Statistic stat =
            test.evaluateNullHypothesis( scoreClassPairs );
        
        final double EPS = 1e-5;
        assertEquals( 7.0, stat.getU() );
        assertEquals( 5, stat.getN1() );
        assertEquals( 8, stat.getN2() );
        assertEquals( -1.903005, stat.getZ(), EPS );
        assertEquals(  0.057040, stat.getNullHypothesisProbability(), EPS );
    }
    
    
    public void testEvaluateNullHypothesis2()
    {
        System.out.println("evaluateNullHypothesis2");
        
        
        Collection<Double> data1 = Arrays.asList( 23.0, 18.0, 29.0, 42.0, 21.0 );
        Collection<Double> data2 = Arrays.asList( 37.0, 56.0, 39.0, 34.0, 26.0, 104.0, 48.0, 25.0 );
        
        MannWhitneyUConfidence test = new MannWhitneyUConfidence();
        MannWhitneyUConfidence.Statistic stat =
            test.evaluateNullHypothesis( data1, data2 );
        
        final double EPS = 1e-5;
        assertEquals( 7.0, stat.getU() );
        assertEquals( 5, stat.getN1() );
        assertEquals( 8, stat.getN2() );
        assertEquals( -1.903005, stat.getZ(), EPS );
        assertEquals(  0.057040, stat.getNullHypothesisProbability(), EPS );
        
    }
    
    
    public static MannWhitneyUConfidence.Statistic createStatisticInstance()
    {
        
        int N = random.nextInt(100) + 10;
        int N1 = random.nextInt(N-2) + 1;
        int N2 = N-N1;
        Collection<Double> data1 = new LinkedList<Double>();
        for( int i = 0; i < N1; i++ )
        {
            data1.add( random.nextDouble() );
        }
        Collection<Double> data2 = new LinkedList<Double>();
        for( int i = 0; i < N2; i++ )
        {
            data2.add( random.nextDouble() );
        }
        
        Collection<Double> dataAll = new LinkedList<Double>();
        dataAll.addAll( data1 );
        dataAll.addAll( data2 );
        double[] rank = WilcoxonSignedRankConfidence.ranks( dataAll );
        double sum1 = 0.0;
        for( int i = 0; i < N1; i++ )
        {
            sum1 += rank[i];
        }
        double sum2 = 0.0;
        for( int i = N1; i < (N1+N2); i++ )
        {
            sum2 += rank[i];
        }
        
        return new MannWhitneyUConfidence.Statistic( sum1, N1, sum2, N2 );
        
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticClone()
    {
        System.out.println("Statistic.clone");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        MannWhitneyUConfidence.Statistic clone = (Statistic) instance.clone();
        assertEquals( instance.getN1(), clone.getN1() );
        assertEquals( instance.getN2(), clone.getN2() );
        assertEquals( instance.getU(), clone.getU() );
        assertEquals( instance.getZ(), clone.getZ() );
        assertEquals( instance.getNullHypothesisProbability(), clone.getNullHypothesisProbability() );

        
    }
    
    /**
     * Test of computeU method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticComputeU()
    {
        System.out.println("Statistic.computeU");
        
        System.out.println( "This is checked in testEvaluateNullHypothesis()" );
    }
    
    /**
     * Test of computeZ method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticComputeZ()
    {
        System.out.println("Statistic.computeZ");
        
        System.out.println( "This is checked in testEvaluateNullHypothesis()" );

    }
    
    /**
     * Test of computeNullHypothesisProbability method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticComputeNullHypothesisProbability()
    {
        System.out.println("Statistic.computeNullHypothesisProbability");
        
        System.out.println( "This is checked in testEvaluateNullHypothesis()" );

    }
    
    /**
     * Test of getU method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticGetU()
    {
        System.out.println("Statistic.getU");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        
        assertTrue( instance.getU() > 0.0 );
    }
    
    /**
     * Test of setU method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticSetU()
    {
        System.out.println("Statistic.setU");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        
        assertTrue( instance.getU() > 0.0 );
        
        double U2 = instance.getU() + 1.0;
        instance.setU( U2 );
        assertEquals( U2, instance.getU() );

    }
    
    /**
     * Test of getZ method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticGetZ()
    {
        System.out.println("Statistic.getZ");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getZ() <= 0.0 );
    }
    
    /**
     * Test of setZ method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticSetZ()
    {
        System.out.println("Statistic.setZ");

        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getZ() <= 0.0 );
        
        double z2 = instance.getZ() - 1.0;
        instance.setZ( z2 );
        assertEquals( z2, instance.getZ() );
    }
    
    /**
     * Test of getN1 method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticGetN1()
    {
        System.out.println("Statistic.getN1");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getN1() > 0 );

    }
    
    /**
     * Test of setN1 method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticSetN1()
    {
        System.out.println("Statistic.setN1");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getN1() > 0 );

        int N12 = instance.getN1() + 1;
        instance.setN1( N12 );
        assertEquals( N12, instance.getN1() );
        
    }
    
    /**
     * Test of getN2 method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticGetN2()
    {
        System.out.println("Statistic.getN2");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getN2() > 0 );

    }
    
    /**
     * Test of setN2 method, of class gov.sandia.cognition.learning.util.statistics.MannWhitneyUConfidence.Statistic.
     */
    public void testStatisticSetN2()
    {
        System.out.println("Statistic.setN2");
        
        MannWhitneyUConfidence.Statistic instance = createStatisticInstance();
        assertTrue( instance.getN2() > 0 );

        int N22 = instance.getN2() + 1;
        instance.setN2( N22 );
        assertEquals( N22, instance.getN2() );
    }
    
}
