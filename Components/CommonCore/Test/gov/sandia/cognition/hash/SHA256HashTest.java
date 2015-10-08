/*
 * File:                SHA256Hash.java
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
 * Unit tests for SHA256HashTest.
 *
 * @author krdixon
 */
public class SHA256HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class SHA256HashTest.
     * @param testName Name of the test.
     */
    public SHA256HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class SHA256HashTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        SHA256Hash instance = new SHA256Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class SHA256Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        SHA256Hash instance = this.createInstance();
        assertEquals( 32, instance.length() );
        assertEquals( 32, SHA256Hash.LENGTH );
    }

    @Override
    public SHA256Hash createInstance()
    {
        return new SHA256Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "Evaluate Known Values" );

        // From: http://jssha.sourceforge.net/

        assertEquals( "09ca7e4eaa6e8ae9c7d261167129184883644d07dfba7cbfbc4c8a2e08360d5b",
            HashFunctionUtil.toHexString( SHA256Hash.hash("hello, world".getBytes()) ) );
        assertEquals( "8f17a981ba86c3aa908962f0dbb019b65774c6808f7357a1c31c186ced790e88",
            HashFunctionUtil.toHexString( SHA256Hash.hash( "www.sandia.gov".getBytes() )) );
        assertEquals( "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            HashFunctionUtil.toHexString( SHA256Hash.hash( "".getBytes() ) ) );
        assertEquals( "630ea93dc403a9babbcd1947084148bd67628dd01aab6dcb55a8246d3152172e",
            HashFunctionUtil.toHexString( SHA256Hash.hash( "java.sun.com".getBytes() )) );
        assertEquals( "a2869d1b4c864c01fce83491f38e74e242fba3e47dcd3a794760d477b42c63dd",
            HashFunctionUtil.toHexString( SHA256Hash.hash( "something that is really really really really really long".getBytes() )) );
        assertEquals( "0000000000000000000000000000000000000000000000000000000000000000",
            HashFunctionUtil.toHexString( SHA256Hash.hash( null )) );


    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  22.757
        //Total Average Time: 32.861
        //Relative Entropy = 1.8674856619554925E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
