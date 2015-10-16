/*
 * File:                Prime32Hash.java
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

import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of the prime-hash function using 32-bit (4-byte) precision.
 * This hash function is similar to the one Java uses, but it has very bad
 * properties and should only be used for benchmarking.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
public class Prime32Hash
    extends AbstractHashFunction
{

    /**
     * Default prime number, {@value}.
     */
    private static final int DEFAULT_PRIME = 31;


    /**
     * Length of the hash is 32 bits (4 bytes), {@value}.
     */
    public static final int LENGTH = 4;

    /**
     * Default seed, the first seed from the SHA-2 256-bit hash.
     * 31 is another common default seed, which actually used in Java.
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x6a, (byte)0x09, (byte)0xe6, (byte)0x67
    };

    /** 
     * Creates a new instance of Prime32Hash
     */
    public Prime32Hash()
    {
        super();
    }

    @Override
    public Prime32Hash clone()
    {
        return (Prime32Hash) super.clone();
    }


    @Override
    public int length()
    {
        return LENGTH;
    }

    @Override
    public byte[] getDefaultSeed()
    {
        return ObjectUtil.deepCopy(DEFAULT_SEED);
    }

    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed)
    {
        if( seed.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected seed to be of length: " + LENGTH );
        }

        final int hash = hash( input, HashFunctionUtil.toInteger(seed) );
        HashFunctionUtil.toByteArray( hash, output );

    }

    /**
     * Computes the prime-number hash of the given input, prime number, and seed
     * @param input
     * Input to hash
     * @return
     * 32-bit hash code of the input
     */
    public static int hash(
        byte[] input )
    {
        int seed = HashFunctionUtil.toInteger(DEFAULT_SEED);
        return hash( input, seed );
    }

    /**
     * Computes the prime-number hash of the given input, prime number, and seed
     * @param input
     * Input to hash
     * @param seed
     * Seed to start the hash process
     * @return
     * 32-bit hash code of the input
     */
    public static int hash(
        byte[] input,
        int seed )
    {
        return hash( input, DEFAULT_PRIME, seed );
    }

    /**
     * Computes the prime-number hash of the given input, prime number, and seed
     * @param input
     * Input to hash
     * @param prime
     * Prime number to recursively multiply the input
     * @param seed
     * Seed to start the hash process
     * @return
     * 32-bit hash code of the input
     */
    public static int hash(
        byte[] input,
        int prime,
        int seed )
    {

        if( input == null )
        {
            return 0;
        }

        if( prime == 0 )
        {
            throw new IllegalArgumentException(
                "Prime cannot be 0" );
        }

        int result = seed;
        final int length = input.length;
        for( int i = 0; i < length; i++ )
        {
            result = (result * prime) + input[i];
        }

        return result;

    }


}
