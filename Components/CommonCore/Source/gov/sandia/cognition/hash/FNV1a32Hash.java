/*
 * File:                FNV1a32Hash.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 14, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.hash;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of the 32-bit (4-byte) Fowler-Noll-Vo (FNV-1a) hash function.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Fowler–Noll–Vo hash function",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function"
        )
        ,
        @PublicationReference(
            author="Landon Curt Noll",
            title="FNV-1a",
            type=PublicationType.WebPage,
            year=2011,
            url="http://isthe.com/chongo/tech/comp/fnv/#FNV-1a"
        )
    }
)
public class FNV1a32Hash 
    extends AbstractHashFunction
{


    /**
     * Length of the hash is 32-bits (4-bytes), {@value}.
     */
    public static final int LENGTH = 4;

    /**
     * Default FNV-1 seed, {@value} == (signed) 2166136261
     */
    public static final int DEFAULT_SEED_INT = -2128831035;

    /**
     * Byte representation of DEFAULT_SEED_INT
     */
    protected static final byte[] DEFAULT_SEED =
        HashFunctionUtil.toByteArray(DEFAULT_SEED_INT);

    /**
     * Default FNV-1 prime, {@value}.
     */
    public static final int DEFAULT_PRIME = 16777619;

    /** 
     * Creates a new instance of FNV1a32Hash 
     */
    public FNV1a32Hash()
    {
        super();
    }

    @Override
    public FNV1a32Hash clone()
    {
        return (FNV1a32Hash) super.clone();
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

    /**
     * FNV-1a 32-bit hash function
     * @param input
     * Input to hash
     * @return
     * 32-bit FNV-1a hash
     */
    public static int hash(
        byte[] input )
    {
        return hash( input, DEFAULT_SEED_INT );
    }

    /**
     * FNV-1a 32-bit hash function
     * @param input
     * Input to hash
     * @param seed
     * Seed to use as the offset
     * @return
     * 32-bit FNV-1a hash
     */
    public static int hash(
        byte[] input,
        int seed )
    {
        if( input == null )
        {
            return 0;
        }

        final int length = input.length;
        int hash = seed;
        for( int i = 0; i < length; i++ )
        {
            hash ^= input[i];
            hash *= DEFAULT_PRIME;
        }
        return hash;
    }
    
    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed)
    {
        int s = HashFunctionUtil.toInteger(seed);
        int hash = hash( input, s );
        HashFunctionUtil.toByteArray(hash, output);
    }

}
