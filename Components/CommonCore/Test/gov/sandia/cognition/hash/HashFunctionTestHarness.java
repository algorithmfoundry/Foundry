/*
 * File:                HashFunctionUtilTestHarness.java
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

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for HashFunctionTestHarness.
 *
 * @author krdixon
 */
public abstract class HashFunctionTestHarness
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Number of samples, {@value}.
     */
    public int NUM_SAMPLES = 100;

    /**
     * Tests for class HashFunctionTestHarness.
     * @param testName Name of the test.
     */
    public HashFunctionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Determines if two byte arrays are equal, element-by-element
     * @param a1
     * a1
     * @param a2
     * a2
     * @return
     * true if equal, false if not equal
     */
    public static boolean byteArrayEquals(
        byte[] a1,
        byte[] a2 )
    {
        if( a1.length != a2.length )
        {
            return false;
        }
        for( int i = 0; i < a1.length; i++ )
        {
            if( a1[i] != a2[i] )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests the constructors of class HashFunctionTestHarness.
     */
    public abstract void testConstructors();

    /**
     * Creates a new instance of HashFunction
     * @return
     * new HashFunction to evaluate
     */
    public abstract HashFunction createInstance();

    /**
     * Test of length method, of class HashFunction.
     */
    public abstract void testLength();

    /**
     * Test of evaluate method, of class HashFunction.
     */
    public abstract void testEvaluateKnownValues();

    /**
     * Test of clone method, of class HashFunction.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        HashFunction instance = this.createInstance();
        HashFunction clone = ObjectUtil.cloneSafe(instance);
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            int inputLength = RANDOM.nextInt(100)+10;
            byte[] input = new byte[inputLength];
            RANDOM.nextBytes(input);
            byte[] seed = new byte[ instance.length() ];
            RANDOM.nextBytes(seed);
            byte[] r1 = instance.evaluate(input, seed);
            byte[] r2 = clone.evaluate(input, seed);
            assertTrue( byteArrayEquals(r1, r2) );
        }
    }

    /**
     * Test of evaluate method, of class HashFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        HashFunction instance = this.createInstance();

        byte[] seed1 = new byte[ instance.length() ];
        RANDOM.nextBytes(seed1);
        byte[] seed2 = new byte[ instance.length() ];
        RANDOM.nextBytes(seed2);
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            int inputLength = RANDOM.nextInt(100)+10;
            byte[] input = new byte[inputLength];
            RANDOM.nextBytes(input);
            byte[] r1 = instance.evaluate(input);
            assertEquals( instance.length(), r1.length );
            byte[] r2 = instance.evaluate(input, instance.getDefaultSeed() );
            assertEquals( instance.length(), r2.length );
            assertTrue( byteArrayEquals( r1, r2 ) );

            byte[] s1 = instance.evaluate(input, seed1);
            assertEquals( instance.length(), s1.length );
            byte[] s2 = instance.evaluate(input, seed2);
            assertEquals( instance.length(), s2.length );
            assertFalse( byteArrayEquals( s1, s2 ) );
            
            byte[] s3 = instance.evaluate(input,seed1 );
            assertEquals( instance.length(), s3.length );
            assertTrue( byteArrayEquals(s1, s3) );
        }

        byte[] r1 = instance.evaluate(null);
        byte[] r2 = instance.evaluate(null, instance.getDefaultSeed() );
        assertTrue( byteArrayEquals(r1, r2) );
        byte[] r3 = instance.evaluate(new byte[0] );
        byte[] r4 = instance.evaluate(new byte[0], instance.getDefaultSeed() );
        assertTrue( byteArrayEquals(r3, r4) );

        byte[] s1 = instance.evaluate(null,seed1);
        byte[] s2 = instance.evaluate(null,seed2);
        assertEquals( instance.length(), s1.length );
        assertEquals( instance.length(), s2.length );
        byte[] s3 = instance.evaluate(new byte[0],seed1 );
        byte[] s4 = instance.evaluate(new byte[0],seed2 );
        assertEquals( instance.length(), s3.length );
        assertEquals( instance.length(), s4.length );
    }

    /**
     * Test of evaluateInto method, of class HashFunction.
     */
    public void testEvaluateInto_byteArr_byteArr()
    {
        System.out.println("evaluateInto");
        HashFunction instance = this.createInstance();

        int length = instance.length();
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            int inputLength = RANDOM.nextInt(100)+10;
            byte[] input = new byte[inputLength];
            RANDOM.nextBytes(input);
            byte[] r1 = new byte[length];
            instance.evaluateInto(input, r1);
            byte[] r2 = instance.evaluate(input);
            assertEquals( length, r1.length );
            assertEquals( length, r2.length );
            assertTrue( byteArrayEquals( r1, r2 ) );

            byte[] r3 = new byte[length];
            instance.evaluateInto(input, r3, instance.getDefaultSeed() );
            assertEquals( length, r3.length );
            assertTrue( byteArrayEquals(r1, r3) );

        }

        int inputLength = RANDOM.nextInt(100)+10;
        byte[] input = new byte[inputLength];
        RANDOM.nextBytes(input);
        byte[] r1 = new byte[length];
        instance.evaluateInto(null,r1);
        byte[] r2 = new byte[length];
        instance.evaluateInto(null,r2,instance.getDefaultSeed());
        assertTrue( byteArrayEquals(r1, r2) );
        instance.evaluateInto(new byte[0],r1 );
        instance.evaluateInto(new byte[0],r2,instance.getDefaultSeed());
        assertTrue( byteArrayEquals(r1, r2) );


        byte[] output = new byte[length+1];
        try
        {
            instance.evaluateInto(input, output);
            fail( "Output is the wrong length" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        output = new byte[length-1];
        try
        {
            instance.evaluateInto(input, output);
            fail( "Output is the wrong length" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        byte[] badSeed = new byte[ length+1 ];
        try
        {
            instance.evaluateInto(input, new byte[length], badSeed );
            fail( "Seed is the wrong length!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of evaluateInto method, of class HashFunction.
     */
    public void testEvaluateInto_3args()
    {
        System.out.println("evaluateInto");
        HashFunction instance = this.createInstance();
        int length = instance.length();
        byte[] seed1 = new byte[ length ];
        RANDOM.nextBytes(seed1);
        byte[] seed2 = new byte[ length ];
        RANDOM.nextBytes(seed2);
        byte[] r1 = new byte[length];
        byte[] r2 = new byte[length];
        byte[] r3 = new byte[length];
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            int inputLength = RANDOM.nextInt(100)+10;
            byte[] input = new byte[inputLength];
            RANDOM.nextBytes(input);

            instance.evaluateInto(input, r1, seed1);
            instance.evaluateInto(input, r2, seed2);
            instance.evaluateInto(input, r3, seed1);
            assertFalse( byteArrayEquals(r1, r2) );
            assertTrue( byteArrayEquals(r1, r3) );
            assertFalse( byteArrayEquals(r2, r3) );
        }

        int inputLength = RANDOM.nextInt(100)+10;
        byte[] input = new byte[inputLength];
        RANDOM.nextBytes(input);

        r1 = new byte[length];
        instance.evaluateInto(null,r1,seed1);
        assertNotNull( r1 );

        byte[] output = new byte[length+1];
        try
        {
            instance.evaluateInto(input, output, seed1);
            fail( "Output is the wrong length" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        output = new byte[length-1];
        try
        {
            instance.evaluateInto(input, output, seed2);
            fail( "Output is the wrong length" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }


    /**
     * Computes the relative entropy for the hash function, lower relative
     * entropy is better (ideally 0.0).
     * @param num
     * Number of samples to draw.
     * @return
     * Average relative entropy of the hash function output on a byte-by-byte
     * basis
     */
    public double computeRelativeEntropy(
        int num )
    {

        HashFunction hash = this.createInstance();
        final int length = hash.length();
        final byte[] seed = hash.getDefaultSeed();

        long[][] distribution = new long[ length ][];
        for( int i = 0; i < length; i++ )
        {
            distribution[i] = new long[ 256 ];
        }

        byte[] output = new byte[ length ];
        long start = System.currentTimeMillis();
        long ht = 0;
        for( int n = 0; n < num; n++ )
        {
            int l = RANDOM.nextInt(1000) + 1;
            byte[] input = new byte[ l ];
            RANDOM.nextBytes(input);
            long hstart = System.currentTimeMillis();
            hash.evaluateInto(input, output, seed );
            long hstop = System.currentTimeMillis();
            ht += hstop-hstart;
            for( int i = 0; i < length; i++ )
            {
                distribution[i][ output[i] & 0xff ]++;
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println( "Hash Average Time:  " + 1000.0*(ht) / num );
        System.out.println( "Total Average Time: " + 1000.0*(stop-start) / num );

        ArrayList<Double> al = new ArrayList<Double>( 256 );
        for( int i = 0; i < 256; i++ )
        {
            al.add( 0.0 );
        }
        double entropy = 0.0;
        for( int i = 0; i < distribution.length; i++ )
        {
            for( int j = 0; j < distribution[i].length; j++ )
            {
                al.set( j, ((double) distribution[i][j]) / num );
            }

            // Each byte has a maximum of 8-bits... this is how close
            // we got to the 8-bit target!
            entropy += 8.0 - UnivariateStatisticsUtil.computeEntropy( al );
        }

        return entropy / length;

    }

    /**
     * getDefaultSeed
     */
    public void testGetDefaultSeed()
    {
        System.out.println( "getDefaultSeed" );

        HashFunction instance = this.createInstance();
        byte[] seed = instance.getDefaultSeed();
        assertEquals( instance.length(), seed.length );

        // Make sure it's not a reference to the actual default seed
        assertEquals( seed[0], instance.getDefaultSeed()[0] );
        seed[0] += 1;
        assertFalse( "Must return a copy of the default seed!",
            seed[0] == instance.getDefaultSeed()[0] );

        byte[] input = new byte[ RANDOM.nextInt(100) + 100 ];
        RANDOM.nextBytes(input);
        byte[] r1 = instance.evaluate(input);
        byte[] r2 = instance.evaluate(input, instance.getDefaultSeed() );
        assertTrue( byteArrayEquals(r1, r2) );
    }

}
