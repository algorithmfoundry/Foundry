/*
 * File:                WinnowTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 * 
 * Copyright January 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import java.util.Random;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;import junit.framework.TestCase;
;

/**
 * Unit tests for class Winnow.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class WinnowTest
    extends TestCase
{
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public WinnowTest()
    {
    }

    /**
     * Test of constructors of class Winnow.
     */
    public void testConstructors()
    {
        double weightUpdate = Winnow.DEFAULT_WEIGHT_UPDATE;
        boolean demoteToZero = Winnow.DEFAULT_DEMOTE_TO_ZERO;
        Winnow instance = new Winnow();
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);
        assertEquals(demoteToZero, instance.isDemoteToZero());

        weightUpdate = 1.234;
        instance = new Winnow(weightUpdate);
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);
        assertEquals(demoteToZero, instance.isDemoteToZero());

        weightUpdate = 2.345;
        demoteToZero = !demoteToZero;
        instance = new Winnow(weightUpdate, demoteToZero);
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);
        assertEquals(demoteToZero, instance.isDemoteToZero());
    }

    /**
     * Test of createInitialLearnedObject method, of class Winnow.
     */
    public void testCreateInitialLearnedObject()
    {
        Winnow instance = new Winnow();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(result.getThreshold(), 0.0, 0.0);
    }

    /**
     * Test of update method, of class Winnow.
     */
    public void testUpdate()
    {
        int d = 20;
        int k = 4;
        double p = 0.25;
        VectorFactory<?> f = VectorFactory.getDenseDefault();
        Winnow instance = new Winnow();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();

        // This tests a k-disjunction, which Winnow should be able to learn.
        for (int i = 0; i < 300; i++)
        {
            Vector input = f.createVector(d);
            for (int j = 0; j < d; j++)
            {
                input.setElement(j, this.random.nextDouble() <= p ? 1.0 : 0.0);
            }

            boolean output = false;
            for (int j = 0; j < k && !output; j++)
            {
                output = input.getElement(j) != 0.0;
            }

            instance.update(result, DefaultInputOutputPair.create(input, output));
        }

        for (int i = 0; i < 100; i++)
        {
            Vector input = f.createVector(d);
            for (int j = 0; j < d; j++)
            {
                input.setElement(j, this.random.nextDouble() <= p ? 1.0 : 0.0);
            }

            boolean output = false;
            for (int j = 0; j < k && !output; j++)
            {
                output = input.getElement(j) != 0.0;
            }

            assertEquals(output, (boolean) result.evaluate(input));
        }
    }

    /**
     * Test of getWeightUpdate method, of class Winnow.
     */
    public void testGetWeightUpdate()
    {
        this.testSetWeightUpdate();
    }

    /**
     * Test of setWeightUpdate method, of class Winnow.
     */
    public void testSetWeightUpdate()
    {
        double weightUpdate = Winnow.DEFAULT_WEIGHT_UPDATE;
        Winnow instance = new Winnow();
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);

        weightUpdate = 1.234;
        instance.setWeightUpdate(weightUpdate);
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);

        weightUpdate = 2.345;
        instance.setWeightUpdate(weightUpdate);
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setWeightUpdate(1.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);


        exceptionThrown = false;
        try
        {
            instance.setWeightUpdate(0.5);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);


        exceptionThrown = false;
        try
        {
            instance.setWeightUpdate(0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);


        exceptionThrown = false;
        try
        {
            instance.setWeightUpdate(-1.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(weightUpdate, instance.getWeightUpdate(), 0.0);

    }

    /**
     * Test of isDemoteToZero method, of class Winnow.
     */
    public void testIsDemoteToZero()
    {
        this.testSetDemoteToZero();
    }

    /**
     * Test of setDemoteToZero method, of class Winnow.
     */
    public void testSetDemoteToZero()
    {
        boolean demoteToZero = Winnow.DEFAULT_DEMOTE_TO_ZERO;
        Winnow instance = new Winnow();
        assertEquals(demoteToZero, instance.isDemoteToZero());

        demoteToZero = true;
        instance.setDemoteToZero(demoteToZero);
        assertEquals(demoteToZero, instance.isDemoteToZero());

        demoteToZero = false;
        instance.setDemoteToZero(demoteToZero);
        assertEquals(demoteToZero, instance.isDemoteToZero());

        demoteToZero = true;
        instance.setDemoteToZero(demoteToZero);
        assertEquals(demoteToZero, instance.isDemoteToZero());
    }

}