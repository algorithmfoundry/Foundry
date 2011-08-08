/*
 * File:                DecoupledVectorFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.SigmoidFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class DecoupledVectorFunctionTest
    extends TestCase
{
    /** The random number generator for the tests. */
    protected Random random = new Random(1);
    
    public DecoupledVectorFunctionTest(
        String testName)
    {
        super(testName);
    }

    public DecoupledVectorFunction createInstance()
    {
        int num = (int) (Math.random() * 3) + 1;
        Collection<Evaluator<Double, Double>> functions =
            new ArrayList<Evaluator<Double, Double>>(num);
        for (int i = 0; i < num; i++)
        {
            functions.add(new PolynomialFunction(Math.random() * 10));
        }
        return new DecoupledVectorFunction(functions);
    }

    /**
     * Test of getScalarFunctions method, of class gov.sandia.cognition.learning.util.function.DecoupledVectorFunction.
     */
    public void testGetScalarFunctions()
    {
        System.out.println("getScalarFunctions");

        DecoupledVectorFunction instance = this.createInstance();
        assertNotNull(instance.getScalarFunctions());

    }

    /**
     * Test of setScalarFunctions method, of class gov.sandia.cognition.learning.util.function.DecoupledVectorFunction.
     */
    public void testSetScalarFunctions()
    {
        System.out.println("setScalarFunctions");

        DecoupledVectorFunction instance = createInstance();

        LinkedList<Evaluator<? super Double, Double>> f2 =
            new LinkedList<Evaluator<? super Double, Double>>(instance.getScalarFunctions());
        assertNotSame(f2, instance.getScalarFunctions());
        instance.setScalarFunctions(f2);
        assertSame(f2, instance.getScalarFunctions());

        try
        {
            instance.setScalarFunctions(null);
            fail("Scalar Functions can't be null");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            instance.setScalarFunctions(new LinkedList<Evaluator<Double, Double>>());
            fail("Must have at least one function!");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.DecoupledVectorFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        DecoupledVectorFunction instance = this.createInstance();
        int M = instance.getDimensionality();
        Vector input = VectorFactory.getDefault().createUniformRandom(M, 0, 2, random);
        Vector output = instance.evaluate(input);

        assertEquals(M, output.getDimensionality());

        int i = 0;
        for (Evaluator<? super Double, Double> f : instance.getScalarFunctions())
        {
            assertEquals(f.evaluate(input.getElement(i)), output.getElement(i), 1e-5);
            i++;
        }

        Vector x2 = VectorFactory.getDefault().createUniformRandom(M + 1, -1, 1, random);
        try
        {
            instance.evaluate(x2);
            fail("Dimensionalities don't match");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.DecoupledVectorFunction.
     */
    public void testClone()
    {
        System.out.println("clone");

        DecoupledVectorFunction instance = this.createInstance();
        DecoupledVectorFunction clone = (DecoupledVectorFunction) instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.getDimensionality(), clone.getDimensionality());

    }

    /**
     * Test of getDimensionality method, of class gov.sandia.cognition.learning.util.function.DecoupledVectorFunction.
     */
    public void testGetDimensionality()
    {
        System.out.println("getDimensionality");

        DecoupledVectorFunction instance = new DecoupledVectorFunction(
            new SigmoidFunction(), new PolynomialFunction(1.0));
        assertEquals(instance.getScalarFunctions().size(), instance.getDimensionality());

    }

}
