/*
 * File:                ImportanceSamplerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.WeightedValue;

/**
 * Unit tests for ImportanceSamplerTest.
 *
 * @author krdixon
 */
public class ImportanceSamplerTest
    extends MonteCarloSamplerTestHarness<Double,WeightedValue<Double>,Evaluator<Double,Double>>
{

    /**
     * Tests for class ImportanceSamplerTest.
     * @param testName Name of the test.
     */
    public ImportanceSamplerTest(
        String testName)
    {
        super(testName);
        NUM_SAMPLES = 100;
    }


    @Override
    public ImportanceSampler<Double> createInstance()
    {
        double mean = 5.0;
        double variance = 5.0;
        return new ImportanceSampler<Double>(
            new UnivariateGaussian.PDF( mean, variance ) );
    }

    @Override
    public GammaDistribution.PDF createTargetFunctionInstance()
    {
        double shape = 5.0;
        double scale = 1.0;
        return new GammaDistribution.PDF( shape, scale );
    }

    @Override
    public void testKnownValues()
    {
    }



    /**
     * Tests the constructors of class ImportanceSamplerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ImportanceSampler<Double> instance = new ImportanceSampler<Double>();
        assertNull( instance.getImportanceDistribution() );

        GammaDistribution.PDF f = this.createTargetFunctionInstance();
        instance = new ImportanceSampler<Double>( f );
        assertSame( f, instance.getImportanceDistribution() );
    }

    /**
     * Test of getImportanceDistribution method, of class ImportanceSampler.
     */
    public void testGetImportanceDistribution()
    {
        System.out.println("getImportanceDistribution");
        ImportanceSampler<Double> instance = new ImportanceSampler<Double>();
        assertNull( instance.getImportanceDistribution() );
    }

    /**
     * Test of setImportanceDistribution method, of class ImportanceSampler.
     */
    public void testSetImportanceDistribution()
    {
        System.out.println("setImportanceDistribution");
        ImportanceSampler<Double> instance = new ImportanceSampler<Double>();
        assertNull( instance.getImportanceDistribution() );

        GammaDistribution.PDF f = this.createTargetFunctionInstance();
        instance.setImportanceDistribution(f);
        assertSame( f, instance.getImportanceDistribution() );

    }


}
