/*
 * File:                RootFinderNewtonsMethodTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.root;

/**
 * Unit tests for RootFinderNewtonsMethodTest.
 *
 * @author krdixon
 */
public class RootFinderNewtonsMethodTest
    extends RootFinderTestHarness
{

    /**
     * Tests for class RootFinderNewtonsMethodTest.
     * @param testName Name of the test.
     */
    public RootFinderNewtonsMethodTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class RootFinderNewtonsMethodTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        RootFinderNewtonsMethod instance = new RootFinderNewtonsMethod();
        assertNotNull( instance );
        assertNull( instance.getResult() );

    }

    @Override
    public RootFinderNewtonsMethod createInstance()
    {
        return new RootFinderNewtonsMethod();
    }


}
