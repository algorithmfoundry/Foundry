/*
 * File:                Eva32Hash.java
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
 * This implements the 32-bit "evahash" due to Robert Jenkins.  This is a Java
 * port of Jenkins's C implementation.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReference(
    author="Robert J. Jenkins, Jr.",
    title="Hash Functions for Hash Table Lookup",
    type=PublicationType.WebPage,
    year=1997,
    url="http://burtleburtle.net/bob/hash/evahash.html",
    notes="Ported from Robert Jenkins's C implementation"
)
public class Eva32Hash 
    extends AbstractHashFunction
{

    /**
     * A 32-bit, 4-byte hash, {@value}.
     */
    public static final int LENGTH = 4;

    /**
     * Default seed: ( 0, 0, 0, 0 ).
     */
    protected static final byte[] DEFAULT_SEED = HashFunctionUtil.toByteArray(0);

    /** 
     * Creates a new instance of Eva32Hash 
     */
    public Eva32Hash()
    {
    }

    @Override
    public Eva32Hash clone()
    {
        Eva32Hash clone = (Eva32Hash) super.clone();
        return clone;
    }

    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed)
    {
        int hash = hash( input, HashFunctionUtil.toInteger(seed) );
        HashFunctionUtil.toByteArray(hash,output);
    }

    @Override
    public int length()
    {
        return LENGTH;
    }

    /**
     * 32-bit (4-byte) hash code for the given input and seed
     * @param input
     * Input to hash
     * @param seed
     * Seed, or offset, of the hash code
     * @return
     * 32-bit hash code of the input and seed.
     */
    @SuppressWarnings("fallthrough")
    public static int hash(
        byte[] input,
        int seed )
    {

        // null hashes to zero
        if( input == null )
        {
            return 0;
        }

        final int inputLength = input.length;

        // Initialize the values. A and b are initialized to the golden ratio,
        // which is an arbitrary value.
        int a = 0x9e3779b9;
        int b = a;

        // Since this is a 32-bit hash, just mask the low 32-bits from the seed
        int c = seed;

        // Initialize the bytes to the input;
        byte[] k = input;

        // The offset into the input array
        int o = 0;

        // Process 12 characters at a time until we hit the end of the string.
        int remaining = inputLength;
        while (remaining >= 12)
        {
            a += k[o+0] + (k[o+1] << 8) + (k[o+2]  << 16) + (k[o+3]  << 24);
            b += k[o+4] + (k[o+5] << 8) + (k[o+6]  << 16) + (k[o+7]  << 24);
            c += k[o+8] + (k[o+9] << 8) + (k[o+10] << 16) + (k[o+11] << 24);

            // Jenkins uses a macro to "mix"... Java doesn't have those, so
            // I'm just going to write it out: mix( a, b, c )
            // There is one subtle difference between C and Java when it
            // comes to unsigned arithmetic (sigh)... the right shift operator
            // http://www.javamex.com/java_equivalents/unsigned.shtml
            a-=b; a-=c; a^=(c >>>13);
            b-=c; b-=a; b^=(a << 8);
            c-=a; c-=b; c^=(b >>>13);
            a-=b; a-=c; a^=(c >>>12);
            b-=c; b-=a; b^=(a << 16);
            c-=a; c-=b; c^=(b >>>5);
            a-=b; a-=c; a^=(c >>>3);
            b-=c; b-=a; b^=(a << 10);
            c-=a; c-=b; c^=(b >>>15);

            // We've knocked off twelve bytes, let's see how much is remaining
            remaining -= 12;
            o += 12;
        }

        // The fall-through on the switch statement is deliberate here...
        // there should be no breaks in this switch statement.
        c += inputLength;
        switch (remaining)
        {
            case 11: c += k[o+10] << 24;
            case 10: c += k[o+9] << 16;
            case 9:  c += k[o+8] << 8;
            // Note: The first byte of c is reserved for the length.
            case 8:  b += k[o+7] << 24;
            case 7:  b += k[o+6] << 16;
            case 6:  b += k[o+5] << 8;
            case 5:  b += k[o+4];
            case 4:  a += k[o+3] << 24;
            case 3:  a += k[o+2] << 16;
            case 2:  a += k[o+1] << 8;
            case 1:  a += k[o+0];
        }

        // We have to write out mix(a,b,c) again... sigh.
        a-=b; a-=c; a^=(c >>>13);
        b-=c; b-=a; b^=(a << 8);
        c-=a; c-=b; c^=(b >>>13);
        a-=b; a-=c; a^=(c >>>12);
        b-=c; b-=a; b^=(a << 16);
        c-=a; c-=b; c^=(b >>>5);
        a-=b; a-=c; a^=(c >>>3);
        b-=c; b-=a; b^=(a << 10);
        c-=a; c-=b; c^=(b >>>15);

        return c;

    }

    @Override
    public byte[] getDefaultSeed()
    {
        return ObjectUtil.deepCopy(DEFAULT_SEED);
    }

}
