/*
 * File:                ProjectronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class Projectron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class ProjectronTest
    extends OnlineKernelBinaryLearnerTestHarness<DefaultKernelBinaryCategorizer<Vector>>
{
    /**
     * Creates a new test.
     */
    public ProjectronTest()
    {
    }

    @Override
    protected Projectron<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new Projectron<Vector>(kernel);
    }

    /**
     * Test of constructors of class Projectron.
     */
    @Test
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        double eta = Projectron.DEFAULT_ETA;
        Projectron<Vector> instance = new Projectron<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);

        kernel = new PolynomialKernel(
            1 + this.random.nextInt(10), this.random.nextDouble());
        instance = new Projectron<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);

        eta = random.nextDouble();
        instance = new Projectron<Vector>(kernel, eta);
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);
    }


    /**
     * Test of update method, of class Projectron.
     */
    @Test
    public void testUpdate()
    {
        Projectron<Vector> instance = this.createInstance(new LinearKernel());
        DefaultKernelBinaryCategorizer<Vector> learned =
            instance.createInitialLearnedObject();;

        Vector input = new Vector2(2.0, 3.0);
        Boolean output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(1, learned.getExamples().size());
        assertEquals(output, learned.evaluate(input));

        input = new Vector2(4.0, 4.0);
        output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(1, learned.getExamples().size());
        assertEquals(output, learned.evaluate(input));

        input = new Vector2(1.0, -1.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(output, learned.evaluate(input));

        input = new Vector2(1.0, -1.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(output, learned.evaluate(input));

        input = new Vector2(2.0, 3.0);
        output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(output, learned.evaluate(input));

        input = new Vector2(2.0, -3.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(output, learned.evaluate(input));
    }

    /**
     * Test of getEta method, of class Projectron.
     */
    @Test
    public void testGetEta()
    {
        this.testSetEta();
    }

    /**
     * Test of setEta method, of class Projectron.
     */
    @Test
    public void testSetEta()
    {
        double eta = Projectron.DEFAULT_ETA;
        Projectron<Vector> instance = this.createInstance(null);
        assertEquals(eta, instance.getEta(), 0.0);

        double[] goodValues = {0.0, 0.1, 0.001, 1.0, 2.0, random.nextDouble()};
        for (double goodValue : goodValues)
        {
            eta = goodValue;
            instance.setEta(eta);
            assertEquals(eta, instance.getEta(), 0.0);
        }

        double[] badValues = {-0.1, -1.0, -1.0, -random.nextDouble() };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setEta(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(eta, instance.getEta(), 0.0);
        }
    }

}