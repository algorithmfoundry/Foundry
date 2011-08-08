/*
 * File:                MinimizerBasedRootFinderTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 9, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.root;

/**
 * Unit tests for MinimizerBasedRootFinderTest.
 *
 * @author krdixon
 */
public class MinimizerBasedRootFinderTest
    extends RootFinderTestHarness
{
    
    /**
     * Creates tests for MinimizerBasedRootFinderTest
     * @param testName Name
     */
    public MinimizerBasedRootFinderTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public MinimizerBasedRootFinder createInstance()
    {
        return new MinimizerBasedRootFinder();
    }


}
