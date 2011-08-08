/*
 * File:                OnlinePassiveAggressivePerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.perceptron.kernel.KernelBinaryCategorizerOnlineLearnerAdapter;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import org.junit.Test;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlinePassiveAggressivePerceptron.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class OnlinePassiveAggressivePerceptronTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{

    protected boolean aggressiveCheck = true;

    /**
     * Creates a new test.
     */
    public OnlinePassiveAggressivePerceptronTest()
    {
    }


    /**
     * Test of constructors of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlinePassiveAggressivePerceptron(factory);
        assertSame(factory, instance.getVectorFactory());
    }


    /**
     * Test of createInitialLearnedObject method, of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testCreateInitialLearnedObject()
    {
        OnlinePassiveAggressivePerceptron instance = this.createLinearInstance();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test of computeUpdate method, of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testComputeUpdate()
    {
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        LinearBinaryCategorizer result = new LinearBinaryCategorizer();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);

        Vector input = new Vector2(2.0, 3.0);
        Boolean output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(4.0, 4.0);
        output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(2.0, 3.0);
        output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));


        result = instance.createInitialLearnedObject();

        MultivariateGaussian positive = new MultivariateGaussian(2);
        positive.setMean(new Vector2(1.0, 1.0));
        positive.getCovariance().setElement(0, 0, 0.2);
        positive.getCovariance().setElement(1, 1, 2.0);

        MultivariateGaussian negative = new MultivariateGaussian(2);
        negative.setMean(new Vector2(-1.0, -1.0));
        negative.getCovariance().setElement(0, 0, 0.2);
        negative.getCovariance().setElement(1, 1, 2.0);

        for (int i = 0; i < 4000; i++)
        {
            output = random.nextBoolean();
            input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);

            double loss = 1.0 - prediction * (output ? +1 : -1);
            instance.update(result, DefaultInputOutputPair.create(input, output));
            assertEquals(output, result.evaluate(input));
            if (loss <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    /**
     * Test of update method, of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testUpdate()
    {
        OnlinePassiveAggressivePerceptron instance = this.createLinearInstance();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);

        Vector input = new Vector2(2.0, 3.0);
        Boolean output = true;
        instance.update(result, input, output);
        if (this.aggressiveCheck)
        {
            assertEquals(output, result.evaluate(input));
        }

        input = new Vector2(4.0, 4.0);
        output = true;
        instance.update(result, input, output);
        if (this.aggressiveCheck)
        {
            assertEquals(output, result.evaluate(input));
        }

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, input, output);
        if (this.aggressiveCheck)
        {
            assertEquals(output, result.evaluate(input));
        }

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, input, output);
        if (this.aggressiveCheck)
        {
            assertEquals(output, result.evaluate(input));
        }

        input = new Vector2(2.0, 3.0);
        output = true;
        instance.update(result, input, output);
        if (this.aggressiveCheck)
        {
            assertEquals(output, result.evaluate(input));
        }


        result = instance.createInitialLearnedObject();

        MultivariateGaussian positive = new MultivariateGaussian(2);
        positive.setMean(new Vector2(1.0, 1.0));
        positive.getCovariance().setElement(0, 0, 0.2);
        positive.getCovariance().setElement(1, 1, 2.0);

        MultivariateGaussian negative = new MultivariateGaussian(2);
        negative.setMean(new Vector2(-1.0, -1.0));
        negative.getCovariance().setElement(0, 0, 0.2);
        negative.getCovariance().setElement(1, 1, 2.0);

        for (int i = 0; i < 4000; i++)
        {
            output = random.nextBoolean();
            input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);

            double loss = 1.0 - prediction * (output ? +1 : -1);
            instance.update(result, DefaultInputOutputPair.create(input, output));

            if (loss <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
            if (this.aggressiveCheck)
            {
                assertEquals(output, result.evaluate(input));
            }
        }
    }

    /**
     * Test of getVectorFactory method, of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class OnlinePassiveAggressivePerceptron.
     */
    @Test
    public void testSetVectorFactory()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePassiveAggressivePerceptron instance = this.createLinearInstance();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = VectorFactory.getSparseDefault();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = null;
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = VectorFactory.getDenseDefault();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());
    }

    @Override
    protected OnlinePassiveAggressivePerceptron createLinearInstance()
    {
        return new OnlinePassiveAggressivePerceptron();
    }

    @Override
    protected void applyUpdate(
        KernelBinaryCategorizerOnlineLearnerAdapter<Vector> learner,
        DefaultKernelBinaryCategorizer<Vector> target,
        InputOutputPair<Vector, Boolean> example)
    {
        super.applyUpdate(learner, target, example);

        if (this.aggressiveCheck)
        {
            assertEquals(example.getOutput(), target.evaluate(example.getInput()));
        }
    }

    @Override
    protected void applyUpdate(
        KernelizableBinaryCategorizerOnlineLearner learner,
        LinearBinaryCategorizer target,
        InputOutputPair<Vector, Boolean> example)
    {
        super.applyUpdate(learner, target, example);

        if (this.aggressiveCheck)
        {
            assertEquals(example.getOutput(), target.evaluate(example.getInput()));
        }
    }


}
