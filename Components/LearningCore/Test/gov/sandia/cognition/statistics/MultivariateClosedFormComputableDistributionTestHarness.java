/*
 * File:                MultivariateDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;

/**
 * Unit tests for MultivariateDistributionTestHarness.
 *
 * @param <RingType> RingType
 * @author krdixon
 */
public abstract class MultivariateClosedFormComputableDistributionTestHarness<RingType extends Ring<RingType>>
    extends MultivariateClosedFormDistributionTestHarness<RingType>
{

    /**
     * Tests for class MultivariateDistributionTestHarness.
     * @param testName Name of the test.
     */
    public MultivariateClosedFormComputableDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance.
     */
    abstract public ClosedFormComputableDistribution<RingType> createInstance();

    /**
     * Tests the constructors of class MultivariateDistributionTestHarness.
     */
    abstract public void testProbabilityFunctionConstructors();

    /**
     * Tests against known values.
     */
    abstract public void testProbabilityFunctionKnownValues();

    /**
     * Tries to ensure that the PDF is nonnegative
     */
    public void testProbabilityFunctionNonnegative()
    {
        System.out.println( "PDF.nonnegative" );

        ProbabilityFunction<RingType> f =
            this.createInstance().getProbabilityFunction();
        ArrayList<? extends RingType> data = f.sample(RANDOM,NUM_SAMPLES);
        for( RingType x : data )
        {
            assertTrue( f.evaluate(x) >= 0.0 );
        }
    }

    /**
     * Tests the clone of the PDF
     */
    public void testProbabilityFunctionClone()
    {
        System.out.println( "PDF.clone" );
        super.testClone();

        ProbabilityFunction<RingType> f = this.createInstance().getProbabilityFunction();

        @SuppressWarnings("unchecked")
        ProbabilityFunction<RingType> clone =
            (ProbabilityFunction<RingType>) f.clone();

        Vector p1 = ((ClosedFormDistribution) f).convertToVector();
        Vector p2 = ((ClosedFormDistribution) clone).convertToVector();
        assertNotSame( p1, p2 );
        assertEquals( p1, p2 );
    }

    /**
     * logEvaluate
     */
    public void testProbabilityFunctionLogEvaluate()
    {
        System.out.println( "logEvaluate" );

        ProbabilityFunction<RingType> pdf =
            this.createInstance().getProbabilityFunction();
        ArrayList<? extends RingType> samples = pdf.sample(RANDOM, NUM_SAMPLES);

        for( RingType sample : samples )
        {
            double plog = pdf.logEvaluate(sample);
            double p = pdf.evaluate(sample);
            double phat = Math.exp(plog);
            assertEquals( p, phat, TOLERANCE );
        }

    }

    /**
     * PDF.getDistributionFunction
     */
    public void testProbabilityFunctionGetProbabilityFunction()
    {
        System.out.println( "PDF.getProbabilityFunction" );

        ProbabilityFunction<RingType> pdf =
            this.createInstance().getProbabilityFunction();
        assertSame( pdf, pdf.getProbabilityFunction() );

    }

    /**
     * Distribution.getDistributionFunction
     */
    public void testDistributionGetProbabilityFunction()
    {
        System.out.println( "Distribution.getProbabilityFunction" );
        ClosedFormComputableDistribution<RingType> instance =
            this.createInstance();
        ProbabilityFunction<RingType> pdf = instance.getProbabilityFunction();
        assertNotSame( instance, pdf );
        assertEquals( instance.convertToVector(),
            ((ClosedFormDistribution) pdf).convertToVector() );
    }

}
