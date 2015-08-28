/*
 * File:                Prime64HashTest.java
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
 * Unit tests for Prime64HashTest.
 *
 * @author krdixon
 */
public class Prime64HashTest
    extends HashFunctionTestHarness
{

    /**
     * Tests for class Prime64HashTest.
     * @param testName Name of the test.
     */
    public Prime64HashTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class Prime64HashTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        Prime64Hash instance = new Prime64Hash();
        assertNotNull( instance );
    }

    /**
     * Test of length method, of class SHA256Hash.
     */
    public void testLength()
    {
        System.out.println("length");
        Prime64Hash instance = this.createInstance();
        assertEquals( 8, instance.length() );
        assertEquals( 8, Prime64Hash.LENGTH );
    }

    @Override
    public Prime64Hash createInstance()
    {
        return new Prime64Hash();
    }

    public void testRelativeEntropy()
    {
        System.out.println( "Relative Entropy" );

        //Hash Average Time:  5.114
        //Total Average Time: 14.282
        //Relative Entropy = 0.0010146040933032285
        double entropy = this.computeRelativeEntropy( 1000 );
        System.out.println( "Relative Entropy = " + entropy );

    }

    @Override
    public void testEvaluateKnownValues()
    {
        System.out.println( "evaluate known values" );

        String s;

        // No overflow
        s = "";
        assertEquals( s.hashCode(), Prime64Hash.hash(s.getBytes(), 31, 0 ) );

        // No overflow
        s = "hell";
        assertEquals( s.hashCode(), Prime64Hash.hash(s.getBytes(), 31, 0 ) );

        // Overflow
        s = "The quick brown fox jumps over the lazy dog.";
        assertFalse( s.hashCode() == Prime64Hash.hash(s.getBytes(), 31, 0 ) );

        assertEquals( 0, Prime32Hash.hash(null) );

        try
        {
            Prime64Hash.hash(s.getBytes(), 0, 0);
            fail( "Prime cannot be zero" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

}
