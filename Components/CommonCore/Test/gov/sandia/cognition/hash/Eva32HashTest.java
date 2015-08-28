/*
 * File:                Eva32HashTest.java
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
 * Unit tests for Eva32HashTest.
 *
 * @author krdixon
 */
public class Eva32HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class Eva32HashTest.
     * @param testName Name of the test.
     */
    public Eva32HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class Eva32HashTest.
     */
    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        Eva32Hash instance = new Eva32Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class Eva32Hash.
     */
    @Override
    public void testLength()
    {
        System.out.println("length");
        Eva32Hash instance = this.createInstance();
        assertEquals( 4, instance.length() );
        assertEquals( 4, Eva32Hash.LENGTH );
    }

    @Override
    public Eva32Hash createInstance()
    {
        return new Eva32Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        int seed = 0x12345678;
        int result;

        result = Eva32Hash.hash("www.sandia.gov".getBytes(), seed);
        assertEquals("617cec55", Integer.toHexString(result));
        result = Eva32Hash.hash("www.sandia.gov".getBytes(), result);
        assertEquals("fbecf998", Integer.toHexString(result));
        
        result = Eva32Hash.hash("".getBytes(), seed);
        assertEquals("3df641a9", Integer.toHexString(result));
        result = Eva32Hash.hash("".getBytes(), result);
        assertEquals("4ffdcb80", Integer.toHexString(result));

        result = Eva32Hash.hash("something that is really really really really really long".getBytes(), seed);
        assertEquals("aca4cbee", Integer.toHexString(result));


        result = Eva32Hash.hash(null, seed);
        assertEquals("0", Integer.toHexString(result));
        result = Eva32Hash.hash(null, result);
        assertEquals("0", Integer.toHexString(result));
    }

    /**
     * Relative Entropy
     */
    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  2.233
        //Total Average Time: 12.108
        //Relative Entropy = 1.783615112920245E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }


}
