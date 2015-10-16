/*
 * File:                Murmur32Hash.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 1, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

// Ported by Derek Young from the C version (specifically the endian-neutral
// version) from:
//   http://murmurhash.googlepages.com/
//
// released to the public domain - dmy999@gmail.com


package gov.sandia.cognition.hash;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.ObjectUtil;



/**
 * Implementation of the MurmurHash2 32-bit (4-byte) non-cryptographic hash
 * function.  This implementation is based on Derek Young's port of the
 * original MurmurHash2 hash function of Austin Appleby.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Derek Young",
            title="MurmurHash 2 Java port",
            type=PublicationType.WebPage,
            year=2009,
            url="http://dmy999.com/article/50/murmurhash-2-java-port"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="MurmurHash",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/MurmurHash"
        )
    }
)
public class Murmur32Hash
    extends AbstractHashFunction
{

    /**
     * Length of the hash function is 32-bits or 4 bytes, {@value}.
     */
    public static final int LENGTH = 4;

    /**
     * Default seed: ( 0, 0, 0, 0).
     */
    protected static final byte[] DEFAULT_SEED = HashFunctionUtil.toByteArray(0);

    /**
     * Default constructor
     */
    public Murmur32Hash()
    {
        super();
    }

    @Override
    public Murmur32Hash clone()
    {
        return (Murmur32Hash) super.clone();
    }

    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed)
    {
        if( output.length != 4 )
        {
            throw new IllegalArgumentException(
                "Expected output as length " + LENGTH );
        }

        final int key = hash( input, HashFunctionUtil.toInteger(seed) );
        HashFunctionUtil.toByteArray(key, output);
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
     * Static hash method that implements the MurmurHash2 32-bit (4-byte)
     * hash function
     * @param data
     * Input data to hash
     * @param seed
     * Seed offset to use
     * @return
     * 32-bit (4-byte) hash function output
     */
    @SuppressWarnings("fallthrough")
    public static int hash(
        byte[] data,
        int seed )
    {

        if( data == null )
        {
            return 0;
        }

        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        int m = 0x5bd1e995;
        int r = 24;

        // Initialize the hash to a 'random' value
        int len = data.length;
        int h = seed ^ len;

        int i = 0;
        while (len >= 4)
        {
            int k = data[i + 0] & 0xFF;
            k |= (data[i + 1] & 0xFF) << 8;
            k |= (data[i + 2] & 0xFF) << 16;
            k |= (data[i + 3] & 0xFF) << 24;

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;

            i += 4;
            len -= 4;
        }

        // These case statements are supposed to fall through without a break
        switch (len)
        {
            case 3:
                h ^= (data[i + 2] & 0xFF) << 16;
            case 2:
                h ^= (data[i + 1] & 0xFF) << 8;
            case 1:
                h ^= (data[i + 0] & 0xFF);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

}
