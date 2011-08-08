/*
 * File:                MultivariateGaussianInverseGammaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.MultivariateDistributionTestHarness;

/**
 * Unit tests for MultivariateGaussianInverseGammaDistributionTest.
 *
 * @author krdixon
 */
public class MultivariateGaussianInverseGammaDistributionTest
    extends MultivariateDistributionTestHarness<Vector>
{

    /**
     * Tests for class MultivariateGaussianInverseGammaDistributionTest.
     * @param testName Name of the test.
     */
    public MultivariateGaussianInverseGammaDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateGaussianInverseGammaDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MultivariateGaussianInverseGammaDistribution instance =
            new MultivariateGaussianInverseGammaDistribution();
        assertEquals( MultivariateGaussianInverseGammaDistribution.DEFAULT_DIMENSIONALITY, instance.getGaussian().getInputDimensionality() );
        assertNotNull( instance.getInverseGamma() );
    }


    /**
     * Test of getGaussian method, of class MultivariateGaussianInverseGammaDistribution.
     */
    public void testGetGaussian()
    {
        System.out.println("getGaussian");
        MultivariateGaussianInverseGammaDistribution instance = this.createInstance();
        MultivariateGaussian gaussian = instance.getGaussian();
        assertNotNull( gaussian );
    }

    /**
     * Test of setGaussian method, of class MultivariateGaussianInverseGammaDistribution.
     */
    public void testSetGaussian()
    {
        System.out.println("setGaussian");
        MultivariateGaussianInverseGammaDistribution instance = this.createInstance();
        MultivariateGaussian gaussian = instance.getGaussian();
        assertNotNull( gaussian );
        instance.setGaussian(null);
        assertNull( instance.getGaussian() );
        instance.setGaussian(gaussian);
        assertSame( gaussian, instance.getGaussian() );
    }

    /**
     * Test of getInverseGamma method, of class MultivariateGaussianInverseGammaDistribution.
     */
    public void testGetInverseGamma()
    {
        System.out.println("getInverseGamma");
        MultivariateGaussianInverseGammaDistribution instance = this.createInstance();
        InverseGammaDistribution inverseGamma = instance.getInverseGamma();
        assertNotNull( inverseGamma );
    }

    /**
     * Test of setInverseGamma method, of class MultivariateGaussianInverseGammaDistribution.
     */
    public void testSetInverseGamma()
    {
        System.out.println("setInverseGamma");
        MultivariateGaussianInverseGammaDistribution instance = this.createInstance();
        InverseGammaDistribution inverseGamma = instance.getInverseGamma();
        assertNotNull( inverseGamma );
        instance.setInverseGamma(null);
        assertNull( instance.getInverseGamma() );
        instance.setInverseGamma(inverseGamma);
        assertSame( inverseGamma, instance.getInverseGamma() );
    }

    @Override
    public MultivariateGaussianInverseGammaDistribution createInstance()
    {
        return new MultivariateGaussianInverseGammaDistribution();
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
        MultivariateGaussianInverseGammaDistribution instance = this.createInstance();
        assertEquals( instance.getGaussian().getMean(), instance.getMean() );
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1e-1;
        super.testGetMean();
        TOLERANCE = temp;
    }

}
