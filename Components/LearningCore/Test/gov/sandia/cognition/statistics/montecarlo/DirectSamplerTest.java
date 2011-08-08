/*
 * File:                DirectSamplerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.Random;

/**
 * Unit tests for DirectSamplerTest.
 *
 * @author krdixon
 */
public class DirectSamplerTest
    extends MonteCarloSamplerTestHarness<Double,Double,ProbabilityFunction<Double>>
{

    /**
     * Tests for class DirectSamplerTest.
     * @param testName Name of the test.
     */
    public DirectSamplerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public DirectSampler<Double> createInstance()
    {
        return new DirectSampler<Double>();
    }

    @Override
    public UnivariateGaussian.PDF createTargetFunctionInstance()
    {
        return new UnivariateGaussian.PDF( RANDOM.nextDouble(), RANDOM.nextDouble() );
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        DirectSampler<Double> instance = new DirectSampler<Double>();
        assertNotNull( instance );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        UnivariateGaussian.PDF f = this.createTargetFunctionInstance();
        DirectSampler<Double> instance = this.createInstance();

        Random r1 = new Random(1);

        ArrayList<? extends Double> s1 = instance.sample(f, r1, NUM_SAMPLES);
        assertEquals( NUM_SAMPLES, s1.size() );

        Random r2 = new Random(1);
        ArrayList<Double> s2 = f.sample(r2, NUM_SAMPLES);
        assertEquals( s2.size(), s1.size() );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            assertEquals( s2.get(n), s1.get(n), TOLERANCE );
        }
        
    }

}
