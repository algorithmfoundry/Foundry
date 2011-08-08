/*
 * File:                LevenbergMarquardtEstimationTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.SupervisedLearnerTestHarness;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;

/**
 *
 * @author Kevin R. Dixon
 */
public class LevenbergMarquardtEstimationTest
    extends SupervisedLearnerTestHarness
{
    
    public LevenbergMarquardtEstimationTest(String testName)
    {
        super(testName);
    }

    public LevenbergMarquardtEstimation createInstance(
        GradientDescendable objectToOptimize)
    {
        LevenbergMarquardtEstimation lma = new LevenbergMarquardtEstimation();
        lma.setObjectToOptimize( objectToOptimize );
        return lma;
    }
        
}
