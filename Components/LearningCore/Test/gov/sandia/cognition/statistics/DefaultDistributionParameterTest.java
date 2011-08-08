/*
 * File:                DefaultDistributionParameterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DefaultDistributionParameterTest.
 *
 * @author krdixon
 */
public class DefaultDistributionParameterTest
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
     * Tests for class DefaultDistributionParameterTest.
     * @param testName Name of the test.
     */
    public DefaultDistributionParameterTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DefaultDistributionParameterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        UnivariateGaussian.PDF gaussian =
            new UnivariateGaussian.PDF( mean, variance );

        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>( gaussian, "mean" );

        assertEquals( mean, parameter.getValue() );

        parameter = new DefaultDistributionParameter<Double,UnivariateGaussian>(
            gaussian, "variance" );
        assertEquals( variance, parameter.getValue() );

        try
        {
            parameter = new DefaultDistributionParameter<Double,UnivariateGaussian>(
                gaussian, "barf" );
            parameter.getValue();
            fail( "shouldn't exist!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            parameter = new DefaultDistributionParameter<Double,UnivariateGaussian>(
                gaussian, "CDF" );
            parameter.getValue();
            fail( "Must have setter and getter!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of clone method, of class DefaultDistributionParameter.
     */
    public void testClone()
    {
        System.out.println("clone");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>(
                gaussian, "variance" );


        DefaultDistributionParameter<Double,UnivariateGaussian> clone = parameter.clone();
        assertEquals( parameter.getName(), clone.getName() );
        assertEquals( parameter.getValue(), clone.getValue() );
        assertNotSame( parameter.getConditionalDistribution(), clone.getConditionalDistribution() );

        gaussian.setMean( RANDOM.nextGaussian() );
        assertEquals( parameter.getValue(), clone.getValue() );

    }

    /**
     * Test of getValue method, of class DefaultDistributionParameter.
     */
    public void testGetValue()
    {
        System.out.println("getValue");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>( gaussian, "mean" );
        assertEquals( gaussian.getMean(), parameter.getValue() );
        gaussian.setMean(RANDOM.nextGaussian());
        assertEquals( gaussian.getMean(), parameter.getValue() );

    }

    /**
     * Test of setValue method, of class DefaultDistributionParameter.
     */
    public void testSetValue()
    {
        System.out.println("setValue");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>( gaussian, "mean" );
        assertEquals( gaussian.getMean(), parameter.getValue() );
        parameter.setValue( RANDOM.nextGaussian() );
        assertEquals( gaussian.getMean(), parameter.getValue() );
    }

    /**
     * Test of getConditionalDistribution method, of class DefaultDistributionParameter.
     */
    public void testGetConditionalDistribution()
    {
        System.out.println("getConditionalDistribution");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>( gaussian, "mean" );
        assertSame( gaussian, parameter.getConditionalDistribution() );
    }

    /**
     * Test of setConditionalDistribution method, of class DefaultDistributionParameter.
     */
    public void testSetConditionalDistribution()
    {
        System.out.println("setConditionalDistribution");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>(
                gaussian, "variance" );
        assertSame( gaussian, parameter.getConditionalDistribution() );

        try
        {
            parameter.setConditionalDistribution(null);
            parameter.getValue();
            fail( "Can't be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        UnivariateGaussian.PDF g2 = new UnivariateGaussian.PDF(
            RANDOM.nextGaussian(), RANDOM.nextDouble() );
        parameter.setConditionalDistribution(g2);
        assertEquals( g2, parameter.getConditionalDistribution() );
        assertEquals( g2.getVariance(), parameter.getValue() );
    }

    /**
     * Test of setName method, of class DefaultDistributionParameter.
     */
    public void testSetName()
    {
        System.out.println("setName");
        UnivariateGaussian.PDF gaussian = new UnivariateGaussian.PDF();
        DefaultDistributionParameter<Double,UnivariateGaussian> parameter =
            new DefaultDistributionParameter<Double,UnivariateGaussian>( gaussian, "variance" );
        assertEquals( gaussian.getVariance(), parameter.getValue() );
        assertNotNull( parameter.parameterGetter );
        assertNotNull( parameter.parameterSetter );

        parameter.setName("mean");
        assertNull( parameter.parameterGetter );
        assertNull( parameter.parameterSetter );
    }

}
