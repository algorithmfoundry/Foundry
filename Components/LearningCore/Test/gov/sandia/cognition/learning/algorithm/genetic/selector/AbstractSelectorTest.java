/*
 * File:                AbstractSelectorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.selector;

import junit.framework.*;
import java.util.ArrayList;
import java.util.Collection;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;

/**
 *
 * @author jdbasil
 */
public class AbstractSelectorTest extends TestCase
{
    
    public AbstractSelectorTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractSelectorTest.class);
        
        return suite;
    }

    /**
     * Test of reproduce method, of class gov.sandia.isrc.learning.genetic.reproducer.selector.AbstractSelector.
     */
    @SuppressWarnings("unchecked")
    public void testReproduce()
    {
        System.out.println("reproduce");
        
        int N = 100;
        Collection<EvaluatedGenome<Double>> genomes =
            new ArrayList<EvaluatedGenome<Double>>( N );
        for( int i = 0; i < N; i++ )
        {
            genomes.add( new EvaluatedGenome<Double>( Math.random(), Math.random() ) );
        }
        double pct = Math.random();
        AbstractSelector<Double> instance = new TournamentSelector<Double>( pct, 5 );
        Collection<? super Double> result = instance.reproduce( genomes );
        assertNotNull( result );
        assertEquals( (int) Math.round( genomes.size() * pct ), result.size() );
        
        boolean npe = false;
        try
        {
            instance.reproduce( null );
            npe = false;
        }
        catch ( NullPointerException e)
        {
            npe = true;
        }

        if( npe )
        {
            System.out.println( "Good!  Threw null-pointer exception" );
        }
        else
        {
            fail( "Should have thrown null-pointer exception" );
        }
        
    }


    
}
