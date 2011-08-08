/*
 * File:                MultiCategoryAdaBoostTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 24, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import gov.sandia.cognition.learning.data.InputOutputPair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class MultiCategoryAdaBoost.
 *
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class MultiCategoryAdaBoostTest
{
    /**
     * Creates a new test.
     */
    public MultiCategoryAdaBoostTest()
    {
    }

    /**
     * Test of constructors of class MultiCategoryAdaBoost.
     */
    @Test
    public void testConstructors()
    {
        BinaryCategorizerSelector<Vectorizable> weakLearner = null;
        int maxIterations = MultiCategoryAdaBoost.DEFAULT_MAX_ITERATIONS;
        MultiCategoryAdaBoost<Vectorizable, Boolean> instance =
            new MultiCategoryAdaBoost<Vectorizable, Boolean>();
        assertSame(weakLearner, instance.getWeakLearner());
        assertEquals(maxIterations, instance.getMaxIterations());

        weakLearner = new BinaryCategorizerSelector<Vectorizable>();
        maxIterations = 2;
        instance = new MultiCategoryAdaBoost<Vectorizable, Boolean>(
            weakLearner, maxIterations);
        assertSame(weakLearner, instance.getWeakLearner());
        assertEquals(maxIterations, instance.getMaxIterations());
    }

    /**
     * Test of learn method, of class MultiCategoryAdaBoost.
     */
    @Test
    public void testLearn()
    {

        Vector2[] positives = new Vector2[]
        {
            new Vector2(2.00, 3.00),
            new Vector2(2.00, 4.00),
            new Vector2(3.00, 2.00),
            new Vector2(4.25, 3.75),
            new Vector2(4.00, 7.00),
            new Vector2(7.00, 4.00)
        };

        Vector2[] negatives = new Vector2[]
        {
            new Vector2(1.00, 1.00),
            new Vector2(1.00, 3.00),
            new Vector2(0.25, 4.00),
            new Vector2(2.00, 1.00),
            new Vector2(5.00, -3.00)
        };


        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example,
                true));
        }

        for (Vector2 example : negatives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example,
                false));
        }

        BinaryCategorizerSelector<Vectorizable> weakLearner =
            new BinaryCategorizerSelector<Vectorizable>();
        for (int i = 0; i < 8; i++)
        {
            double value = (double) i;
            weakLearner.getCategorizers().add(
                new LinearBinaryCategorizer(new Vector2(1.0, 0.0), -value));
            weakLearner.getCategorizers().add(
                new LinearBinaryCategorizer(new Vector2(0.0, 1.0), -value));
        }

        // This loop ensures that all of the learners are "weak". This means
        // none of them is 100% accurate.
        for (BinaryCategorizer<? super Vectorizable> categorizer :
            weakLearner.getCategorizers())
        {
            int numIncorrect = 0;
            for (InputOutputPair<Vector2, Boolean> example : examples)
            {
                boolean predicted = categorizer.evaluate(example.getInput());
                if (!example.getOutput().equals(predicted))
                {
                    numIncorrect++;
                }
            }
            assertTrue(numIncorrect > 0);
        }

        int maxIterations = 10;
        MultiCategoryAdaBoost<Vectorizable, Boolean> instance = new MultiCategoryAdaBoost<Vectorizable, Boolean>(
            weakLearner, maxIterations);

        WeightedVotingCategorizerEnsemble<Vectorizable, Boolean, ?> learned = instance.learn(
            examples);
        assertNotNull(learned);
        assertSame(learned, instance.getResult());

        for (Vector2 example : positives)
        {
            assertTrue(learned.evaluate(example));
        }

        for (Vector2 example : negatives)
        {
            assertFalse(learned.evaluate(example));
        }

        examples = new ArrayList<InputOutputPair<Vector2, Boolean>>();
        learned = instance.learn(examples);
        assertNull(learned);

        learned = instance.learn(null);
        assertNull(learned);

    }
    
    /**
     * Test of getWeakLearner method, of class MultiCategoryAdaBoost.
     */
    @Test
    public void testGetWeakLearner()
    {
        this.testSetWeakLearner();
    }

    /**
     * Test of setWeakLearner method, of class MultiCategoryAdaBoost.
     */
    @Test
    public void testSetWeakLearner()
    {
        BinaryCategorizerSelector<Vectorizable> weakLearner = null;
        MultiCategoryAdaBoost<Vectorizable, Boolean> instance =
            new MultiCategoryAdaBoost<Vectorizable, Boolean>();
        assertSame(weakLearner, instance.getWeakLearner());

        weakLearner = new BinaryCategorizerSelector<Vectorizable>();
        instance.setWeakLearner(weakLearner);
        assertSame(weakLearner, instance.getWeakLearner());

        weakLearner = null;
        instance.setWeakLearner(weakLearner);
        assertSame(weakLearner, instance.getWeakLearner());

        weakLearner = new BinaryCategorizerSelector<Vectorizable>();
        instance.setWeakLearner(weakLearner);
        assertSame(weakLearner, instance.getWeakLearner());
    }

}
