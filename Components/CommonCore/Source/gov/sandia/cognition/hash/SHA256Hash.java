/*
 * File:                SHA256Hash.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 7, 2011, Sandia Corporation.
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
import java.util.Arrays;

/**
 * The SHA-2, 256-bit (32 byte) hash function.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReference(
    author="Wikipedia",
    title="SHA-2",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/SHA-2"
)
public class SHA256Hash
    extends AbstractHashFunction
{

    /**
     * Length of the hash is 256 bits (32 bytes), {@value}.
     */
    public static final int LENGTH = 32;

    /**
     * Length of the hash block is 512 bits (64 bytes), {@value}.
     */
    public static final int BLOCK_SIZE = 64;

    /**
     * Default seed
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x6a, (byte)0x09, (byte)0xe6, (byte)0x67,
        (byte)0xbb, (byte)0x67, (byte)0xae, (byte)0x85,
        (byte)0x3c, (byte)0x6e, (byte)0xf3, (byte)0x72,
        (byte)0xa5, (byte)0x4f, (byte)0xf5, (byte)0x3a,
        (byte)0x51, (byte)0x0e, (byte)0x52, (byte)0x7f,
        (byte)0x9b, (byte)0x05, (byte)0x68, (byte)0x8c,
        (byte)0x1f, (byte)0x83, (byte)0xd9, (byte)0xab,
        (byte)0x5b, (byte)0xe0, (byte)0xcd, (byte)0x19
    };

    /**
     * Round constants
     */
    private static final int K[] = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
        0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
        0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
        0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
        0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    /**
     * Default constructor
     */
    public SHA256Hash()
    {
        super();
    }

    @Override
    public SHA256Hash clone()
    {
        return (SHA256Hash) super.clone();
    }

    @Override
    public int length()
    {
        return LENGTH;
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
                "Expected seed to be of length " + LENGTH );
        }

        hash( input, output, seed );

    }

    /**
     * SHA-2 256-bit hash function
     * @param input
     * Input to hash
     * @return
     * Output to store the hash
     */
    public static byte[] hash(
        byte[] input )
    {
        byte[] output = new byte[ LENGTH ];
        hash( input, output );
        return output;
    }

    /**
     * SHA-2 256-bit hash function
     * @param input
     * Input to hash
     * @param output
     * Output to store the hash
     */
    public static void hash(
        byte[] input,
        byte[] output )
    {
        hash( input, output, DEFAULT_SEED );
    }

    /**
     * SHA-2 256-bit hash function
     * @param input
     * Input to hash
     * @param output
     * Output to store the hash
     * @param seed
     * Seed values
     */
    public static void hash(
        byte[] input,
        byte[] output,
        byte[] seed )
    {

        if( output.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected output to be of length " + LENGTH );
        }

        if( seed.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected seed to be of length: " + LENGTH );
        }

        if( input == null )
        {
            Arrays.fill(output, (byte) 0 );
            return;
        }

        // The way padMessage works, paddedLength will be an even multiple
        // of 64-bytes (512-bits)
        final byte[] paddedInput = padMessage(input);
        final int paddedLength = paddedInput.length;
        final int numBlocks = paddedLength / BLOCK_SIZE;

        // The "h" variables store the hash
        int h0 = HashFunctionUtil.toInteger( seed, 0*4 );
        int h1 = HashFunctionUtil.toInteger( seed, 1*4 );
        int h2 = HashFunctionUtil.toInteger( seed, 2*4 );
        int h3 = HashFunctionUtil.toInteger( seed, 3*4 );
        int h4 = HashFunctionUtil.toInteger( seed, 4*4 );
        int h5 = HashFunctionUtil.toInteger( seed, 5*4 );
        int h6 = HashFunctionUtil.toInteger( seed, 6*4 );
        int h7 = HashFunctionUtil.toInteger( seed, 7*4 );

        final int[] w = new int[ BLOCK_SIZE ];
        final int wlength = w.length;
        int index = 0;
        for( int block = 0; block < numBlocks; block++ )
        {
            // Convert this 64-byte (512-bit) block into 16 4-byte (32-bit) ints
            for( int i = 0; i < 16; i++ )
            {
                w[i] = ((paddedInput[index++] & 0xff) << 24)
                    |  ((paddedInput[index++] & 0xff) << 16)
                    |  ((paddedInput[index++] & 0xff) <<  8)
                    |  ((paddedInput[index++] & 0xff)      );
            }

            // This spawns words 16..63 in the block
            for( int i = 16; i < wlength; i++ )
            {
                final int wim15 = w[i-15];
                final int wim2 = w[i-2];
                final int s0 = Integer.rotateRight(wim15, 7) ^ Integer.rotateRight(wim15,18) ^ (wim15 >>> 3);
                final int s1 = Integer.rotateRight(wim2, 17) ^ Integer.rotateRight(wim2, 19) ^ (wim2 >>> 10);
                w[i] = w[i-16] + s0 + w[i-7] + s1;
            }

            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;
            int f = h5;
            int g = h6;
            int h = h7;

            // Run 64 rounds of the SHA2 loop for this block
            for( int round = 0; round < 64; round++ )
            {
                int s0 = Integer.rotateRight(a,2) ^ Integer.rotateRight(a,13) ^ Integer.rotateRight(a,22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int t2 = s0 + maj;
                int s1 = Integer.rotateRight(e,6) ^ Integer.rotateRight(e,11) ^ Integer.rotateRight(e,25);
                int ch = (e & f) ^ ((~e) & g);
                int t1 = h + s1 + ch + K[round] + w[round];

                h = g;
                g = f;
                f = e;
                e = d + t1;
                d = c;
                c = b;
                b = a;
                a = t1 + t2;
            }

            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;

        }

        byte[] word = new byte[ 4 ];
        HashFunctionUtil.toByteArray(h0, word );
        System.arraycopy(word, 0, output, 0, word.length);
        HashFunctionUtil.toByteArray(h1, word );
        System.arraycopy(word, 0, output, 1*4, word.length);
        HashFunctionUtil.toByteArray(h2, word );
        System.arraycopy(word, 0, output, 2*4, word.length);
        HashFunctionUtil.toByteArray(h3, word );
        System.arraycopy(word, 0, output, 3*4, word.length);
        HashFunctionUtil.toByteArray(h4, word );
        System.arraycopy(word, 0, output, 4*4, word.length);
        HashFunctionUtil.toByteArray(h5, word );
        System.arraycopy(word, 0, output, 5*4, word.length);
        HashFunctionUtil.toByteArray(h6, word );
        System.arraycopy(word, 0, output, 6*4, word.length);
        HashFunctionUtil.toByteArray(h7, word );
        System.arraycopy(word, 0, output, 7*4, word.length);

    }
    
    /**
     * Pads the given input message with the padding specified by the SHA-2
     * standard
     * @param input
     * Input message to pad, unchanged.
     * @return
     * Padded input message, length will be an integer multiple of 64 bytes.
     */
    private static byte[] padMessage(
        byte[] input )
    {

        final int inputLength = input.length;
        final int remainderLength = inputLength % BLOCK_SIZE;
        
        // We have to append a '1' to the message, then "bunch of zeros",
        // finally the original message length as a 64-bit (8-byte) big endian
        // number.  But the "bunch of zeros" is the number needed to make this
        // an even 64-bit length...
        // Confusing, eh?
        int tailLength;
        if( (BLOCK_SIZE - remainderLength) >= 9 )
        {
            tailLength = BLOCK_SIZE - remainderLength;
        }
        else
        {
            tailLength = 2*BLOCK_SIZE - remainderLength;
        }

        byte[] tail = new byte[ tailLength ];

        // This will append a '1' to the end of the message
        tail[0] = (byte) 0x80;

        // This will shove the length in bits as a big-endian 64-bit number
        // at the end of the block.
        // Magically, the rest of the pad will be zeros.
        final long lengthInBits = inputLength * 8;
        tail[tailLength-9+1] = (byte)(lengthInBits >>> 56);
        tail[tailLength-9+2] = (byte)(lengthInBits >>> 48);
        tail[tailLength-9+3] = (byte)(lengthInBits >>> 40);
        tail[tailLength-9+4] = (byte)(lengthInBits >>> 32);
        tail[tailLength-9+5] = (byte)(lengthInBits >>> 24);
        tail[tailLength-9+6] = (byte)(lengthInBits >>> 16);
        tail[tailLength-9+7] = (byte)(lengthInBits >>> 8);
        tail[tailLength-9+8] = (byte)(lengthInBits);

        // Concatenate the original message with the tail block (or blocks)
        byte[] paddedMessage = new byte[ inputLength + tailLength ];
        System.arraycopy(input, 0, paddedMessage, 0, inputLength);
        System.arraycopy(tail, 0, paddedMessage, inputLength, tailLength);

        return paddedMessage;

    }

    @Override
    public byte[] getDefaultSeed()
    {
        return ObjectUtil.deepCopy(DEFAULT_SEED);
    }

}
