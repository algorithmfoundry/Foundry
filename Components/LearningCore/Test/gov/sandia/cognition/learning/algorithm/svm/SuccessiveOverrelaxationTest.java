/*
 * File:                SuccessiveOverrelaxationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Tests of SuccessiveOverrelaxation
 * @author  Justin Basilico
 * @since   2.1
 */
public class SuccessiveOverrelaxationTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public SuccessiveOverrelaxationTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        final SuccessiveOverrelaxation<Vector> instance = new SuccessiveOverrelaxation<Vector>(
            LinearKernel.getInstance());

        SuccessiveOverrelaxation<Vector> clone = (SuccessiveOverrelaxation<Vector>) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getKernel() );
        assertSame( instance.getKernel(), clone.getKernel() );
    }


    public void testLearn()
    {
        final SuccessiveOverrelaxation<Vector> instance = new SuccessiveOverrelaxation<Vector>(
            LinearKernel.getInstance());
        
        Vector2[] positives = new Vector2[] {
            new Vector2(1.00, 1.00),
            new Vector2(1.00, 3.00),
            new Vector2(0.25, 4.00),
            new Vector2(2.00, 1.00),
            new Vector2(5.00, -3.00)
        };

        Vector2[] negatives = new Vector2[] {
            new Vector2(2.00, 3.00),
            new Vector2(2.00, 4.00),
            new Vector2(3.00, 2.00),
            new Vector2(4.25, 3.75),
            new Vector2(4.00, 7.00),
            new Vector2(7.00, 4.00)
        };

        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, true));
        }

        for (Vector2 example : negatives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, false));
        }
        
        Evaluator<? super Vector,Boolean> result = instance.learn(examples);
//System.out.println("Iteration: " + instance.getIteration());
//System.out.println("Change: " + instance.getTotalChange());
//for (WeightedValue<? extends Vector> example : result.getExamples())
//{
//    System.out.println("" + example.getWeight());
//}

        assertEquals(result, instance.getResult());
        assertTrue(instance.getTotalChange() < instance.getMinChange());
        
        // A 2D example that is linearly separable will have exactly 3 support
        // vectors.
        assertEquals(3, ((KernelBinaryCategorizer) result).getExamples().size());

        for (Vector2 example : positives)
        {
            assertTrue(result.evaluate(example));
        }

        for (Vector2 example : negatives)
        {
            assertFalse(result.evaluate(example));
        }
        
        instance.setMaxIterations(1000);
        result = instance.learn(examples);
        assertEquals(result, instance.getResult());


        for (Vector2 example : positives)
        {
            assertTrue(result.evaluate(example));
        }

        for (Vector2 example : negatives)
        {
            assertFalse(result.evaluate(example));
        }

        instance.setMaxIterations(instance.getIteration() / 2);
        result = instance.learn(examples);
        //assertTrue(instance.getErrorCount() > 0);

        examples = new ArrayList<InputOutputPair<Vector2, Boolean>>();
        result = instance.learn(examples);
        assertNull(result);

        result = instance.learn(null);
        assertNull(result);
    }


}
