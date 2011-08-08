/*
 * File:                InverseTransformSamplingTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.algorithm.root.AbstractBracketedRootFinder;
import gov.sandia.cognition.learning.algorithm.root.RootFinderRiddersMethod;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests of InverseTransformSampling
 * @author krdixon
 */
public class InverseTransformSamplingTest
    extends TestCase
{

    /**
     * Random number generator
     */
    Random RANDOM = new Random( 1 );

    /**
     * Confidence
     */
    final double CONFIDENCE = 0.95;

    /**
     * Creates a test
     * @param testName
     * Name of the test
     */
    public InverseTransformSamplingTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Constructors
     */
    public void testConstructor()
    {
        System.out.println( "constructor" );

        InverseTransformSampling its = new InverseTransformSampling();
        assertNotNull( its );

    }

    /**
     * eval.cdf
     * @param cdf
     */
    public void evaluateCDF(
        CumulativeDistributionFunction<Double> cdf )
    {

        System.out.println( "Evaluating CDF: " + cdf );

        int numSamples = 0;
        ArrayList<Double> result =
            InverseTransformSampling.sample(cdf, RANDOM, numSamples);
        assertEquals( numSamples, result.size() );

        numSamples = 1000;
        result = InverseTransformSampling.sample(cdf, RANDOM, numSamples);
        assertEquals( numSamples, result.size() );

        StudentTConfidence ttest = new StudentTConfidence();
        ConfidenceInterval c =
            ttest.computeConfidenceInterval(result, CONFIDENCE);
        System.out.println( "Mean = " + cdf.getMean() + " Interval = " + c );

        assertTrue( c.withinInterval(cdf.getMean()) );

        KolmogorovSmirnovConfidence.Statistic stat =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(result, cdf);
        System.out.println( "K-S test: " + stat );
        assertEquals( 1.0, stat.getNullHypothesisProbability(), CONFIDENCE );

        this.invertCDF(cdf);
    }


    public void invertCDF(
        CumulativeDistributionFunction<Double> cdf )
    {
        System.out.println( "Inverting CDF: " + cdf );

        final int numSamples = 1000;
        final double TOLERANCE = 1e-3;
        for( int i = 0; i < numSamples; i++ )
        {
            double p = RANDOM.nextDouble();
            InputOutputPair<Double,Double> root =
                InverseTransformSampling.inverse(cdf, p);
            double phat = root.getOutput();
            assertEquals( phat, cdf.evaluate(root.getInput()) );
            assertEquals( p, phat, TOLERANCE );
        }
    }


    public void testUnitGaussian()
    {
        System.out.println( "Unit Gaussian" );

        this.evaluateCDF( new UnivariateGaussian.CDF() );

    }

    public void testSharpGaussian()
    {
        System.out.println( "Sharp Gaussian" );
        this.evaluateCDF( new UnivariateGaussian.CDF( RANDOM.nextGaussian(), 1e-5 ) );
    }

    public void testStudentT10()
    {
        System.out.println( "Student t 10" );
        this.evaluateCDF(new StudentTDistribution.CDF(10.0));
    }

    public void testStudentT2()
    {
        // This distribution has infinite variance, so this should be telling
        System.out.println( "Student t 2" );
        this.evaluateCDF(new StudentTDistribution.CDF( 2.0 ));
    }

    /**
     * Test of sample method, of class InverseTransformSampling.
     */
    public void testSample_3args()
    {
        System.out.println("sample");

        CumulativeDistributionFunction<Double> cdf = new UnivariateGaussian.CDF();
        int numSamples = 100;
        ArrayList<Double> result =
            InverseTransformSampling.sample(cdf, RANDOM, numSamples);
        assertEquals( numSamples, result.size() );
        KolmogorovSmirnovConfidence.Statistic stat =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(result, cdf);
        System.out.println( "K-S Test: " + stat );
        assertEquals( 1.0, stat.getNullHypothesisProbability(), CONFIDENCE );
        
    }

    /**
     * Test of inverseRootFinder method, of class InverseTransformSampling.
     */
    public void testInverse_CumulativeDistributionFunction_double()
    {
        System.out.println("inverse");
        CumulativeDistributionFunction<Double> cdf =
            new StudentTDistribution.CDF( 10.0 );
        try
        {
            InverseTransformSampling.inverse(cdf,-0.1);
            fail( "p must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            InverseTransformSampling.inverse(cdf,1.0+1e-10);
            fail( "p must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * inverseNewtonsMethod
     */
    public void testInverseNewtonsMethod()
    {
        System.out.println( "inverseNewtonsMethod" );
        InputOutputPair<Double,Double> result;

        int numSamples = 10;
        ArrayList<Double> ps = new ArrayList<Double>( numSamples );
        ArrayList<IT_CDF> cdfs = new ArrayList<IT_CDF>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            cdfs.add( new IT_CDF() );
            ps.add( RANDOM.nextDouble() );
        }

        ArrayList<Double> r1 = new ArrayList<Double>( numSamples*numSamples );
        ArrayList<Double> e1 = new ArrayList<Double>( numSamples*numSamples );
        ArrayList<Double> r2 = new ArrayList<Double>( numSamples*numSamples );
        ArrayList<Double> e2 = new ArrayList<Double>( numSamples*numSamples );

        double err;
        final double tolerance = AbstractBracketedRootFinder.DEFAULT_TOLERANCE*1e-3;

        long start1 = System.currentTimeMillis();
        for( int n = 0; n < numSamples; n++ )
        {
            IT_CDF cdf = cdfs.get(n);
            for( int i = 0; i < numSamples; i++ )
            {
                double p = ps.get(i);
                cdf.numEvals = 0;
                result = InverseTransformSampling.inverseRootFinder(new RootFinderRiddersMethod(), cdf, p);
                e1.add( Math.abs( result.getOutput()-p ) );
                r1.add( (double) cdf.numEvals );
            }
        }
        long stop1 = System.currentTimeMillis();

        long start2 = System.currentTimeMillis();
        for( int n = 0; n < numSamples; n++ )
        {
            IT_CDF cdf = cdfs.get(n);
            for( int i = 0; i < numSamples; i++ )
            {
                double p = ps.get(i);
                cdf.numEvals = 0;
                result = InverseTransformSampling.inverseNewtonsMethod(cdf, p, tolerance );
                e2.add( Math.abs( result.getOutput()-p ) );
                r2.add( (double) cdf.numEvals );
            }
        }
        long stop2 = System.currentTimeMillis();

        System.out.println( "Time Root:   " + ((double) (stop1-start1))/1000.0 );
        System.out.println( "Time Newton: " + ((double) (stop2-start2))/1000.0 );

        GaussianConfidence t = new GaussianConfidence();

        try
        {
            ConfidenceInterval cr1 = t.computeConfidenceInterval(r1, CONFIDENCE);
            System.out.println( "Evals Root:   " + cr1 );
            ConfidenceInterval cr2 = t.computeConfidenceInterval(r2, CONFIDENCE);
            System.out.println( "Evals Newton: " + cr2 );
        }
        catch (Exception e)
        {
        }
        ConfidenceInterval ce1 = t.computeConfidenceInterval(e1, CONFIDENCE);
        System.out.println( "Accuracy Root:   " + ce1 );
        ConfidenceInterval ce2 = t.computeConfidenceInterval(e2, CONFIDENCE);
        System.out.println( "Accuracy Newton: " + ce2 );


    }

    public class IT_CDF
        extends BetaDistribution.CDF
    {

        public int numEvals;

        public IT_CDF()
        {
            super( Math.abs(RANDOM.nextGaussian())+1.0, Math.abs(RANDOM.nextGaussian())+1.0 );
            this.numEvals = 0;
        }

        @Override
        public double evaluate(
            double input)
        {
            this.numEvals++;
            return super.evaluate(input);
        }

    }


}
