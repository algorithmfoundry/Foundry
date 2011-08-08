/*
 * File:                GaussNewtonAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 4, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.SupervisedLearnerTestHarness;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;

/**
 * JUnit tests for class GaussNewtonAlgorithmTest
 * @author Kevin R. Dixon
 */
public class GaussNewtonAlgorithmTest
    extends SupervisedLearnerTestHarness
{

    /**
     * Entry point for JUnit tests for class GaussNewtonAlgorithmTest
     * @param testName name of this test
     */
    public GaussNewtonAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public GaussNewtonAlgorithm createInstance(
        GradientDescendable objectToOptimize )
    {
        GaussNewtonAlgorithm gna = new GaussNewtonAlgorithm();
        gna.setObjectToOptimize( objectToOptimize );
        return gna;
    }

}
