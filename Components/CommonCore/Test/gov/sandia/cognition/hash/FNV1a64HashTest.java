/*
 * File:                FNV1a64Hash.java
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
public class FNV1a64HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class FNV1a32HashTest.
     * @param testName Name of the test.
     */
    public FNV1a64HashTest(
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
        FNV1a64Hash instance = new FNV1a64Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class FNV1a32Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        FNV1a64Hash instance = this.createInstance();
        assertEquals( 8, instance.length() );
        assertEquals( 8, FNV1a64Hash.LENGTH );
    }

    @Override
    public FNV1a64Hash createInstance()
    {
        return new FNV1a64Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "evaluate known values" );

        // http://isthe.com/chongo/src/fnv/test_fnv.c
        FNV1a64Hash instance = this.createInstance();

        assertEquals( "cbf29ce484222325", HashFunctionUtil.toHexString(
            instance.evaluate( "".getBytes() ) ) );
        assertEquals( "af63dc4c8601ec8c", HashFunctionUtil.toHexString(
            instance.evaluate( "a".getBytes() ) ) );
        assertEquals( "af63df4c8601f1a5", HashFunctionUtil.toHexString(
            instance.evaluate( "b".getBytes() ) ) );
        assertEquals( "af63de4c8601eff2", HashFunctionUtil.toHexString(
            instance.evaluate( "c".getBytes() ) ) );
        assertEquals( "85944171f73967e8", HashFunctionUtil.toHexString(
            instance.evaluate( "foobar".getBytes() ) ) );
        assertEquals( "5b21060aed8412e5", HashFunctionUtil.toHexString(
            instance.evaluate( "http://www.nature.nps.gov/air/webcams/parks/havoso2alert/timelines_24.cfm".getBytes() ) ) );

    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  3.86
        //Total Average Time: 14.125
        //Relative Entropy = 2.686181899786977E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
