/*
 * File:                SHA1HashTest.java
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

/**
 * Unit tests for SHA1HashTest.
 *
 * @author krdixon
 */
public class SHA1HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class SHA1HashTest.
     * @param testName Name of the test.
     */
    public SHA1HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class SHA1HashTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        SHA1Hash instance = new SHA1Hash();
        assertNotNull( instance );
    }

    @Override
    public SHA1Hash createInstance()
    {
        return new SHA1Hash();
    }

    @Override
    public void testLength()
    {
        System.out.println( "length" );

        SHA1Hash instance = this.createInstance();
        assertEquals( 20, instance.length() );
        assertEquals( 20, SHA1Hash.LENGTH );
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "EvaluateKnownValues" );

        // http://people.eku.edu/styere/Encrypt/JS-SHA1.html

        assertEquals( "b7e23ec29af22b0b4e41da31e868d57226121c84",
            HashFunctionUtil.toHexString( SHA1Hash.hash( "hello, world".getBytes() )) );
        assertEquals( "960efcd68035fbbf73c16becadb6a4c8ad615a01",
            HashFunctionUtil.toHexString( SHA1Hash.hash( "www.sandia.gov".getBytes() )) );
        assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709",
            HashFunctionUtil.toHexString( SHA1Hash.hash( "".getBytes() )) );
        assertEquals( "39145145afde8ec0b09b0ef374a1935f66815728",
            HashFunctionUtil.toHexString( SHA1Hash.hash( "java.sun.com".getBytes() )) );
        assertEquals( "694b24544537a47d53e8d4b29ae31d839f61da6b",
            HashFunctionUtil.toHexString( SHA1Hash.hash( "something that is really really really really really long".getBytes() )) );
        assertEquals( "0000000000000000000000000000000000000000",
            HashFunctionUtil.toHexString( SHA1Hash.hash( null )) );


    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  21.202
        //Total Average Time: 31.45
        //Relative Entropy = 1.8471565919866428E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }


}
