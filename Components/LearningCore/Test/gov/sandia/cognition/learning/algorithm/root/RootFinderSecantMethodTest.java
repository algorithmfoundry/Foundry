/*
 * File:                RootFinderSecantMethodTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.root;

/**
 * JUnit tests for class RootFinderSecantMethodTest
 * @author Kevin R. Dixon
 */
public class RootFinderSecantMethodTest
    extends RootFinderTestHarness
{

    /**
     * Entry point for JUnit tests for class RootFinderSecantMethodTest
     * @param testName name of this test
     */
    public RootFinderSecantMethodTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public RootFinderSecantMethod createInstance()
    {
        return new RootFinderSecantMethod();
    }

}
