/*
 * File:                RootFinderBisectionMethodTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.root;

/**
 * JUnit tests for class RootFinderBisectionMethodTest
 * @author Kevin R. Dixon
 */
public class RootFinderBisectionMethodTest
    extends RootFinderTestHarness
{

    /**
     * Entry point for JUnit tests for class RootFinderBisectionMethodTest
     * @param testName name of this test
     */
    public RootFinderBisectionMethodTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public RootFinderBisectionMethod createInstance()
    {
        return new RootFinderBisectionMethod();
    }

}
