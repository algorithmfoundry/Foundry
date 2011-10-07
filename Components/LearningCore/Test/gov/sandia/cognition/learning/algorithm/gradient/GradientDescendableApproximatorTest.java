/*
 * File:                GradientDescendableApproximatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.gradient;

import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.vector.DifferentiableGeneralizedLinearModel;
import gov.sandia.cognition.learning.function.vector.ElementWiseDifferentiableVectorFunction;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class GradientDescendableApproximatorTest extends TestCase
{

    public GradientDescendableApproximatorTest(String testName)
    {
        super(testName);
    }

    int NUM_INPUTS = 3;

    int NUM_OUTPUTS = 2;

    double DELTA_SIZE = 1e-5;
    
    /** The random number generator for the tests. */
    private Random random = new Random();

    public static class F1
        extends MultivariateDiscriminant
    {

        public int evals = 0;
        

        public F1(
            int numInputs,
            int numOutputs,
            Random random)
        {
            super(MatrixFactory.getDefault().createUniformRandom(numOutputs, numInputs, -10.0, 10.0, random));
        }

        public F1(
            F1 other)
        {
            super(other);
        }

        public F1 clone()
        {
            return new F1(this);
        }

        public Vector evaluate(
            Vector input)
        {
            this.evals++;
            return super.evaluate(input);
        }

    }

    public static class F2
        extends DifferentiableGeneralizedLinearModel
    {

        public int evals = 0;

        public F2(
            int numInputs,
            int numOutputs,
            Random random)
        {
            super(new MultivariateDiscriminant(
                MatrixFactory.getDefault().createUniformRandom(numOutputs, numInputs, -10.0, 10.0, random)),
                new ElementWiseDifferentiableVectorFunction(new AtanFunction(1.0)));
//            super( MatrixFactory.getDefault().createUniformRandom( numOutputs, numInputs, -10.0, 10.0 )
        }

        public F2(
            F2 other)
        {
            super(other);
        }

        public F2 clone()
        {
            return new F2(this);
        }

        public Vector evaluate(
            Vector input)
        {
            this.evals++;
            return super.evaluate(input);
        }

    }

    public GradientDescendableApproximator createInstance()
    {
        return new GradientDescendableApproximator(new F2(NUM_INPUTS, NUM_OUTPUTS, random), DELTA_SIZE);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        GradientDescendableApproximator instance = new GradientDescendableApproximator();
        assertNull( instance.getFunction() );        
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testClone()
    {
        System.out.println("clone");

        GradientDescendableApproximator instance = this.createInstance();
        GradientDescendableApproximator clone = instance.clone();


        assertNotSame(instance, clone);
        assertNotSame(instance.getFunction(), clone.getFunction());
        assertEquals(instance.getFunction().convertToVector(), clone.getFunction().convertToVector());
    }

    /**
     * Test of getFunction method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testGetFunction()
    {
        System.out.println("getFunction");

        GradientDescendableApproximator instance = this.createInstance();
        assertNotNull(instance);
    }

    /**
     * Test of setFunction method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testSetFunction()
    {
        System.out.println("setFunction");

        GradientDescendableApproximator instance = this.createInstance();
        assertNotNull(instance);

        VectorizableVectorFunction f = instance.getFunction();
        instance.setFunction(null);
        assertNull(instance.getFunction());

        instance.setFunction(f);
        assertSame(f, instance.getFunction());

    }

    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        GradientDescendableApproximator instance = this.createInstance();
        assertEquals(instance.getFunction().convertToVector(), instance.convertToVector());

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        GradientDescendableApproximator instance = this.createInstance();
        GradientDescendableApproximator clone = instance.clone();

        Vector p = instance.convertToVector();
        p.scaleEquals(Math.random());
        instance.convertFromVector(p);
        assertEquals(p, instance.convertToVector());

        Vector pc = clone.convertToVector();
        assertFalse(pc.equals(instance.convertToVector()));
        instance.convertFromVector(pc);
        assertEquals(pc, instance.convertToVector());


    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");


        double r = 1.0;
        Vector input = VectorFactory.getDefault().createUniformRandom(NUM_INPUTS, -r, r, random);
        GradientDescendableApproximator instance = this.createInstance();
        assertEquals(instance.getFunction().evaluate(input), instance.evaluate(input));

    }

    /**
     * Test of computeParameterGradient method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testComputeParameterGradient()
    {
        System.out.println("computeParameterGradient");

        double r = 1.0;

        for (int i = 0; i < 10; i++)
        {
            Vector input = VectorFactory.getDefault().createUniformRandom(NUM_INPUTS, -r, r, random);
            GradientDescendableApproximator instance = this.createInstance();

            Matrix Jhat = instance.computeParameterGradient(input);
            Matrix J = ((GradientDescendable) instance.getFunction()).computeParameterGradient(input);

            double delta = J.minus(Jhat).normFrobenius();
            System.out.println("Norm = " + delta);
//                + ", Evals: " + ((F1) instance.getFunction()).evals + " J: " + J.getNumRows() * J.getNumColumns() );

            if (delta > 1e-3)
            {
                System.out.println("Jacobian:\n" + J);
                System.out.println("Jacobian estimate:\n" + Jhat);
            }
            assertEquals(0.0, delta, 1e-3);

        }

    }

    /**
     * Test of getDeltaSize method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testGetDeltaSize()
    {
        System.out.println("getDeltaSize");

        GradientDescendableApproximator instance = this.createInstance();

        double result = instance.getDeltaSize();
        assertEquals(DELTA_SIZE, result);

    }

    /**
     * Test of setDeltaSize method, of class gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator.
     */
    public void testSetDeltaSize()
    {
        System.out.println("setDeltaSize");

        GradientDescendableApproximator instance = this.createInstance();

        double result = instance.getDeltaSize();
        assertEquals(DELTA_SIZE, result);

        result *= Math.random();
        instance.setDeltaSize(result);
        assertEquals(result, instance.getDeltaSize());
    }

}
