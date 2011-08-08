/*
 * File:                AbstractBatchLearnerWrapperTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.learning.algorithm.baseline.MeanLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AbstractBatchLearnerWrapperTest.
 *
 * @author krdixon
 */
public class AbstractBatchLearnerWrapperTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class AbstractBatchLearnerWrapperTest.
     * @param testName Name of the test.
     */
    public AbstractBatchLearnerWrapperTest(
        String testName)
    {
        super(testName);
    }

    public static class DefaultWrapper
        extends AbstractBatchLearnerWrapper<Collection<? extends InputOutputPair<?, Double>>,ConstantEvaluator<Double>,MeanLearner>
    {

        public DefaultWrapper()
        {
            super();
            this.setLearner( new MeanLearner() );
        }
        
    }

    public AbstractBatchLearnerWrapper createInstance()
    {
        return new DefaultWrapper();
    }

    /**
     * Tests the constructors of class AbstractBatchLearnerWrapperTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        AbstractBatchLearnerWrapper instance = this.createInstance();
        assertNotNull( instance );
        assertNotNull( instance.getLearner() );

    }

    /**
     * Test of clone method, of class AbstractBatchLearnerWrapper.
     */
    public void testClone()
    {
        System.out.println("clone");
        AbstractBatchLearnerWrapper instance = this.createInstance();
        AbstractBatchLearnerWrapper clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getLearner() );
        assertNotSame( instance.getLearner(), clone.getLearner() );
    }

    /**
     * Test of getLearner method, of class AbstractBatchLearnerWrapper.
     */
    public void testGetLearner()
    {
        System.out.println("getLearner");
        AbstractBatchLearnerWrapper instance = this.createInstance();
        assertNotNull( instance.getLearner() );
    }

}
