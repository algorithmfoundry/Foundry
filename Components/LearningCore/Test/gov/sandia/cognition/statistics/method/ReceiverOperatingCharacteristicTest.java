/*
 * File:                ReceiverOperatingCharacteristicTest.java
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
import gov.sandia.cognition.learning.function.categorization.ScalarThresholdBinaryCategorizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class ReceiverOperatingCharacteristicTest extends TestCase
{

    public static final Random RANDOM = new Random(1);

    public ReceiverOperatingCharacteristicTest(String testName)
    {
        super(testName);
    }    
    
    public static ReceiverOperatingCharacteristic createInstance()
    {
        
        ScalarThresholdBinaryCategorizer targetFunction =
            new ScalarThresholdBinaryCategorizer( RANDOM.nextGaussian() );
        int N = RANDOM.nextInt(10)+10;
        double r = RANDOM.nextDouble();
        LinkedList<InputOutputPair<Double,Boolean>> data =
            new LinkedList<InputOutputPair<Double,Boolean>>();
        for( int i = 0; i < N; i++ )
        {
            double value = RANDOM.nextGaussian();
            double score = value + (RANDOM.nextDouble() * 2*r) - r;
            data.add( new DefaultInputOutputPair<Double,Boolean>( score, targetFunction.evaluate( value ) ) );
        }
        
        return ReceiverOperatingCharacteristic.create( data );
        
    }
    
    
    public static ReceiverOperatingCharacteristic createKnownInstance()
    {
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
        
        return ReceiverOperatingCharacteristic.create( scoreClassPairs );
    }


    public void testClone()
    {
        System.out.println( "clone" );

        ReceiverOperatingCharacteristic roc = createKnownInstance();
        ReceiverOperatingCharacteristic clone = roc.clone();
        assertNotNull( clone );
        assertNotSame( roc, clone );
        assertNotSame( roc.getSortedROCData(), clone.getSortedROCData() );
        assertNotSame( roc.getUtest(), clone.getUtest() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        ReceiverOperatingCharacteristic instance = createKnownInstance();
        
        assertEquals( 0.0/5.0, instance.evaluate( 0.0/8.0 ) );
        assertEquals( 1.0/5.0, instance.evaluate( 3.0/8.0 ) );
        assertEquals( 5.0/5.0, instance.evaluate( 8.0/8.0 ) );
        
        assertEquals( 0.0/5.0, instance.evaluate( 0.1 ) );
        assertEquals( 2.0/5.0, instance.evaluate( 0.8 ) );
        
    }
    
    /**
     * Test of getSortedROCData method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testGetSortedROCData()
    {
        System.out.println("getSortedROCData");
        
        ReceiverOperatingCharacteristic instance = createInstance();
        
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> data = instance.getSortedROCData();
        assertNotNull( data );
        
    }
    
    /**
     * Test of setSortedROCData method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testSetSortedROCData()
    {
        System.out.println("setSortedROCData");
        
        ReceiverOperatingCharacteristic instance = createInstance();
        
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> data = instance.getSortedROCData();
        assertNotNull( data );
        
        instance.setSortedROCData( null );
        assertNull( instance.getSortedROCData() );
        
        instance.setSortedROCData( data );
        assertSame( data, instance.getSortedROCData() );
    }
    
    /**
     * Test of create method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testCreate()
    {
        System.out.println("create");
        
        ReceiverOperatingCharacteristic roc = createKnownInstance();
        ReceiverOperatingCharacteristic.DataPoint data;
        
        for( int i = 0; i < roc.getSortedROCData().size(); i++ )
        {
            data = roc.getSortedROCData().get(i);
            System.out.printf( "%d: <%f> (%f,%f)\n", i, data.getClassifier().getThreshold(), data.getFalsePositiveRate(), data.getTruePositiveRate() );
        }
        
        data = roc.getSortedROCData().get(0);
        assertEquals( 104.0, data.getClassifier().getThreshold() );
        assertEquals( 1.0/8.0, data.getFalsePositiveRate() );
        assertEquals( 0.0/5.0, data.getTruePositiveRate() );
        System.out.println( data.getConfusionMatrix() );
        
        data = roc.getSortedROCData().get(1);
        assertEquals( 56.0, data.getClassifier().getThreshold() );
        assertEquals( 2.0/8.0, data.getFalsePositiveRate() );
        assertEquals( 0.0/5.0, data.getTruePositiveRate() );
        
        data = roc.getSortedROCData().get(8);
        assertEquals( 26.0, data.getClassifier().getThreshold() );
        assertEquals( 7.0/8.0, data.getFalsePositiveRate() );
        assertEquals( 2.0/5.0, data.getTruePositiveRate() );
        
        data = roc.getSortedROCData().get(roc.getSortedROCData().size()-1);
        assertEquals( 18.0, data.getClassifier().getThreshold() );
        assertEquals( 8.0/8.0, data.getFalsePositiveRate() );
        assertEquals( 5.0/5.0, data.getTruePositiveRate() );
        
        final double EPS = 1e-5;
        MannWhitneyUConfidence.Statistic stat = roc.getUtest();
        assertEquals( 7.0, stat.getU() );
        assertEquals( 5, stat.getN1() );
        assertEquals( 8, stat.getN2() );
        assertEquals( -1.903005, stat.getZ(), EPS );
        assertEquals(  0.057040, stat.getNullHypothesisProbability(), EPS );
        
    }
    
    /**
     * Test of computeStatistics method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testComputeStatistics()
    {
        System.out.println("computeStatistics");
        
        ReceiverOperatingCharacteristic instance = createKnownInstance();
        
        ReceiverOperatingCharacteristic.Statistic stat = instance.computeStatistics();
        
        final double EPS = 1e-5;
        assertEquals( 7.0, stat.getU() );
        assertEquals( 5, stat.getN1() );
        assertEquals( 8, stat.getN2() );
        assertEquals( -1.903005, stat.getZ(), EPS );
        assertEquals(  0.057040, stat.getNullHypothesisProbability(), EPS );
        
        double auc = (3.0/8.0)*(1.0/5.0) + (2.0/8.0)*(2.0/5.0);
        
        System.out.println( "AUC: " + stat.getAreaUnderCurve() );
        assertEquals( auc, stat.getAreaUnderCurve(), EPS );
        
        System.out.println( "Optimal: " + stat.getOptimalThreshold().getClassifier().getThreshold() );
        assertEquals( 18.0, stat.getOptimalThreshold().getClassifier().getThreshold() );
        
        System.out.println( "D': " + stat.getDPrime() );
        
    }
    
    /**
     * Test of getUtest method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testGetUtest()
    {
        System.out.println("getUtest");
        
        ReceiverOperatingCharacteristic instance = createInstance();
        assertNotNull( instance.getUtest() );
        
    }
    
    /**
     * Test of setUtest method, of class gov.sandia.cognition.learning.util.statistics.ReceiverOperatingCharacteristic.
     */
    public void testSetUtest()
    {
        System.out.println("setUtest");
        
        ReceiverOperatingCharacteristic instance = createInstance();
        MannWhitneyUConfidence.Statistic Utest = instance.getUtest();
        
        assertNotNull( Utest );
        instance.setUtest( null );
        
        instance.setUtest( Utest );
        assertSame( Utest, instance.getUtest() );
        
    }

    public void testKnownAUC1()
    {
        System.out.println( "Known AUC1" );

        LinkedList<InputOutputPair<Double,Boolean>> data =
            new LinkedList<InputOutputPair<Double, Boolean>>();

        data.add( DefaultInputOutputPair.create( 0.95, true ) );
        data.add( DefaultInputOutputPair.create( 0.9,  true ) );
        data.add( DefaultInputOutputPair.create( 0.8,  false ) );
        data.add( DefaultInputOutputPair.create( 0.7,  true ) );
        data.add( DefaultInputOutputPair.create( 0.65, true ) );
        data.add( DefaultInputOutputPair.create( 0.6,  true ) );
        data.add( DefaultInputOutputPair.create( 0.5,  true ) );
        data.add( DefaultInputOutputPair.create( 0.4,  false ) );
        data.add( DefaultInputOutputPair.create( 0.3,  true ) );
        data.add( DefaultInputOutputPair.create( 0.2,  true ) );
        data.add( DefaultInputOutputPair.create( 0.1,  false ) );
        data.add( DefaultInputOutputPair.create( 0.05, false ) );

        assertEquals( 0.75, ReceiverOperatingCharacteristic.create(data).computeStatistics().getAreaUnderCurve(), 1e-5 );
    }

}
