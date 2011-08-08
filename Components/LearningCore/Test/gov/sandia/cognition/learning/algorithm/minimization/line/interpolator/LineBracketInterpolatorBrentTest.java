/*
 * File:                LineBracketInterpolatorBrentTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 27, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * JUnit tests for class LineBracketInterpolatorBrentTest
 * @author Kevin R. Dixon
 */
public class LineBracketInterpolatorBrentTest
    extends LineBracketInterpolatorTestHarness<Evaluator<Double,Double>>
{

    /**
     * Entry point for JUnit tests for class LineBracketInterpolatorBrentTest
     * @param testName name of this test
     */
    public LineBracketInterpolatorBrentTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public LineBracketInterpolatorBrent createInstance()
    {
        return new LineBracketInterpolatorBrent();
    }

}
