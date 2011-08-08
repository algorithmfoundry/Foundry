/*
 * File:                TournamentSelectorTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 8, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.selector;

import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import junit.framework.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     TournamentSelector
 *
 * @author Jonathan McClain
 * @since 1.0
 */
public class TournamentSelectorTest extends TestCase 
{
    /** The selector to use in the tests. */
    private TournamentSelector<Boolean> selector;
    
    /** The population to use in the tests. */
    private ArrayList<EvaluatedGenome<Boolean>> testPopulation; 
    
    /**
     * Creates a new instance of TournamentSelectorTest.
     *
     * @param testName The name of the test.
     */
    public TournamentSelectorTest(String testName)
    {
        super(testName);
    }

    /**
     * Called before each test is run. Prepares the TouramentSelector and test
     * populations.
     */
    protected void setUp() throws Exception 
    {
        this.setSelector(new TournamentSelector<Boolean>(1.0, 2));
        ArrayList<EvaluatedGenome<Boolean>> testPopulation = 
                new ArrayList<EvaluatedGenome<Boolean>>(2);
        testPopulation.add(new EvaluatedGenome<Boolean>(1.0, false));
        testPopulation.add(new EvaluatedGenome<Boolean>(0.0, true));
        this.setTestPopulation(testPopulation);
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
        TestSuite suite = new TestSuite(TournamentSelectorTest.class);
        
        return suite;
    }

    /**
     * Test of select method, of class 
     * gov.sandia.isrc.learning.reinforcement.TournamentSelector.
     */
    public void testSelect() 
    {
        System.out.println("select");
        
        int trueCount = 0;
        int falseCount = 0;
        for(int i = 0; i < 1000; i++)
        {
            Collection<EvaluatedGenome<Boolean>> result =
                this.getSelector().select(this.getTestPopulation());
            assertEquals(
                    "Selector did not return the correct sized population",
                    result.size(), 
                    this.getTestPopulation().size());
            for(EvaluatedGenome<Boolean> genome : result)
            {
                if(genome.getGenome())
                {
                    trueCount++;
                }
                else
                {
                    falseCount++;
                }
            }
        }
        assertTrue(
                "More falses were returned than trues", 
                trueCount > falseCount);
    }

    /**
     * Test of getPercent method, of class 
     * gov.sandia.isrc.learning.reinforcement.TournamentSelector.
     */
    public void testGetPercent() {
        System.out.println("getPercent");
        
        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        assertEquals( pct, selector.getPercent() );
        
    }

    /**
     * Test of getTournamentSize method, of class 
     * gov.sandia.isrc.learning.reinforcement.TournamentSelector.
     */
    public void testGetTournamentSize() {
        System.out.println("getTournamentSize");
        
        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        assertEquals( tournieSize, selector.getTournamentSize() );

    }

    /**
     * Test of setPercent method, of class 
     * gov.sandia.isrc.learning.reinforcement.TournamentSelector.
     */
    public void testSetPercent() {
        System.out.println("setPercent");
        
        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        assertEquals( pct, selector.getPercent() );

        double pct2 = pct / 0.5;
        selector.setPercent( pct2 );
        assertEquals( pct2, selector.getPercent() );
        
        double pct0 = 0.0;
        try
        {
            selector.setPercent( pct0 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Properly threw exception" );
        }
        
        double pctn = -1.0 * Math.random();
        try
        {
            selector.setPercent( pctn );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Properly threw exception" );
        }        
        
    }

    /**
     * Test of setTournamentSize method, of class 
     * gov.sandia.isrc.learning.reinforcement.TournamentSelector.
     */
    public void testSetTournamentSize() {
        System.out.println("setTournamentSize");
        
        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        assertEquals( tournieSize, selector.getTournamentSize() );
        int t2 = tournieSize + 1;
        selector.setTournamentSize( t2 );
        assertEquals( t2, selector.getTournamentSize() );
        
        int t3 = 0;
        try
        {
            selector.setTournamentSize( t3 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Properly threw exception" );
        }

        int t4 = -tournieSize;
        try
        {
            selector.setTournamentSize( t4 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Properly threw exception" );
        }
                
        
        
    }
    
    /**
     * Gets the selector to use in the test.
     *
     * @return The selector.
     */
    private TournamentSelector<Boolean> getSelector()
    {
        return this.selector;
    }
    
    /**
     * Gets the population to use in the test.
     *
     * @return The population to use in the test.
     */
    private ArrayList<EvaluatedGenome<Boolean>> getTestPopulation()
    {
        return this.testPopulation;
    }
    
    /**
     * Sets the selector to use in the test.
     *
     * @param selector The new selector.
     */
    private void setSelector(TournamentSelector<Boolean> selector)
    {
        this.selector = selector;
    }
    
    /**
     * Sets the population to use in the test.
     *
     * @param testPopulation The new population to use in the test.
     */
    private void setTestPopulation(ArrayList<EvaluatedGenome<Boolean>> testPopulation)
    {
        this.testPopulation = testPopulation;
    }

    /**
     * Test of getRandom method, of class gov.sandia.isrc.learning.genetic.reproducer.selector.TournamentSelector.
     */
    public void testGetRandom()
    {
        System.out.println("getRandom");
        
        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        assertNotNull( selector.getRandom() );

    }

    /**
     * Test of setRandom method, of class gov.sandia.isrc.learning.genetic.reproducer.selector.TournamentSelector.
     */
    public void testSetRandom()
    {
        System.out.println("setRandom");

        double pct = Math.random();
        int tournieSize = (int) (Math.random() * 1000) + 1;
        TournamentSelector<Double> selector = new TournamentSelector<Double>( pct, tournieSize );
        Random random = selector.getRandom();
        assertNotNull( random );
        selector.setRandom( null );
        assertNull( selector.getRandom() );
        selector.setRandom( random );
        assertSame( random, selector.getRandom() );
        
        
    }
}
