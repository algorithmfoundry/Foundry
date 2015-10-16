/*
 * File:                HashFunctionUtil.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * Static class with helper functions for hash codes
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
public class HashFunctionUtil 
{

    /**
     * Converts an int into a 4-byte array in Big Endian order
     * (most-significant byte in slot 0)
     * @param value
     * Integer to convert
     * @return
     * 4-byte array in Big Endian order (most-significant byte in slot 0)
     */
    public static byte[] toByteArray(
        int value )
    {
        byte[] output = new byte[4];
        toByteArray(value, output);
        return output;
    }

    /**
     * Converts an int into a 4-byte array in Big Endian order
     * (most-significant byte in slot 0)
     * @param value
     * Integer to convert
     * @param output
     * 4-byte array to store the int
     */
    public static void toByteArray(
        int value,
        byte[] output )
    {
        if( output.length != 4 )
        {
            throw new IllegalArgumentException(
                "Expected output array of length 4, got length = " + output.length );
        }
        output[0] = (byte)(value >>> 24);
        output[1] = (byte)(value >>> 16);
        output[2] = (byte)(value >>> 8);
        output[3] = (byte)(value);
    }

    /**
     * Converts a long into an 8-byte array in Big Endian order
     * (most-significant byte in slot 0)
     * @param value
     * long to convert
     * @return
     * 8-byte array to store the long
     */
    public static byte[] toByteArray(
        long value )
    {
        byte[] output = new byte[8];
        toByteArray(value, output);
        return output;
    }

    /**
     * Converts a long into an 8-byte array in Big Endian order
     * (most-significant byte in slot 0)
     * @param value
     * long to convert
     * @param output
     * 8-byte array to store the long
     */
    public static void toByteArray(
        long value,
        byte[] output )
    {
        if( output.length != 8 )
        {
            throw new IllegalArgumentException(
                "Expected output array of length 8, got length = " + output.length );
        }
        output[0] = (byte)(value >>> 56);
        output[1] = (byte)(value >>> 48);
        output[2] = (byte)(value >>> 40);
        output[3] = (byte)(value >>> 32);
        output[4] = (byte)(value >>> 24);
        output[5] = (byte)(value >>> 16);
        output[6] = (byte)(value >>> 8);
        output[7] = (byte)(value);
    }

    /**
     * Cascades the hash codes
     * @param hash
     * HashFunction used to compute the results
     * @param ordered
     * true for ordered hash, false for unordered
     * @param inputs
     * Inputs to compute the hash of
     * @return
     * Cascaded hash function of the inputs
     */
    public static byte[] combineHash(
        HashFunction hash,
        boolean ordered,
        byte[] ... inputs )
    {

        byte[] seed = hash.getDefaultSeed();
        byte[] output = new byte[ hash.length() ];
        byte[] total = new byte[ hash.length() ];
        for( int n = 0; n < inputs.length; n++ )
        {
            if( ordered )
            {
                for( int i = 0; i < seed.length; i++ )
                {
                    seed[i]++;
                }
            }

            // Use the previous output as the seed into the next hash
            hash.evaluateInto(inputs[n], output, seed );
            for( int i = 0; i < output.length; i++ )
            {
                total[i] += output[i];
            }
        }

        return total;

    }

    /**
     * Computes the salted hash of the given inputs, that is, we concatenate
     * the inputs together and compute the hash of the concatenated bytes.
     * @param hash
     * HashFunction that computes the hash
     * @param inputs
     * Ordered inputs to the hash function
     * @return
     * Output into which we place the output (salted hash)
     */
    public static byte[] salt(
        final HashFunction hash,
        final byte[] ... inputs )
    {
        byte[] output = new byte[ hash.length() ];
        saltInto( output, hash, inputs );
        return output;
    }

    /**
     * Computes the salted hash of the given inputs, that is, we concatenate
     * the inputs together and compute the hash of the concatenated bytes.
     * @param output
     * Output into which we place the output (salted hash)
     * @param hash
     * HashFunction that computes the hash
     * @param inputs
     * Ordered inputs to the hash function
     */
    public static void saltInto(
        final byte[] output,
        final HashFunction hash,
        final byte[] ... inputs )
    {

        // Combine the inputs together
        int length = 0;
        for( int i = 0; i < inputs.length; i++ )
        {
            length += inputs[i].length;
        }

        // We just concatenate the inputs together
        final byte[] saltedInput = new byte[ length ];
        int offset = 0;
        for( int i = 0; i < inputs.length; i++ )
        {
            final int il = inputs[i].length;
            System.arraycopy(inputs[i], 0, saltedInput, offset, il );
            offset += il;
        }

        hash.evaluateInto(saltedInput, output);

    }

    /**
     * Char table for convenience
     */
    static final char[] HEX_CHAR_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Converts the given int to a Big Endian hex string
     * @param input
     * Input to convert
     * @return
     * hex string (8 chars)
     */
    public static String toHexString(
        int input )
    {
        // Java's Integer.toHexString doesn't put the leading zero if needed,
        // so we have to do it ourselves... sigh.
        char[] hex = new char[ 8 ];
        int index = 0;
        for( int i = 0; i < 4; i++ )
        {
            // If we didn't do the 24-8*i, then we'd end up with a
            // Little Endian result... gross.
            final int b = (input >>> (24-8*i)) & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[b >>> 4];
            hex[index++] = HEX_CHAR_TABLE[b & 0xF];
        }

        String result =  null;
        try
        {
            result = new String(hex);
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
        return result;
        

    }

    /**
     * Converts the given long to a Big Endian hex string
     * @param input
     * Input to convert
     * @return
     * hex string (16 chars)
     */
    public static String toHexString(
        long input )
    {
        // Java's Integer.toHexString doesn't put the leading zero if needed,
        // so we have to do it ourselves... sigh.
        char[] hex = new char[ 16 ];
        int index = 0;
        for( int i = 0; i < 8; i++ )
        {
            // If we didn't do the 24-8*i, then we'd end up with a
            // Little Endian result... gross.
            final int b = (int) ((input >>> (56-8*i)) & 0xFF);
            hex[index++] = HEX_CHAR_TABLE[b >>> 4];
            hex[index++] = HEX_CHAR_TABLE[b & 0xF];
        }

        String result =  null;
        try
        {
            result = new String(hex);
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
        
        return result;

    }

    /**
     * Converts a byte array to a lower-case Hex string
     * @param bytes
     * byte array to convert
     * @return
     * String representing the byte array
     */
    public static String toHexString(
        byte[] bytes )
    {
        char[] hex = new char[ 2*bytes.length];
        int index = 0;
        for( int i = 0; i < bytes.length; i++ )
        {
            final int b = (bytes[i] & 0xFF);
            hex[index++] = HEX_CHAR_TABLE[b >>> 4];
            hex[index++] = HEX_CHAR_TABLE[b & 0xF];
        }

        String result =  null;
        try
        {
            result = new String(hex);
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
        return result;
    }

    /**
     * Returns the hex value of the given character
     * @param hexChar
     * Hex character, such as '0' or 'A'
     * @return
     */
    public static byte valueOf(
        char hexChar )
    {
        hexChar = Character.toLowerCase(hexChar);
        for( byte i = 0; i < HEX_CHAR_TABLE.length; i++ )
        {
            if( hexChar == HEX_CHAR_TABLE[i] )
            {
                return i;
            }
        }
        throw new IllegalArgumentException( hexChar + " is not a hex char" );
    }

    /**
     * Converts the hex-string back into an array of bytes
     * @param hexString
     * Hex string, such as "0123456789ABCDEF", where each byte is represented
     * by two characters
     * @return
     * Byte representation of the hex string, will be half the length of string
     */
    public static byte[] fromHexString(
        String hexString )
    {

        // It takes two chars to represent a single byte
        final int sl = hexString.length();
        if( (sl % 2) != 0 )
        {
            throw new IllegalArgumentException( "hexString must be even length" );
        }

        byte[] bytes = new byte[ sl/2 ];
        for( int i = 0; i < sl; i += 2 )
        {
            final int j = i/2;
            byte high = valueOf(hexString.charAt(i));
            byte low  = valueOf(hexString.charAt(i+1));
            byte value = (byte) (((high & 0xf) << 4) | (low & 0xf));
            bytes[j] = value;
        }

        return bytes;

    }

    /**
     * Converts the 4-byte array to a Big Endian int.
     * @param data
     * 4-byte array to convert
     * @return
     * Big Endian int representation of the byte array
     */
    public static int toInteger(
        final byte[] data )
    {
        if( data.length != 4 )
        {
            throw new IllegalArgumentException(
                "data must be of length 4" );
        }
        return toInteger(data,0);
    }

    /**
     * Converts the 4 bytes in the given array starting at "offset"
     * to a Big Endian int.
     * @param data
     * Array to convert
     * @param offset
     * Offset into the array
     * @return
     * Big Endian int representation of the byte array at offset
     */
    public static int toInteger(
        final byte[] data,
        final int offset )
    {
        return ((data[offset  ] & 0xff) << 24)
            |  ((data[offset+1] & 0xff) << 16)
            |  ((data[offset+2] & 0xff) <<  8)
            |  ((data[offset+3] & 0xff)      );
    }

    /**
     * Converts the 8-byte value to a Big Endian long
     * @param data
     * byte array to convert
     * @return
     * Long of the given byte array
     */
    public static long toLong(
        final byte[] data )
    {
        if( data.length != 8 )
        {
            throw new IllegalArgumentException(
                "data must be of length 8" );
        }
        return toLong( data, 0 );
    }

    /**
     * Converts the 8-byte value at "offset" to a Big Endian long
     * @param data
     * byte array to convert
     * @param offset
     * Offset into the byte array
     * @return
     * Long at the given offset
     */
    public static long toLong(
        final byte[] data,
        final int offset )
    {
        return ((data[offset  ] & 0xffL) << 56)
            |  ((data[offset+1] & 0xffL) << 48)
            |  ((data[offset+2] & 0xffL) << 40)
            |  ((data[offset+3] & 0xffL) << 32)
            |  ((data[offset+4] & 0xffL) << 24)
            |  ((data[offset+5] & 0xffL) << 16)
            |  ((data[offset+6] & 0xffL) <<  8)
            |  ((data[offset+7] & 0xffL)      );
    }

    /**
     * Determines if a number is prime or not
     * @param num
     * Number to determine if it's a prime number or not
     * @return
     * True if prime, false if not
     */
    @PublicationReference(
        author="Snort IDS",
        title="sfhashfcn.c",
        type= PublicationType.Misc,
        year=2012,
        url="http://www.rajivchakravorty.com/source-code/.tmp/snort-html/sfhashfcn_8c-source.html",
        notes= {
            "Not an efficient implementation.",
            "My improvements make the algorithm O(sqrt(n/2)), instead of O(n), but it still sucks"
        }
    )
    public static boolean isPrime(
        long num )
    {
// TODO: Implement the AKS primality test using the library
// http://www-ti.informatik.uni-tuebingen.de/~reinhard/krypto/AKSTest/AKSTest.java
        
        if( num == 2 )
        {
            return true;
        }
        else if( num == 3 )
        {
            return true;
        }
        
        // See if the number is even
        else if( (num % 2) == 0 )
        {
            return false;
        }
        else if( (num % 3) == 0 )
        {
            return false;
        }
        
        // We only need to go up to the square root to check factors
        // and not check even numbers, so we can increment by 2
        long max = (long) Math.floor(Math.sqrt(num));
        for( long i = 5; i <= max; i += 6 )
        {
            if( (num % i) == 0 )
            {
                return false;
            }
            else if( (num % (i+2)) == 0 )
            {
                return false;
            }
        }
        
        return true;
        
    }
    
    
    /**
     * Returns the next-greater prime, sort of like "ceil" but for primes.
     * @param num
     * Number to return the next-greater prime number of
     * @return
     * Prime number such that it is greater than or equal to "num"
     */
    @PublicationReference(
        author="Snort IDS",
        title="sfhashfcn.c",
        type= PublicationType.Misc,
        year=2012,
        url="http://www.rajivchakravorty.com/source-code/.tmp/snort-html/sfhashfcn_8c-source.html",
        notes= "Not an efficient implementation."
    )
    public static long nextPrime(
        long num )
    {
        
        while( !isPrime(num) )
        {
            num++;
        }
        
        return num;
        
    }
    
}
