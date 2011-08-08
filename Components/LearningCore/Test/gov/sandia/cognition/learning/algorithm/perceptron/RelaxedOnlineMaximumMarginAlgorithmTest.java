/*
 * File:                RelaxedOnlineMaximumMarginAlgorithmTest.java
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

import gov.sandia.cognition.learning.algorithm.perceptron.RelaxedOnlineMaximumMarginAlgorithm;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class RelaxedOnlineMaximumMarginAlgorithm.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class RelaxedOnlineMaximumMarginAlgorithmTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{
    /**
     * Creates a new test.
     */
    public RelaxedOnlineMaximumMarginAlgorithmTest()
    {
    }

    /**
     * Test of constructors method, of class RelaxedOnlineMaximumMarginAlgorithm.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        RelaxedOnlineMaximumMarginAlgorithm instance = new RelaxedOnlineMaximumMarginAlgorithm();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getSparseDefault();
        instance = new RelaxedOnlineMaximumMarginAlgorithm(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    @Override
    protected RelaxedOnlineMaximumMarginAlgorithm createLinearInstance()
    {
        return new RelaxedOnlineMaximumMarginAlgorithm();
    }

}