/*
 * File:                OnlineKernelBinaryLearnerTestHarness.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.algorithm.SupervisedIncrementalLearner;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import java.util.Random;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test harness for a kernel binary learner.
 *
 * @param   <LearnedType>
 *      The type of kernel binary categorizer learned by the algorithm.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class OnlineKernelBinaryLearnerTestHarness<LearnedType extends KernelBinaryCategorizer<Vector, ?>>
{

    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new instance to perform testing on, using the given kernel.
     *
     * @param   kernel
     *      The kernel function to use.
     * @return
     *      A new instance.
     */
    protected abstract SupervisedIncrementalLearner<Vector, Boolean, LearnedType> createInstance(
        final Kernel<? super Vector> kernel);

    /**
     * Test learning a linearly separable example.
     */
    @Test
    public void testKernelLearnSeparable()
    {
        System.out.println("testKernelLearnSeparable");
        int d = 10;
        int trainCount = 1000;
        int testCount = 100;
        LinearBinaryCategorizer real = new LinearBinaryCategorizer(
            VectorFactory.getDenseDefault().createUniformRandom(d, -1, +1, random), 0.0);

        ArrayList<InputOutputPair<Vector, Boolean>> trainData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(trainCount);
        for (int i = 0; i < trainCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            boolean output = real.evaluate(input);
            trainData.add(DefaultInputOutputPair.create(input, output));
        }

        ArrayList<InputOutputPair<Vector, Boolean>> testData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(testCount);
        for (int i = 0; i < testCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            boolean actual = real.evaluate(input);
            testData.add(DefaultInputOutputPair.create(input, actual));
        }

        SupervisedIncrementalLearner<Vector, Boolean, LearnedType> instance =
            this.createInstance(new LinearKernel());

        LearnedType learned = instance.createInitialLearnedObject();

        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
        }

        int correctCount = 0;
        for (InputOutputPair<Vector, Boolean> example : testData)
        {
            boolean actual = example.getOutput();
            boolean predicted = learned.evaluate(example.getInput());

            if (actual == predicted)
            {
                correctCount++;
            }
        }

        double accuracy = (double) correctCount / testData.size();
        System.out.println("Accuracy: " + accuracy);
        assertTrue(accuracy >= 0.95);

        RingAccumulator<Vector> accumulator = new RingAccumulator<Vector>();
        for (WeightedValue<? extends Vector> example : learned.getExamples())
        {
            accumulator.accumulate(example.getValue().scale(example.getWeight()));
        }
        double cosine = accumulator.getSum().unitVector().cosine(real.getWeights().unitVector());
        System.out.println("Cosine: " + cosine);
        assertTrue(cosine >= 0.95);
    }


    /**
     * Test learning a linearly separable example.
     */
    @Test
    public void testKernelXOR()
    {
        System.out.println("testKernelXOR");
        int trainCount = 1000;
        int testCount = 1000;

        ArrayList<InputOutputPair<Vector, Boolean>> trainData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(trainCount);
        for (int i = 0; i < trainCount; i++)
        {
            double x = 2.0 * this.random.nextDouble() - 1.0;
            double y = 2.0 * this.random.nextDouble() - 1.0;
            Vector input = new Vector2(x, y);
            boolean output = (x >= 0.0) ^ (y >= 0.0);
            trainData.add(DefaultInputOutputPair.create(input, output));
        }

        ArrayList<InputOutputPair<Vector, Boolean>> testData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(testCount);
        for (int i = 0; i < testCount; i++)
        {
            double x = 2.0 * this.random.nextDouble() - 1.0;
            double y = 2.0 * this.random.nextDouble() - 1.0;
            Vector input = new Vector2(x, y);
            boolean output = (x >= 0.0) ^ (y >= 0.0);
            testData.add(DefaultInputOutputPair.create(input, output));
        }

        SupervisedIncrementalLearner<Vector, Boolean, LearnedType> instance =
            this.createInstance(new PolynomialKernel(2, 1.0));

        LearnedType learned = instance.createInitialLearnedObject();
        
        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
        }

        int correctCount = 0;
        for (InputOutputPair<Vector, Boolean> example : testData)
        {
            boolean actual = example.getOutput();
            boolean predicted = learned.evaluate(example.getInput());

            if (actual == predicted)
            {
                correctCount++;
            }
        }
        double accuracy = (double) correctCount / testData.size();
        System.out.println("Accuracy: " + accuracy);
        assertTrue(accuracy >= 0.90);
    }

    /**
     * Transforms the given kernel categorizer of vectors into a single weight
     * vector by doing a weighted sum. This assumes a linear kernel is being
     * used.
     *
     * @param   categorizer
     *      The categorizer.
     * @return
     *      The weighted sum of the support vectors.
     */
    public static Vector toWeights(
        final DefaultKernelBinaryCategorizer<Vector> categorizer)
    {
        RingAccumulator<Vector> accumulator = new RingAccumulator<Vector>();
        for (WeightedValue<Vector> example : categorizer.getExamples())
        {
            accumulator.accumulate(example.getValue().scale(example.getWeight()));
        }
        return accumulator.getSum();
    }
    
}
