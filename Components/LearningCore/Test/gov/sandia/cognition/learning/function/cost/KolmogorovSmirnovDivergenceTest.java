/*
 * File:                KolmogorovSmirnovDivergenceTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.statistics.ScalarDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for KolmogorovSmirnovDivergenceTest.
 *
 * @author krdixon
 */
public class KolmogorovSmirnovDivergenceTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class KolmogorovSmirnovDivergenceTest.
     * @param testName Name of the test.
     */
    public KolmogorovSmirnovDivergenceTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class KolmogorovSmirnovDivergenceTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        KolmogorovSmirnovDivergence<Double> instance =
            new KolmogorovSmirnovDivergence<Double>();
        assertNull( instance.getCostParameters() );

        UnivariateGaussian g = new UnivariateGaussian();
        ArrayList<Double> data = g.sample(RANDOM,1000);
        instance = new KolmogorovSmirnovDivergence<Double>( data );
        assertSame( data, instance.getCostParameters() );
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        UnivariateGaussian g = new UnivariateGaussian();
        ArrayList<Double> data = g.sample(RANDOM,1000);
        KolmogorovSmirnovDivergence<Double> instance =
            new KolmogorovSmirnovDivergence<Double>( data );
        KolmogorovSmirnovDivergence<Double> clone =
            (KolmogorovSmirnovDivergence<Double>) instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getCostParameters(), clone.getCostParameters() );

        double r1 = instance.evaluate(g);
        double r2 = clone.evaluate(g);
        assertEquals( r1, r2, TOLERANCE );
    }

    /**
     * Test of evaluate method, of class KolmogorovSmirnovDivergence.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        UnivariateGaussian g = new UnivariateGaussian();
        ArrayList<Double> data = g.sample(RANDOM,1000);
        KolmogorovSmirnovDivergence<Double> instance =
            new KolmogorovSmirnovDivergence<Double>( data );

        double result = instance.evaluate(g);

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(data, g.getCDF());
        assertEquals( kstest.getD(), result );
    }



}
