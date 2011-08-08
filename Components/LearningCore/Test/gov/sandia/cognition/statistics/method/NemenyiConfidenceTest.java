/*
 * File:                NemenyiConfidenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 9, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class NemenyiConfidenceTest.
 * @author krdixon
 */
public class NemenyiConfidenceTest
    extends MultipleHypothesisComparisonTestHarness
{

    /**
     * Default Constructor
     */
    public NemenyiConfidenceTest()
    {
    }

    /**
     * Test of clone method, of class FriedmanConfidence.
     */
    @Test
    @Override
    public void testConstructors()
    {
        System.out.println("Constructors");
        NemenyiConfidence instance = new NemenyiConfidence();
        assertNotNull( instance );
    }

    @Override
    public NemenyiConfidence createInstance()
    {
        return new NemenyiConfidence();
    }
 
    @Override
    @Test
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
        Collection<? extends Collection<Double>> experiment = createData1();

        NemenyiConfidence instance = this.createInstance();
        NemenyiConfidence.Statistic result = instance.evaluateNullHypotheses( experiment );
        System.out.println( "Result: " + result );

        assertEquals( 3, result.getTreatmentCount() );
        assertEquals( 10, result.getSubjectCount() );
        assertEquals( 2.65, result.getTreatmentMeans().get(0), TOLERANCE );
        assertEquals( 2.1, result.getTreatmentMeans().get(1), TOLERANCE );
        assertEquals( 1.25, result.getTreatmentMeans().get(2), TOLERANCE );

        for( int i = 0; i < result.getTreatmentCount(); i++ )
        {
            for( int j = 0; j < result.getTreatmentCount(); j++ )
            {
                System.out.println( "(" + i + "," + j + "): " +
                    "z = " + result.getTestStatistic(i, j) +
                    ", p = " + result.getNullHypothesisProbability(i, j) +
                    ", NH = " + result.acceptNullHypothesis(i, j) );
            }
        }

        NemenyiConfidence.Statistic clone = result.clone();
        assertNotSame( result.getTreatmentMeans(), clone.getTreatmentMeans() );
        assertEquals( result.toString(), clone.toString() );

    }

}
