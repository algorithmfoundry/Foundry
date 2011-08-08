/*
 * File:                BinaryBaggingLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     BinaryBaggingLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class BinaryBaggingLearnerTest
    extends TestCase
{

    public BinaryBaggingLearnerTest(
        String testName )
    {
        super( testName );
    }

    public void testConstants()
    {
        assertEquals( 100, BinaryBaggingLearner.DEFAULT_MAX_ITERATIONS );
    }

    public void testConstructors()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNull( instance.getLearner() );
        assertEquals( BinaryBaggingLearner.DEFAULT_MAX_ITERATIONS,
            instance.getMaxIterations() );
        assertNotNull( instance.getRandom() );

        Perceptron learner = new Perceptron();
        instance = new BinaryBaggingLearner<Vectorizable>( learner );
        assertSame( learner, instance.getLearner() );
        assertEquals( BinaryBaggingLearner.DEFAULT_MAX_ITERATIONS,
            instance.getMaxIterations() );
        assertNotNull( instance.getRandom() );


        int maxIterations = BinaryBaggingLearner.DEFAULT_MAX_ITERATIONS + 10;
        instance = new BinaryBaggingLearner<Vectorizable>( learner, maxIterations );
        assertSame( learner, instance.getLearner() );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertNotNull( instance.getRandom() );

        Random random = new Random();
        instance = new BinaryBaggingLearner<Vectorizable>( learner, maxIterations, random );
        assertSame( learner, instance.getLearner() );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertSame( random, instance.getRandom() );
    }

    public void testLearn()
    {
        int numMembers = 50;
        Perceptron learner = new Perceptron();
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>(
            learner, numMembers );

        Vector2[] positives = new Vector2[]{
            new Vector2( 1.00, 1.00 ),
            new Vector2( 1.00, 3.00 ),
            new Vector2( 0.25, 4.00 ),
            new Vector2( 2.00, 1.00 ),
            new Vector2( 5.00, -3.00 )
        };

        Vector2[] negatives = new Vector2[]{
            new Vector2( 2.00, 3.00 ),
            new Vector2( 2.00, 4.00 ),
            new Vector2( 3.00, 2.00 ),
            new Vector2( 4.25, 3.75 ),
            new Vector2( 4.00, 7.00 ),
            new Vector2( 7.00, 4.00 )
        };

        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, true ) );
        }

        for (Vector2 example : negatives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, false ) );
        }

        WeightedBinaryEnsemble<Vectorizable> learned = instance.learn( examples );
        assertNotNull( learned );
        assertSame( learned, instance.getResult() );
        assertSame( learned, instance.getEnsemble() );
        assertNull( instance.getDataList() );

        assertEquals( numMembers, learned.getMembers().size() );
        for (Vector2 example : positives)
        {
            assertTrue( learned.evaluate( example ) );
        }

        for (Vector2 example : negatives)
        {
            assertFalse( learned.evaluate( example ) );
        }

        numMembers = 2 * BinaryBaggingLearner.DEFAULT_MAX_ITERATIONS;
        instance.setMaxIterations( numMembers );
        learned = instance.learn( examples );
        assertNotNull( learned );
        assertSame( learned, instance.getResult() );
        assertSame( learned, instance.getEnsemble() );
        assertNull( instance.getDataList() );

        assertEquals( numMembers, learned.getMembers().size() );
        for (Vector2 example : positives)
        {
            assertTrue( learned.evaluate( example ) );
        }

        for (Vector2 example : negatives)
        {
            assertFalse( learned.evaluate( example ) );
        }

        examples = new ArrayList<InputOutputPair<Vector2, Boolean>>();
        learned = instance.learn( examples );
        assertNull( learned );

        learned = instance.learn( null );
        assertNull( learned );
    }

    /**
     * Test of getResult method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testGetResult()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNull( instance.getResult() );
    }

    /**
     * Test of getLearner method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testSetLearner()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNull( instance.getLearner() );

        Perceptron learner = new Perceptron();
        instance.setLearner( learner );
        assertSame( learner, instance.getLearner() );

        instance.setLearner( null );
        assertNull( instance.getLearner() );
    }

    /**
     * Test of getRandom method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testSetRandom()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNotNull( instance.getRandom() );

        Random random = new Random();
        instance.setRandom( random );
        assertSame( random, instance.getRandom() );

        random = new Random();
        instance.setRandom( random );
        assertSame( random, instance.getRandom() );

        instance.setRandom( null );
        assertNull( instance.getRandom() );
    }

    /**
     * Test of getEnsemble method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testGetEnsemble()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNull( instance.getEnsemble() );
    }

    /**
     * Test of getDataList method, of class gov.sandia.cognition.learning.ensemble.BinaryBaggingLearner.
     */
    public void testGetDataList()
    {
        BinaryBaggingLearner<Vectorizable> instance = new BinaryBaggingLearner<Vectorizable>();
        assertNull( instance.getDataList() );
    }

}
