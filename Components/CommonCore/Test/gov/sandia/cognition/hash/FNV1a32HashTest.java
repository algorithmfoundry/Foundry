/*
 * File:                FNV1a32Hash.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 14, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.hash;

/**
 * Unit tests for FNV1a32HashTest.
 *
 * @author krdixon
 */
public class FNV1a32HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class FNV1a32HashTest.
     * @param testName Name of the test.
     */
    public FNV1a32HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class FNV1a32HashTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        FNV1a32Hash instance = new FNV1a32Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class FNV1a32Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        FNV1a32Hash instance = this.createInstance();
        assertEquals( 4, instance.length() );
        assertEquals( 4, FNV1a32Hash.LENGTH );
    }

    @Override
    public FNV1a32Hash createInstance()
    {
        return new FNV1a32Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "evaluate known values" );

        // http://isthe.com/chongo/src/fnv/test_fnv.c

        assertEquals( "811c9dc5", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "".getBytes() ) ) );
        assertEquals( "e40c292c", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "a".getBytes() ) ) );
        assertEquals( "e70c2de5", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "b".getBytes() ) ) );
        assertEquals( "e60c2c52", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "c".getBytes() ) ) );
        assertEquals( "bf9cf968", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "foobar".getBytes() ) ) );
        assertEquals( "9a8b6805", HashFunctionUtil.toHexString(
            FNV1a32Hash.hash( "http://www.nature.nps.gov/air/webcams/parks/havoso2alert/timelines_24.cfm".getBytes() ) ) );

    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  2.122
        //Total Average Time: 11.641
        //Relative Entropy = 2.021905682678682E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
