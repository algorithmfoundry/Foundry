/*
 * File:                BallseptronTest.java
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

import gov.sandia.cognition.learning.algorithm.perceptron.KernelizableBinaryCategorizerOnlineLearner;
import gov.sandia.cognition.learning.algorithm.perceptron.Ballseptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for class Ballseptron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class BallseptronTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{
    /**
     * Creates a new test.
     */
    public BallseptronTest()
    {
    }

    @Override
    protected KernelizableBinaryCategorizerOnlineLearner createLinearInstance()
    {
        return new Ballseptron();
    }

    /**
     * Test of constructors of class Ballseptron.
     */
    @Test
    public void testConstructors()
    {
        double radius = Ballseptron.DEFAULT_RADIUS;
        Ballseptron instance = new Ballseptron();
        assertEquals(radius, instance.getRadius(), 0.0);

        radius = this.random.nextDouble();
        instance = new Ballseptron(radius);
        assertEquals(radius, instance.getRadius(), 0.0);
    }

    /**
     * Test of update method, of class Ballseptron.
     */
    @Test
    public void testUpdate()
    {
        double epsilon = 1E-5;
        Ballseptron instance = new Ballseptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
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

        input = new Vector2(1.0, -1.0);
        output = false;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(1.0, -1.0);
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
            double actual = (output ? +1 : -1);

            double margin = prediction * actual;
            instance.update(result, DefaultInputOutputPair.create(input, output));

            if (oldWeights == null)
            {
                assertEquals(input, result.getWeights());
                assertNotSame(input, result.getWeights());
            }
            else if (margin <= 0.0)
            {
                Vector expectedWeights = oldWeights.plus(input.scale(actual));
                if(!
                    expectedWeights.equals(
                        result.getWeights(), epsilon))
                {
                    System.out.println("Actual " + result.getWeights());
                    System.out.println("Expected: " + expectedWeights);
                }
            }
            else if (margin/oldWeights.norm2() <= instance.getRadius())
            {

            }
            else
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    /**
     * Test of getRadius method, of class Ballseptron.
     */
    @Test
    public void testGetRadius()
    {
        this.testSetRadius();
    }

    /**
     * Test of setRadius method, of class Ballseptron.
     */
    @Test
    public void testSetRadius()
    {
        double radius = Ballseptron.DEFAULT_RADIUS;
        Ballseptron instance = new Ballseptron();
        assertEquals(radius, instance.getRadius(), 0.0);

        radius = 2.0 * radius;
        instance.setRadius(radius);
        assertEquals(radius, instance.getRadius(), 0.0);

        radius = 1.0;
        instance.setRadius(radius);
        assertEquals(radius, instance.getRadius(), 0.0);

        radius = 0.00000001;
        instance.setRadius(radius);
        assertEquals(radius, instance.getRadius(), 0.0);

        double[] badValues = {0.0, -0.1, -1.0};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setRadius(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(radius, instance.getRadius(), 0.0);
        }
    }

}