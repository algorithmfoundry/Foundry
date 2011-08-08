/*
 * File:                MutationReproducerTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 9, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.learning.algorithm.annealing.DummyPerturber;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.learning.algorithm.annealing.Perturber;
import gov.sandia.cognition.learning.algorithm.genetic.selector.DummySelector;
import gov.sandia.cognition.learning.algorithm.genetic.selector.Selector;
import java.util.ArrayList;
import junit.framework.*;
import java.util.Collection;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MutationReproducerTest
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class MutationReproducerTest extends TestCase 
{
    
    /**
     * Creates a new instance of MutationReproducerTest.
     */
    public MutationReproducerTest(String testName)
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
        TestSuite suite = new TestSuite(MutationReproducerTest.class);
        
        return suite;
    }

    /**
     * Test of reproduce method, of class 
     * gov.sandia.isrc.learning.reinforcement.MutationReproducer.
     */
    public void testReproduce() {
        System.out.println("reproduce");
        
        DummyPerturber dummyPerturber = new DummyPerturber();
        DummySelector dummySelector = new DummySelector();
        MutationReproducer<Integer> reproducer = 
                new MutationReproducer<Integer>(
                    dummyPerturber,
                    dummySelector);
        ArrayList<EvaluatedGenome<Integer>> population = 
                new ArrayList<EvaluatedGenome<Integer>>(10);
        for(int i = 0; i < 10; i++)
        {
            EvaluatedGenome<Integer> genome = 
                    new EvaluatedGenome<Integer>(0.0, 0);
            population.add(i, genome);
        }
        Collection<Integer> newPopulation = reproducer.reproduce(population);
        assertEquals(
                "Perturb was called an incorrect number of times", 
                10, 
                dummyPerturber.getPerturbCount());
        assertEquals(
                "Select was called an incorrect number of times", 
                1, 
                dummySelector.getSelectCount());
        assertEquals(
                "Returned population was incorrect size", 
                population.size(), 
                newPopulation.size());
    }

    /**
     * Test of getPerturber method, of class 
     * gov.sandia.isrc.learning.reinforcement.MutationReproducer.
     */
    public void testGetPerturber() {
        System.out.println("getPerturber");
        
        MutationReproducer<Integer> reproducer = 
                new MutationReproducer<Integer>(
                    null,
                    null);
        DummyPerturber expected = new DummyPerturber();
        reproducer.setPerturber(expected);
        Perturber<Integer> actual = reproducer.getPerturber();
        assertEquals(
                "getPerturber did not return the expected Perturber",
                expected,
                actual);
    }

    /**
     * Test of getSelector method, of class 
     * gov.sandia.isrc.learning.reinforcement.MutationReproducer.
     */
    public void testGetSelector() {
        System.out.println("getSelector");
        
        MutationReproducer<Integer> reproducer = 
                new MutationReproducer<Integer>(
                    null,
                    null);
        DummySelector expected = new DummySelector();
        reproducer.setSelector(expected);
        Selector<Integer> actual = reproducer.getSelector();
        assertEquals(
                "getSelector did not return the expected Selector",
                expected,
                actual);
    }

    /**
     * Test of setPerturber method, of class 
     * gov.sandia.isrc.learning.reinforcement.MutationReproducer.
     */
    public void testSetPerturber() {
        System.out.println("setPerturber");
        
        MutationReproducer<Integer> reproducer = 
                new MutationReproducer<Integer>(
                    null,
                    null);
        DummyPerturber expected = new DummyPerturber();
        reproducer.setPerturber(expected);
        Perturber<Integer> actual = reproducer.getPerturber();
        assertEquals(
                "setPerturber did not set the expected Perturber",
                expected,
                actual);
    }

    /**
     * Test of setSelector method, of class 
     * gov.sandia.isrc.learning.reinforcement.MutationReproducer.
     */
    public void testSetSelector() {
        System.out.println("setSelector");
        
        MutationReproducer<Integer> reproducer = 
                new MutationReproducer<Integer>(
                    null,
                    null);
        DummySelector expected = new DummySelector();
        reproducer.setSelector(expected);
        Selector<Integer> actual = reproducer.getSelector();
        assertEquals(
                "setSelector did not set the expected Selector",
                expected,
                actual);
    }
}
