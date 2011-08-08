/*
 * File:                AdaBoostTest.java
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

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AdaBoost
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class AdaBoostTest
    extends TestCase
{

    public AdaBoostTest(
        String testName )
    {
        super( testName );
    }

    public void testConstants()
    {
        assertEquals( 100, AdaBoost.DEFAULT_MAX_ITERATIONS );
    }

    public void testConstructors()
    {
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>();
        assertNull( instance.getWeakLearner() );
        assertEquals( AdaBoost.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );

        BinaryCategorizerSelector<Vectorizable> weakLearner =
            new BinaryCategorizerSelector<Vectorizable>();
        instance = new AdaBoost<Vectorizable>( weakLearner );
        assertSame( weakLearner, instance.getWeakLearner() );
        assertEquals( AdaBoost.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );

        int maxIterations = AdaBoost.DEFAULT_MAX_ITERATIONS + 10;

        instance = new AdaBoost<Vectorizable>( weakLearner, maxIterations );
        assertSame( weakLearner, instance.getWeakLearner() );
        assertEquals( maxIterations, instance.getMaxIterations() );
    }

    public void testLearn()
    {

        Vector2[] positives = new Vector2[]{
            new Vector2( 2.00, 3.00 ),
            new Vector2( 2.00, 4.00 ),
            new Vector2( 3.00, 2.00 ),
            new Vector2( 4.25, 3.75 ),
            new Vector2( 4.00, 7.00 ),
            new Vector2( 7.00, 4.00 )
        };

        Vector2[] negatives = new Vector2[]{
            new Vector2( 1.00, 1.00 ),
            new Vector2( 1.00, 3.00 ),
            new Vector2( 0.25, 4.00 ),
            new Vector2( 2.00, 1.00 ),
            new Vector2( 5.00, -3.00 )
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

        BinaryCategorizerSelector<Vectorizable> weakLearner =
            new BinaryCategorizerSelector<Vectorizable>();
        for (int i = 0; i < 8; i++)
        {
            double value = (double) i;
            weakLearner.getCategorizers().add(
                new LinearBinaryCategorizer( new Vector2( 1.0, 0.0 ), -value ) );
            weakLearner.getCategorizers().add(
                new LinearBinaryCategorizer( new Vector2( 0.0, 1.0 ), -value ) );
        }

        // This loop ensures that all of the learners are "weak". This means
        // none of them is 100% accurate.
        for (BinaryCategorizer<? super Vectorizable> categorizer : weakLearner.getCategorizers())
        {
            int numIncorrect = 0;
            for (InputOutputPair<Vector2, Boolean> example : examples)
            {
                boolean predicted = categorizer.evaluate( example.getInput() );
                if (!example.getOutput().equals( predicted ))
                {
                    numIncorrect++;
                }
            }
            assertTrue( numIncorrect > 0 );
        }

        int maxIterations = 10;
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>(
            weakLearner, maxIterations );

        WeightedBinaryEnsemble<Vectorizable> learned = instance.learn( examples );
        assertNotNull( learned );
        assertSame( learned, instance.getResult() );
        assertSame( learned, instance.getEnsemble() );
        assertNull( instance.getWeightedData() );

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
     * Test of getResult method, of class gov.sandia.cognition.learning.ensemble.AdaBoost.
     */
    public void testGetResult()
    {
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>();
        assertNull( instance.getResult() );
    }

    /**
     * Test of getWeakLearner method, of class gov.sandia.cognition.learning.ensemble.AdaBoost.
     */
    public void testGetWeakLearner()
    {
        this.testSetWeakLearner();
    }

    /**
     * Test of setWeakLearner method, of class gov.sandia.cognition.learning.ensemble.AdaBoost.
     */
    public void testSetWeakLearner()
    {
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>();
        assertNull( instance.getWeakLearner() );

        BinaryCategorizerSelector<Vectorizable> weakLearner =
            new BinaryCategorizerSelector<Vectorizable>();
        instance.setWeakLearner( weakLearner );
        assertSame( weakLearner, instance.getWeakLearner() );

        instance.setWeakLearner( null );
        assertNull( instance.getWeakLearner() );
    }

    /**
     * Test of getEnsemble method, of class gov.sandia.cognition.learning.ensemble.AdaBoost.
     */
    public void testGetEnsemble()
    {
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>();
        assertNull( instance.getEnsemble() );
    }

    /**
     * Test of getWeightedData method, of class gov.sandia.cognition.learning.ensemble.AdaBoost.
     */
    public void testGetWeightedData()
    {
        AdaBoost<Vectorizable> instance = new AdaBoost<Vectorizable>();
        assertNull( instance.getWeightedData() );
    }

    public static class DummyWeakLearner
    {
    }

}
