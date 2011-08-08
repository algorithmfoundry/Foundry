/*
 * File:                MultivariateClosedFormComputableDiscreteDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Questa/Chama
 * 
 * Copyright May 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.method.ChiSquareConfidence;
import java.util.Collection;

/**
 * Test harness for MultivariateClosedFormComputableDiscreteDistribution
 * @param <RingType> Type of Ring
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class MultivariateClosedFormComputableDiscreteDistributionTestHarness<RingType extends Ring<RingType>>
    extends MultivariateClosedFormComputableDistributionTestHarness<RingType>
{

    /** 
     * Creates a new instance of MultivariateClosedFormComputableDiscreteDistributionTestHarness
     * @param testName
     * Test Name
     */
    public MultivariateClosedFormComputableDiscreteDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    @Override
    public abstract ClosedFormComputableDiscreteDistribution<RingType> createInstance();


    /**
     * Tests the distribution against a known domain.
     */
    public abstract void testKnownGetDomain();


    /**
     * Tests that a PMF abides by the rules of being a PMF:
     *  - nonnegative
     *  - less than or equal to 1.0
     *  - sums to 1.0
     */
    public void testPMFEvaluate()
    {
        System.out.println( "PMF.evaluate" );
        DiscreteDistribution<RingType> instance = this.createInstance();
        ProbabilityMassFunction<RingType> pmf = instance.getProbabilityFunction();
        double sum = 0.0;
        for( RingType value : pmf.getDomain() )
        {
            double prob = pmf.evaluate( value );
            assertTrue( "prob: " + prob + " must be [0,1]", 0.0 <= prob );
            assertTrue( "prob: " + prob + " must be [0,1]", prob <= 1.0 );

            sum += prob;
        }

        assertEquals( 1.0, sum, TOLERANCE );

    }

    /**
     * Tests getEntropy()
     */
    public void testPMFGetEntropy()
    {
        System.out.println( "PMF.getEntropy" );

        DiscreteDistribution<RingType> instance = this.createInstance();
        ProbabilityMassFunction<RingType> pmf = instance.getProbabilityFunction();
        double sum = 0.0;
        for( RingType value : pmf.getDomain() )
        {
            double p = pmf.evaluate( value );
            if( p > 0.0 )
            {
                sum -= p*MathUtil.log2( p );
            }
        }
        assertEquals( sum, pmf.getEntropy(), TOLERANCE );
    }


    /**
     * PMF.getDomain
     */
    public void testPMFGetDomain()
    {
        System.out.println( "PMF.getDomain" );
        DiscreteDistribution<RingType> instance = this.createInstance();
        ProbabilityMassFunction<RingType> pmf = instance.getProbabilityFunction();

        Collection<? extends RingType> d1 = instance.getDomain();
        Collection<? extends RingType> d2 = pmf.getDomain();
        assertEquals( d1.size(), d2.size() );

    }

    /**
     * PMF.getInputDimensionality()
     */
    public void testPMFGetInputDimensionality()
    {
        System.out.println( "PMF.getInputDimensionality" );
        DiscreteDistribution instance = this.createInstance();
        @SuppressWarnings("unchecked")
        ProbabilityMassFunction<Vector> pmf =
            (ProbabilityMassFunction<Vector>) instance.getProbabilityFunction();

        if( pmf instanceof VectorInputEvaluator )
        {
            Vector sample = pmf.sample(RANDOM);
            assertEquals( sample.getDimensionality(), ((VectorInputEvaluator) pmf).getInputDimensionality() );
        }
        else
        {
            throw new IllegalArgumentException(
                "PMF should extend VectorInputEvaluator with appropriate dim!" );
        }
    }

    /**
     * PMF Chi Square test
     */
    public void testPMFChiSquare()
    {
        System.out.println( "PMF Chi-Square Test" );

        ProbabilityMassFunction<RingType> pdf =
            this.createInstance().getProbabilityFunction();
        Collection<? extends RingType> samples = pdf.sample(RANDOM, NUM_SAMPLES);
        ChiSquareConfidence.Statistic chisquare =
            ChiSquareConfidence.evaluateNullHypothesis(samples, pdf);
        System.out.println( "Chi-Square Test: " + chisquare );
        assertEquals( 1.0, chisquare.getNullHypothesisProbability(), CONFIDENCE );
    }

}
