/*
 * File:                SHA512Hash.java
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
 * The SHA-2 512-bit (64-byte) hash function.
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
public class SHA512Hash 
    extends AbstractHashFunction
{

    /**
     * Length of the hash is 512 bits (64 bytes), {@value}.
     */
    public static final int LENGTH = 64;

    /**
     * Length of the hash block is 1024 bits (128 bytes), {@value}.
     */
    public static final int BLOCK_SIZE = 128;

    /**
     * Default seed
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x6a, (byte)0x09, (byte)0xe6, (byte)0x67, (byte)0xf3, (byte)0xbc, (byte)0xc9, (byte)0x08,
        (byte)0xbb, (byte)0x67, (byte)0xae, (byte)0x85, (byte)0x84, (byte)0xca, (byte)0xa7, (byte)0x3b,
        (byte)0x3c, (byte)0x6e, (byte)0xf3, (byte)0x72, (byte)0xfe, (byte)0x94, (byte)0xf8, (byte)0x2b,
        (byte)0xa5, (byte)0x4f, (byte)0xf5, (byte)0x3a, (byte)0x5f, (byte)0x1d, (byte)0x36, (byte)0xf1,
        (byte)0x51, (byte)0x0e, (byte)0x52, (byte)0x7f, (byte)0xad, (byte)0xe6, (byte)0x82, (byte)0xd1,
        (byte)0x9b, (byte)0x05, (byte)0x68, (byte)0x8c, (byte)0x2b, (byte)0x3e, (byte)0x6c, (byte)0x1f,
        (byte)0x1f, (byte)0x83, (byte)0xd9, (byte)0xab, (byte)0xfb, (byte)0x41, (byte)0xbd, (byte)0x6b,
        (byte)0x5b, (byte)0xe0, (byte)0xcd, (byte)0x19, (byte)0x13, (byte)0x7e, (byte)0x21, (byte)0x79
    };

    /**
     * Round constants
     */
    private static final long[] K = {
        0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
        0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
        0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
        0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
        0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
        0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
        0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
        0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
        0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
        0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
        0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
        0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
        0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
        0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
        0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
        0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
        0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
        0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
        0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
        0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
    };


    /** 
     * Creates a new instance of SHA512Hash 
     */
    public SHA512Hash()
    {
        super();
    }

    @Override
    public SHA512Hash clone()
    {
        return (SHA512Hash) super.clone();
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
        long h0 = HashFunctionUtil.toLong( seed, 0*8 );
        long h1 = HashFunctionUtil.toLong( seed, 1*8 );
        long h2 = HashFunctionUtil.toLong( seed, 2*8 );
        long h3 = HashFunctionUtil.toLong( seed, 3*8 );
        long h4 = HashFunctionUtil.toLong( seed, 4*8 );
        long h5 = HashFunctionUtil.toLong( seed, 5*8 );
        long h6 = HashFunctionUtil.toLong( seed, 6*8 );
        long h7 = HashFunctionUtil.toLong( seed, 7*8 );

        final long[] w = new long[ 80 ];
        final int wlength = w.length;
        int index = 0;
        for( int block = 0; block < numBlocks; block++ )
        {
            // Convert this 128-byte (1024-bit) block into 16 8-byte (64-bit) longs
            for( int i = 0; i < 16; i++ )
            {
                w[i] = ((paddedInput[index++] & 0xffL) << 56)
                    |  ((paddedInput[index++] & 0xffL) << 48)
                    |  ((paddedInput[index++] & 0xffL) << 40)
                    |  ((paddedInput[index++] & 0xffL) << 32)
                    |  ((paddedInput[index++] & 0xffL) << 24)
                    |  ((paddedInput[index++] & 0xffL) << 16)
                    |  ((paddedInput[index++] & 0xffL) <<  8)
                    |  ((paddedInput[index++] & 0xffL));
            }

            for( int i = 16; i < wlength; i++ )
            {
                final long wim15 = w[i-15];
                final long wim2 = w[i-2];
                final long s0 = Long.rotateRight(wim15, 1) ^ Long.rotateRight(wim15,8) ^ (wim15 >>> 7);
                final long s1 = Long.rotateRight(wim2, 19) ^ Long.rotateRight(wim2, 61) ^ (wim2 >>> 6);
                w[i] = w[i-16] + s0 + w[i-7] + s1;
            }

            long a = h0;
            long b = h1;
            long c = h2;
            long d = h3;
            long e = h4;
            long f = h5;
            long g = h6;
            long h = h7;

            // Run 64 rounds of the SHA2 loop for this block
            for( int round = 0; round < 80; round++ )
            {
                long s0 = Long.rotateRight(a,28) ^ Long.rotateRight(a,34) ^ Long.rotateRight(a,39);
                long maj = (a & b) ^ (a & c) ^ (b & c);
                long t2 = s0 + maj;
                long s1 = Long.rotateRight(e,14) ^ Long.rotateRight(e,18) ^ Long.rotateRight(e,41);
                long ch = (e & f) ^ ((~e) & g);
                long t1 = h + s1 + ch + K[round] + w[round];

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

        byte[] word = new byte[ 8 ];
        HashFunctionUtil.toByteArray(h0, word );
        System.arraycopy(word, 0, output, 0, word.length);
        HashFunctionUtil.toByteArray(h1, word );
        System.arraycopy(word, 0, output, 1*8, word.length);
        HashFunctionUtil.toByteArray(h2, word );
        System.arraycopy(word, 0, output, 2*8, word.length);
        HashFunctionUtil.toByteArray(h3, word );
        System.arraycopy(word, 0, output, 3*8, word.length);
        HashFunctionUtil.toByteArray(h4, word );
        System.arraycopy(word, 0, output, 4*8, word.length);
        HashFunctionUtil.toByteArray(h5, word );
        System.arraycopy(word, 0, output, 5*8, word.length);
        HashFunctionUtil.toByteArray(h6, word );
        System.arraycopy(word, 0, output, 6*8, word.length);
        HashFunctionUtil.toByteArray(h7, word );
        System.arraycopy(word, 0, output, 7*8, word.length);

    }

    @Override
    public byte[] getDefaultSeed()
    {
        return ObjectUtil.deepCopy(DEFAULT_SEED);
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

}
