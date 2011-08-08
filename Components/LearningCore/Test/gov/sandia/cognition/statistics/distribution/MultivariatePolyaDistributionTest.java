/*
 * File:                MultivariatePolyaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 15, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDiscreteDistributionTestHarness;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Unit tests for MultivariatePolyaDistributionTest.
 *
 * @author krdixon
 */
public class MultivariatePolyaDistributionTest
    extends MultivariateClosedFormComputableDiscreteDistributionTestHarness<Vector>
{

    /**
     * Tests for class MultivariatePolyaDistributionTest.
     * @param testName Name of the test.
     */
    public MultivariatePolyaDistributionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public MultivariatePolyaDistribution createInstance()
    {
        final double r = 10.0;

        int N = 6;
        Vector a = VectorFactory.getDefault().copyValues(
            r*RANDOM.nextDouble(), r*RANDOM.nextDouble(), r*RANDOM.nextDouble() );
        return new MultivariatePolyaDistribution( a, N );
    }

    /**
     * Tests the constructors of class MultivariatePolyaDistributionTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariatePolyaDistribution instance = new MultivariatePolyaDistribution();
        assertEquals( MultivariatePolyaDistribution.DEFAULT_DIMENSIONALITY,
            instance.getInputDimensionality() );
        assertEquals( MultivariatePolyaDistribution.DEFAULT_NUM_TRIALS,
            instance.getNumTrials() );

        int dim = RANDOM.nextInt(100) + 10;
        int numTrials = RANDOM.nextInt(100) + 10;
        instance = new MultivariatePolyaDistribution( dim, numTrials );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( numTrials, instance.getNumTrials() );

        Vector p = VectorFactory.getDefault().createUniformRandom(
            dim, 0.0,10.0, RANDOM );
        instance = new MultivariatePolyaDistribution( p, numTrials );
        assertSame( p, instance.getParameters() );
        assertEquals( numTrials, instance.getNumTrials() );

        MultivariatePolyaDistribution i2 = new MultivariatePolyaDistribution(instance);
        assertNotSame( i2.getParameters(), instance.getParameters() );
        assertEquals( i2.getParameters(), instance.getParameters() );
        assertEquals( i2.getNumTrials(), instance.getNumTrials() );

    }

    /**
     * PMF constructors
     */
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PMF Constructors" );

        MultivariatePolyaDistribution.PMF instance = new MultivariatePolyaDistribution.PMF();
        assertEquals( MultivariatePolyaDistribution.DEFAULT_DIMENSIONALITY,
            instance.getInputDimensionality() );
        assertEquals( MultivariatePolyaDistribution.DEFAULT_NUM_TRIALS,
            instance.getNumTrials() );

        int dim = RANDOM.nextInt(100) + 10;
        int numTrials = RANDOM.nextInt(100) + 10;
        instance = new MultivariatePolyaDistribution.PMF( dim, numTrials );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( numTrials, instance.getNumTrials() );

        Vector p = VectorFactory.getDefault().createUniformRandom(
            dim, 0.0,10.0, RANDOM );
        instance = new MultivariatePolyaDistribution.PMF( p, numTrials );
        assertSame( p, instance.getParameters() );
        assertEquals( numTrials, instance.getNumTrials() );

        MultivariatePolyaDistribution.PMF i2 =
            new MultivariatePolyaDistribution.PMF(instance);
        assertNotSame( i2.getParameters(), instance.getParameters() );
        assertEquals( i2.getParameters(), instance.getParameters() );
        assertEquals( i2.getNumTrials(), instance.getNumTrials() );

    }

    /**
     * Test of getNumTrials method, of class MultivariatePolyaDistribution.
     */
    public void testGetNumTrials()
    {
        System.out.println("getNumTrials");
        int numTrials = RANDOM.nextInt(100) + 1;
        MultivariatePolyaDistribution instance = this.createInstance();
        instance.setNumTrials(numTrials);
        assertEquals( numTrials, instance.getNumTrials() );
    }

    @Override
    public void testGetMean()
    {
        int temp = NUM_SAMPLES;
        NUM_SAMPLES = 1000;
        double t = TOLERANCE;
        TOLERANCE = 1e-1;
        super.testGetMean();
        TOLERANCE = t;
        NUM_SAMPLES = temp;
    }

    /**
     * Test of setNumTrials method, of class MultivariatePolyaDistribution.
     */
    public void testSetNumTrials()
    {
        System.out.println("setNumTrials");
        int numTrials = RANDOM.nextInt(100) + 1;
        MultivariatePolyaDistribution instance = this.createInstance();
        instance.setNumTrials(numTrials);
        assertEquals( numTrials, instance.getNumTrials() );
        try
        {
            instance.setNumTrials(0);
            fail( "numTrials must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getInputDimensionality method, of class MultivariatePolyaDistribution.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        MultivariatePolyaDistribution instance = this.createInstance();
        assertEquals( instance.getInputDimensionality(),
            instance.getParameters().getDimensionality() );
    }

    /**
     * Test of getParameters method, of class MultivariatePolyaDistribution.
     */
    public void testGetParameters()
    {
        System.out.println("getParameters");
        MultivariatePolyaDistribution instance = this.createInstance();
        Vector p = instance.getParameters().scale( 2.0 );
        instance.setParameters(p);
        assertEquals( p, instance.getParameters() );
    }

    /**
     * Test of setParameters method, of class MultivariatePolyaDistribution.
     */
    public void testSetParameters()
    {
        System.out.println("setParameters");
        MultivariatePolyaDistribution instance = this.createInstance();
        Vector p = instance.getParameters().scale( 2.0 );
        instance.setParameters(p);
        assertEquals( p, instance.getParameters() );

        p.setElement(0, 0.0);
        try
        {
            instance.setParameters(p);
            fail( "Parameters must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setParameters(null);
            fail( "Cannot have null parameters" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setParameters(VectorFactory.getDefault().copyValues(1.0));
            fail( "Must have dimensionality >= 2" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getDomain method, of class MultivariatePolyaDistribution.
     */
    public void testKnownGetDomain()
    {
        System.out.println("getDomain");
        MultivariatePolyaDistribution instance = this.createInstance();

        int count = 0;
        MultinomialDistribution.Domain domain = instance.getDomain();
        System.out.println( "Enumerating all subsets: " + domain.size() );
        for( Vector subset : domain )
        {
            System.out.println( "Subset: " + subset );
            count++;
        }

        assertEquals( count, domain.size() );

        Iterator<Vector> iterator = domain.iterator();
        try
        {
            iterator.remove();
            fail( "Cannot remove" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            iterator = new MultinomialDistribution.Domain( 0, 10 ).iterator();
            fail( "numClasses must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            iterator = new MultinomialDistribution.Domain( 3, -1 ).iterator();
            fail( "numTrials must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );
        MultivariatePolyaDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( instance.getInputDimensionality(), p.getDimensionality() );
        assertNotSame( p, instance.getParameters() );
        assertEquals( p, instance.getParameters() );
    }

    @Override
    public void testKnownValues()
    {
    }
    
    /**
     * Chi-Square
     */
    public void testChiSquare()
    {
        System.out.println( "Chi-Square" );

        MultivariatePolyaDistribution.PMF pmf =
            new MultivariatePolyaDistribution.PMF(
                VectorFactory.getDefault().copyValues(1.0, 200.0, 300.0 ), 100 );
        ArrayList<Vector> samples = pmf.sample(RANDOM,NUM_SAMPLES);
        MultinomialDistribution.PMF mnd =
            new MultinomialDistribution.PMF( pmf.getParameters().scale( 1.0 / pmf.getParameters().norm1() ), pmf.getNumTrials() );
//        for( int i = 0; i < samples.size(); i++ )
//        {
//            double p1 = pmf.evaluate(samples.get(i));
//            double p2 = mnd.evaluate(samples.get(i));
//            System.out.println( "Delta = " + (p1-p2) + ", P(" + samples.get(i) + ") = " + p1 + ", MND = " + p2 );
//        }
//        Vector bad = VectorFactory.getDefault().copyValues(4,40,56);
//        System.out.println( "MPD: P(bad) = " + pmf.evaluate(bad) );
//        System.out.println( "MND: P(bad) = " + mnd.evaluate(bad) );
//        ChiSquareConfidence.Statistic chisquare =
//            ChiSquareConfidence.evaluateNullHypothesis( Arrays.asList(bad), pmf );
//        System.out.println( "Chi-Square: " + chisquare );        

    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}
