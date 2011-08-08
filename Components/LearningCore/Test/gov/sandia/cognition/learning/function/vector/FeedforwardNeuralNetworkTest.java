/*
 * File:                FeedforwardNeuralNetworkTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.learning.function.scalar.SigmoidFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for FeedforwardNeuralNetworkTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class FeedforwardNeuralNetworkTest
    extends TestCase
{

    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    public FeedforwardNeuralNetworkTest(
        String testName)
    {
        super(testName);
    }

    public DifferentiableFeedforwardNeuralNetwork createRandom()
    {
        int numLayers = 2;
        ArrayList<Integer> numPerLayer = new ArrayList<Integer>(numLayers);
        for (int n = 0; n < numLayers; n++)
        {
            numPerLayer.add( random.nextInt(10) + 1 );
        }

        ArrayList<DifferentiableUnivariateScalarFunction> squashingFunctions =
            new ArrayList<DifferentiableUnivariateScalarFunction>(numLayers - 1);
        for (int n = 0; n < numLayers - 1; n++)
        {
            int which = random.nextInt(3);
            DifferentiableUnivariateScalarFunction foo = null;
            if (which == 0)
            {
                foo = new LinearFunction();
            }
            else if (which == 1)
            {
                foo = new AtanFunction(random.nextDouble());
            }
            else
            {
                foo = new SigmoidFunction();
            }
            squashingFunctions.add(foo);
        }

        DifferentiableFeedforwardNeuralNetwork ann =
            new DifferentiableFeedforwardNeuralNetwork(
            numPerLayer, squashingFunctions, random);
        Vector p = ann.convertToVector();
        Vector p2 = VectorFactory.getDefault().createUniformRandom(p.getDimensionality(), -1.0, 1.0, random);
        ann.convertFromVector(p2);
        return ann;

    }

    public void testConstuctor()
    {
        System.out.println("constructors");
        int numLayers = random.nextInt(5) + 3;
        ArrayList<Integer> nodes = new ArrayList<Integer>(numLayers);
        ArrayList<DifferentiableUnivariateScalarFunction> squashingFunctions =
            new ArrayList<DifferentiableUnivariateScalarFunction>(numLayers - 1);
        for (int n = 0; n < numLayers; n++)
        {
            nodes.add(random.nextInt(10) + 10);
            squashingFunctions.add( new AtanFunction() );
        }

        try
        {
            FeedforwardNeuralNetwork f =
                new FeedforwardNeuralNetwork(nodes, squashingFunctions);
            fail("Number of layers must equal layerActivationFunction + 1");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        squashingFunctions.remove(numLayers - 1);
        FeedforwardNeuralNetwork f =
            new FeedforwardNeuralNetwork(nodes, squashingFunctions);
        assertEquals(numLayers - 1, f.getLayers().size());
        for (int n = 0; n < numLayers - 1; n++)
        {
            assertEquals(nodes.get(n).intValue(), f.getLayers().get(n).getMatrixMultiply().getInternalMatrix().getNumColumns());
            assertEquals(nodes.get(n + 1).intValue(), f.getLayers().get(n).getMatrixMultiply().getInternalMatrix().getNumRows());
        }


    }

    /**
     * Test of clone method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testClone()
    {
        System.out.println("clone");

        FeedforwardNeuralNetwork instance = new FeedforwardNeuralNetwork(1, 2, 3, new AtanFunction(1.0));
        Vector p = instance.convertToVector();
        Vector p2 = VectorFactory.getDefault().createUniformRandom(p.getDimensionality(), -10.0, 10.0, random);
        instance.convertFromVector(p2);

        System.out.println("Neural Net:\n" + instance);
        FeedforwardNeuralNetwork clone = instance.clone();

        Vector v1 = instance.convertToVector();
        Vector v2 = clone.convertToVector();

        assertNotSame(instance, clone);
        assertNotSame(v1, v2);
        assertEquals(v1, v2);

        Vector v1delta = v1.scale(this.random.nextDouble() );
        instance.convertFromVector(v1delta);
        Vector v12 = instance.convertToVector();
        Vector v22 = clone.convertToVector();
        assertEquals(v2, v22);

        assertEquals(v12, v1delta);
        assertFalse(v12.equals(v1));
    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        FeedforwardNeuralNetwork instance = this.createRandom();
        Vector p1 = instance.convertToVector();
        FeedforwardNeuralNetwork clone = instance.clone();
        Vector p2 = clone.convertToVector();
        assertEquals(p1, p2);

        Vector p3 = p1.scale( this.random.nextDouble() );
        assertFalse(p3.equals(p1));
        instance.convertFromVector(p3);
        Vector p4 = instance.convertToVector();
        assertEquals(p3, p4);

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        FeedforwardNeuralNetwork instance = this.createRandom();
        Vector p1 = instance.convertToVector();
        FeedforwardNeuralNetwork clone = instance.clone();
        Vector p2 = clone.convertToVector();
        assertEquals(p1, p2);

        Vector p3 = p1.scale( this.random.nextDouble() );
        assertFalse(p3.equals(p1));
        instance.convertFromVector(p3);
        Vector p4 = instance.convertToVector();
        assertEquals(p3, p4);
    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        for (int i = 0; i < 10; i++)
        {
            FeedforwardNeuralNetwork ann = this.createRandom();
            int N = ann.getLayers().get(0).getMatrixMultiply().getInternalMatrix().getNumColumns();
            for (int j = 0; j < 100; j++)
            {
                Vector x = VectorFactory.getDefault().createUniformRandom(N, -10.0, 10.0, random);
                Vector xclone = x.clone();
                Vector yhat = ann.evaluate(x);
                assertEquals(x, xclone);

                Vector activation = x;
                for (int k = 0; k < ann.getLayers().size(); k++)
                {
                    activation = ann.getLayers().get(k).evaluate(activation);
                }
                assertEquals(activation, yhat);

            }
        }


    }

    /**
     * Test of getLayers method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testGetLayers()
    {
        System.out.println("getLayers");

        FeedforwardNeuralNetwork ann = this.createRandom();
        assertNotNull(ann.getLayers());

    }

    /**
     * Test of setLayers method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testSetLayers()
    {
        System.out.println("setLayers");

        FeedforwardNeuralNetwork ann = this.createRandom();

        assertNotNull(ann.getLayers());
        ArrayList<? extends SquashedMatrixMultiplyVectorFunction> layers = ann.getLayers();
        assertNotNull(layers);
        ann.setLayers(null);
        assertNull(ann.getLayers());
        ann.setLayers(layers);
        assertNotNull(ann.getLayers());

    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.FeedforwardNeuralNetwork.
     */
    public void testEvaluateTime()
    {
        System.out.println("evaluate.Time");
        int ni = 30;
        FeedforwardNeuralNetwork ann =
            new FeedforwardNeuralNetwork(ni, 20, 10, new AtanFunction(1.0));
        Vector p = ann.convertToVector();
        Vector p2 = VectorFactory.getDefault().createVector(p.getDimensionality(), 0.1);
        ann.convertFromVector(p2);

        int N = 100;
        ArrayList<Vector> inputs = new ArrayList<Vector>(N);
        Random r = new Random(0);
        for (int i = 0; i < N; i++)
        {
            inputs.add(VectorFactory.getDefault().createVector(ni, r.nextDouble()));
        }

        LinkedList<Double> times = new LinkedList<Double>();
        for (int i = 0; i < 100; i++)
        {
            LinkedList<Vector> outputs = new LinkedList<Vector>();
            long start = System.currentTimeMillis();
            for (Vector x : inputs)
            {
                Vector y = ann.evaluate(x);
                outputs.add(y);
            }
            long stop = System.currentTimeMillis();
            times.add((double) (stop - start));
        }

        ConfidenceInterval ci = new StudentTConfidence().computeConfidenceInterval(times, 0.95);
        System.out.println("Time: " + ci);

    }

}
