/*
 * File:                MaximumLikelihoodDistributionEstimatorTest.java
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

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorReader;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistribution;
import gov.sandia.cognition.statistics.SmoothScalarDistribution;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.CauchyDistribution;
import gov.sandia.cognition.statistics.distribution.ExponentialDistribution;
import gov.sandia.cognition.statistics.distribution.InverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.LaplaceDistribution;
import gov.sandia.cognition.statistics.distribution.LogNormalDistribution;
import gov.sandia.cognition.statistics.distribution.ParetoDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UniformDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.distribution.WeibullDistribution;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MaximumLikelihoodDistributionEstimatorTest.
 *
 * @author krdixon
 */
public class MaximumLikelihoodDistributionEstimatorTest
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
     * Tests for class MaximumLikelihoodDistributionEstimatorTest.
     * @param testName Name of the test.
     */
    public MaximumLikelihoodDistributionEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MaximumLikelihoodDistributionEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MaximumLikelihoodDistributionEstimator<Double> instance =
            new MaximumLikelihoodDistributionEstimator<Double>();
        assertNull( instance.getDistributions() );

        Collection<UnivariateGaussian> gs = Arrays.asList(new UnivariateGaussian());
        instance = new MaximumLikelihoodDistributionEstimator<Double>( gs );
        assertSame( gs, instance.getDistributions() );
    }

    /**
     * Test of clone method, of class MaximumLikelihoodDistributionEstimator.
     */
    public void testClone()
    {
        System.out.println("clone");
        Collection<UnivariateGaussian> gs = Arrays.asList(new UnivariateGaussian());
        MaximumLikelihoodDistributionEstimator<Double> instance =
            new MaximumLikelihoodDistributionEstimator<Double>( gs );
        MaximumLikelihoodDistributionEstimator<Double> clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getDistributions(), clone.getDistributions() );
        assertEquals( instance.getDistributions().size(), clone.getDistributions().size() );
    }

    /**
     * Test of getDistributions method, of class MaximumLikelihoodDistributionEstimator.
     */
    public void testGetDistributions()
    {
        System.out.println("getDistributions");
        Collection<UnivariateGaussian> gs = Arrays.asList(new UnivariateGaussian());
        MaximumLikelihoodDistributionEstimator<Double> instance =
            new MaximumLikelihoodDistributionEstimator<Double>(gs);
        assertSame( gs, instance.getDistributions() );
    }

    /**
     * Test of setDistributions method, of class MaximumLikelihoodDistributionEstimator.
     */
    public void testSetDistributions()
    {
        System.out.println("setDistributions");
        Collection<UnivariateGaussian> gs = Arrays.asList(new UnivariateGaussian());
        MaximumLikelihoodDistributionEstimator<Double> instance =
            new MaximumLikelihoodDistributionEstimator<Double>(gs);
        assertSame( gs, instance.getDistributions() );
        instance.setDistributions(null);
        assertNull( instance.getDistributions() );
        instance.setDistributions(gs);
        assertSame( gs, instance.getDistributions() );
    }

    /**
     * Test of learn method, of class MaximumLikelihoodDistributionEstimator.
     */
    public void testLearn()
    {
        System.out.println("learn");

        WeibullDistribution target = new WeibullDistribution( 5.0, 1.0 );
        ArrayList<Double> samples = target.sample(RANDOM,10);

        Collection<? extends ClosedFormComputableDistribution<Double>> distributions = Arrays.asList(
            new UnivariateGaussian(),
//            new WeibullDistribution(),
//            new GammaDistribution(),
            new BetaDistribution(),
            new ExponentialDistribution(),
            new InverseGammaDistribution(),
            new LaplaceDistribution(),
            new LogNormalDistribution(),
            new ParetoDistribution(),
            new StudentTDistribution(),
            new UniformDistribution(),
            new CauchyDistribution() );

        MaximumLikelihoodDistributionEstimator<Double> estimator =
            new MaximumLikelihoodDistributionEstimator<Double>( distributions );
        ClosedFormComputableDistribution<Double> best = estimator.learn(samples);
        System.out.println( "Best: " + best.getClass().getSimpleName() + ", Parameters: " + best.convertToVector() );
    }


    public void testGetDistributionClasses()
        throws Exception
    {
        System.out.println( "getDistributionClasses" );

        System.out.println( "\n=========== Smooth =========" );
        LinkedList<SmoothScalarDistribution> i1 =
            MaximumLikelihoodDistributionEstimator.getDistributionClasses( SmoothScalarDistribution.class );
        for( SmoothScalarDistribution distribution : i1 )
        {
            System.out.println( distribution.getClass().getCanonicalName() + ": " + distribution.convertToVector() );
        }

        System.out.println( "\n=========== Discrete =======" );
        LinkedList<ClosedFormDiscreteScalarDistribution> i2 =
            MaximumLikelihoodDistributionEstimator.getDistributionClasses( ClosedFormDiscreteScalarDistribution.class );
        for( ClosedFormDiscreteScalarDistribution distribution : i2 )
        {
            System.out.println( distribution.getClass().getCanonicalName() + ": " + distribution.convertToVector() );
        }

    }

    public void testEstimateContinuousDistribution()
        throws Exception
    {
        System.out.println( "estimateContinuousDistribution" );

        WeibullDistribution target = new WeibullDistribution( 5.0, 1.0 );
        ArrayList<Double> samples = target.sample(RANDOM,100);

        SmoothScalarDistribution distribution =
            MaximumLikelihoodDistributionEstimator.estimateContinuousDistribution(samples);
        System.out.println( "ML Distribution: " + distribution.getClass().getCanonicalName() + ": " + distribution.convertToVector() );

        

    }

}
