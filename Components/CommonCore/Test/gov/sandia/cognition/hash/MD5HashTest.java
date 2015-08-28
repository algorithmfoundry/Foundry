/*
 * File:                MD5HashTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Jan 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.hash;

import gov.sandia.cognition.hash.MD5Hash;
import gov.sandia.cognition.hash.HashFunctionUtil;

/**
 * Unit tests for MD5HashTest.
 *
 * @author krdixon
 */
public class MD5HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class MD5HashTest.
     * @param testName Name of the test.
     */
    public MD5HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MD5HashTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        MD5Hash instance = new MD5Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class MD5Hash.
     */
    @Override
    public void testLength()
    {
        System.out.println("length");
        MD5Hash instance = this.createInstance();
        assertEquals( 16, instance.length() );
        assertEquals( 16, MD5Hash.LENGTH );
    }

    @Override
    public MD5Hash createInstance()
    {
        return new MD5Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "Evaluate known values" );

        MD5Hash instance = this.createInstance();

        assertEquals( "d41d8cd98f00b204e9800998ecf8427e", HashFunctionUtil.toHexString(
            instance.evaluate( "".getBytes() ) ) );

        assertEquals( "9e107d9d372bb6826bd81d3542a419d6", HashFunctionUtil.toHexString(
            instance.evaluate( "The quick brown fox jumps over the lazy dog".getBytes() ) ) );

        assertEquals( "e4d909c290d0fb1ca068ffaddf22cbd0", HashFunctionUtil.toHexString(
            instance.evaluate( "The quick brown fox jumps over the lazy dog.".getBytes() ) ) );

    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  5.872
        //Total Average Time: 15.968
        //Relative Entropy = 1.8742367034413343E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
