/*
 * File:                DefaultKernelBinaryCategorizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 06, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.collection.CollectionUtil;
import java.util.Random;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultKernelBinaryCategorizer.
 *
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class DefaultKernelBinaryCategorizerTest
{

    /** Random number generator for tests. */
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public DefaultKernelBinaryCategorizerTest()
    {
    }

    /**
     * Test constructors of class DefaultKernelBinaryCategorizer.
     */
    public void testConstructors()
    {
        double bias = KernelBinaryCategorizer.DEFAULT_BIAS;
        DefaultKernelBinaryCategorizer<Vector> instance =
            new DefaultKernelBinaryCategorizer<Vector>();
        assertNull(instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(bias, instance.getBias(), 0.0);

        LinearKernel kernel = LinearKernel.getInstance();
        instance = new DefaultKernelBinaryCategorizer<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertTrue(instance.getExamples().isEmpty());
        assertEquals(bias, instance.getBias(), 0.0);

        ArrayList<DefaultWeightedValue<Vector>> examples =
            new ArrayList<DefaultWeightedValue<Vector>>();
        examples.add(
            new DefaultWeightedValue<Vector>(new Vector3(
                random.nextGaussian(), random.nextGaussian(), random.nextGaussian())) );
        bias = random.nextGaussian();
        instance = new DefaultKernelBinaryCategorizer<Vector>(kernel, examples, bias);
        assertSame(kernel, instance.getKernel());
        assertSame(examples, instance.getExamples());
        assertEquals(bias, instance.getBias(), 0.0);
    }

    /**
     * Test of add method, of class DefaultKernelBinaryCategorizer.
     */
    @Test
    public void testAdd()
    {
        DefaultKernelBinaryCategorizer<Vector> instance =
            new DefaultKernelBinaryCategorizer<Vector>();
        assertTrue(instance.getExamples().isEmpty());

        Vector v1 = new Vector3();
        double w1 = random.nextGaussian();
        instance.add(v1, w1);
        assertEquals(1, instance.getExamples().size());
        assertSame(v1, CollectionUtil.getFirst(instance.getExamples()).getValue());
        assertEquals(w1, CollectionUtil.getFirst(instance.getExamples()).getWeight(), 0.0);

        Vector v2 = new Vector3(
                random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
        double w2 = random.nextGaussian();
        instance.add(v2, w2);
        assertEquals(2, instance.getExamples().size());
        assertSame(v2, CollectionUtil.getElement(instance.getExamples(), 1).getValue());
        assertEquals(w2, CollectionUtil.getElement(instance.getExamples(), 1).getWeight(), 0.0);

        instance.add(v2, w2);
        assertEquals(3, instance.getExamples().size());
        assertSame(v2, CollectionUtil.getElement(instance.getExamples(), 2).getValue());
        assertEquals(w2, CollectionUtil.getElement(instance.getExamples(), 2).getWeight(), 0.0);

        // Test adding a zero-weight.
        instance.add(v2, 0.0);
        assertEquals(4, instance.getExamples().size());
        assertSame(v2, CollectionUtil.getElement(instance.getExamples(), 3).getValue());
        assertEquals(0.0, CollectionUtil.getElement(instance.getExamples(), 3).getWeight(), 0.0);

        // Test adding null.
        Vector v5 = null;
        double w5 = random.nextGaussian();
        instance.add(v5, w5);
        assertEquals(5, instance.getExamples().size());
        assertSame(v5, CollectionUtil.getElement(instance.getExamples(), 4).getValue());
        assertEquals(w5, CollectionUtil.getElement(instance.getExamples(), 4).getWeight(), 0.0);
    }

}