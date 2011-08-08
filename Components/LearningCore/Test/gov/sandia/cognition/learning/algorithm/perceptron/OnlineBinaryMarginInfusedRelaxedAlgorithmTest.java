/*
 * File:                OnlineBinaryMarginInfusedRelaxedAlgorithmTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 08, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlineBinaryMarginInfusedRelaxedAlgorithm.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineBinaryMarginInfusedRelaxedAlgorithmTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{

    /**
     * Creates a new test.
     */
    public OnlineBinaryMarginInfusedRelaxedAlgorithmTest()
    {
    }

    @Override
    protected OnlineBinaryMarginInfusedRelaxedAlgorithm createLinearInstance()
    {
        return new OnlineBinaryMarginInfusedRelaxedAlgorithm();
    }
    
    /**
     * Test of constructors of class OnlineBinaryMarginInfusedRelaxedAlgorithm.
     */
    @Test
    public void testConstructors()
    {
        double minMargin = OnlineBinaryMarginInfusedRelaxedAlgorithm.DEFAULT_MIN_MARGIN;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        OnlineBinaryMarginInfusedRelaxedAlgorithm instance =
            new OnlineBinaryMarginInfusedRelaxedAlgorithm();
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());

        minMargin = random.nextDouble();
        instance = new OnlineBinaryMarginInfusedRelaxedAlgorithm(minMargin);
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());

        minMargin = random.nextDouble();
        vectorFactory = VectorFactory.getSparseDefault();
        instance = new OnlineBinaryMarginInfusedRelaxedAlgorithm(minMargin,
            vectorFactory);
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());
    }
    
    /**
     * Test of update method, of class OnlineBinaryMarginInfusedRelaxedAlgorithm.
     */
    @Test
    public void testUpdate()
    {
        OnlineBinaryMarginInfusedRelaxedAlgorithm instance =
            new OnlineBinaryMarginInfusedRelaxedAlgorithm();
        LinearBinaryCategorizer result = new LinearBinaryCategorizer();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);


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
            boolean output = random.nextBoolean();
            Vector input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);

            double margin = prediction * (output ? +1 : -1);
            double g = -margin / input.norm2Squared();
            instance.update(result, DefaultInputOutputPair.create(input, output));

            if (oldWeights == null)
            {
                assertTrue(result.getWeights().norm1() != 0.0);
            }
            else if (g <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    /**
     * Test of getMinMargin method, of class OnlineBinaryMarginInfusedRelaxedAlgorithm.
     */
    @Test
    public void testGetMinMargin()
    {
        this.testSetMinMargin();
    }

    /**
     * Test of setMinMargin method, of class OnlineBinaryMarginInfusedRelaxedAlgorithm.
     */
    @Test
    public void testSetMinMargin()
    {
        double minMargin = OnlineBinaryMarginInfusedRelaxedAlgorithm.DEFAULT_MIN_MARGIN;
        OnlineBinaryMarginInfusedRelaxedAlgorithm instance =
            new OnlineBinaryMarginInfusedRelaxedAlgorithm();
        assertEquals(minMargin, instance.getMinMargin(), 0.0);

        double[] goodValues = { 0.0, 0.1, 1.0, 2.0 };
        for (double goodValue : goodValues)
        {
            minMargin = goodValue;
            instance.setMinMargin(minMargin);
            assertEquals(minMargin, instance.getMinMargin(), 0.0);
        }

        double[] badValues = {-0.1, -1.0, -2.0};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setMinMargin(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(minMargin, instance.getMinMargin(), 0.0);
        }
    }

}