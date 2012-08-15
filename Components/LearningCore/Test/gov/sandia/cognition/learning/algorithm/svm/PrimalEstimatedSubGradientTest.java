/*
 * File:                PrimalEstimatedSubGradientTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 06, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.event.AbstractIterativeAlgorithmListener;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class PrimalEstimatedSubGradient.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class PrimalEstimatedSubGradientTest
    extends TestCase
{
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public PrimalEstimatedSubGradientTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of learn method, of class PrimalEstimatedSubGradient.
     */
    public void testLearn()
    {
        // Generate some data using the example synthetic data from Platt's
        // original SMO paper.
        int d = 300;
        int pointsToGenerate = 100;
        final ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>(pointsToGenerate);

        Vector target = VectorFactory.getDenseDefault().createUniformRandom(d, -1.0, 1.0, random);
        while (data.size() < pointsToGenerate)
        {
            Vector input = VectorFactory.getSparseDefault().createVector(d, 0.0);

            for (int i = 0; i < d / 10; i++)
            {
                int index = random.nextInt(d);
                input.setElement(index, 1.0);
            }

            double dotProduct = input.dotProduct(target);
            if (dotProduct < -1.0)
            {
                data.add(DefaultInputOutputPair.create(input, false));
            }
            else if (dotProduct > +1.0)
            {
                data.add(DefaultInputOutputPair.create(input, true));
            }
            // else - The dot product wsa between -1.0 and +1.0, try again.
        }

        final PrimalEstimatedSubGradient instance = new PrimalEstimatedSubGradient(
            10, 0.01, 200, random);

        // Plug in a little algorithm listener to print out the performance
        // information.
        instance.addIterativeAlgorithmListener(new AbstractIterativeAlgorithmListener()
        {

            @Override
            public void stepEnded(
                final IterativeAlgorithm algorithm)
            {
                final LinearBinaryCategorizer result = instance.getResult();

                // Compute the loss of the data as well as the number of
                // errors.
                double loss = 0.0;
                int errorCount = 0;
                for (InputOutputPair<? extends Vectorizable, Boolean> example
                    : instance.getData())
                {
                    final double predicted = result.evaluateAsDouble(
                        example.getInput());
                    final double actual = example.getOutput() ? 1.0 : -1.0;
                    loss += Math.max(0, 1.0 - actual * predicted);
                    if (predicted * actual <= 0.0)
                    {
                        errorCount += 1;
                    }
                }

                loss /= instance.getData().size();

                // Compute the regularization term.
                final double regularization =
                        instance.getRegularizationWeight() / 2.0
                        * result.getWeights().norm2Squared();
                final double objective = loss + regularization;

                System.out.println(
                    "Iteration: " + instance.getIteration()
                    + " Objective: " + objective
                    + " Loss: " + loss
                    + " Regularization: " + regularization
                    + " Errors: " + errorCount);
            }
        });

        final LinearBinaryCategorizer result = instance.learn(data);
        assertSame(result, instance.getResult());

        // Make sure there is perfect learning on this example.
        for (InputOutputPair<Vector, Boolean> example : data)
        {
//            System.out.println("" + example.getInput() + " -> " + example.getOutput());
            assertEquals(example.getOutput(), result.evaluate(example.getInput()));
        }
    }

    /**
     * Test of getResult method, of class PrimalEstimatedSubGradient.
     */
    public void testGetResult()
    {
        PrimalEstimatedSubGradient instance = new PrimalEstimatedSubGradient();
        assertNull(instance.getResult());
    }

    /**
     * Test of getSampleSize method, of class PrimalEstimatedSubGradient.
     */
    public void testGetSampleSize()
    {
        this.testSetSampleSize();
    }

    /**
     * Test of setRequestedSampleCount method, of class PrimalEstimatedSubGradient.
     */
    public void testSetSampleSize()
    {
        int sampleSize = PrimalEstimatedSubGradient.DEFAULT_SAMPLE_SIZE;
        PrimalEstimatedSubGradient instance = new PrimalEstimatedSubGradient();
        assertEquals(sampleSize, instance.getSampleSize());

        sampleSize /= 3;
        instance.setSampleSize(sampleSize);
        assertEquals(sampleSize, instance.getSampleSize());

        sampleSize = Integer.MAX_VALUE;
        instance.setSampleSize(sampleSize);
        assertEquals(sampleSize, instance.getSampleSize());

        sampleSize = 1;
        instance.setSampleSize(sampleSize);
        assertEquals(sampleSize, instance.getSampleSize());

        boolean exceptionThrown = false;
        try
        {
            instance.setSampleSize(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(sampleSize, instance.getSampleSize());


        exceptionThrown = false;
        try
        {
            instance.setSampleSize(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(sampleSize, instance.getSampleSize());
    }

    /**
     * Test of getRegularizationWeight method, of class PrimalEstimatedSubGradient.
     */
    public void testGetRegularizationWeight()
    {
        this.testSetRegularizationWeight();
    }

    /**
     * Test of setRegularizationWeight method, of class PrimalEstimatedSubGradient.
     */
    public void testSetRegularizationWeight()
    {
        double regularizationWeight = PrimalEstimatedSubGradient.DEFAULT_REGULARIZATION_WEIGHT;
        PrimalEstimatedSubGradient instance = new PrimalEstimatedSubGradient();
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);

        regularizationWeight *= this.random.nextDouble();
        instance.setRegularizationWeight(regularizationWeight);
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);

        regularizationWeight = 1.0;
        instance.setRegularizationWeight(regularizationWeight);
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);

        regularizationWeight = this.random.nextDouble();
        instance.setRegularizationWeight(regularizationWeight);
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setRegularizationWeight(0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);

        exceptionThrown = false;
        try
        {
            instance.setRegularizationWeight(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(regularizationWeight, instance.getRegularizationWeight(), 0.0);
    }

    /**
     * Test of getRandom method, of class PrimalEstimatedSubGradient.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class PrimalEstimatedSubGradient.
     */
    public void testSetRandom()
    {
        Random random = null;
        PrimalEstimatedSubGradient instance = new PrimalEstimatedSubGradient();
        assertNotNull(instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

}
