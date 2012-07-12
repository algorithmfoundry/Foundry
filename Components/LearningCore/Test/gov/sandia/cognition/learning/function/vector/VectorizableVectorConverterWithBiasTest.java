/*
 * File:                VectorizableVectorConverterWithBiasTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright December 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorizableVectorConverterWithBias
 * 
 * @author Justin Basilico
 */
public class VectorizableVectorConverterWithBiasTest
    extends TestCase
{

    /**
     * Creates a new unit test instance.
     * 
     * @param testName
     *      The test name.
     */
    public VectorizableVectorConverterWithBiasTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class VectorizableVectorConverterWithBias.
     */
    public void testConstructors()
    {
        VectorizableVectorConverterWithBias instance =
            new VectorizableVectorConverterWithBias();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        VectorFactory<?> vectorFactory = new DenseVectorFactoryMTJ();
        instance = new VectorizableVectorConverterWithBias(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of clone method, of class VectorizableVectorConverterWithBias.
     */
    public void testClone()
    {
        VectorizableVectorConverterWithBias instance =
            new VectorizableVectorConverterWithBias();
        VectorizableVectorConverterWithBias clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);
        assertSame(instance.getVectorFactory(), clone.getVectorFactory());
    }

    /**
     * Test of evaluate method, of class VectorizableVectorConverterWithBias.
     */
    public void testEvaluate()
    {
        VectorizableVectorConverterWithBias instance =
            new VectorizableVectorConverterWithBias();

        double x = Math.random();
        double y = Math.random();
        Vector2 input = new Vector2(x, y);
        Vector3 output = new Vector3(x, y, 1.0);
        assertEquals(output, instance.evaluate(input));
    }

    /**
     * Test of getVectorFactory method, of class VectorizableVectorConverterWithBias.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class VectorizableVectorConverterWithBias.
     */
    public void testSetVectorFactory()
    {
        VectorizableVectorConverterWithBias instance =
            new VectorizableVectorConverterWithBias();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        VectorFactory<?> vectorFactory = new DenseVectorFactoryMTJ();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = null;
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

}
