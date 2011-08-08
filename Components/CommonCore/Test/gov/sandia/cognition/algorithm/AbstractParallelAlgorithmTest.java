/*
 * File:                AbstractParallelAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 14, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.algorithm;

import java.util.concurrent.ThreadPoolExecutor;
import junit.framework.TestCase;

/**
 * JUnit tests for class AbstractParallelAlgorithmTest
 * @author Kevin R. Dixon
 */
public class AbstractParallelAlgorithmTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class AbstractParallelAlgorithmTest
     * @param testName name of this test
     */
    public AbstractParallelAlgorithmTest(
        String testName)
    {
        super(testName);
    }
    
    public static final int NUM_THREADS = 1;
    
    AbstractParallelAlgorithm createInstance()
    {
        return new AbstractParallelAlgorithm( ParallelUtil.createThreadPool(NUM_THREADS) ) {};
    }

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        AbstractParallelAlgorithm instance = new AbstractParallelAlgorithm() {};
        assertNotNull( instance.getThreadPool() );

        instance = new AbstractParallelAlgorithm( ParallelUtil.createThreadPool() ) {};
        assertNotNull( instance.getThreadPool() );

    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        AbstractParallelAlgorithm instance = new AbstractParallelAlgorithm() {};
        AbstractParallelAlgorithm clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getThreadPool(), clone.getThreadPool() );

        instance = new AbstractParallelAlgorithm( ParallelUtil.createThreadPool() ) {};
        clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getThreadPool(), clone.getThreadPool() );
        assertEquals( instance.getNumThreads(), clone.getNumThreads() );

    }

    /**
     * Test of getNumThreads method, of class AbstractParallelAlgorithm.
     */
    public void testGetNumThreads()
    {
        System.out.println( "getNumThreads" );
        AbstractParallelAlgorithm instance = this.createInstance();
        assertEquals( NUM_THREADS, instance.getNumThreads() );
    }

    /**
     * Test of getThreadPool method, of class AbstractParallelAlgorithm.
     */
    public void testGetThreadPool()
    {
        System.out.println( "getThreadPool" );
        AbstractParallelAlgorithm instance = this.createInstance();
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of setThreadPool method, of class AbstractParallelAlgorithm.
     */
    public void testSetThreadPool()
    {
        System.out.println( "setThreadPool" );
        AbstractParallelAlgorithm instance = this.createInstance();
        ThreadPoolExecutor t1 = instance.getThreadPool();
        
        // The class should always automatically create the appropriate thread pool
        instance.setThreadPool( null );
        assertNotNull( instance.getThreadPool() );
        assertNotSame( t1, instance.getThreadPool() );
        
        ThreadPoolExecutor threadPool = ParallelUtil.createThreadPool( NUM_THREADS );
        instance.setThreadPool( threadPool );
        assertSame( threadPool, instance.getThreadPool() );
        
    }

}
