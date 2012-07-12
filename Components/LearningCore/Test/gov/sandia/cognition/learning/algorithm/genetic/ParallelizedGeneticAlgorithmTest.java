/*
 * File:                ParallelizedGeneticAlgorithmTest.java
 * Authors:             Christina Warrender
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 14, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class implements JUnit tests for the following classes
 *
 *     ParallelizedGeneticAlgorithm
 * 
 * @author  Christina Warrender
 * @since   2.1
 */
public class ParallelizedGeneticAlgorithmTest 
        extends GeneticAlgorithmTest 
{
    /**
     * Entry point for JUnit tests for class ParallelizedGeneticAlgorithm
     * @param testName name of this test
     */
    public ParallelizedGeneticAlgorithmTest
            (String testName)
    {
        super(testName);
    }
    
    
    /**
     * Creates an instance of ParallelizedGeneticAlgorithm.
     *
     * @return Returns a new instance.
     */
    @Override
    public ParallelizedGeneticAlgorithm<Vectorizable, Vectorizable> createInstance()
    {
        return new ParallelizedGeneticAlgorithm<Vectorizable, Vectorizable>(
             this.initialPopulation, this.multiReproducer, this.cost, null);    
    }
    
    
    /**
     * Test of getThreadPool method, of class ParallelizedGeneticAlgorithm.
     */
    @SuppressWarnings("unchecked")
    public void testGetThreadPool()
    {
        System.out.println( "getThreadPool" );
        ParallelizedGeneticAlgorithm<?,?> instance = this.createInstance();
        assertNull( instance.getThreadPool() );
        
        instance.initializeAlgorithm();
        assertNotNull( instance.getThreadPool() );
    }

    /**
     * Test of setThreadPool method, of class ParallelizedGeneticAlgorithm.
     */
    @SuppressWarnings("unchecked")
    public void testSetThreadPool()
    {
        System.out.println( "setThreadPool" );
        ParallelizedGeneticAlgorithm<?,?> instance = this.createInstance();
        assertNull( instance.getThreadPool() );
        
        instance.initializeAlgorithm();
        
        ThreadPoolExecutor pool = instance.getThreadPool();
        assertNotNull( pool );
        
        instance.setThreadPool( null );
        assertNull( instance.getThreadPool() );
        instance.setThreadPool( pool );
        assertSame( pool, instance.getThreadPool() );        
    }   
  
}
