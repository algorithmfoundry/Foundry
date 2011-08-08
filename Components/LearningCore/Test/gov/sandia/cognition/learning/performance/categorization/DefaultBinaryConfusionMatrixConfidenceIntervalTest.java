/*
 * File:                ConfusionMatrixConfidenceIntervalTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class DefaultBinaryConfusionMatrixConfidenceIntervalTest
    extends TestCase
{
    
    public static Random random = new Random( 1 );
    
    public DefaultBinaryConfusionMatrixConfidenceIntervalTest(String testName)
    {
        super(testName);
    }
    
    public static DefaultBinaryConfusionMatrixConfidenceInterval createInstance()
    {
        
        LinkedList<DefaultBinaryConfusionMatrix> data = new LinkedList<DefaultBinaryConfusionMatrix>();
        
        for( int i = 0; i < 10; i++ )
        {
            data.add( DefaultBinaryConfusionMatrixTest.createRandomInstance(random) );
        }
        
        return DefaultBinaryConfusionMatrixConfidenceInterval.compute( data, random.nextDouble() );
        
    }
    
    /**
     * Test of getFalsePositivesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetFalsePositivesRate()
    {
        System.out.println("getFalsePositivesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        assertNotNull( instance.getFalsePositivesRate() );
    }

    /**
     * Test of setFalsePositivesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetFalsePositivesRate()
    {
        System.out.println("setFalsePositivesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalsePositivesRate();
        assertNotNull( ci );
        
        instance.setFalsePositivesRate( null );
        assertNull( instance.getFalsePositivesRate() );
        
        instance.setFalsePositivesRate( ci );
        assertSame( ci, instance.getFalsePositivesRate() );
    }

    /**
     * Test of getFalseNegativesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetFalseNegativesRate()
    {
        System.out.println("getFalseNegativesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalseNegativesRate();
        assertNotNull( ci );
    }

    /**
     * Test of setFalseNegativesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetFalseNegativesRate()
    {
        System.out.println("setFalseNegativesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalseNegativesRate();
        assertNotNull( ci );
        
        instance.setFalseNegativesRate( null );
        assertNull( instance.getFalseNegativesRate() );
        
        instance.setFalseNegativesRate( ci );
        assertSame( ci, instance.getFalseNegativesRate() );
    }

    /**
     * Test of getTruePositivesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetTruePositivesRate()
    {
        System.out.println("getTruePositivesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTruePositivesRate();
        assertNotNull( ci );
    }

    /**
     * Test of setTruePositivesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetTruePositivesRate()
    {
        System.out.println("setTruePositivesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTruePositivesRate();
        assertNotNull( ci );
        
        instance.setTruePositivesRate( null );
        assertNull( instance.getTruePositivesRate() );
        
        instance.setTruePositivesRate( ci );
        assertSame( ci, instance.getTruePositivesRate() );
    }

    /**
     * Test of getTrueNegativesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetTrueNegativesRate()
    {
        System.out.println("getTrueNegativesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTrueNegativesRate();
        assertNotNull( ci );

    }

    /**
     * Test of setTrueNegativesRate method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetTrueNegativesRate()
    {
        System.out.println("setTrueNegativesRate");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTrueNegativesRate();
        assertNotNull( ci );
        
        instance.setTrueNegativesRate( null );
        assertNull( instance.getTrueNegativesRate() );
        
        instance.setTrueNegativesRate( ci );
        assertSame( ci, instance.getTrueNegativesRate() );
    }

    /**
     * Test of getConfidence method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetConfidence()
    {
        System.out.println("getConfidence");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        assertTrue( 0.0 <= instance.getConfidence() );
        assertTrue( instance.getConfidence() <= 1.0 );

        // 0.0 is legal, but the createInstance() should never make 0.0
        assertTrue( 0.0 != instance.getConfidence() );
    }

    /**
     * Test of setConfidence method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetConfidence()
    {
        System.out.println("setConfidence");
        
        DefaultBinaryConfusionMatrixConfidenceInterval instance = createInstance();
        
        double c2 = instance.getConfidence() * 0.5;
        instance.setConfidence( c2 );
        assertEquals( c2, instance.getConfidence() );
        
        instance.setConfidence( 0.0 );
        instance.setConfidence( 1.0 );
        
        try
        {
            instance.setConfidence( c2 - 1.0 );
            fail( "0.0 <= confidence <= 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            instance.setConfidence( c2 + 1.0 );
            fail( "0.0 <= confidence <= 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }        
        
    }

    /**
     * Test of compute method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testCompute()
    {
        System.out.println("compute");
        
        
        Collection<DefaultBinaryConfusionMatrix> data = new LinkedList<DefaultBinaryConfusionMatrix>();
        int N = 3;
        for( int i = 0; i < N; i++ )
        {
            data.add( DefaultBinaryConfusionMatrixTest.createRandomInstance(random) );
        }
        double confidence = random.nextDouble();
        DefaultBinaryConfusionMatrixConfidenceInterval instance =
            DefaultBinaryConfusionMatrixConfidenceInterval.compute( data, confidence );
        
        assertEquals( confidence, instance.getConfidence() );
        
        
        ArrayList<Double> fpp = new ArrayList<Double>( N );
        ArrayList<Double> fnp = new ArrayList<Double>( N );
        ArrayList<Double> tpp = new ArrayList<Double>( N );
        ArrayList<Double> tnp = new ArrayList<Double>( N );
        
        for( DefaultBinaryConfusionMatrix cm : data )
        {
            fpp.add( cm.getFalsePositivesRate() );
            fnp.add( cm.getFalseNegativesRate() );
            tnp.add( cm.getTrueNegativesRate() );
            tpp.add( cm.getTruePositivesRate() );
        }
        
        StudentTConfidence ttest = new StudentTConfidence();
        ConfidenceInterval fpci = ttest.computeConfidenceInterval( fpp, confidence );
        ConfidenceInterval fnci = ttest.computeConfidenceInterval( fnp, confidence );
        ConfidenceInterval tpci = ttest.computeConfidenceInterval( tpp, confidence );
        ConfidenceInterval tnci = ttest.computeConfidenceInterval( tnp, confidence );
        
        final double EPS = 1e-5;
        
        assertEquals( N, instance.getFalsePositivesRate().getNumSamples() );
        assertEquals( confidence, instance.getFalsePositivesRate().getConfidence() );
        assertEquals( fpci.getLowerBound(), instance.getFalsePositivesRate().getLowerBound(), EPS );
        assertEquals( fpci.getUpperBound(), instance.getFalsePositivesRate().getUpperBound(), EPS );
        assertEquals( fpci.getCentralValue(), instance.getFalsePositivesRate().getCentralValue(), EPS );
        
        assertEquals( N, instance.getFalseNegativesRate().getNumSamples() );
        assertEquals( confidence, instance.getFalseNegativesRate().getConfidence() );
        assertEquals( fnci.getLowerBound(), instance.getFalseNegativesRate().getLowerBound(), EPS );
        assertEquals( fnci.getUpperBound(), instance.getFalseNegativesRate().getUpperBound(), EPS );
        assertEquals( fnci.getCentralValue(), instance.getFalseNegativesRate().getCentralValue(), EPS );

        assertEquals( N, instance.getTruePositivesRate().getNumSamples() );
        assertEquals( confidence, instance.getTruePositivesRate().getConfidence() );
        assertEquals( tpci.getLowerBound(), instance.getTruePositivesRate().getLowerBound(), EPS );
        assertEquals( tpci.getUpperBound(), instance.getTruePositivesRate().getUpperBound(), EPS );
        assertEquals( tpci.getCentralValue(), instance.getTruePositivesRate().getCentralValue(), EPS );
        
        assertEquals( N, instance.getTrueNegativesRate().getNumSamples() );
        assertEquals( confidence, instance.getTrueNegativesRate().getConfidence() );
        assertEquals( tnci.getLowerBound(), instance.getTrueNegativesRate().getLowerBound(), EPS );
        assertEquals( tnci.getUpperBound(), instance.getTrueNegativesRate().getUpperBound(), EPS );
        assertEquals( tnci.getCentralValue(), instance.getTrueNegativesRate().getCentralValue(), EPS );
        
        data.clear();
        
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 0.0, 1.0, 0.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 1.0, 0.0, 0.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 0.0, 1.0, 0.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 0.0, 0.0, 1.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(1.0, 0.0, 0.0, 0.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 0.0, 0.0, 1.0));
        data.add(DefaultBinaryConfusionMatrixTest.createFPFNTPTN(0.0, 0.0, 0.0, 1.0));
        instance = DefaultBinaryConfusionMatrixConfidenceInterval.compute(data, confidence);
        assertEquals(3, instance.getTruePositivesRate().getNumSamples());
        assertEquals(3, instance.getFalseNegativesRate().getNumSamples());
        assertEquals(4, instance.getTrueNegativesRate().getNumSamples());
        assertEquals(4, instance.getFalsePositivesRate().getNumSamples());
    }
    
}
