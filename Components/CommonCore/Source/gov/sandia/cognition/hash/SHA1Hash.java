/*
 * File:                SHA1Hash.java
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
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Arrays;

/*
 * A Java implementation of the Secure Hash Algorithm, SHA-1, as defined
 * in FIPS PUB 180-1
 * Copyright (C) Sam Ruby 2004
 * All rights reserved
 *
 * Based on code Copyright (C) Paul Johnston 2000 - 2002.
 * See http://pajhome.org.uk/site/legal.html for details.
 *
 * Converted to Java by Russell Beattie 2004
 * Base64 logic and inlining by Sam Ruby 2004
 * Bug fix correcting single bit error in base64 code by John Wilson
 *
 *                                BSD License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * A Java implementation of the Secure Hash Algorithm, SHA-1, as defined
 * in FIPS PUB 180-1.  This is a 160-bit (20-byte) cryptographic
 * hash function.  SHA1 is generally secure, though there are potential
 * weaknesses on the order of 2^51.  This implementation is based on the
 * implementation due to Sam Ruby, Paul Johnson, and John Wilson.
 * @author krdixon
 * @since  3.4.2
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="SHA-1",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/SHA-1"
        )
        ,
        @PublicationReference(
            author="Sam Ruby",
            title="SHA1.java",
            type=PublicationType.WebPage,
            year=2004,
            url="http://intertwingly.net/stories/2004/07/18/SHA1.java"
        )
    }
)

public class SHA1Hash
    extends AbstractHashFunction
{

    /**
     * SHA-1 hash function output is 160-bits or 20-bytes, {@value}.
     */
    public static final int LENGTH = 20;

    /**
     * Default seed
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x67, (byte)0x45, (byte)0x23, (byte)0x01,
        (byte)0xEF, (byte)0xCD, (byte)0xAB, (byte)0x89,
        (byte)0x98, (byte)0xBA, (byte)0xDC, (byte)0xFE,
        (byte)0x10, (byte)0x32, (byte)0x54, (byte)0x76,
        (byte)0xC3, (byte)0xD2, (byte)0xE1, (byte)0xF0
    };

    /**
     * Default constructor
     */
    public SHA1Hash()
    {
        super();
    }

    /**
     * Bitwise rotate a 32-bit number to the left
     * @param num
     * Number to rotate
     * @param cnt
     * Count to rotate
     * @return
     * Rotate number
     */
    private static int rol(
        int num,
        int cnt)
    {
        return (num << cnt) | (num >>> (32 - cnt));
    }

    /**
     * Computes the SHA-1 representation of the given input.
     * @param input
     * Input byte array to compute the SHA-1 hash of.
     * @return
     * 160-bit (20-byte) SHA-1 representation of the input.
     */
    public static byte[] hash(
        byte[] input )
    {
        byte[] output = new byte[ LENGTH ];
        hash( input, output );
        return output;
    }

    /**
     * Computes the SHA-1 representation of the given input.
     * @param input 
     * Input byte array to compute the SHA-1 hash of.
     * @param output
     * Output to store the hash into
     */
    public static void hash(
        byte[] input,
        byte[] output )
    {
        hash( input, output, DEFAULT_SEED );
    }

    /**
     * Computes the SHA-1 representation of the given input and seed.
     * @param x
     * Input byte array to compute the SHA-1 hash of.
     * @param seed
     * Seed to use to offset the hash function
     * @param output
     * 160-bit (20-byte) SHA-1 representation of the input and seed.
     */
    public static void hash(
        byte[] x,
        byte[] output,
        byte[] seed )
    {

        if( output.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected output to be of length: " + LENGTH );
        }

        if( seed.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected seed to be of length: " + LENGTH );
        }

        // Null input is all zeros.
        if( x == null )
        {
            Arrays.fill(output, (byte) 0);
            return;
        }

        // Convert a string to a sequence of 16-word blocks, stored as an array.
        // Append padding bits and the length, as described in the SHA1 standard
        int[] blks = new int[(((x.length + 8) >> 6) + 1) * 16];
        int i;

        for(i = 0; i < x.length; i++)
        {
            blks[i >> 2] |= x[i] << (24 - (i % 4) * 8);
        }

        blks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
        blks[blks.length - 1] = x.length * 8;

        // calculate 160 bit SHA1 hash of the sequence of blocks

        int[] w = new int[80];

        int a = HashFunctionUtil.toInteger(seed, 0*4);
        int b = HashFunctionUtil.toInteger(seed, 1*4);
        int c = HashFunctionUtil.toInteger(seed, 2*4);
        int d = HashFunctionUtil.toInteger(seed, 3*4);
        int e = HashFunctionUtil.toInteger(seed, 4*4);

        for(i = 0; i < blks.length; i += 16)
        {
            int olda = a;
            int oldb = b;
            int oldc = c;
            int oldd = d;
            int olde = e;

            for(int j = 0; j < 80; j++)
            {
                w[j] = (j < 16) ? blks[i + j] :
                    (rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1));

                int t = rol(a, 5) + e + w[j] +
                    ((j < 20) ?  1518500249 + ((b & c) | ((~b) & d))
                    : (j < 40) ?  1859775393 + (b ^ c ^ d)
                    : (j < 60) ? -1894007588 + ((b & c) | (b & d) | (c & d))
                    : -899497514 + (b ^ c ^ d));
                e = d;
                d = c;
                c = rol(b, 30);
                b = a;
                a = t;
            }
            a += olda;
            b += oldb;
            c += oldc;
            d += oldd;
            e += olde;
        }

        byte[] word = new byte[4];
        int offset = 0;
        HashFunctionUtil.toByteArray(a, word);
        for( i = 0; i < word.length; i++ )
        {
            output[offset] = word[i];
            offset++;
        }
        HashFunctionUtil.toByteArray(b, word);
        for( i = 0; i < word.length; i++ )
        {
            output[offset] = word[i];
            offset++;
        }
        HashFunctionUtil.toByteArray(c, word);
        for( i = 0; i < word.length; i++ )
        {
            output[offset] = word[i];
            offset++;
        }
        HashFunctionUtil.toByteArray(d, word);
        for( i = 0; i < word.length; i++ )
        {
            output[offset] = word[i];
            offset++;
        }
        HashFunctionUtil.toByteArray(e, word);
        for( i = 0; i < word.length; i++ )
        {
            output[offset] = word[i];
            offset++;
        }
    }

    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed)
    {
        hash( input, output, seed );
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

}


