/*
 * File:                MultiReproducerTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.learning.algorithm.genetic.DummyReproducer;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import junit.framework.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MultiReproducerTest
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class MultiReproducerTest extends TestCase 
{
    
    /**
     * Creates a new instance of MultiReproducerTest.
     */
    public MultiReproducerTest(String testName)
    {
        super(testName);
    }

    /**
     * Called before each test is run.
     */
    protected void setUp() throws Exception 
    {
    }

    /**
     * Called after each test is run.
     */
    protected void tearDown() throws Exception 
    {
    }

    /**
     * Returns the test.
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite(MultiReproducerTest.class);
        
        return suite;
    }

    /**
     * Test of reproduce method, of class 
     * gov.sandia.isrc.learning.reinforcement.MultiReproducer.
     */
    public void testReproduce() 
    {
        System.out.println("reproduce");
        
        ArrayList<Reproducer<Integer>> reproducers = 
                new ArrayList<Reproducer<Integer>>(2);
        DummyReproducer reproducer1 = new DummyReproducer();
        DummyReproducer reproducer2 = new DummyReproducer();
        reproducers.add(reproducer1);
        reproducers.add(reproducer2);
        MultiReproducer<Integer> multiReproducer = 
                new MultiReproducer<Integer>(reproducers);
        ArrayList<EvaluatedGenome<Integer>> population = 
                new ArrayList<EvaluatedGenome<Integer>>(10);
        for(int i = 0; i < 10; i++)
        {
            EvaluatedGenome<Integer> genome = 
                    new EvaluatedGenome<Integer>(0.0, 0);
            population.add(i, genome);
        }
        Collection<Integer> newPopulation = 
                multiReproducer.reproduce(population);
        assertEquals(
                "Reproduce was called an incorrect number of times", 
                1, 
                reproducer1.getReproduceCount());
        assertEquals(
                "Reproduce was called an incorrect number of times",
                1, 
                reproducer2.getReproduceCount());
        assertEquals(
                "New population was incorrect size", 
                population.size() * 2, newPopulation.size());
    }

    /**
     * Test of getReproducers method, of class 
     * gov.sandia.isrc.learning.reinforcement.MultiReproducer.
     */
    public void testGetReproducers() 
    {
        System.out.println("getReproducers");
        
        MultiReproducer<Integer> multiReproducer = 
                new MultiReproducer<Integer>(null);
        ArrayList<Reproducer<Integer>> expected = 
                new ArrayList<Reproducer<Integer>>();
        multiReproducer.setReproducers(expected);
        Collection<Reproducer<Integer>> actual = 
                multiReproducer.getReproducers();
        assertEquals(
                "getReproducers did not return the expected Collection", 
                expected, 
                actual);
    }

    /**
     * Test of setReproducers method, of class 
     * gov.sandia.isrc.learning.reinforcement.MultiReproducer.
     */
    public void testSetReproducers() 
    {
        System.out.println("setReproducers");
        
        MultiReproducer<Integer> multiReproducer = 
                new MultiReproducer<Integer>(null);
        ArrayList<Reproducer<Integer>> expected = 
                new ArrayList<Reproducer<Integer>>();
        multiReproducer.setReproducers(expected);
        Collection<Reproducer<Integer>> actual = 
                multiReproducer.getReproducers();
        assertEquals(
                "setReproducers did not set the expected Collection", 
                expected, 
                actual);
    }
    
}
