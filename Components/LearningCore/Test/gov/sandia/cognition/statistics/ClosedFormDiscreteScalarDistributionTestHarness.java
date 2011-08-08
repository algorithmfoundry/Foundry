/*
 * File:                ClosedFormDiscreteScalarDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.statistics.method.ChiSquareConfidence;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests the properties of all PMFs
 * @param <DataType> Data type of the domain
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class ClosedFormDiscreteScalarDistributionTestHarness<DataType extends Number>
    extends ClosedFormScalarDistributionTestHarness<DataType>
{

    /**
     * Entry point for JUnit tests for class ClosedFormDiscreteScalarDistributionTestHarness
     * @param testName name of this test
     */
    public ClosedFormDiscreteScalarDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new PMF instance
     * @return
     * new PMF instance
     */
    public abstract ClosedFormDiscreteScalarDistribution<DataType> createInstance();

    /**
     * PMF constructors
     */
    public abstract void testPMFConstructors();

    /**
     * Tests the distribution against a known domain.
     */
    public abstract void testKnownGetDomain();

    /**
     * Tests the PMF against known values
     */
    public abstract void testPMFKnownValues();
    
    /**
     * Tests that a PMF abides by the rules of being a PMF:
     *  - nonnegative
     *  - less than or equal to 1.0
     *  - sums to 1.0
     */
    public void testPMFEvaluate()
    {
        System.out.println( "PMF.evaluate" );
        DiscreteDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();
        double sum = 0.0;
        for( DataType value : pmf.getDomain() )
        {
            double prob = pmf.evaluate( value );
            assertTrue( "prob: " + prob + " must be [0,1]", 0.0 <= prob );
            assertTrue( "prob: " + prob + " must be [0,1]", prob <= 1.0 );

            sum += prob;
        }

        assertEquals( "PMF does not sum to 1.0: " + sum, 1.0, sum, TOLERANCE );
        
    }
    
    /**
     * Tests the sampling of the PMF
     */
    public void testPMFSample()
    {
        System.out.println( "PMF.sample" );
        
        DiscreteDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();
        ArrayList<? extends DataType> samples =
            pmf.sample( RANDOM, NUM_SAMPLES );
        ChiSquareConfidence.Statistic chiSquare =
            ChiSquareConfidence.evaluateNullHypothesis(samples, pmf);
        System.out.println( "Chi Square: " + chiSquare );
        assertEquals( 1.0, chiSquare.getNullHypothesisProbability(), CONFIDENCE );

    }
    
    /**
     * Tests getEntropy()
     */
    public void testPMFEntropy()
    {
        System.out.println( "PMF.getEntropy" );
        
        DiscreteDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();
        double sum = 0.0;
        for( DataType value : pmf.getDomain() )
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
     * getDomain
     */
    public void testGetDomain()
    {
        System.out.println( "getDomain()" );

        ClosedFormDiscreteScalarDistribution<DataType> instance = this.createInstance();
        Collection<? extends DataType> domain = instance.getDomain();
        assertTrue( domain.size() > 0 );
        for( DataType value : domain )
        {
            assertNotNull( value );
        }
        
    }

    /**
     * getDomainSize
     */
    public void testGetDomainSize()
    {
        System.out.println( "getDomainSize()" );
        ClosedFormDiscreteScalarDistribution<DataType> instance = this.createInstance();
        assertEquals( instance.getDomain().size(), instance.getDomainSize() );
    }

    /**
     * PMF.getDistributionFunction
     */
    public void testPMFGetDistributionFunction()
    {
        System.out.println( "PMF.getDistributionFunction()" );
        ClosedFormDiscreteScalarDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();

        assertSame( pmf, pmf.getProbabilityFunction() );

    }

    /**
     * PMF.getDomain
     */
    public void testPMFGetDomain()
    {
        System.out.println( "PMF.getDomain" );
        ClosedFormDiscreteScalarDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();

        Collection<? extends DataType> d1 = instance.getDomain();
        Collection<? extends DataType> d2 = pmf.getDomain();
        assertEquals( d1.size(), d2.size() );

    }
    
    /**
     * CDF.sumPMF
     */
    public void testCDFSumPMF()
    {
        System.out.println( "CDF.sumPMF" );

        ClosedFormDiscreteScalarDistribution<DataType> instance = this.createInstance();
        ProbabilityMassFunction<DataType> pmf = instance.getProbabilityFunction();
        CumulativeDistributionFunction<DataType> cdf = instance.getCDF();
        ArrayList<? extends DataType> samples =
            instance.sample( RANDOM, NUM_SAMPLES );
        Collection<? extends DataType> domain = pmf.getDomain();
        for( DataType sample : samples )
        {
            double cy = cdf.evaluate(sample);
            double sum = 0.0;
            for( DataType x : domain )
            {
                if( x.doubleValue() > sample.doubleValue() )
                {
                    break;
                }
                sum += pmf.evaluate(x);
            }
            assertEquals( sum, cy, TOLERANCE );
        }
        

    }

}
