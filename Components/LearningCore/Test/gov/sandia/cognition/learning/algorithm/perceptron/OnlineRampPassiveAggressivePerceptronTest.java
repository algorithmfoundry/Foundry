/*
 * File:                OnlineRampPassiveAggressivePerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.perceptron.OnlineRampPassiveAggressivePerceptron;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlineRampPassiveAggressivePerceptron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineRampPassiveAggressivePerceptronTest
    extends OnlinePassiveAggressivePerceptronQuadraticSoftMarginTest
{
    
    /**
     * Creates a new test.
     */
    public OnlineRampPassiveAggressivePerceptronTest()
    {
    }

    /**
     * Test of constructors of class OnlineRampPassiveAggressivePerceptron.
     */
    @Test
    @Override
    public void testConstructors()
    {
        double aggressiveness = OnlinePassiveAggressivePerceptron.QuadraticSoftMargin.DEFAULT_AGGRESSIVENESS;
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlineRampPassiveAggressivePerceptron instance = new OnlineRampPassiveAggressivePerceptron();
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        aggressiveness = this.random.nextDouble();
        instance = new OnlineRampPassiveAggressivePerceptron(aggressiveness);
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlineRampPassiveAggressivePerceptron(aggressiveness, factory);
        assertEquals(aggressiveness, instance.getAggressiveness(), 0.0);
        assertSame(factory, instance.getVectorFactory());
    }

    @Override
    protected OnlineRampPassiveAggressivePerceptron createLinearInstance()
    {
        return new OnlineRampPassiveAggressivePerceptron();
    }

}