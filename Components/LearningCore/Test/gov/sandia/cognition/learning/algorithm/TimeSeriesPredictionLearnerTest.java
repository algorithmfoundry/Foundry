/*
 * File:                TimeSeriesPredictionLearnerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.algorithm.nearest.KNearestNeighborExhaustive;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for TimeSeriesPredictionLearnerTest.
 *
 * @author krdixon
 */
public class TimeSeriesPredictionLearnerTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Tests for class TimeSeriesPredictionLearnerTest.
     * @param testName Name of the test.
     */
    public TimeSeriesPredictionLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getPredictionHorizon method, of class TimeSeriesPredictionLearner.
     */
    public void testGetPredictionHorizon()
    {
        System.out.println( "getPredictionHorizon" );
        TimeSeriesPredictionLearner instance = new TimeSeriesPredictionLearner();
        assertEquals( TimeSeriesPredictionLearner.DEFAULT_PREDICTION_HORIZON, instance.getPredictionHorizon() );
    }

    /**
     * Test of setPredictionHorizon method, of class TimeSeriesPredictionLearner.
     */
    public void testSetPredictionHorizon()
    {
        System.out.println( "setPredictionHorizon" );
        TimeSeriesPredictionLearner instance = new TimeSeriesPredictionLearner();
        assertEquals( TimeSeriesPredictionLearner.DEFAULT_PREDICTION_HORIZON, instance.getPredictionHorizon() );
        final int p2 = 10;
        instance.setPredictionHorizon( p2 );
        assertEquals( p2, instance.getPredictionHorizon() );
    }

    /**
     * Test of getSupervisedLearner method, of class TimeSeriesPredictionLearner.
     */
    public void testGetSupervisedLearner()
    {
        System.out.println( "getSupervisedLearner" );
        TimeSeriesPredictionLearner instance = new TimeSeriesPredictionLearner();
        assertNull( instance.getSupervisedLearner() );
    }

    /**
     * Test of setSupervisedLearner method, of class TimeSeriesPredictionLearner.
     */
    @SuppressWarnings("unchecked")
    public void testSetSupervisedLearner()
    {
        System.out.println( "setSupervisedLearner" );
        TimeSeriesPredictionLearner instance = new TimeSeriesPredictionLearner();
        assertNull( instance.getSupervisedLearner() );

        SupervisedBatchLearner learner = new KNearestNeighborExhaustive.Learner();
        instance.setSupervisedLearner( learner );
        assertSame( learner, instance.getSupervisedLearner() );
    }

    /**
     * Test of learn method, of class TimeSeriesPredictionLearner.
     */
    public void testLearn()
    {
        System.out.println( "learn" );
        final int predictionHorizon = 2;
        KNearestNeighborExhaustive.Learner<Double,Double> learner =
            new KNearestNeighborExhaustive.Learner<Double,Double>();
        TimeSeriesPredictionLearner<Double,Double,KNearestNeighborExhaustive<Double,Double>> instance =
            new TimeSeriesPredictionLearner<Double,Double,KNearestNeighborExhaustive<Double,Double>>( predictionHorizon, learner );

        @SuppressWarnings("unchecked")
        final List<DefaultInputOutputPair<Double,Double>> data = Arrays.asList(
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() )
        );

        KNearestNeighborExhaustive<Double,Double> result = instance.learn( data );
        assertEquals( data.size()-predictionHorizon, result.getData().size() );

    }

    /**
     * Test of createPredictionDataset method, of class TimeSeriesPredictionLearner.
     */
    public void testCreatePredictionDataset()
    {
        System.out.println( "createPredictionDataset" );
        final int predictionHorizon = 2;
        @SuppressWarnings("unchecked")
        final List<DefaultInputOutputPair<Double,Double>> data = Arrays.asList(
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() ),
            new DefaultInputOutputPair<Double,Double>( random.nextGaussian(), random.nextGaussian() )
        );

        ArrayList<InputOutputPair<Double,Double>> result =
            TimeSeriesPredictionLearner.createPredictionDataset( predictionHorizon, data );

        assertEquals( data.size()-predictionHorizon, result.size() );

        for( int i = 0; i < result.size(); i++ )
        {
            assertSame( data.get(i).getInput(), result.get(i).getInput() );
            assertSame( data.get(i+predictionHorizon).getOutput(), result.get(i).getOutput() );
        }

        try
        {
            result = TimeSeriesPredictionLearner.createPredictionDataset( -1, data );
            fail( "Prediction horizon must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


        result = TimeSeriesPredictionLearner.createPredictionDataset( data.size(), data );
        assertEquals( 0, result.size() );

        result = TimeSeriesPredictionLearner.createPredictionDataset( data.size()+1, data );
        assertEquals( 0, result.size() );

    }

}
