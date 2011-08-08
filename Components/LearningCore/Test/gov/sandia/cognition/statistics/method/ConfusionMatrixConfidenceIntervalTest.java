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

package gov.sandia.cognition.statistics.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class ConfusionMatrixConfidenceIntervalTest
    extends TestCase
{
    
    public static Random random = new Random( 1 );
    
    public ConfusionMatrixConfidenceIntervalTest(String testName)
    {
        super(testName);
    }
    
    public static ConfusionMatrixConfidenceInterval createInstance()
    {
        
        LinkedList<ConfusionMatrix> data = new LinkedList<ConfusionMatrix>();
        
        for( int i = 0; i < 10; i++ )
        {
            data.add( ConfusionMatrixTest.createInstance() );
        }
        
        return ConfusionMatrixConfidenceInterval.compute( data, random.nextDouble() );
        
    }
    
    /**
     * Test of getFalsePositivesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetFalsePositivesPct()
    {
        System.out.println("getFalsePositivesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        assertNotNull( instance.getFalsePositivesPct() );
    }

    /**
     * Test of setFalsePositivesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetFalsePositivesPct()
    {
        System.out.println("setFalsePositivesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalsePositivesPct();
        assertNotNull( ci );
        
        instance.setFalsePositivesPct( null );
        assertNull( instance.getFalsePositivesPct() );
        
        instance.setFalsePositivesPct( ci );
        assertSame( ci, instance.getFalsePositivesPct() );
    }

    /**
     * Test of getFalseNegativesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetFalseNegativesPct()
    {
        System.out.println("getFalseNegativesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalseNegativesPct();
        assertNotNull( ci );
    }

    /**
     * Test of setFalseNegativesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetFalseNegativesPct()
    {
        System.out.println("setFalseNegativesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getFalseNegativesPct();
        assertNotNull( ci );
        
        instance.setFalseNegativesPct( null );
        assertNull( instance.getFalseNegativesPct() );
        
        instance.setFalseNegativesPct( ci );
        assertSame( ci, instance.getFalseNegativesPct() );
    }

    /**
     * Test of getTruePositivesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetTruePositivesPct()
    {
        System.out.println("getTruePositivesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTruePositivesPct();
        assertNotNull( ci );
    }

    /**
     * Test of setTruePositivesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetTruePositivesPct()
    {
        System.out.println("setTruePositivesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTruePositivesPct();
        assertNotNull( ci );
        
        instance.setTruePositivesPct( null );
        assertNull( instance.getTruePositivesPct() );
        
        instance.setTruePositivesPct( ci );
        assertSame( ci, instance.getTruePositivesPct() );
    }

    /**
     * Test of getTrueNegativesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetTrueNegativesPct()
    {
        System.out.println("getTrueNegativesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTrueNegativesPct();
        assertNotNull( ci );

    }

    /**
     * Test of setTrueNegativesPct method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testSetTrueNegativesPct()
    {
        System.out.println("setTrueNegativesPct");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        ConfidenceInterval ci = instance.getTrueNegativesPct();
        assertNotNull( ci );
        
        instance.setTrueNegativesPct( null );
        assertNull( instance.getTrueNegativesPct() );
        
        instance.setTrueNegativesPct( ci );
        assertSame( ci, instance.getTrueNegativesPct() );
    }

    /**
     * Test of getConfidence method, of class gov.sandia.cognition.learning.util.statistics.ConfusionMatrixConfidenceInterval.
     */
    public void testGetConfidence()
    {
        System.out.println("getConfidence");
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
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
        
        ConfusionMatrixConfidenceInterval instance = createInstance();
        
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
        
        
        Collection<ConfusionMatrix> data = new LinkedList<ConfusionMatrix>();
        int N = 3;
        for( int i = 0; i < N; i++ )
        {
            data.add( ConfusionMatrixTest.createInstance() );
        }
        double confidence = random.nextDouble();
        ConfusionMatrixConfidenceInterval instance =
            ConfusionMatrixConfidenceInterval.compute( data, confidence );
        
        assertEquals( confidence, instance.getConfidence() );
        
        
        ArrayList<Double> fpp = new ArrayList<Double>( N );
        ArrayList<Double> fnp = new ArrayList<Double>( N );
        ArrayList<Double> tpp = new ArrayList<Double>( N );
        ArrayList<Double> tnp = new ArrayList<Double>( N );
        
        for( ConfusionMatrix cm : data )
        {
            fpp.add( cm.getFalsePositivesPct() );
            fnp.add( cm.getFalseNegativesPct() );
            tnp.add( cm.getTrueNegativesPct() );
            tpp.add( cm.getTruePositivesPct() );
        }
        
        StudentTConfidence ttest = new StudentTConfidence();
        ConfidenceInterval fpci = ttest.computeConfidenceInterval( fpp, confidence );
        ConfidenceInterval fnci = ttest.computeConfidenceInterval( fnp, confidence );
        ConfidenceInterval tpci = ttest.computeConfidenceInterval( tpp, confidence );
        ConfidenceInterval tnci = ttest.computeConfidenceInterval( tnp, confidence );
        
        final double EPS = 1e-5;
        
        assertEquals( N, instance.getFalsePositivesPct().getNumSamples() );
        assertEquals( confidence, instance.getFalsePositivesPct().getConfidence() );
        assertEquals( fpci.getLowerBound(), instance.getFalsePositivesPct().getLowerBound(), EPS );
        assertEquals( fpci.getUpperBound(), instance.getFalsePositivesPct().getUpperBound(), EPS );
        assertEquals( fpci.getCentralValue(), instance.getFalsePositivesPct().getCentralValue(), EPS );
        
        assertEquals( N, instance.getFalseNegativesPct().getNumSamples() );
        assertEquals( confidence, instance.getFalseNegativesPct().getConfidence() );
        assertEquals( fnci.getLowerBound(), instance.getFalseNegativesPct().getLowerBound(), EPS );
        assertEquals( fnci.getUpperBound(), instance.getFalseNegativesPct().getUpperBound(), EPS );
        assertEquals( fnci.getCentralValue(), instance.getFalseNegativesPct().getCentralValue(), EPS );

        assertEquals( N, instance.getTruePositivesPct().getNumSamples() );
        assertEquals( confidence, instance.getTruePositivesPct().getConfidence() );
        assertEquals( tpci.getLowerBound(), instance.getTruePositivesPct().getLowerBound(), EPS );
        assertEquals( tpci.getUpperBound(), instance.getTruePositivesPct().getUpperBound(), EPS );
        assertEquals( tpci.getCentralValue(), instance.getTruePositivesPct().getCentralValue(), EPS );
        
        assertEquals( N, instance.getTrueNegativesPct().getNumSamples() );
        assertEquals( confidence, instance.getTrueNegativesPct().getConfidence() );
        assertEquals( tnci.getLowerBound(), instance.getTrueNegativesPct().getLowerBound(), EPS );
        assertEquals( tnci.getUpperBound(), instance.getTrueNegativesPct().getUpperBound(), EPS );
        assertEquals( tnci.getCentralValue(), instance.getTrueNegativesPct().getCentralValue(), EPS );
        
        data.clear();
        
        data.add(new ConfusionMatrix(0.0, 0.0, 1.0, 0.0));
        data.add(new ConfusionMatrix(0.0, 1.0, 0.0, 0.0));
        data.add(new ConfusionMatrix(0.0, 0.0, 1.0, 0.0));
        data.add(new ConfusionMatrix(0.0, 0.0, 0.0, 1.0));
        data.add(new ConfusionMatrix(1.0, 0.0, 0.0, 0.0));
        data.add(new ConfusionMatrix(0.0, 0.0, 0.0, 1.0));
        data.add(new ConfusionMatrix(0.0, 0.0, 0.0, 1.0));
        instance = ConfusionMatrixConfidenceInterval.compute(data, confidence);
        assertEquals(3, instance.getTruePositivesPct().getNumSamples());
        assertEquals(3, instance.getFalseNegativesPct().getNumSamples());
        assertEquals(4, instance.getTrueNegativesPct().getNumSamples());
        assertEquals(4, instance.getFalsePositivesPct().getNumSamples());
    }
    
}
