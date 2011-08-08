/*
 * File:                VectorFactoryContainerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import junit.framework.TestCase;

/**
 * Unit tests for class VectorFactoryContainer.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorFactoryContainerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public VectorFactoryContainerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class VectorFactoryContainer.
     */
    public void testConstructors()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        VectorFactoryContainer instance = new VectorFactoryContainer();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new DenseVectorFactoryMTJ();
        instance = new VectorFactoryContainer(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of getVectorFactory method, of class VectorFactoryContainer.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class VectorFactoryContainer.
     */
    public void testSetVectorFactory()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        VectorFactoryContainer instance = new VectorFactoryContainer();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new DenseVectorFactoryMTJ();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());


        vectorFactory = new DenseVectorFactoryMTJ();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = null;
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

}
