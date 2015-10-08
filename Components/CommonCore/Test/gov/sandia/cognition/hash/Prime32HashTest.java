/*
 * File:                Prime32HashTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 10, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.hash;

/**
 * Unit tests for Prime32HashTest.
 *
 * @author krdixon
 */
public class Prime32HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class Prime32HashTest.
     * @param testName Name of the test.
     */
    public Prime32HashTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class Prime32HashTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        Prime64Hash instance = new Prime64Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class Prime32Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        Prime32Hash instance = this.createInstance();
        assertEquals( 4, instance.length() );
        assertEquals( 4, Prime32Hash.LENGTH );
    }

    @Override
    public Prime32Hash createInstance()
    {
        return new Prime32Hash();
    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "evaluate known values" );

        String s;

        s = "";
        assertEquals( s.hashCode(), Prime32Hash.hash(s.getBytes(), 31, 0 ) );

        s = "The quick brown fox jumps over the lazy dog.";
        assertEquals( s.hashCode(), Prime32Hash.hash(s.getBytes(), 31, 0 ) );

        s = "hello, world";
        assertEquals( s.hashCode(), Prime32Hash.hash(s.getBytes(), 31, 0 ) );

        s = "The quick brown fox jumps over the lazy dog.";
        assertEquals( s.hashCode(), Prime32Hash.hash(s.getBytes(), 31, 0 ) );

        assertEquals( 0, Prime32Hash.hash(null) );

        try
        {
            Prime32Hash.hash(s.getBytes(), 0, 0);
            fail( "Prime cannot be zero" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }


    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  1.677
        //Total Average Time: 11.578
        //Relative Entropy = 4.972245212166992E-4
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

}
