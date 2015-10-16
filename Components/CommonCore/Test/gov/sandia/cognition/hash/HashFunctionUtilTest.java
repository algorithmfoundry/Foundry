/*
 * File:                HashFunctionUtilTest.java
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for HashFunctionUtilTest.
 *
 * @author krdixon
 */
public class HashFunctionUtilTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Number of samples to draw, {@value}.
     */
    public final int NUM_SAMPLES = 10;

    /**
     * Tests for class HashFunctionUtilTest.
     * @param testName Name of the test.
     */
    public HashFunctionUtilTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class HashFunctionUtilTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        HashFunctionUtil instance = new HashFunctionUtil();
        assertNotNull( instance );
    }

    /**
     * Test of toByteArray method, of class HashFunctionUtil.
     */
    public void testToByteArray_int()
    {
        System.out.println("toByteArray");
        int value = 0;
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            value = RANDOM.nextInt();
            byte[] r1 = HashFunctionUtil.toByteArray(value);
            assertEquals( 4, r1.length );
            byte[] r2 = new byte[4];
            HashFunctionUtil.toByteArray(value,r2);
            assertTrue( HashFunctionTestHarness.byteArrayEquals(r1, r2) );

            assertEquals( (byte) (0xff & (value >>> 24)), r1[0] );
            assertEquals( (byte) (0xff & (value >>> 16)), r1[1] );
            assertEquals( (byte) (0xff & (value >>>  8)), r1[2] );
            assertEquals( (byte) (0xff & (value)       ), r1[3] );
        }

        byte[] output = new byte[5];
        try
        {
            HashFunctionUtil.toByteArray(value, output);
            fail( "Input length must be 4" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        output = new byte[3];
        try
        {
            HashFunctionUtil.toByteArray(value, output);
            fail( "Input length must be 4" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of toByteArray method, of class HashFunctionUtil.
     */
    public void testToByteArray_long()
    {
        System.out.println("toByteArray");
        long value = 0L;
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            value = RANDOM.nextLong();
            byte[] r1 = HashFunctionUtil.toByteArray(value);
            assertEquals( 8, r1.length );
            byte[] r2 = new byte[8];
            HashFunctionUtil.toByteArray(value, r2);
            assertTrue( HashFunctionTestHarness.byteArrayEquals(r1, r2) );
            assertEquals( (byte) (0xff & (value >>> 56)), r1[0] );
            assertEquals( (byte) (0xff & (value >>> 48)), r1[1] );
            assertEquals( (byte) (0xff & (value >>> 40)), r1[2] );
            assertEquals( (byte) (0xff & (value >>> 32)), r1[3] );
            assertEquals( (byte) (0xff & (value >>> 24)), r1[4] );
            assertEquals( (byte) (0xff & (value >>> 16)), r1[5] );
            assertEquals( (byte) (0xff & (value >>>  8)), r1[6] );
            assertEquals( (byte) (0xff & (value)       ), r1[7] );
        }
     
        byte[] output = new byte[7];
        try
        {
            HashFunctionUtil.toByteArray(value, output);
            fail( "Input length must be 8" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        output = new byte[9];
        try
        {
            HashFunctionUtil.toByteArray(value, output);
            fail( "Input length must be 8" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of combineHash method, of class HashFunctionUtil.
     */
    public void testCombineHash()
    {
        System.out.println("combineHash");

        HashFunction hash = new Eva64Hash();
        byte[] x1 = new byte[ 100 ];
        RANDOM.nextBytes(x1);
        byte[] x2 = new byte[ 10 ];
        RANDOM.nextBytes(x2);

        boolean ordered = false;
        byte[] r12 = HashFunctionUtil.combineHash(hash, ordered, x1, x2 );
        byte[] r21 = HashFunctionUtil.combineHash(hash, ordered, x2, x1 );
        System.out.println( "Unordered12: " + HashFunctionUtil.toHexString(r12) );
        System.out.println( "Unordered21: " + HashFunctionUtil.toHexString(r21) );
        assertEquals( HashFunctionUtil.toHexString(r12),
            HashFunctionUtil.toHexString(r21) );

        ordered = true;
        byte[] o12 = HashFunctionUtil.combineHash(hash, ordered, x1, x2 );
        byte[] o21 = HashFunctionUtil.combineHash(hash, ordered, x2, x1 );
        System.out.println( "Ordered12:   " + HashFunctionUtil.toHexString(o12) );
        System.out.println( "Ordered21:   " + HashFunctionUtil.toHexString(o21) );
        assertFalse( HashFunctionUtil.toHexString(r12).equals(
            HashFunctionUtil.toHexString(o12) ) );
        assertFalse( HashFunctionUtil.toHexString(r21).equals(
            HashFunctionUtil.toHexString(o21) ) );
        assertFalse( HashFunctionUtil.toHexString(o12).equals(
            HashFunctionUtil.toHexString(o21) ) );

    }

    /**
     * Test of toHexString method, of class HashFunctionUtil.
     */
    public void testToHexString()
    {
        System.out.println("toHexString");
        byte[] bytes = { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        String result = HashFunctionUtil.toHexString(bytes);
        System.out.println( "Bytes: " + result );
        assertEquals( "0123456789abcdef", result );
    }

    /**
     * toInteger
     */
    public void testToInteger()
    {
        System.out.println( "toInteger" );

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            int x = RANDOM.nextInt();
            byte[] b = HashFunctionUtil.toByteArray(x);
            int y = HashFunctionUtil.toInteger(b);
            assertEquals( x, y );
        }

        byte[] badb = new byte[ 5 ];
        try
        {
            HashFunctionUtil.toInteger(badb);
            fail( "Wrong length!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        assertEquals( 0, HashFunctionUtil.toInteger(badb,0) );
        assertEquals( 0, HashFunctionUtil.toInteger(badb,1) );
        try
        {
            HashFunctionUtil.toInteger(badb, 2);
            fail( "Out of bounds" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * toLong
     */
    public void testToLong()
    {
        System.out.println( "toLong" );

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            long x = RANDOM.nextLong();
            byte[] b = HashFunctionUtil.toByteArray(x);
            long y = HashFunctionUtil.toLong(b);
            assertEquals( x, y );
        }

        byte[] badb = new byte[ 9 ];
        try
        {
            HashFunctionUtil.toLong(badb);
            fail( "Wrong length!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        assertEquals( 0, HashFunctionUtil.toLong(badb,0) );
        assertEquals( 0, HashFunctionUtil.toLong(badb,1) );
        try
        {
            HashFunctionUtil.toLong(badb, 2);
            fail( "Out of bounds" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }


    /**
     * toHexString(int)
     */
    public void testToHexStringInt()
    {
        System.out.println( "toHexString" );

        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            int value = RANDOM.nextInt();
            assertEquals( HashFunctionUtil.toHexString( HashFunctionUtil.toByteArray(value)),
                HashFunctionUtil.toHexString(value) );
        }

    }

    /**
     * toHexString(long)
     */
    public void testToHexStringLong()
    {
        System.out.println( "toHexString" );

        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            long value = RANDOM.nextInt();
            assertEquals( HashFunctionUtil.toHexString( HashFunctionUtil.toByteArray(value)),
                HashFunctionUtil.toHexString(value) );
        }

    }

    /**
     * salt
     * @throws UnsupportedEncodingException
     */
    public void testSalt()
        throws UnsupportedEncodingException
    {
        System.out.println( "salt" );

        // These are salts/passwords from the Sun hack...  just checking.
        String salt = "drogers";
        String password = "magnet";
        String expected = "83437c10ac27a63caedfdcb871ae693b";

        HashFunction hash = new MD5Hash();
        String result = HashFunctionUtil.toHexString(HashFunctionUtil.salt(hash, salt.getBytes("UTF-8"), password.getBytes("UTF-8") ));
        assertEquals( expected, result );

        salt = "jcampbell";
        password = "grape";
        expected = "ba8d1d9740ab78e5e7f3541f71a6bbdd";
        result = HashFunctionUtil.toHexString(HashFunctionUtil.salt(hash, salt.getBytes("UTF-8"), password.getBytes("UTF-8") ));
        assertEquals( expected, result );

        salt = "rebekah";
        password = "63000";
        expected = "62dd0bd92bf4fafae73c531ee5108c77";
        result = HashFunctionUtil.toHexString(HashFunctionUtil.salt(hash, salt.getBytes("UTF-8"), password.getBytes("UTF-8") ));
        assertEquals( expected, result );

    }

    public void testFromHexString()
    {
        System.out.println( "fromHexString" );

        byte[] b1 = SHA1Hash.hash( "hello".getBytes() );
        String h1 = HashFunctionUtil.toHexString(b1);
        byte[] r1 = HashFunctionUtil.fromHexString(h1);
        String j1 = HashFunctionUtil.toHexString(r1);
        assertEquals( h1, j1 );
        assertTrue( Arrays.equals(b1, r1) );

    }

    public void testIsPrime()
    {
        System.out.println( "isPrime" );
        
        long[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43,
            47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109,
            113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181,
            191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257,
            263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337,
            347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419,
            421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491,
            499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587,
            593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659,
            661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751,
            757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839,
            853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937,
            941, 947, 953, 967, 971, 977, 983, 991, 997 };
        
        for( int i = 0; i < primes.length; i++ )
        {
            assertTrue( HashFunctionUtil.isPrime( primes[i] ) );
        }
        
        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            boolean keepGoing = true;
            long num = 0;
            while( keepGoing )
            {
                num = Math.abs(RANDOM.nextLong()) % primes[primes.length-1];
                keepGoing = false;
                for( int j = 0; j < primes.length; j++ )
                {
                    if( num == primes[j] )
                    {
                        keepGoing = true;
                        break;
                    }
                }
            }
            assertFalse( num + " is not prime!", HashFunctionUtil.isPrime(num) );
        }
        
    }
    
    public void testNextPrime()
    {
        System.out.println( "nextPrime" );
        
        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            long value = Math.abs(RANDOM.nextLong()) % 10000;
            long next = HashFunctionUtil.nextPrime(value);
            assertTrue( HashFunctionUtil.isPrime(next) );
            for( long j = value; j < next; j++ )
            {
                assertFalse( HashFunctionUtil.isPrime(j) );
            }
            
        }
        
        
    }
    
    
}
