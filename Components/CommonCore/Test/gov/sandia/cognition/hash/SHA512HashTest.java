/*
 * File:                SHA512HashTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 7, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.hash;

/**
 * Unit tests for SHA512HashTest.
 *
 * @author krdixon
 */
public class SHA512HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class SHA512HashTest.
     * @param testName Name of the test.
     */
    public SHA512HashTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public SHA512Hash createInstance()
    {
        return new SHA512Hash();
    }

    /**
     * Tests the constructors of class SHA512HashTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        SHA512Hash instance = new SHA512Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class SHA256Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        SHA512Hash instance = this.createInstance();
        assertEquals( 64, instance.length() );
        assertEquals( 64, SHA512Hash.LENGTH );
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "Evaluate Known Values" );

        SHA512Hash instance = this.createInstance();

        // From: http://jssha.sourceforge.net/

        assertEquals( "8710339dcb6814d0d9d2290ef422285c9322b7163951f9a0ca8f883d3305286f44139aa374848e4174f5aada663027e4548637b6d19894aec4fb6c46a139fbf9",
            HashFunctionUtil.toHexString(instance.evaluate("hello, world".getBytes()) ) );
        assertEquals( "950c3aca364028239f9ce208395a8d58617418a78ef0c5f58288f3b8507bdcb512d20969b5e8a0df371479d90abd1891c9a752b5676ce442c169ccf1431ba9e0",
            HashFunctionUtil.toHexString( instance.evaluate( "www.sandia.gov".getBytes() )) );
        assertEquals( "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            HashFunctionUtil.toHexString( instance.evaluate( "".getBytes() ) ) );
        assertEquals( "eb062da2da48a82cdcbdf530de537689e981734856d35f233aded757071ee6650eb90ec47fce9f12b245d9b10a0cbecc8a35fa5e904d67d66154784fd71a792a",
            HashFunctionUtil.toHexString( instance.evaluate( "java.sun.com".getBytes() )) );
        assertEquals( "76e7c47d7711286a29c45aaafedd168480682c15a79789eb1b644a2a3d5bd52a7eb51df18c62455118535282f1a0c03e00a1797bac27ee5a1bb8f2b740b4061c",
            HashFunctionUtil.toHexString( instance.evaluate( "something that is really really really really really long".getBytes() )) );
        assertEquals( "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            HashFunctionUtil.toHexString( instance.evaluate( null )) );

    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        // Running time=56.741 for num=1e6
        // Relative Entropy = 1.8847375789177168E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
