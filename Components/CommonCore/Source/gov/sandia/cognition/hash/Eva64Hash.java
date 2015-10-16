/*
 * File:                Eva64Hash.java
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
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This implements the 64-bit "evahash" due to Robert Jenkins.  This is a Java
 * port of Jenkins's C implementation.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReference(
    author="Robert J. Jenkins, Jr.",
    title="Hash Functions for Hash Table Lookup",
    type=PublicationType.WebPage,
    year=1997,
    url="http://burtleburtle.net/bob/hash/evahash.html"
)
public class Eva64Hash 
    extends AbstractHashFunction
{

    /**
     * Length of the hash in bytes, {@value}.
     */
    public static final int LENGTH = 8;

    /**
     * Default seed: ( 0, 0, 0, 0, 0, 0, 0, 0 ).
     */
    protected static final byte[] DEFAULT_SEED = HashFunctionUtil.toByteArray(0L);

    /** 
     * Creates a new instance of Eva64Hash 
     */
    public Eva64Hash()
    {
        super();
    }

    @Override
    public Eva64Hash clone()
    {
        Eva64Hash clone = (Eva64Hash) super.clone();
        return clone;
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
        long result = hash( input, HashFunctionUtil.toLong(seed) );
        HashFunctionUtil.toByteArray(result, output);
    }

    /**
     * Computes the Eva64Hash of the given input and the seed
     * @param input
     * Input to consider
     * @param seed
     * Seed to offset the hash
     * @return
     * long representing the 64-bit hash function
     */
    public static long hash(
        byte[] input,
        long seed )
    {

        // First calculate the first part by hashing the string from the given
        // initial value.
        final int part1 = Eva32Hash.hash( input, (int) seed );

        // Now calculate another part using the first part as the initial value.
        final int part2 = Eva32Hash.hash( input, part1 );

        // Finally combine them. Note: While it may make more sense to have
        // part1 as the high 32 bits, part2 is the high 32 bits because of how
        // the 64-bit hash code is created in the C code.
        return ((part2 & 0xFFFFFFFFL) << 32)
            +  ((part1 & 0xFFFFFFFFL));
    }

}
