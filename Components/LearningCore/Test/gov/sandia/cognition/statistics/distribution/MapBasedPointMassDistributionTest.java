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

import gov.sandia.cognition.statistics.DataDistribution;
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
    DefaultDataDistribution<String> createInstance()
    {
        DefaultDataDistribution<String> f =
            new DefaultDataDistribution<String>();
        f.increment( "a", 0.5 );
        f.increment( "b", 2.0 );
        f.increment( "c", 2.5 );
        return f;
    }


    /**
     * Tests the constructors of class MapBasedPointMassDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.getTotal() );

        instance = new DefaultDataDistribution<String>(2);
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.getTotal() );
    }

    /**
     * Test of clone method, of class DefaultDataDistribution.
     */
    public void testClone()
    {
        System.out.println("clone");
        DefaultDataDistribution<String> f = this.createInstance();
        @SuppressWarnings("unchecked")
        DefaultDataDistribution<String> clone = f.clone();
        assertNotSame( clone, f );
        assertNotNull( clone );
        assertEquals( f.getTotal(), clone.getTotal() );
        assertNotSame( f.asMap(), clone.asMap() );
        assertEquals( f.getDomain().size(), clone.asMap().size() );
        for( String value : f.getDomain() )
        {
            assertEquals( f.get(value), clone.get(value) );
        }

    }

    /**
     * Test of sample method, of class DefaultDataDistribution.
     */
    public void testSample()
    {
        System.out.println("sample");
        int numSamples = 10;
        DefaultDataDistribution<String> f = this.createInstance();
        Collection<String> samples = f.sample( RANDOM, numSamples );
        assertEquals( numSamples, samples.size() );
    }

    /**
     * Test of add method, of class DefaultDataDistribution.
     */
    public void testAdd()
    {
        System.out.println("add");

        DefaultDataDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.get(value);
            f.increment( value );
            assertEquals( 1.0+w1, f.get(value) );
            tm += 1.0;
            assertEquals( tm, f.getTotal() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.get(z) );
        assertEquals( tm, f.getTotal() );
        f.increment( z );
        assertEquals( 1.0, f.get(z) );
        assertEquals( tm+1.0, f.getTotal() );
        assertEquals( n0+1, f.getDomain().size() );

    }

    /**
     * Test of add method, of class DefaultDataDistribution.
     */
    public void testAdd_GenericType_double()
    {
        System.out.println("add");

        DefaultDataDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.get(value);
            double mass = RANDOM.nextDouble();
            tm += mass;
            f.increment( value, mass );
            assertEquals( w1+mass, f.get(value) );
            assertEquals( tm, f.getTotal() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.get(z) );
        assertEquals( tm, f.getTotal() );
        f.increment( z, 0.0 );
        assertEquals( 0.0, f.get(z) );
        assertEquals( n0, f.getDomain().size() );

        double mz = RANDOM.nextDouble();
        tm += mz;
        f.increment( z, mz );
        assertEquals( mz, f.get(z) );
        assertEquals( tm, f.getTotal() );
        assertEquals( n0+1, f.getDomain().size() );

        f.increment(z);
        double before = f.get(z);
        f.increment( z, -1.0 );
        assertEquals( before-1.0, f.get(z) );
        assertEquals( tm, f.getTotal() );

    }

    /**
     * Test of remove method, of class DefaultDataDistribution.
     */
    public void testRemove()
    {
        System.out.println("remove");

        DefaultDataDistribution<String> f = this.createInstance();
        double tm = 5.0;

        String z = "z";
        int nz = f.getDomain().size();
        f.increment( z );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( 1.0, f.get(z) );
        f.decrement(z);
        assertEquals( tm, f.getTotal() );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( 0.0, f.get(z) );
        f.compact();
        assertEquals( nz, f.getDomain().size() );
        assertEquals( 0.0, f.get(z) );
        f.increment( z, 1.0 + RANDOM.nextDouble() );
        assertEquals( nz+1, f.getDomain().size() );
        f.decrement(z);
        assertEquals( nz+1, f.getDomain().size() );
        f.compact();
        assertEquals( nz+1, f.getDomain().size() );

    }

    /**
     * Test of remove method, of class DefaultDataDistribution.
     */
    public void testRemove_GenericType_double()
    {
        System.out.println("remove");
        DefaultDataDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.get(value);
            double rm = RANDOM.nextDouble();
            if( w1 > rm )
            {
                tm -= rm;
                f.decrement( value, rm );
                double em = w1-rm;
                assertEquals( em, f.get(value) );
                assertEquals( tm, f.getTotal() );
            }
        }

        String z = "z";
        int nz = f.getDomain().size();
        double value = RANDOM.nextDouble();
        f.increment( z, value );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value, f.get(z) );

        f.decrement( z, 0.0 );
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value, f.get(z) );

        f.decrement(z, value*2.0);
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( 0.0, f.get(z) );
        f.compact();
        assertEquals( nz, f.getDomain().size() );
        assertEquals( 0.0, f.get(z) );

        assertEquals( tm, f.getTotal() );
        f.increment( z, value );
        tm += value;
        assertEquals( tm, f.getTotal() );
        assertEquals( nz+1, f.getDomain().size() );
        tm -= value/2.0;
        f.decrement(z, value/2.0);
        assertEquals( nz+1, f.getDomain().size() );
        assertEquals( value/2.0, f.get(z) );
        assertEquals( tm, f.getTotal() );

        f.increment(z,1.0);
        double before = f.get(z);
        f.decrement( z, -1.0 );
        assertEquals( before+1.0, f.get(z) );
        double expected = f.getTotal() - f.get(z);
        f.decrement(z,1000.0);
        assertEquals( 0.0, f.get(z) );
        assertEquals( expected, f.getTotal() );

    }

    /**
     * Test of setMass method, of class DefaultDataDistribution.
     */
    public void testSetMass()
    {
        System.out.println("add");

        DefaultDataDistribution<String> f = this.createInstance();
        double tm = 5.0;
        for( String value : f.getDomain() )
        {
            double w1 = f.get(value);
            double mass = RANDOM.nextDouble();
            tm += mass - w1;
            f.set( value, mass );
            assertEquals( mass, f.get(value) );
            assertEquals( tm, f.getTotal() );
        }

        int n0 = f.getDomain().size();
        String z = "z";
        assertEquals( 0.0, f.get(z) );
        assertEquals( tm, f.getTotal() );
        f.set( z, 0.0 );
        assertEquals( 0.0, f.get(z) );
        assertEquals( n0, f.getDomain().size() );

        double mz = RANDOM.nextDouble();
        tm += mz;
        f.set( z, mz );
        assertEquals( mz, f.get(z) );
        assertEquals( tm, f.getTotal() );
        assertEquals( n0+1, f.getDomain().size() );

        tm -= mz;
        f.set( z, -0.1 );
        assertEquals( 0.0, f.get(z) );
        assertEquals( tm, f.getTotal() );

    }
    
    public void testSetBelowZeroBug()
    {
        DefaultDataDistribution<String> f = new DefaultDataDistribution<>();
        
        f.set("a", 3);
        f.set("b", 4);
        assertEquals(7, f.getTotal(), 0.0);
        
        f.set("b", 2);
        assertEquals(5, f.getTotal(), 0.0);
        
        f.set("b", 0);
        assertEquals(3, f.getTotal(), 0.0);

        f.set("b", 5);
        assertEquals(8, f.getTotal(), 0.0);
        
        f.set("b", -4);
        assertEquals(3, f.getTotal(), 0.0);
    }

    /**
     * Test of getMass method, of class DefaultDataDistribution.
     */
    public void testget()
    {
        System.out.println("getMass");

        DefaultDataDistribution<String> f = this.createInstance();
        assertEquals( 0.5, f.get("a") );
        assertEquals( 2.0, f.get("b") );
        assertEquals( 2.5, f.get("c") );
        assertEquals( 0.0, f.get("z") );

    }

/**
     * Test of getFraction method, of class DefaultDataDistribution.
     */
    public void testGetFraction()
    {
        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertEquals(0.0, instance.getFraction("a"));
        assertEquals(0.0, instance.getFraction("b"));
        assertEquals(0.0, instance.getFraction("c"));
        assertEquals(0.0, instance.getFraction("d"));
        
        double epsilon = 0.000000001;

        instance.increment("a");
        assertEquals(1 / 1.0, instance.getFraction("a"), epsilon);

        instance.increment("a");
        assertEquals(2 / 2.0, instance.getFraction("a"), epsilon);

        instance.increment("b");
        assertEquals(2 / 3.0, instance.getFraction("a"), epsilon);
        assertEquals(1 / 3.0, instance.getFraction("b"), epsilon);

        instance.increment("c", 4.7);
        assertEquals(2 / 7.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 7.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 7.7, instance.getFraction("c"), epsilon);

        instance.increment("a", 2);
        assertEquals(4 / 9.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 9.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 9.7, instance.getFraction("c"), epsilon);

        instance.decrement("a", 1.0);
        assertEquals(3 / 8.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 8.7, instance.getFraction("b"), epsilon);
        assertEquals(4.7 / 8.7, instance.getFraction("c"), epsilon);

        instance.decrement("c", 3);
        assertEquals(3 / 5.7, instance.getFraction("a"), epsilon);
        assertEquals(1 / 5.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 5.7, instance.getFraction("c"), epsilon);

        instance.decrement("b", 1);
        assertEquals(3 / 4.7, instance.getFraction("a"), epsilon);
        assertEquals(0 / 4.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 4.7, instance.getFraction("c"), epsilon);

        instance.increment("d");
        assertEquals(3 / 5.7, instance.getFraction("a"), epsilon);
        assertEquals(0 / 5.7, instance.getFraction("b"), epsilon);
        assertEquals(1.7 / 5.7, instance.getFraction("c"), epsilon);
        assertEquals(1 / 5.7, instance.getFraction("d"), epsilon);
    }

    /**
     * Test of getMaxValue method, of class DefaultDataDistribution.
     */
    public void testGetMaxValue()
    {
        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertEquals(0.0, instance.getMaxValue());

        instance.increment("a");
        assertEquals(1.0, instance.getMaxValue());
        instance.increment("b");
        assertEquals(1.0, instance.getMaxValue());
        instance.increment("b");
        assertEquals(2.0, instance.getMaxValue());
        instance.increment("c", 7.4);
        assertEquals(7.4, instance.getMaxValue());
    }
        
    /**
     * Test of getMaxValueKey method, of class DefaultDataDistribution.
     */
    public void testgetMaxValueKey()
    {
        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertNull(instance.getMaxValueKey());
        
        instance.increment("a");
        assertEquals("a", instance.getMaxValueKey());
        instance.increment("b");
        assertTrue("a".equals(instance.getMaxValueKey())); // a should be the first value encountered.
        instance.increment("b");
        assertEquals("b", instance.getMaxValueKey());
        instance.increment("c", 7.4);
        assertEquals("c", instance.getMaxValueKey());
    }

    /**
     * Test of getMaxValueKey method, of class DefaultDataDistribution.
     */
    public void testgetMaxValueKeys()
    {
        DefaultDataDistribution<String> instance =
            new DefaultDataDistribution<String>();
        assertTrue(instance.getMaxValueKeys().isEmpty());

        instance.increment("a");
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("a"));

        instance.increment("b");
        assertEquals(2, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("a"));
        assertTrue(instance.getMaxValueKeys().contains("b"));

        instance.increment("b");
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("b"));

        instance.increment("c", 7.4);
        assertEquals(1, instance.getMaxValueKeys().size());
        assertTrue(instance.getMaxValueKeys().contains("c"));
    }

    /**
     * Test of getDomain method, of class DefaultDataDistribution.
     */
    public void testGetDomain()
    {
        System.out.println("getDomain");
        DefaultDataDistribution<String> instance = this.createInstance();
        assertEquals( 3, instance.getDomain().size() );

    }

    /**
     * Test of getDistributionFunction method, of class DefaultDataDistribution.
     */
    public void testGetDistributionFunction()
    {
        System.out.println("getDistributionFunction");
        DefaultDataDistribution<String> instance = this.createInstance();
        DataDistribution.PMF<String> pmf = instance.getProbabilityFunction();
        assertNotNull( pmf );
        assertNotSame( instance, pmf );
    }

    /**
     * PMF.getDistributionFunction
     */
    public void testPMFGetDistributionFunction()
    {
        System.out.println("PMF.getDistributionFunction");
        DefaultDataDistribution.PMF<String> instance =
            (DefaultDataDistribution.PMF<String>) this.createInstance().getProbabilityFunction();
        assertSame( instance, instance.getProbabilityFunction() );
    }

    /**
     * Test of getEntropy method, of class DefaultDataDistribution.
     */
    public void testPMFGetEntropy()
    {
        System.out.println("getEntropy");
        DataDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        assertEquals( ProbabilityMassFunctionUtil.getEntropy( instance ), instance.getEntropy() );
    }

    /**
     * Test of evaluate method, of class DefaultDataDistribution.
     */
    public void testPMFEvaluate()
    {
        System.out.println("evaluate");
        DataDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        for( String value : instance.getDomain() )
        {
            assertEquals( instance.get(value)/instance.getTotal(), instance.evaluate(value) );
        }

        assertEquals( 0.0, instance.get("z") );

        instance = new DefaultDataDistribution.PMF<String>();
        assertEquals( 0.0, instance.get("a") );

        instance = new DefaultDataDistribution.PMF<String>();
        assertEquals( 0.0, instance.get("z") );
        assertEquals( 0.0, instance.get("z") );

    }

    /**
     * Test of asMap method, of class DefaultDataDistribution.
     */
    public void testasMap()
    {
        System.out.println("asMap");
        DefaultDataDistribution<String> instance = this.createInstance();
        assertNotNull( instance.asMap() );

    }

    /**
     * Test of getTotal method, of class DefaultDataDistribution.
     */
    public void testgetTotal()
    {
        System.out.println("getTotal");
        DefaultDataDistribution<String> instance = this.createInstance();
        assertEquals( 5.0, instance.getTotal() );

    }

    /**
     * clear()
     */
    public void testClear()
    {
        System.out.println( "clear" );

        DataDistribution.PMF<String> instance =
            this.createInstance().getProbabilityFunction();
        assertEquals( 5.0, instance.getTotal() );
        assertEquals( 3, instance.getDomain().size() );
        instance.clear();
        assertEquals( 0.0, instance.getTotal() );
        assertEquals( 0, instance.getDomain().size() );

        instance.clear();
        assertEquals( 0.0, instance.getTotal() );
        assertEquals( 0, instance.getDomain().size() );
        assertEquals( 0.0, instance.evaluate("z") );

    }

    /**
     * toString()
     */
    public void testToString()
    {
        System.out.println( "toString" );
        DefaultDataDistribution<String> instance = this.createInstance();

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

        DefaultDataDistribution<String> instance = this.createInstance();

        ArrayList<WeightedValue<String>> values =
            new ArrayList<WeightedValue<String>>( instance.getDomain().size() );
       for( String s : instance.getDomain() )
       {
           values.add( new DefaultWeightedValue<String>( s, instance.get(s) ) );
       }

        DefaultDataDistribution.WeightedEstimator<String> learner =
            new DefaultDataDistribution.WeightedEstimator<String>();

        DefaultDataDistribution<String> pmf = learner.learn(values);

        assertEquals( instance.getDomain().size(), pmf.getDomain().size() );
        for( String s : instance.getDomain() )
        {
            assertEquals( instance.get(s), pmf.get(s) );
        }

    }

}
