/*
 * File:                RecursiveBayesianEstimatorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 13, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.DistributionWithMean;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for RecursiveBayesianEstimatorTestHarness.
 *
 * @param <ObservationType>
 * @param <ParameterType>
 * @param <BeliefType> 
 * @author krdixon
 */
public abstract class RecursiveBayesianEstimatorTestHarness<ObservationType,ParameterType,BeliefType extends Distribution<ParameterType>>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Default number of samples to draw, {@value}.
     */
    public int NUM_SAMPLES = 100;

    /**
     * Tests for class RecursiveBayesianEstimatorTestHarness.
     * @param testName Name of the test.
     */
    public RecursiveBayesianEstimatorTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new instance
     * @return
     * new instance.
     */
    public abstract RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> createInstance();

    /**
     * Creates the estimated type
     * @return
     * estimate type distribution
     */
    public abstract ClosedFormDistribution<ObservationType> createConditionalDistribution();

    /**
     * Creates data sampled from the conditional distribution.
     * @param conditional Conditional to sample from.
     * @return
     * Sampled data.
     */
    public Collection<? extends ObservationType> createData(
        Distribution<ObservationType> conditional )
    {
        return conditional.sample(RANDOM, NUM_SAMPLES);
    }

    /**
     * 
     * @param <DataType>
     * @param d1
     * @param d2
     * @return
     */
    public <DataType> boolean identical(
        DistributionWithMean<? extends DataType> d1,
        DistributionWithMean<? extends DataType> d2 )
    {

        if( !this.equals( d1.getMean(), d2.getMean() ) )
        {
            return false;
        }

        final int seed = RANDOM.nextInt(100);
        Random r1 = new Random(seed);
        Random r2 = new Random(seed);
        
        return this.identical( d1.sample(r1, NUM_SAMPLES), d2.sample(r2, NUM_SAMPLES) );

    }

    /**
     *
     * @param <DataType>
     * @param s1
     * @param s2
     * @return
     */
    public <DataType> boolean identical(
        Collection<? extends DataType> s1,
        Collection<? extends DataType> s2 )
    {

        if( s1.size() != s2.size() )
        {
            return false;
        }

        Iterator<? extends DataType> i1 = s1.iterator();
        Iterator<? extends DataType> i2 = s2.iterator();
        while( i1.hasNext() )
        {
            if( !this.equals( i1.next(), i2.next() ) )
            {
                return false;
            }
        }
        return true;

    }

    /**
     * Checks for equality
     * @param <DataType>
     * @param o1
     * @param o2
     * @return
     */
    @SuppressWarnings("unchecked")
    public <DataType> boolean equals(
        DataType o1,
        DataType o2 )
    {
        
        if( o1 instanceof Ring )
        {
            return ((Ring) o1).equals( (Ring) o2, TOLERANCE);
        }
        else if( o1 instanceof Number )
        {
            return Math.abs( ((Number) o1).doubleValue() - ((Number) o2).doubleValue() ) <= TOLERANCE;
        }
        else
        {
            return o1.equals( o2 );
        }
    }

    /**
     * Harness methods
     */
    public void testHarnessMethods()
    {
        System.out.println( "Harness Methods" );

        Distribution<ObservationType> conditional =
            this.createConditionalDistribution();
        assertNotNull( conditional );
        Collection<? extends ObservationType> data = this.createData( conditional );
        assertEquals( NUM_SAMPLES, data.size() );
        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> estimator = this.createInstance();
        assertNotNull( estimator );
        assertNotNull( estimator.learn(data) );
    }

    /**
     * Tests the constructors of class RecursiveBayesianEstimatorTestHarness.
     */
    public abstract void testConstructors();

    /**
     * Tests estimation against known values.
     */
    public abstract void testKnownValues();

    /**
     * Test of clone method, of class AbstractRecursiveBayesianEstimator.
     */
    public void testRBEClone()
    {
        System.out.println("RBE clone");
        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> instance = this.createInstance();
        @SuppressWarnings("unchecked")
        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> clone =
            (RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType>) instance.clone();

        assertNotSame( instance, clone );
        assertNotNull( clone );

//        if( instance instanceof Randomized )
//        {
//            int seed = RANDOM.nextInt();
//            Random r1 = new Random( seed );
//            Random r2 = new Random( seed );
//            ((Randomized) instance).setRandom(r1);
//            ((Randomized) clone).setRandom(r2);
//        }

        Distribution<ObservationType> conditional =
            this.createConditionalDistribution();
        Collection<? extends ObservationType> data = this.createData( conditional );

        assertNotSame( instance.createInitialLearnedObject(),
            clone.createInitialLearnedObject() );
        BeliefType i1 = instance.learn(data);
        BeliefType i2 = clone.learn(data);

        assertNotSame( i1, i2 );
//        assertTrue( this.identical(i1, i2) );
        
    }

    /**
     * learn and online learning
     */
    @SuppressWarnings("unchecked")
    public void testRBELearnAndOnlineLearning()
    {
        System.out.println( "RBE Learn and Online Learning" );

        Distribution<ObservationType> conditional =
            this.createConditionalDistribution();
        Collection<? extends ObservationType> data = this.createData( conditional );

        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> instance = this.createInstance();
        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> c1 =
            (RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType>) instance.clone();

        BeliefType p1 = (BeliefType) c1.createInitialLearnedObject().clone();

        for( ObservationType value : data )
        {
            c1.update( p1, value );
        }

        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> c2 =
            (RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType>) instance.clone();

        BeliefType p2 = c2.learn(data);

        // We'll just make sure the means are approximately the same
        ParameterType onlineMean = ((DistributionWithMean<ParameterType>)p1).getMean();
        ParameterType batchMean = ((DistributionWithMean<ParameterType>)p2).getMean();
        System.out.println( "Conditional: " + conditional );
        System.out.println( "Online Mean: " + onlineMean );
        System.out.println( "Batch Mean:  " + batchMean );

        this.identical((DistributionWithMean<ParameterType>)p1, (DistributionWithMean<ParameterType>)p2);

    }

    /**
     * online and batch online learning
     */
    @SuppressWarnings("unchecked")
    public void testRBEOnlineandBatchOnline()
    {
        System.out.println( "RBE Online and Batch Online" );

        Distribution<ObservationType> conditional =
            this.createConditionalDistribution();
        Collection<? extends ObservationType> data = this.createData( conditional );

        RecursiveBayesianEstimator<ObservationType,ParameterType,BeliefType> instance = this.createInstance();

        BeliefType p1 = instance.createInitialLearnedObject();
        BeliefType pclone = (BeliefType) p1.clone();

        for( ObservationType value : data )
        {
            instance.update( p1, value );
        }

        BeliefType p2 = instance.createInitialLearnedObject();
        this.identical((DistributionWithMean<ParameterType>)pclone, (DistributionWithMean<ParameterType>)p2);

        instance.update(p2, data);

        this.identical((DistributionWithMean<ParameterType>)p1, (DistributionWithMean<ParameterType>)p2);

    }

}
