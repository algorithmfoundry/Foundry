/*
 * File:                ChineseRestaurantProcessTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.MultiCollection;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDiscreteDistributionTestHarness;
import java.util.ArrayList;

/**
 * Unit tests for ChineseRestaurantProcessTest.
 *
 * @author krdixon
 */
public class ChineseRestaurantProcessTest
    extends MultivariateClosedFormComputableDiscreteDistributionTestHarness<Vector>
{

    /**
     * Tests for class ChineseRestaurantProcessTest.
     * @param testName Name of the test.
     */
    public ChineseRestaurantProcessTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance.
     * @return
     * Instance.
     */
    public ChineseRestaurantProcess createInstance()
    {
        double alpha = 1.0;
        int numCustomers = 5;
        return new ChineseRestaurantProcess(alpha, numCustomers);
    }


    /**
     * Tests the constructors of class ChineseRestaurantProcessTest.
     */
    public void testConstructors()
    {
//        System.out.println( "Constructors" );
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }


    @Override
    public void testProbabilityFunctionConstructors()
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void testPMFEvaluate()
    {
//        super.testPMFEvaluate();
    }

    @Override
    public void testPMFChiSquare()
    {
//        super.testPMFChiSquare();
    }

    

    /**
     * Test of getMean method, of class ChineseRestaurantProcess.
     */
    @Override
    public void testGetMean()
    {
        System.out.println("getMean");
        ChineseRestaurantProcess instance = this.createInstance();
        try
        {
            instance.getMean();
            fail( "Mean throws Exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of sample method, of class ChineseRestaurantProcess.
     */
    @Override
    public void testKnownValues()
    {
        System.out.println("sample");
        ChineseRestaurantProcess instance = this.createInstance();
        ArrayList<Vector> samples = instance.sample(RANDOM,20);
        for( int n = 0; n < samples.size(); n++ )
        {
            System.out.println( n + ": Tables = " + samples.get(n).getDimensionality()
                + " Assignments = " + samples.get(n) );
        }
    }

    /**
     * Test of getAlpha method, of class ChineseRestaurantProcess.
     */
    public void testGetAlpha()
    {
        System.out.println("getAlpha");
        ChineseRestaurantProcess instance = this.createInstance();
        assertTrue( instance.getAlpha() > 0.0 );
    }

    /**
     * Test of setAlpha method, of class ChineseRestaurantProcess.
     */
    public void testSetAlpha()
    {
        System.out.println("setAlpha");
        ChineseRestaurantProcess instance = this.createInstance();
        double alpha = RANDOM.nextDouble() * 10.0;
        instance.setAlpha(alpha);
        assertEquals( alpha, instance.getAlpha() );

        try
        {
            instance.setAlpha(0.0);
            fail( "Alpha must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getNumCustomers method, of class ChineseRestaurantProcess.
     */
    public void testGetNumCustomers()
    {
        System.out.println("getNumCustomers");
        ChineseRestaurantProcess instance = this.createInstance();
        assertTrue( instance.getNumCustomers() > 0 );
    }

    /**
     * Test of setNumCustomers method, of class ChineseRestaurantProcess.
     */
    public void testSetNumCustomers()
    {
        System.out.println("setNumCustomers");
        ChineseRestaurantProcess instance = this.createInstance();
        int numCustomers = instance.getNumCustomers() + 1;
        instance.setNumCustomers(numCustomers);
        assertEquals( numCustomers, instance.getNumCustomers() );
        try
        {
            instance.setNumCustomers(0);
            fail( "numCustomers must be > 0 " );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testPMFGetInputDimensionality()
    {
        // We need to nerf this test because our dimensionality varies...
//        super.testPMFGetInputDimensionality();
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "ProbabilityFunctionKnownValues" );

        // http://www.cs.princeton.edu/courses/archive/fall07/cos597C/scribe/20070921.pdf
        Vector x = VectorFactory.getDefault().copyValues( 3.0, 4.0, 3.0 );
        double alpha = RANDOM.nextDouble();
        ChineseRestaurantProcess.PMF instance =
            new ChineseRestaurantProcess( alpha, 10 ).getProbabilityFunction();
        double expected = 1.0 * alpha/(1.0+alpha) * 1.0/(2.0+alpha) * alpha/(3.0+alpha) * 1.0/(4.0+alpha) * 1.0/(5.0+alpha)
            * 2.0/(6.0+alpha) * 2.0/(7.0+alpha) * 2.0/(8.0+alpha) * 3.0/(9.0+alpha);
        assertEquals( expected, instance.evaluate(x), 1e-10 );

        instance.setAlpha(alpha);
        instance.setNumCustomers(4);
        expected = 1.0 * 1.0/(1.0+alpha) * 2.0/(2.0+alpha) * 3.0/(3.0+alpha);
        x = VectorFactory.getDefault().copyValues(4.0);
        assertEquals( expected, instance.evaluate(x), 1e-10 );

        expected = 1.0 * alpha/(1.0+alpha) * alpha/(2.0+alpha) * alpha/(3.0+alpha);
        x = VectorFactory.getDefault().copyValues( 1.0, 1.0, 1.0, 1.0 );
        assertEquals( expected, instance.evaluate(x), 1e-10 );

//        alpha = 1.5;
        alpha = 1e-10;
        instance.setAlpha(alpha);
        instance.setNumCustomers(4);
        x = VectorFactory.getDefault().copyValues( 3.0, 1.0 );
        expected = 1.0 * 1.0/(1.0+alpha) * 2.0/(2.0+alpha) * alpha/(3.0+alpha);
        System.out.println( "Expected: " + expected + ", x=" + x );
        assertEquals( expected, instance.evaluate(x), 1e-10 );


    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        ChineseRestaurantProcess instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getAlpha(), p.getElement(0) );
        assertEquals( (double) instance.getNumCustomers(), p.getElement(1) );
    }

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known Domain" );

        ChineseRestaurantProcess.PMF instance =
            this.createInstance().getProbabilityFunction();
        instance.setAlpha(2.0);
        MultiCollection<Vector> domain = instance.getDomain();
        
        ArrayList<Vector> samples = instance.sample(RANDOM, 1000);
        MapBasedDataHistogram<Vector> hist = new MapBasedDataHistogram<Vector>( instance.getNumCustomers() );
        for( Vector sample : samples )
        {
            hist.add( sample );
        }

        double sum = 0.0;
        for( Vector d : domain )
        {
            double p = instance.evaluate(d);
            if( p > 0.0 )
            {
                System.out.printf( "SAMPLE = %.4e, PMF = %.4e, ratio = %.1f  ", hist.evaluate(d), p, hist.evaluate(d)/p );
                System.out.println( d );
            }
            else
            {
//                System.out.println( "BAD: " + d );
            }
            sum += p;
        }


        


        System.out.println( "Sum = " + sum );


    }

}
