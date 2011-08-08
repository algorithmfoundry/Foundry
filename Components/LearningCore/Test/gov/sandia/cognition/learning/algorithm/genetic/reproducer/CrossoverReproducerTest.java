/*
 * File:                CrossoverReproducerTest.java
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

import gov.sandia.cognition.learning.algorithm.genetic.reproducer.DummyCrossoverFunction;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.learning.algorithm.genetic.selector.DummySelector;
import junit.framework.*;
import java.util.ArrayList;
import java.util.Collection;
import gov.sandia.cognition.learning.algorithm.genetic.selector.Selector;
import java.util.Iterator;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CrossoverReproducerTest
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class CrossoverReproducerTest extends TestCase
{
    

    private CrossoverReproducer<Integer> reproducer;

    
    /**
     * Creates a new instance of CrossoverReproducerTest.
     */
    public CrossoverReproducerTest(String testName)
    {
        super(testName);
    }

    /**
     * Called before each test is run.
     */
    protected void setUp() throws Exception 
    {
        DummySelector dummySelector = new DummySelector();
        DummyCrossoverFunction dummyCrossoverFunction = 
                new DummyCrossoverFunction();
        this.reproducer = new CrossoverReproducer<Integer>(
                    dummySelector, dummyCrossoverFunction);
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
        TestSuite suite = new TestSuite(CrossoverReproducerTest.class);
        
        return suite;
    }

    /**
     * Test of reproduce method, of class 
     * gov.sandia.isrc.learning.reinforcement.CrossoverReproducer.
     */
    public void testReproduce() {
        System.out.println("reproduce");
        
        DummySelector dummySelector = new DummySelector();
        DummyCrossoverFunction dummyCrossoverFunction = 
                new DummyCrossoverFunction();
        CrossoverReproducer<Integer> reproducer = 
                new CrossoverReproducer<Integer>(
                    dummySelector, 
                    dummyCrossoverFunction);
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
                "Select was called an incorrect number of times", 
                2, 
                dummySelector.getSelectCount());
        assertEquals(
                "Crossover was called an incorrect number of times", 
                10, 
                dummyCrossoverFunction.getCrossoverCount());
        assertEquals(
                "Returned population was incorrect size", 
                population.size(), 
                newPopulation.size());        
    }

    /**
     * Test of getSelector method, of class gov.sandia.isrc.learning.genetic.reproducer.CrossoverReproducer.
     */
    public void testGetSelector()
    {
        System.out.println("getSelector");
        
        Selector<Integer> selector = this.reproducer.getSelector();
        assertNotNull( selector );
    }

    /**
     * Test of getCrossoverFunction method, of class gov.sandia.isrc.learning.genetic.reproducer.CrossoverReproducer.
     */
    public void testGetCrossoverFunction()
    {
        System.out.println("getCrossoverFunction");
        
        CrossoverFunction<Integer> function = this.reproducer.getCrossoverFunction();
        assertNotNull( function );
        
    }

    /**
     * Test of setSelector method, of class gov.sandia.isrc.learning.genetic.reproducer.CrossoverReproducer.
     */
    public void testSetSelector()
    {
        System.out.println("setSelector");
        
        Selector<Integer> selector = this.reproducer.getSelector();
        assertNotNull( selector );
        this.reproducer.setSelector( null );
        assertNull( this.reproducer.getSelector() );
        this.reproducer.setSelector( selector );
        assertSame( selector, this.reproducer.getSelector() );
    }

    /**
     * Test of setCrossoverFunction method, of class gov.sandia.isrc.learning.genetic.reproducer.CrossoverReproducer.
     */
    public void testSetCrossoverFunction()
    {
        System.out.println("setCrossoverFunction");
        
        CrossoverFunction<Integer> function = this.reproducer.getCrossoverFunction();
        assertNotNull( function );
        this.reproducer.setCrossoverFunction( null );
        assertNull( this.reproducer.getCrossoverFunction() );
        this.reproducer.setCrossoverFunction( function );
        assertSame( function, this.reproducer.getCrossoverFunction() );
    }
    
}
