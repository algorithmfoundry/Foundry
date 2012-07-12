/*
 * File:                ScalarMixtureDensityModelTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 12, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothUnivariateDistributionTestHarness;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tests for class ScalarMixtureDensityModelTest.
 * @author krdixon
 */
public class ScalarMixtureDensityModelTest
    extends SmoothUnivariateDistributionTestHarness
{

    /**
     * Default Constructor
     * @param testName Test name
     */
    public ScalarMixtureDensityModelTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public ScalarMixtureDensityModel createInstance()
    {

        ScalarMixtureDensityModel instance = new ScalarMixtureDensityModel(
            new UnivariateGaussian.PDF( 1.0, 1.0 ),
            new UnivariateGaussian.PDF( -1.0, 0.5 ) );
        instance.getPriorWeights()[0] = RANDOM.nextDouble();
        instance.getPriorWeights()[1] = RANDOM.nextDouble();
        return instance;
    }



    /**
     * Tests the case of 2 Gaussians
     */
    public void testLearner2Gaussians()
    {
        System.out.println( "Learner 2 Gaussians" );

        ScalarMixtureDensityModel target = this.createInstance();
        ArrayList<Double> samples = target.sample(RANDOM, NUM_SAMPLES);

        ScalarMixtureDensityModel.EMLearner learner =
            new ScalarMixtureDensityModel.EMLearner(
                2, new UnivariateGaussian.WeightedMaximumLikelihoodEstimator(), RANDOM );
        ScalarMixtureDensityModel estimate = learner.learn(samples);
        System.out.println( "Estimate: " + estimate );

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis( samples, estimate.getCDF() );
        System.out.println( "K-S Test: " + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );
    }


    public void testLearner3Distribution()
    {
        System.out.println( "Learner 3 Distributions" );

        ScalarMixtureDensityModel target = new ScalarMixtureDensityModel(
            new UnivariateGaussian( 1.0, 1.0 ),
            new ExponentialDistribution( 10.0 ),
            new LaplaceDistribution( -1.0, 2.0 ) );
        ArrayList<Double> samples = target.sample(RANDOM, NUM_SAMPLES);

        @SuppressWarnings("unchecked")
        ScalarMixtureDensityModel.EMLearner learner =
            new ScalarMixtureDensityModel.EMLearner(
                RANDOM,
                Arrays.asList(
                new UnivariateGaussian.WeightedMaximumLikelihoodEstimator(),
                new ExponentialDistribution.WeightedMaximumLikelihoodEstimator(),
                new LaplaceDistribution.WeightedMaximumLikelihoodEstimator() ));
        ScalarMixtureDensityModel estimate = learner.learn(samples);
        System.out.println( "Estimate: " + estimate );

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis( samples, estimate.getCDF() );
        System.out.println( "K-S Test: " + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );        

    }


    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        ScalarMixtureDensityModel instance = new ScalarMixtureDensityModel();
        assertEquals( 1, instance.getDistributionCount() );
        assertEquals( 1.0, instance.getPriorWeights()[0] );
        assertTrue( instance.getDistributions().get(0) instanceof UnivariateGaussian );

        UnivariateGaussian g1 = new UnivariateGaussian();
        ExponentialDistribution e1 = new ExponentialDistribution();
        instance = new ScalarMixtureDensityModel( g1, e1 );
        assertEquals( 2, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertSame( e1, instance.getDistributions().get(1) );
        assertEquals( instance.getPriorWeights()[0], instance.getPriorWeights()[1] );

        ScalarMixtureDensityModel i2 = new ScalarMixtureDensityModel( instance );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertTrue( Arrays.equals(i2.getPriorWeights(), instance.getPriorWeights() ) );
        

        try
        {
            instance = new ScalarMixtureDensityModel( Arrays.asList( g1 ), new double[]{ 1.0, 2.0 } );
            fail( "Must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        ScalarMixtureDensityModel.CDF instance = new ScalarMixtureDensityModel.CDF();
        assertEquals( 1, instance.getDistributionCount() );
        assertEquals( 1.0, instance.getPriorWeights()[0] );
        assertTrue( instance.getDistributions().get(0) instanceof UnivariateGaussian );

        UnivariateGaussian g1 = new UnivariateGaussian();
        ExponentialDistribution e1 = new ExponentialDistribution();
        instance = new ScalarMixtureDensityModel.CDF( g1, e1 );
        assertEquals( 2, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertSame( e1, instance.getDistributions().get(1) );
        assertEquals( instance.getPriorWeights()[0], instance.getPriorWeights()[1] );

        ScalarMixtureDensityModel i2 = new ScalarMixtureDensityModel( instance );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertTrue( Arrays.equals(i2.getPriorWeights(), instance.getPriorWeights() ) );


        try
        {
            instance = new ScalarMixtureDensityModel.CDF(
                Arrays.asList( g1 ), new double[]{ 1.0, 2.0 } );
            fail( "Must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }


    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );


        ScalarMixtureDensityModel.PDF instance = new ScalarMixtureDensityModel.PDF();
        assertEquals( 1, instance.getDistributionCount() );
        assertEquals( 1.0, instance.getPriorWeights()[0] );
        assertTrue( instance.getDistributions().get(0) instanceof UnivariateGaussian );

        UnivariateGaussian g1 = new UnivariateGaussian();
        ExponentialDistribution e1 = new ExponentialDistribution();
        instance = new ScalarMixtureDensityModel.PDF( g1, e1 );
        assertEquals( 2, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertSame( e1, instance.getDistributions().get(1) );
        assertEquals( instance.getPriorWeights()[0], instance.getPriorWeights()[1] );

        ScalarMixtureDensityModel i2 = new ScalarMixtureDensityModel( instance );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertTrue( Arrays.equals(i2.getPriorWeights(), instance.getPriorWeights() ) );

        try
        {
            instance = new ScalarMixtureDensityModel.PDF(
                Arrays.asList( g1 ), new double[]{ 1.0, 2.0 } );
            fail( "Must be same size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF Known Values" );

        ScalarMixtureDensityModel.PDF pdf = new ScalarMixtureDensityModel.PDF(
            Arrays.asList( new UnivariateGaussian( 0.0, 1.0 ),
                new UnivariateGaussian( 5.0, 2.0 ) ),
            new double[]{ 0.7, 0.3 } );

        // Dan Morrow calculated these...
        assertEquals(0.279422967598279, pdf.evaluate(0.0), TOLERANCE);
        assertEquals(0.074871303008531, pdf.evaluate(5.7), TOLERANCE);
        assertEquals(0.119962166165605, pdf.evaluate(-1.3), TOLERANCE);
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );

        ScalarMixtureDensityModel.CDF cdf = new ScalarMixtureDensityModel.CDF(
            Arrays.asList( new UnivariateGaussian( 0.0, 1.0 ),
                new UnivariateGaussian( 5.0, 2.0 ) ),
            new double[]{ 0.7, 0.3 } );

        // Dan Morrow calculated these...
        assertEquals(0.215991370396509, cdf.evaluate(-0.5), TOLERANCE);
        assertEquals(0.3500610428078071, cdf.evaluate(0.0), TOLERANCE);
        assertEquals(0.633573006281660, cdf.evaluate(1.3), TOLERANCE);
        assertEquals(0.906907303841087, cdf.evaluate(5.7), TOLERANCE);
        assertEquals(0.999996686425450, cdf.evaluate(11.0), TOLERANCE);

    }



    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        ScalarMixtureDensityModel instance = this.createInstance();
        Vector p = instance.convertToVector();

        Vector d1 = instance.getDistributions().get(0).convertToVector();
        Vector d2 = instance.getDistributions().get(1).convertToVector();
        assertEquals( 6, p.getDimensionality() );
        int index = 0;
        assertEquals( instance.getPriorWeights()[0], p.getElement(index++) );
        assertEquals( instance.getPriorWeights()[1], p.getElement(index++) );
        assertEquals( d1.getElement(0), p.getElement(index++) );
        assertEquals( d1.getElement(1), p.getElement(index++) );
        assertEquals( d2.getElement(0), p.getElement(index++) );
        assertEquals( d2.getElement(1), p.getElement(index++) );
    }

    @Override
    public void testPDFMonteCarlo()
    {
        double temp = MONTE_CARLO_FACTOR;
        MONTE_CARLO_FACTOR = 10.0;
        super.testPDFMonteCarlo();
        MONTE_CARLO_FACTOR = temp;
    }

}
