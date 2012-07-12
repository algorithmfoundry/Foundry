/*
 * File:                MultivariateMixtureDensityModelTest.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author krdixon
 */
public class MultivariateMixtureDensityModelTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Vector>
{

    /**
     * stuff
     * @param testName
     */
    public MultivariateMixtureDensityModelTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public MixtureOfGaussians.PDF createInstance()
    {
        return createMixture(3, 2, RANDOM);
    }    

    public static MixtureOfGaussians.PDF createMixture(
        int numGaussians,
        int numDimensions,
        Random random)
    {

        double range = 10.0;
        ArrayList<MultivariateGaussian.PDF> gaussians =
            new ArrayList<MultivariateGaussian.PDF>(numGaussians);
        for (int i = 0; i < numGaussians; i++)
        {
            Vector mean = VectorFactory.getDefault().createUniformRandom(numDimensions, -range, range, random);
            Matrix covariance = MatrixFactory.getDefault().createIdentity(
                numDimensions, numDimensions).scale(0.5 * range);
            gaussians.add(new MultivariateGaussian.PDF(mean, covariance));
        }

        if (random.nextBoolean() == true)
        {
            double[] prior = new double[ numGaussians ];
            for( int i = 0; i < numGaussians; i++ )
            {
                prior[i] = random.nextDouble();
            }

            return new MixtureOfGaussians.PDF(gaussians, prior);
        }
        else
        {
            return new MixtureOfGaussians.PDF(gaussians);
        }

    }

    @Override
    public void testDistributionGetProbabilityFunction()
    {
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 5e-1;
        super.testGetMean();
        TOLERANCE = temp;
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateGaussian g1 = new MultivariateGaussian();
        MultivariateMixtureDensityModel<MultivariateGaussian> instance =
            new MultivariateMixtureDensityModel<MultivariateGaussian>( Arrays.asList(g1) );
        assertEquals( 1, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertEquals( 1.0, instance.getPriorWeights()[0] );

        MultivariateGaussian g2 = new MultivariateGaussian();
        MultivariateMixtureDensityModel<MultivariateGaussian> i2 =
            new MultivariateMixtureDensityModel<MultivariateGaussian>( Arrays.asList(g1, g2) );

        instance = new MultivariateMixtureDensityModel<MultivariateGaussian>( i2 );
        assertEquals( i2.getDistributionCount(), instance.getDistributionCount() );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertEquals( i2.getPriorWeightSum(), instance.getPriorWeightSum() );

    }

    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF Constructors" );

        MultivariateGaussian g1 = new MultivariateGaussian();
        MultivariateMixtureDensityModel.PDF<MultivariateGaussian> instance =
            new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( Arrays.asList(g1) );
        assertEquals( 1, instance.getDistributionCount() );
        assertSame( g1, instance.getDistributions().get(0) );
        assertEquals( 1.0, instance.getPriorWeights()[0] );

        MultivariateGaussian g2 = new MultivariateGaussian();
        MultivariateMixtureDensityModel.PDF<MultivariateGaussian> i2 =
            new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( Arrays.asList(g1, g2) );

        instance = new MultivariateMixtureDensityModel.PDF<MultivariateGaussian>( i2 );
        assertEquals( i2.getDistributionCount(), instance.getDistributionCount() );
        assertNotSame( i2.getDistributions(), instance.getDistributions() );
        assertNotSame( i2.getPriorWeights(), instance.getPriorWeights() );
        assertEquals( i2.getPriorWeightSum(), instance.getPriorWeightSum() );
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        int numGaussians = 5;
        int numDimensions = 2;
        MultivariateMixtureDensityModel.PDF<MultivariateGaussian> mog =
            createMixture(numGaussians, numDimensions, RANDOM);

        double r = 2.0;
        Vector input = VectorFactory.getDefault().createUniformRandom(
            numDimensions, -r, r, RANDOM );

        double sum = 0.0;
        for( int i = 0; i < numGaussians; i++ )
        {
            sum += mog.getDistributions().get(i).getProbabilityFunction().evaluate(input) * mog.getPriorWeights()[i];
        }

        assertEquals( sum/mog.getPriorWeightSum(), mog.evaluate(input),TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        MixtureOfGaussians.PDF instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( VectorFactory.getDefault().copyArray(instance.getPriorWeights()), p );
        assertNotSame( instance.getPriorWeights(), p );
    }

    @Override
    public void testKnownValues()
    {
        this.testProbabilityFunctionKnownValues();
    }


}
