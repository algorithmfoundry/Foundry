/*
 * File:                DefaultVectorFactoryContainerTest.java
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
 * Unit tests for class DefaultVectorFactoryContainer.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultVectorFactoryContainerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultVectorFactoryContainerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class DefaultVectorFactoryContainer.
     */
    public void testConstructors()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        DefaultVectorFactoryContainer instance = new DefaultVectorFactoryContainer();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new DenseVectorFactoryMTJ();
        instance = new DefaultVectorFactoryContainer(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of getVectorFactory method, of class DefaultVectorFactoryContainer.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class DefaultVectorFactoryContainer.
     */
    public void testSetVectorFactory()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        DefaultVectorFactoryContainer instance = new DefaultVectorFactoryContainer();
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
