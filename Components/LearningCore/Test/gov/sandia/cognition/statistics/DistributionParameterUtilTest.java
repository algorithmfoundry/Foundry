/*
 * File:                DistributionParameterUtilTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DistributionParameterUtilTest.
 *
 * @author krdixon
 */
public class DistributionParameterUtilTest
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
     * Tests for class DistributionParameterUtilTest.
     * @param testName Name of the test.
     */
    public DistributionParameterUtilTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class DistributionParameterUtilTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        DistributionParameterUtil instance = new DistributionParameterUtil();
        assertNotNull( instance );
    }

    /**
     * Test of findAll method, of class DistributionParameterUtil.
     * @throws Exception Can't introspect
     */
    public void testFindAll()
        throws Exception
    {
        System.out.println("findAll");
        UnivariateGaussian g = new UnivariateGaussian(
            RANDOM.nextGaussian(), RANDOM.nextDouble() );
        Collection<DistributionParameter<?,UnivariateGaussian>> result =
            DistributionParameterUtil.findAll(g);
        assertEquals( 2, result.size() );
        assertEquals( "mean", CollectionUtil.getElement(result, 0).getName() );
        assertEquals( g.getMean(), CollectionUtil.getElement(result, 0).getValue() );
        assertEquals( "variance", CollectionUtil.getElement(result, 1).getName() );
        assertEquals( g.getVariance(), CollectionUtil.getElement(result, 1).getValue() );

        BetaDistribution beta = new BetaDistribution(
            RANDOM.nextDouble(), RANDOM.nextDouble() );
        Collection<DistributionParameter<?,BetaDistribution>> result2 =
            DistributionParameterUtil.findAll(beta);
        assertEquals( 2, result.size() );
        assertEquals( "alpha", CollectionUtil.getElement(result2, 0).getName() );
        assertEquals( beta.getAlpha(), CollectionUtil.getElement(result2, 0).getValue() );
        assertEquals( "beta", CollectionUtil.getElement(result2, 1).getName() );
        assertEquals( beta.getBeta(), CollectionUtil.getElement(result2, 1).getValue() );

        try
        {
            DistributionParameterUtil.findAll(null);
            fail( "Can't do null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
