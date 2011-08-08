/*
 * File:                AggressiveRelaxedOnlineMaximumMarginAlgorithmTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 07, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.perceptron.AggressiveRelaxedOnlineMaximumMarginAlgorithm;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AggressiveRelaxedOnlineMaximumMarginAlgorithm.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class AggressiveRelaxedOnlineMaximumMarginAlgorithmTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{
    /**
     * Creates a new test.
     */
    public AggressiveRelaxedOnlineMaximumMarginAlgorithmTest()
    {
    }

    /**
     * Test of constructors method, of class AggressiveRelaxedOnlineMaximumMarginAlgorithm.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        AggressiveRelaxedOnlineMaximumMarginAlgorithm instance = new AggressiveRelaxedOnlineMaximumMarginAlgorithm();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getSparseDefault();
        instance = new AggressiveRelaxedOnlineMaximumMarginAlgorithm(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    @Override
    protected AggressiveRelaxedOnlineMaximumMarginAlgorithm createLinearInstance()
    {
        return new AggressiveRelaxedOnlineMaximumMarginAlgorithm();
    }


}