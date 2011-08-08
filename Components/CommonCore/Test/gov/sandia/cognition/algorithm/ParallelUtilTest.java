/*
 * File:                ParallelUtilTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 10, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import junit.framework.TestCase;

/**
 * JUnit tests for class ParallelUtilTest
 * @author Kevin R. Dixon
 */
public class ParallelUtilTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class ParallelUtilTest
     * @param testName name of this test
     */
    public ParallelUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of createThreadPool method, of class ParallelUtil.
     */
    public void testCreateThreadPool_0args()
    {
        System.out.println( "createThreadPool" );
        ThreadPoolExecutor result = ParallelUtil.createThreadPool();
        ThreadPoolExecutor expected = ParallelUtil.createThreadPool(
            ParallelUtil.OPTIMAL_THREADS );
        assertEquals( expected.getMaximumPoolSize(), result.getMaximumPoolSize() );
    }

    /**
     * Test of createThreadPool method, of class ParallelUtil.
     */
    public void testCreateThreadPool_int()
    {
        System.out.println( "createThreadPool" );
        int numRequestedThreads = 0;
        ThreadPoolExecutor result = ParallelUtil.createThreadPool( ParallelUtil.OPTIMAL_THREADS );
        ThreadPoolExecutor expected = ParallelUtil.createThreadPool( -1 );
        assertEquals( expected.getMaximumPoolSize(), result.getMaximumPoolSize() );
        
        result = ParallelUtil.createThreadPool( -1 );
        assertTrue( result.getMaximumPoolSize() > 0 );
        
        numRequestedThreads = 10;
        result = ParallelUtil.createThreadPool( numRequestedThreads );
        assertEquals( numRequestedThreads, result.getMaximumPoolSize() );
    }

    public static Collection<Callable<Double>> createTasks(
        int num )
    {
        
        ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>( num );
        for( int i = 0; i < num; i++ )
        {
            tasks.add( new DummyTask() );
        }
        
        return tasks;
        
    }
    
    
    public static class DummyTask
        implements Callable<Double>
    {

        public static final int NUM_ITERATIONS = 100000;
        
        private Random localRandom = new Random( 1 );
        
        public Double call()
        {
            double minValue = Double.POSITIVE_INFINITY;
            double maxValue = Double.NEGATIVE_INFINITY;
            for( int i = 0; i < NUM_ITERATIONS; i++ )
            {
                double value = localRandom.nextGaussian();
                if( minValue > value )
                {
                    minValue = value;
                }
                if( maxValue < value )
                {
                    maxValue = value;
                }
            }
            return minValue + maxValue;
        }
    }
    
    
    private class PA
        extends AbstractCloneableSerializable
        implements ParallelAlgorithm
    {

        transient ThreadPoolExecutor threadPool;
        
        public ThreadPoolExecutor getThreadPool()
        {
            return threadPool;
        }

        public void setThreadPool( ThreadPoolExecutor threadPool )
        {
            this.threadPool = threadPool;
        }

        public int getNumThreads()
        {
            return ParallelUtil.getNumThreads( this );
        }

    }
    
    /**
     * Test of getNumThreads method, of class ParallelUtil.
     */
    public void testGetNumThreads()
    {
        System.out.println( "getNumThreads" );
        
        int numThreads = 10;
        PA pa = new PA();
        assertEquals( 0, ParallelUtil.getNumThreads( pa ) );
        
        pa.threadPool = ParallelUtil.createThreadPool( 10 );        
        
        int result = ParallelUtil.getNumThreads( pa );
        assertEquals( numThreads, result );
    }

    /**
     * Test of getNumThreads method, of class ParallelUtil.
     */
    public void testGetNumThreads_ThreadPoolExecutor()
    {
        System.out.println( "getNumThreads" );
        int numThreads = 10;
        PA pa = new PA();
        assertEquals( 0, ParallelUtil.getNumThreads( pa.getThreadPool() ) );
        
        pa.threadPool = ParallelUtil.createThreadPool( 10 );        
        
        int result = ParallelUtil.getNumThreads( pa.getThreadPool() );
        assertEquals( numThreads, result );
    }

    /**
     * Test of executeInParallel method, of class ParallelUtil.
     */
    public void testExecuteInParallel_Collection() throws Exception
    {
        System.out.println( "executeInParallel" );
        Collection<Callable<Double>> tasks = createTasks( 10 );
        Collection<Double> result = ParallelUtil.executeInParallel( tasks );
        assertEquals( result.size(), tasks.size() );
    }

    /**
     * Test of executeInParallel method, of class ParallelUtil.
     */
    public void testExecuteInParallel_Collection_ThreadPoolExecutor() throws Exception
    {
        System.out.println( "executeInParallel" );
        Collection<Callable<Double>> tasks = createTasks( 10 );
        Collection<Double> result = ParallelUtil.executeInParallel( tasks, ParallelUtil.createThreadPool( 1 ) );
        assertEquals( result.size(), tasks.size() );
    }

    /**
     * Test of executeInSequence method, of class ParallelUtil.
     */
    public void testExecuteInSequence() throws Exception
    {
        System.out.println( "executeInSequence" );
        Collection<Callable<Double>> tasks = createTasks( 10 );
        Collection<Double> result = ParallelUtil.executeInSequence( tasks );
        assertEquals( result.size(), tasks.size() );
    }

    /**
     * Test of compareTimes method, of class ParallelUtil.
     */
    public void testCompareTimes_Collection()
    {
        System.out.println( "compareTimes" );
        Collection<Callable<Double>> tasks = createTasks( 10 );
        NamedValue<Double> value = ParallelUtil.compareTimes( tasks );
        System.out.println( "Value: " + value + ", " + value.getValue() );
    }

    /**
     * Test of compareTimes method, of class ParallelUtil.
     */
    public void testCompareTimes_Collection_ThreadPoolExecutor()
    {
        System.out.println( "compareTimes" );
        Collection<Callable<Double>> tasks = createTasks( 10 );
        NamedValue<Double> value = ParallelUtil.compareTimes( tasks, ParallelUtil.createThreadPool( 1 ) );
        System.out.println( "Value: " + value + ", " + value.getValue() );
    }

    public void testGetDefaultNumThreads()
    {
        System.out.println( "getDefaultNumThreads" );
        
        assertEquals( ParallelUtil.OPTIMAL_THREADS, ParallelUtil.getDefaultNumThreads() );
        
    }
    
    public void testSetDefaultNumThreads()
    {
        System.out.println( "setDefaultNumThreads" );
        
        int num = ParallelUtil.getDefaultNumThreads();
        assertEquals( ParallelUtil.OPTIMAL_THREADS, num );
        num = 10;
        ParallelUtil.setDefaultNumThreads( num );
        assertEquals( num, ParallelUtil.getDefaultNumThreads() );
        
        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool();
        assertEquals( num, ParallelUtil.getNumThreads( threadPool ) );
        
        
    }
    
    
}
