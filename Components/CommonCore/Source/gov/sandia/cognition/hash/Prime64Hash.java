/*
 * File:                Prime64Hash.java
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
 * Implementation of the prime-hash function using 64-bit (8-byte) precision.
 * This hash function is similar to the one Java uses, but it has very bad
 * properties and should only be used for benchmarking.
 * @author Kevin R. Dixon
 * @since 3.1
 */
public class Prime64Hash 
    extends AbstractHashFunction
{

    /**
     * Default prime number, {@value}.
     */
    private static final long DEFAULT_PRIME = 31;


    /**
     * Length of the hash is 64 bits (8 bytes), {@value}.
     */
    public static final int LENGTH = 8;
    
    /**
     * Default seed, the first seed from the SHA-2 512-bit hash.
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x6a, (byte)0x09, (byte)0xe6, (byte)0x67, (byte)0xf3, (byte)0xbc, (byte)0xc9, (byte)0x08
    };

    /** 
     * Creates a new instance of Prime64Hash 
     */
    public Prime64Hash()
    {
    }

    @Override
    public Prime64Hash clone()
    {
        return (Prime64Hash) super.clone();
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

        final long hash = hash( input, HashFunctionUtil.toLong(seed) );
        HashFunctionUtil.toByteArray( hash, output );

    }

    /**
     * Computes the prime-number hash of the given input, prime number, and seed
     * @param input
     * Input to hash
     * @return
     * 64-bit hash code of the input
     */
    public static long hash(
        byte[] input )
    {
        long seed = HashFunctionUtil.toLong(DEFAULT_SEED);
        return hash( input, seed );
    }

    /**
     * Computes the prime-number hash of the given input, prime number, and seed
     * @param input
     * Input to hash
     * @param seed
     * Seed to start the hash process
     * @return
     * 64-bit hash code of the input
     */
    public static long hash(
        byte[] input,
        long seed )
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
     * 64-bit hash code of the input
     */
    public static long hash(
        byte[] input,
        long prime,
        long seed )
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

        long result = seed;
        final int length = input.length;
        for( int i = 0; i < length; i++ )
        {
            result = (result * prime) + input[i];
        }

        return result;
        
    }

}
