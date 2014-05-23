/*
 * File:                BetaBinomialDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormIntegerDistributionTestHarness;
import java.util.Collection;

/**
 * Unit tests for BetaBinomialDistributionTest.
 *
 * @author krdixon
 */
public class BetaBinomialDistributionTest
    extends ClosedFormIntegerDistributionTestHarness
{

    /**
     * Tests for class BetaBinomialDistributionTest.
     * @param testName Name of the test.
     */
    public BetaBinomialDistributionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Distribution constructors" );

        BetaBinomialDistribution instance = new BetaBinomialDistribution();
        assertEquals( BetaBinomialDistribution.DEFAULT_N, instance.getN() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SCALE, instance.getScale() );

        int n = RANDOM.nextInt(10) + 10;
        double shape = RANDOM.nextDouble() * 10.0;
        double scale = RANDOM.nextDouble() * 10.0;
        instance = new BetaBinomialDistribution(n, shape, scale);
        assertEquals( n, instance.getN() );
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        BetaBinomialDistribution i2 = new BetaBinomialDistribution(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getN(), i2.getN() );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF.constructors" );

        BetaBinomialDistribution.PMF instance = new BetaBinomialDistribution.PMF();
        assertEquals( BetaBinomialDistribution.DEFAULT_N, instance.getN() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SCALE, instance.getScale() );

        int n = RANDOM.nextInt(10) + 10;
        double shape = RANDOM.nextDouble() * 10.0;
        double scale = RANDOM.nextDouble() * 10.0;
        instance = new BetaBinomialDistribution.PMF(n, shape, scale);
        assertEquals( n, instance.getN() );
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        BetaBinomialDistribution.PMF i2 = new BetaBinomialDistribution.PMF(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getN(), i2.getN() );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF.constructors" );

        BetaBinomialDistribution.CDF instance = new BetaBinomialDistribution.CDF();
        assertEquals( BetaBinomialDistribution.DEFAULT_N, instance.getN() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( BetaBinomialDistribution.DEFAULT_SCALE, instance.getScale() );

        int n = RANDOM.nextInt(10) + 10;
        double shape = RANDOM.nextDouble() * 10.0;
        double scale = RANDOM.nextDouble() * 10.0;
        instance = new BetaBinomialDistribution.CDF(n, shape, scale);
        assertEquals( n, instance.getN() );
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        BetaBinomialDistribution.CDF i2 = new BetaBinomialDistribution.CDF(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getN(), i2.getN() );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }


    /**
     * Test of getShape method, of class BetaBinomialDistribution.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        BetaBinomialDistribution instance = this.createInstance();
        assertTrue( instance.getShape() > 0.0 );
    }

    /**
     * Test of setShape method, of class BetaBinomialDistribution.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        BetaBinomialDistribution instance = this.createInstance();
        double shape = instance.getShape();
        assertTrue( shape > 0.0 );
        shape += RANDOM.nextDouble();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );

        try
        {
            instance.setShape(0.0);
            fail( "Shape must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getScale method, of class BetaBinomialDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        BetaBinomialDistribution instance = this.createInstance();
        assertTrue( instance.getScale() > 0.0 );
    }

    /**
     * Test of setScale method, of class BetaBinomialDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        BetaBinomialDistribution instance = this.createInstance();
        double scale = RANDOM.nextDouble();
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );
        try
        {
            instance.setScale(0.0);
            fail( "scale must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getN method, of class BetaBinomialDistribution.
     */
    public void testGetN()
    {
        System.out.println("getN");
        BetaBinomialDistribution instance = this.createInstance();
        assertTrue( instance.getN() > 0 );
    }

    /**
     * Test of setN method, of class BetaBinomialDistribution.
     */
    public void testSetN()
    {
        System.out.println("setN");
        BetaBinomialDistribution instance = this.createInstance();
        int n = RANDOM.nextInt(11);
        instance.setN(n);
        assertEquals( n, instance.getN() );

        try
        {
            instance.setN(0);
            fail( "n must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public BetaBinomialDistribution createInstance()
    {
        return new BetaBinomialDistribution( 10, 5.0, 8.0 );
    }

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known getDomain" );

        BetaBinomialDistribution instance = this.createInstance();
        Collection<? extends Number> domain = instance.getDomain();
        double n = 0;
        assertEquals( instance.n+1, domain.size() );
        for( Number x : domain )
        {
            assertEquals( n, x.doubleValue() );
            n++;
        }
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF Known values" );

        // http://www.vosesoftware.com/ModelRiskHelp/index.htm#Distributions/Discrete_distributions/Beta-Binomial_distribution.htm
        BetaBinomialDistribution.PMF instance =
            new BetaBinomialDistribution.PMF( 30, 10, 7 );

        assertEquals( 0.08775265, instance.evaluate(18), TOLERANCE );
        assertEquals( 0.014954774, instance.evaluate(26), TOLERANCE );
        assertEquals( 0.021450649, instance.evaluate(10), TOLERANCE );
        assertEquals( 0.061496092, instance.evaluate(14), TOLERANCE );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known vaules" );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        BetaBinomialDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 3, p.getDimensionality() );
        assertEquals( instance.getN(), (int) p.getElement(0) );
        assertEquals( instance.getShape(), p.getElement(1) );
        assertEquals( instance.getScale(), p.getElement(2) );
    }

    @Override
    public void testCDFBoundaryConditions()
    {
//        super.testCDFBoundaryConditions();
    }

    @Override
    public void testCDFConvertFromVector()
    {
//        super.testCDFConvertFromVector();
    }



}


