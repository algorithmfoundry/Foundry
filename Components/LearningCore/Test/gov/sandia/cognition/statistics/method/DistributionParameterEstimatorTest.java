/*
 * File:                DistributionParameterEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 9, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.function.cost.KolmogorovSmirnovDivergence;
import gov.sandia.cognition.learning.function.cost.NegativeLogLikelihood;
import gov.sandia.cognition.learning.function.cost.ParallelNegativeLogLikelihood;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.PoissonDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DistributionParameterEstimatorTest.
 *
 * @author krdixon
 */
public class DistributionParameterEstimatorTest
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

    public final int NUM_SAMPLES = 1000;

    /**
     * Tests for class DistributionParameterEstimatorTest.
     * @param testName Name of the test.
     */
    public DistributionParameterEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DistributionParameterEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
//        fail("The test case is a prototype.");
    }

    /**
     * Test of clone method, of class DistributionParameterEstimator.
     */
    public void testClone()
    {
        System.out.println("clone");
//        fail("The test case is a prototype.");
    }


    public <DataType> void distributionML(
        ClosedFormComputableDistribution<DataType> target,
        ArrayList<? extends DataType> targetData,
        ClosedFormComputableDistribution<DataType> estimate )
    {
        System.out.println( "\nML Distribution: " + estimate.getClass().getCanonicalName() );

        NegativeLogLikelihood<DataType> costFunction =
            new ParallelNegativeLogLikelihood<DataType>( targetData );
        System.out.println( "Target Cost:    " + costFunction.evaluate(target) );

        DistributionParameterEstimator<DataType,ClosedFormComputableDistribution<DataType>> instance =
            new DistributionParameterEstimator<DataType,ClosedFormComputableDistribution<DataType>>( estimate, costFunction );

        ClosedFormComputableDistribution<DataType> result =
            instance.learn(targetData);

        System.out.println( "Target: " + target.convertToVector() );
        System.out.println( "Result: " + result.convertToVector() );
        double dist = target.convertToVector().euclideanDistance(
            result.convertToVector() );
        System.out.println( "Dist:   " + dist );
        assertEquals( 0.0, dist, 1.0 );
    }
    
    public <DataType extends Number> void distributionKS(
        ClosedFormUnivariateDistribution<DataType> target,
        ArrayList<? extends DataType> targetData,
        ClosedFormUnivariateDistribution<DataType> estimate )
    {

        System.out.println( "\nK-S Distribution: " + estimate.getClass().getCanonicalName() );

        KolmogorovSmirnovDivergence<DataType> costFunction =
            new KolmogorovSmirnovDivergence<DataType>( targetData );
        System.out.println( "Target Cost:    " + costFunction.evaluate(target) );

        DistributionParameterEstimator<DataType,ClosedFormUnivariateDistribution<DataType>> instance =
            new DistributionParameterEstimator<DataType,ClosedFormUnivariateDistribution<DataType>>( estimate, costFunction );

        ClosedFormUnivariateDistribution<DataType> result =
            instance.learn(targetData);

        System.out.println( "Target: " + target.convertToVector() );
        System.out.println( "Result: " + result.convertToVector() );
        double dist = target.convertToVector().euclideanDistance(
            result.convertToVector() );
        System.out.println( "Dist:   " + dist );
        assertEquals( 0.0, dist, 1.0 );
    }


    /**
     * Test of learn method, of class DistributionParameterEstimator.
     */
    public void testLearnGamma()
    {
        GammaDistribution.PDF target = new GammaDistribution.PDF( 5.0, 10.0 );
        System.out.println("\n============= Target: " + target.getClass().getCanonicalName() );

        ArrayList<Double> targetData = target.sample(RANDOM,NUM_SAMPLES);

        GammaDistribution.PDF estimate = new GammaDistribution.PDF();

        this.distributionML(target, targetData, estimate.clone());
        this.distributionKS(target, targetData, estimate.clone());
    }

    /**
     * Test of learn method, of class DistributionParameterEstimator.
     */
    public void testLearnPoisson()
    {
        PoissonDistribution.PMF target = new PoissonDistribution.PMF( 10.0 );
        System.out.println("============= Target: " + target.getClass().getCanonicalName() );
        ArrayList<Number> targetData = target.sample(RANDOM,NUM_SAMPLES);

        PoissonDistribution.PMF estimate = new PoissonDistribution.PMF();

        this.distributionML(target, targetData, estimate.clone());
        this.distributionKS(target, targetData, estimate.clone());
    }


    public void testLearnGaussian()
    {

        System.out.println( "Gaussian ML" );
        UnivariateGaussian.PDF target = new UnivariateGaussian.PDF(
            RANDOM.nextGaussian(), 5.0*RANDOM.nextDouble() );
        ArrayList<Double> data = target.sample(RANDOM, NUM_SAMPLES);
        UnivariateGaussian mlest =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(data, 0.0);

        System.out.println( "Target: " + target );
        System.out.println( "ML est: " + mlest );

        UnivariateGaussian.PDF estimate = new UnivariateGaussian.PDF();
        NegativeLogLikelihood<Double> costFunction =
            new ParallelNegativeLogLikelihood<Double>( data );
        DistributionParameterEstimator<Double,UnivariateGaussian> instance =
            new DistributionParameterEstimator<Double,UnivariateGaussian>( estimate, costFunction );

        UnivariateGaussian result = instance.learn( data );
        System.out.println( "Result: " + result );
        if( !mlest.convertToVector().equals( result.convertToVector(), 1e-3 ) )
        {
            assertEquals( mlest.convertToVector(), result.convertToVector() );
        }

    }
}
