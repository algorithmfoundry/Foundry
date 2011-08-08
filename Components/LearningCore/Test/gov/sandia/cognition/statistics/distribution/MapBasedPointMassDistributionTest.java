/*
 * File:                MapBasedPointMassDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MapBasedPointMassDistributionTest.
 *
 * @author krdixon
 */
public class MapBasedPointMassDistributionTest
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
     * Tests for class MapBasedPointMassDistributionTest.
     * @param testName Name of the test.
     */
    public MapBasedPointMassDistributionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance.
     */
    MapBasedPointMassDistribution<String> createInstance()
    {
        MapBasedPointMassDistribution<String> f =
            new MapBasedPointMassDistribution<String>();
        f.add( "a", 0.5 );
        f.add( "b", 2.0 );
        f.add( "c", 2.5 );
        return f;
    }


    /**
     * Tests the constructors of class MapBasedPointMassDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MapBasedPointMassDistribution<String> instance =
            new MapBasedPointMassDistribution<String>();
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.getTotalMass() );

        instance = new MapBasedPointMassDistribution<String>(2);
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.getTotalMass() );
    }

    /**
     * Test of clone method, of class MapBasedPointMassDistribution.
     */
    public void testClone()
    {
        System.out.println("clone");
        MapBasedPointMassDistribution<String> f = this.createInstance();
        @SuppressWarnings("unchecked")
        MapBasedPointMassDistribution<String> clone = f.clone();
        assertNotSame( clone, f );
        assertNotNull( clone );
        assertEquals( f.getTotalMass(), clone.getTotalMass() );
        assertNotSame( f.getDataMap(), clone.getDataMap() );
        assertEquals( f.getDomain().size(), clone.getDataMap().size() );
        for( String value : f.getDomain() )
        {
            assertEquals( f.getMass(value), clone.getMass(value) );
        }

    }

    /**
     * Test of getMean method, of class MapBasedPointMassDistribution.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        MapBasedPointMassDistribution<String> f = this.createInstance();
        try
        {
            String mean = f.getMean();
            fail( "Mean is not defined" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of sample method, of class MapBasedPointMassDistribution.
     */
    public void testSample()
    {
        System.out.println("sample");
        int numSamples = 10;
        MapBasedPointMassDistribution<String> f = this.createInstance();
        Collection<String> samples = f.sample( RANDOM, numSamples );
        assertEquals( numSamples, samples.size() );
    }

    /**
     * Test of add method, of class MapBasedPointMassDistribution.
     */
    public void testAdd()
    {
        System.out.println("add");

        MapBasedPointMassDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.getMass(value);
            f.add( value );
            assertEquals( 1.0+w1, f.getMass(value) );
            tm += 1.0;
            assertEquals( tm, f.getTotalMass() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        f.add( z );
        assertEquals( 1.0, f.getMass(z) );
        assertEquals( tm+1.0, f.getTotalMass() );
        assertEquals( n0+1, f.getDomain().size() );

    }

    /**
     * Test of add method, of class MapBasedPointMassDistribution.
     */
    public void testAdd_GenericType_double()
    {
        System.out.println("add");

        MapBasedPointMassDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.getMass(value);
            double mass = RANDOM.nextDouble();
            tm += mass;
            f.add( value, mass );
            assertEquals( w1+mass, f.getMass(value) );
            assertEquals( tm, f.getTotalMass() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        f.add( z, 0.0 );
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( n0, f.getDomain().size() );

        double mz = RANDOM.nextDouble();
        tm += mz;
        f.add( z, mz );
        assertEquals( mz, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        assertEquals( n0+1, f.getDomain().size() );

        try
        {
            f.add( z, -1.0 );
            fail( "Weight must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        assertEquals( tm, f.getTotalMass() );

    }

    /**
     * Test of remove method, of class MapBasedPointMassDistribution.
     */
    public void testRemove()
    {
        System.out.println("remove");

        MapBasedPointMassDistribution<String> f = this.createInstance();
        double tm = 5.0;

        String z = "z";
        int nz = f.getDomain().size();
        f.add( z );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( 1.0, f.getMass(z) );
        f.remove(z);
        assertEquals( tm, f.getTotalMass() );
        assertEquals( nz, f.getDomain().size() );
        assertEquals( 0.0, f.getMass(z) );
        f.add( z, 1.0 + RANDOM.nextDouble() );
        assertEquals( nz+1, f.getDomain().size() );
        f.remove(z);
        assertEquals( nz, f.getDomain().size() );

    }

    /**
     * Test of remove method, of class MapBasedPointMassDistribution.
     */
    public void testRemove_GenericType_double()
    {
        System.out.println("remove");
        MapBasedPointMassDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.getMass(value);
            double rm = RANDOM.nextDouble();
            if( w1 > rm )
            {
                tm -= rm;
                f.remove( value, rm );
                double em = w1-rm;
                assertEquals( em, f.getMass(value) );
                assertEquals( tm, f.getTotalMass() );
            }
        }

        String z = "z";
        int nz = f.getDomain().size();
        double value = RANDOM.nextDouble();
        f.add( z, value );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value, f.getMass(z) );

        f.remove( z, 0.0 );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value, f.getMass(z) );

        f.remove(z, value*2.0);
        assertEquals( nz, f.getDomain().size() );
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        f.add( z, value );
        tm += value;
        assertEquals( tm, f.getTotalMass() );
        assertEquals( nz+1, f.getDomain().size() );
        tm -= value/2.0;
        f.remove(z, value/2.0);
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value/2.0, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );

        try
        {
            f.remove( z, -1.0 );
            fail( "Weight must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        assertEquals( tm, f.getTotalMass() );

    }

    /**
     * Test of setMass method, of class MapBasedPointMassDistribution.
     */
    public void testSetMass()
    {
        System.out.println("add");

        MapBasedPointMassDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.getMass(value);
            double mass = RANDOM.nextDouble();
            tm += mass - w1;
            f.setMass( value, mass );
            assertEquals( mass, f.getMass(value) );
            assertEquals( tm, f.getTotalMass() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        f.setMass( z, 0.0 );
        assertEquals( 0.0, f.getMass(z) );
        assertEquals( n0, f.getDomain().size() );

        double mz = RANDOM.nextDouble();
        tm += mz;
        f.setMass( z, mz );
        assertEquals( mz, f.getMass(z) );
        assertEquals( tm, f.getTotalMass() );
        assertEquals( n0+1, f.getDomain().size() );

        boolean exceptionThrown = false;
        try
        {
            f.setMass( z, -0.1 );
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        assertEquals( tm, f.getTotalMass() );

    }

    /**
     * Test of getMass method, of class MapBasedPointMassDistribution.
     */
    public void testGetMass()
    {
        System.out.println("getMass");

        MapBasedPointMassDistribution<String> f = this.createInstance();
        assertEquals( 0.5, f.getMass("a") );
        assertEquals( 2.0, f.getMass("b") );
        assertEquals( 2.5, f.getMass("c") );
        assertEquals( 0.0, f.getMass("z") );

    }

/**
     * Test of getFraction method, of class MapBasedPointMassDistribution.
     */
    public void testGetFraction()
    {
        MapBasedPointMassDistribution<String> instance =
            new MapBasedPointMassDistribution<String>();
        assertEquals(0.0, instance.getFraction("a"));
        assertEquals(0.0, instance.getFraction("b"));
        assertEquals(0.0, instance.getFraction("c"));
        assertEquals(0.0, instance.getFraction("d"));
        
        double epsilon = 0.000000001;

        instance.add("a");
        assertEquals(1 / 1.0, instance.getFraction("a"), epsilon);

        instance.add("a");
        assertEquals(2 / 2.0, instance.getFraction("a"), epsilon);

        instance.add("b");
        assertEquals(2 / 3.0, instance.getFraction("a"), epsilon);
        assertEquals(1 / 3.0, instance.getFraction("b"), epsilon);

        instance.add("c", 4.7);
        assertEquals(2 / 7.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 7.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 7.7, instance.getFraction("c"), epsilon);

        instance.add("a", 2);
        assertEquals(4 / 9.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 9.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 9.7, instance.getFraction("c"), epsilon);

        instance.remove("a", 1.0);
        assertEquals(3 / 8.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 8.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 8.7, instance.getFraction("c"), epsilon);

        instance.remove("c", 3);
        assertEquals(3 / 5.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 5.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 5.7, instance.getFraction("c"), epsilon);

        instance.remove("b", 1);
        assertEquals(3 / 4.7, instance.getFraction("a"), epsilon);
        assertEquals(0 / 4.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 4.7, instance.getFraction("c"), epsilon);

        instance.add("d");
        assertEquals(3 / 5.7, instance.getFraction("a"), epsilon);
        assertEquals(0 / 5.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 5.7, instance.getFraction("c"), epsilon);
        assertEquals(1 / 5.7, instance.getFraction("d"), epsilon);
    }

    /**
     * Test of getMaximumMass method, of class MapBasedPointMassDistribution.
     */
    public void testGetMaximumMass()
    {
        MapBasedPointMassDistribution<String> instance =
            new MapBasedPointMassDistribution<String>();
        assertEquals(0.0, instance.getMaximumMass());

        instance.add("a");
        assertEquals(1.0, instance.getMaximumMass());
        instance.add("b");
        assertEquals(1.0, instance.getMaximumMass());
        instance.add("b");
        assertEquals(2.0, instance.getMaximumMass());
        instance.add("c", 7.4);
        assertEquals(7.4, instance.getMaximumMass());
    }
        
    /**
     * Test of getMaximumValue method, of class MapBasedPointMassDistribution.
     */
    public void testGetMaximumValue()
    {
        MapBasedPointMassDistribution<String> instance =
            new MapBasedPointMassDistribution<String>();
        assertNull(instance.getMaximumValue());
        
        instance.add("a");
        assertEquals("a", instance.getMaximumValue());
        instance.add("b");
        assertTrue("a".equals(instance.getMaximumValue())); // a should be the first value encountered.
        instance.add("b");
        assertEquals("b", instance.getMaximumValue());
        instance.add("c", 7.4);
        assertEquals("c", instance.getMaximumValue());
    }

    /**
     * Test of getMaximumValue method, of class MapBasedPointMassDistribution.
     */
    public void testGetMaximumValues()
    {
        MapBasedPointMassDistribution<String> instance =
            new MapBasedPointMassDistribution<String>();
        assertTrue(instance.getMaximumValues().isEmpty());

        instance.add("a");
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("a"));
        assertTrue(instance.getMaximumValues(0.01).contains("a"));

        instance.add("b");
        assertEquals(2, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("a"));
        assertTrue(instance.getMaximumValues().contains("b"));

        assertEquals(2, instance.getMaximumValues(0.01).size());
        assertTrue(instance.getMaximumValues(0.01).contains("a"));
        assertTrue(instance.getMaximumValues(0.01).contains("b"));

        instance.add("b", 0.01);
        assertEquals(2, instance.getMaximumValues(0.01).size());
        assertTrue(instance.getMaximumValues(0.01).contains("a"));
        assertTrue(instance.getMaximumValues(0.01).contains("b"));

        instance.add("b");
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("b"));

        instance.add("c", 7.4);
        assertEquals(1, instance.getMaximumValues().size());
        assertTrue(instance.getMaximumValues().contains("c"));
        assertEquals(1, instance.getMaximumValues(0.01).size());
        assertTrue(instance.getMaximumValues(0.01).contains("c"));
    }

    /**
     * Test of getDomain method, of class MapBasedPointMassDistribution.
     */
    public void testGetDomain()
    {
        System.out.println("getDomain");
        MapBasedPointMassDistribution<String> instance = this.createInstance();
        assertEquals( 3, instance.getDomain().size() );

    }

    /**
     * Test of getDistributionFunction method, of class MapBasedPointMassDistribution.
     */
    public void testGetDistributionFunction()
    {
        System.out.println("getDistributionFunction");
        MapBasedPointMassDistribution<String> instance = this.createInstance();
        MapBasedPointMassDistribution.PMF<String> pmf =
            instance.getProbabilityFunction();
        assertNotNull( pmf );
        assertNotSame( instance, pmf );
    }

    /**
     * PMF.getDistributionFunction
     */
    public void testPMFGetDistributionFunction()
    {
        System.out.println("PMF.getDistributionFunction");
        MapBasedPointMassDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        assertSame( instance, instance.getProbabilityFunction() );
    }

    /**
     * Test of getEntropy method, of class MapBasedPointMassDistribution.
     */
    public void testPMFGetEntropy()
    {
        System.out.println("getEntropy");
        MapBasedPointMassDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        assertEquals( ProbabilityMassFunctionUtil.getEntropy( instance ), instance.getEntropy() );
    }

    /**
     * Test of evaluate method, of class MapBasedPointMassDistribution.
     */
    public void testPMFEvaluate()
    {
        System.out.println("evaluate");
        MapBasedPointMassDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        for( String value : instance.getDomain() )
        {
            assertEquals( instance.getMass(value)/instance.getTotalMass(), instance.evaluate(value) );
        }

        assertEquals( 0.0, instance.getMass("z") );

        instance = new MapBasedPointMassDistribution.PMF<String>();
        assertEquals( 0.0, instance.getMass("a") );

        instance = new MapBasedPointMassDistribution.PMF<String>();
        assertEquals( 0.0, instance.getMass("z") );
        assertEquals( 0.0, instance.getMass("z") );

    }

    /**
     * Test of getDataMap method, of class MapBasedPointMassDistribution.
     */
    public void testGetDataMap()
    {
        System.out.println("getDataMap");
        MapBasedPointMassDistribution<String> instance = this.createInstance();
        assertNotNull( instance.getDataMap() );

    }

    /**
     * Test of getTotalMass method, of class MapBasedPointMassDistribution.
     */
    public void testGetTotalMass()
    {
        System.out.println("getTotalMass");
        MapBasedPointMassDistribution<String> instance = this.createInstance();
        assertEquals( 5.0, instance.getTotalMass() );

    }

    /**
     * clear()
     */
    public void testClear()
    {
        System.out.println( "clear" );

        MapBasedPointMassDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        assertEquals( 5.0, instance.getTotalMass() );
        assertEquals( 3, instance.getDomain().size() );
        instance.clear();
        assertEquals( 0.0, instance.getTotalMass() );
        assertEquals( 0, instance.getDomain().size() );

        instance.clear();
        assertEquals( 0.0, instance.getTotalMass() );
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.evaluate("z") );

    }

    /**
     * toString()
     */
    public void testToString()
    {
        System.out.println( "toString" );
        MapBasedPointMassDistribution<String> instance = this.createInstance();

        String s = instance.toString();
        System.out.println( "Distribution:\n" + s );
        assertNotNull( s );

    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        MapBasedPointMassDistribution<String> instance = this.createInstance();

        ArrayList<WeightedValue<String>> values =
            new ArrayList<WeightedValue<String>>( instance.getDomain().size() );
       for( String s : instance.getDomain() )
       {
           values.add( new DefaultWeightedValue<String>( s, instance.getMass(s) ) );
       }

        MapBasedPointMassDistribution.Learner<String> learner =
            new MapBasedPointMassDistribution.Learner<String>();

        MapBasedPointMassDistribution.PMF<String> pmf = learner.learn(values);

        assertEquals( instance.getDomain().size(), pmf.getDomain().size() );
        for( String s : instance.getDomain() )
        {
            assertEquals( instance.getMass(s), pmf.getMass(s) );
        }

    }

}
