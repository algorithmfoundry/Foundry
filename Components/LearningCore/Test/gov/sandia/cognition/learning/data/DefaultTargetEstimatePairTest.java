/*
 * File:                TargetEstimatePairTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */
package gov.sandia.cognition.learning.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class TargetEstimatePairTest
 * @author Kevin R. Dixon
 */
public class DefaultTargetEstimatePairTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class TargetEstimatePairTest
     * @param testName name of this test
     */
    public DefaultTargetEstimatePairTest(
        String testName)
    {
        super(testName);
    }

    private Random random = new Random( 1 );
    
    public DefaultTargetEstimatePair<Double,Integer> createInstance()
    {
        return new DefaultTargetEstimatePair<Double, Integer>(
            random.nextDouble(), random.nextInt() );
    }
    
    /**
     * Test of clone method, of class TargetEstimatePair.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        DefaultTargetEstimatePair<Double,Integer> instance = this.createInstance();
        DefaultTargetEstimatePair<Double,Integer> clone = instance.clone();
        assertNotSame( clone, instance );
        assertNotNull( clone.getTarget() );
        assertNotNull( clone.getEstimate() );
        assertSame( instance.getTarget(), clone.getTarget() );
        assertSame( instance.getEstimate(), clone.getEstimate() );
    }

    /**
     * Test of getFirst method, of class TargetEstimatePair.
     */
    public void testGetFirst()
    {
        System.out.println( "getFirst" );
        DefaultTargetEstimatePair<Double,Integer> instance =
            new DefaultTargetEstimatePair<Double,Integer>();
        assertNull( instance.getFirst() );
        
        instance = this.createInstance();
        assertNotNull( instance.getFirst() );
    }

    /**
     * Test of getSecond method, of class TargetEstimatePair.
     */
    public void testGetSecond()
    {
        System.out.println( "getSecond" );
        DefaultTargetEstimatePair<Double,Integer> instance =
            new DefaultTargetEstimatePair<Double,Integer>();
        assertNull( instance.getSecond() );
        
        instance = this.createInstance();
        assertNotNull( instance.getSecond() );
    }

    /**
     * Test of getTarget method, of class TargetEstimatePair.
     */
    public void testGetTarget()
    {
        System.out.println( "getTarget" );
        DefaultTargetEstimatePair<Double,Integer> instance = this.createInstance();

        Double x = instance.getTarget();
        assertNotNull( x );

    }

    /**
     * Test of setTarget method, of class TargetEstimatePair.
     */
    public void testSetTarget()
    {
        System.out.println( "setTarget" );
        DefaultTargetEstimatePair<Double,Integer> instance = this.createInstance();

        Double x = instance.getTarget();
        assertNotNull( x );
        instance.setTarget(null);
        assertNull( instance.getTarget() );
        instance.setTarget(x);
        assertSame( x, instance.getTarget() );
    }

    /**
     * Test of getEstimate method, of class TargetEstimatePair.
     */
    public void testGetEstimate()
    {
        System.out.println( "getEstimate" );
        DefaultTargetEstimatePair<Double,Integer> instance = this.createInstance();

        Integer x = instance.getEstimate();
        assertNotNull( x );

    }

    /**
     * Test of setEstimate method, of class TargetEstimatePair.
     */
    public void testSetEstimate()
    {
        System.out.println( "setEstimate" );
        DefaultTargetEstimatePair<Double,Integer> instance = this.createInstance();

        Integer x = instance.getEstimate();
        assertNotNull( x );
        instance.setEstimate(null);
        assertNull( instance.getEstimate() );
        instance.setEstimate(x);
        assertSame( x, instance.getEstimate() );
    }

    /**
     * Test of mergeCollections method, of class TargetEstimatePair.
     */
    public void testMergeCollections()
    {
        System.out.println( "mergeCollections" );
        int num = 100;
        ArrayList<Double> targets = new ArrayList<Double>(num);
        ArrayList<Integer> estimates = new ArrayList<Integer>(num);
        for( int i = 0; i< num; i++ )
        {
            targets.add( random.nextDouble() );
            estimates.add( random.nextInt() );
        }
        
        ArrayList<DefaultTargetEstimatePair<Double, Integer>> result =
            DefaultTargetEstimatePair.mergeCollections(targets, estimates);
        assertEquals( num, result.size() );
        
        for( int i = 0; i < num; i++ )
        {
            assertSame( targets.get(i), result.get(i).getTarget() );
            assertSame( estimates.get(i), result.get(i).getEstimate() );
        }
        
        
        try
        {
            DefaultTargetEstimatePair.mergeCollections(targets, new LinkedList<Integer>() );
            fail( "Targets and Estimates must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
