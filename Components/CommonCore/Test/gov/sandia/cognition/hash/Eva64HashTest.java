/*
 * File:                Eva64HashTest.java
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
 * Unit tests for Eva64HashTest.
 *
 * @author krdixon
 */
public class Eva64HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class Eva64HashTest.
     * @param testName Name of the test.
     */
    public Eva64HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class Eva64HashTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        Eva64Hash instance = new Eva64Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class Eva64Hash.
     */
    @Override
    public void testLength()
    {
        System.out.println("length");
        Eva64Hash instance = this.createInstance();
        assertEquals( 8, instance.length() );
        assertEquals( 8, Eva64Hash.LENGTH );
    }

    @Override
    public Eva64Hash createInstance()
    {
        return new Eva64Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        long seed = 0x12345678;
        long result;

        // We use known values from the C code to match our test against.
        result = Eva64Hash.hash("www.sandia.gov".getBytes(), seed );
        assertEquals("fbecf998617cec55", Long.toHexString(result));

        result = Eva64Hash.hash("".getBytes(), seed);
        assertEquals("4ffdcb803df641a9", Long.toHexString(result));

        result = Eva64Hash.hash("java.sun.com".getBytes(), seed);
        assertEquals("6946620cb66f8299", Long.toHexString(result));

        result = Eva64Hash.hash("abc".getBytes(), seed);
        assertEquals("b4d1c6b34648dcca", Long.toHexString(result));

        result = Eva64Hash.hash("something that is really really really really really long".getBytes(), seed);
        assertEquals("8530b6f3aca4cbee", Long.toHexString(result));

        result = Eva64Hash.hash(null, seed);
        assertEquals("0", Long.toHexString(result));
    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  4.291
        //Total Average Time: 14.328
        //Relative Entropy = 1.8219268193964133E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }


}
