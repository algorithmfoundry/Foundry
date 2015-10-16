/*
 * File:                MD5Hash.java
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

// This class computes MD5 hashes.
// Manually translated by Jon Howell <jonh@cs.dartmouth.edu>
// from some public domain C code (md5.c) included with the ssh-1.2.22 source.
// Tue Jan 19 15:55:50 EST 1999
// $Id: MD5Hash.java,v 1.3 2011/06/28 17:07:08 krdixon Exp $
//
// To compute the message digest of a chunk of bytes, create an
// MD5 object 'md5', call md5.update() as needed on buffers full
// of bytes, and then call md5.md5final(), which
// will fill a supplied 16-byte array with the digest.
//
// A main() method is included that hashes the data on System.in.
//
// It seems to run around 25-30 times slower (JDK1.1.6) than optimized C
// (gcc -O4, version 2.7.2.3). Measured on a Sun Ultra 5 (SPARC 270MHz).
//
// Comments from md5.c from ssh-1.2.22, the basis for this code:
//
/* This code has been heavily hacked by Tatu Ylonen <ylo@cs.hut.fi> to
make it compile on machines like Cray that don't have a 32 bit integer
type. */
/*
 * This code implements the MD5 message-digest algorithm.
 * The algorithm is due to Ron Rivest.  This code was
 * written by Colin Plumb in 1993, no copyright is claimed.
 * This code is in the public domain; do with it what you wish.
 *
 * Equivalent code is available from RSA Data Security, Inc.
 * This code has been tested against that, and is equivalent,
 * except that you don't need to include two pages of legalese
 * with every copy.
 *
 * To compute the message digest of a chunk of bytes, declare an
 * MD5Context structure, pass it to MD5Init, call MD5Update as
 * needed on buffers full of bytes, and then call MD5Final, which
 * will fill a supplied 16-byte array with the digest.
 */

/**
 * Implementation of the MD5 128-bit (16-byte) cryptographic hash function.
 * This algorithm is known to be unsafe for cryptographic purposes and
 * is implemented for historic purposes only.  This implementation is based
 * on the Java port of C code by Jon Howell.  According to Howell, it is
 * 25-30 times slower than the optimized C version.  Do not use MD5.
 *
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="MD5",
            type=PublicationType.WebPage,
            year=2010
        )
        ,
        @PublicationReference(
            author="Jon Howell",
            title="MD5 Java class",
            type=PublicationType.Misc,
            year=1999,
            url="http://www.jonh.net/~jonh/md5/MD5.java",
            notes="A fairly inefficient implementation, but a correct one!"
        )
    }
)
public class MD5Hash
    extends AbstractHashFunction
{

    /**
     * MD5 is a 128-bit (16-byte) length hash.
     */
    public static final int LENGTH = 16;

    /**
     * Default seed
     */
    protected static final byte[] DEFAULT_SEED = {
        (byte)0x67, (byte)0x45, (byte)0x23, (byte)0x01,
        (byte)0xef, (byte)0xcd, (byte)0xab, (byte)0x89,
        (byte)0x98, (byte)0xba, (byte)0xdc, (byte)0xfe,
        (byte)0x10, (byte)0x32, (byte)0x54, (byte)0x76
    };

    /**
     * These were originally unsigned ints.
     * This Java code makes an effort to avoid sign traps.
     * buf[] is where the hash accumulates.
     */
    private int buf[];

    /**
     * This is the count of bits hashed so far.
     */
    private long bits;

    /**
     * This is a buffer where we stash bytes until we have
     * enough (64) to perform a transform operation.
     */
    private byte in[];

    /**
     * Default constructor
     */
    public MD5Hash()
    {
        this.buf = new int[4];

        this.in = new byte[64];
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
                "Output must be of length " + LENGTH );
        }

        if( seed.length != LENGTH )
        {
            throw new IllegalArgumentException(
                "Expected seed to be of length: " + LENGTH );
        }

        if( input == null )
        {
            Arrays.fill( output, (byte) 0 );
            return;
        }


        // no bits yet.
        this.bits = 0L;

        // fill the hash accumulator with a seed value
        this.buf[0] = HashFunctionUtil.toInteger(seed, 0*4);
        this.buf[1] = HashFunctionUtil.toInteger(seed, 1*4);
        this.buf[2] = HashFunctionUtil.toInteger(seed, 2*4);
        this.buf[3] = HashFunctionUtil.toInteger(seed, 3*4);

        this.update(input);
        this.md5final(output);
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
     * Main MD5 update step
     * @param newbuf
     * new buffer to update
     */
    private void update(
        byte[] newbuf)
    {
        update(newbuf, 0, newbuf.length);
    }


    /**
     * Interior MD5 update step
     * @param newbuf
     * new buffer to update
     * @param bufstart
     * Offset into the buffer to update
     * @param buflen
     * how many bytes to update (not necessarily all of them!)
     */
    public void update(
        byte[] newbuf,
        int bufstart,
        int buflen)
    {
        int t;
        int len = buflen;

        // shash old bits value for the "Bytes already in" computation
        // just below.
        t = (int) bits;	// (int) cast should just drop high bits, I hope

        /* update bitcount */
        /* the C code used two 32-bit ints separately, and carefully
         * ensured that the carry carried.
         * Java has a 64-bit long, which is just what the code really wants.
         */
        bits += (long) (len << 3);

        t = (t >>> 3) & 0x3f;	/* Bytes already in this->in */

        /* Handle any leading odd-sized chunks */
        /* (that is, any left-over chunk left by last update() */

        if (t != 0)
        {
            int p = t;
            t = 64 - t;
            if (len < t)
            {
                System.arraycopy(newbuf, bufstart, in, p, len);
                return;
            }
            System.arraycopy(newbuf, bufstart, in, p, t);
            transform();
            bufstart += t;
            len -= t;
        }

        /* Process data in 64-byte chunks */
        while (len >= 64)
        {
            System.arraycopy(newbuf, bufstart, in, 0, 64);
            this.transform();
            bufstart += 64;
            len -= 64;
        }

        /* Handle any remaining bytes of data. */
        /* that is, stash them for the next update(). */
        System.arraycopy(newbuf, bufstart, in, 0, len);
    }

    /**
     * Final wrapup - pad to 64-byte boundary with the bit pattern
     * 1 0* (64-bit count of bits processed, MSB-first)
     * @param digest
     * Output value from the hash code
     */
    private void md5final(
        byte[] digest)
    {
        /* "final" is a poor method name in Java. :v) */
        int count;
        int p;		// in original code, this is a pointer; in this java code
        // it's an index into the array this->in.

        /* Compute number of bytes mod 64 */
        count = (int) ((bits >>> 3) & 0x3F);

        /* Set the first char of padding to 0x80.  This is safe since there is
        always at least one byte free */
        p = count;
        in[p++] = (byte) 0x80;

        /* Bytes of padding needed to make 64 bytes */
        count = 64 - 1 - count;

        /* Pad out to 56 mod 64 */
        if (count < 8)
        {
            /* Two lots of padding:  Pad the first block to 64 bytes */
            zeroByteArray(in, p, count);
            transform();

            /* Now fill the next block with 56 bytes */
            zeroByteArray(in, 0, 56);
        }
        else
        {
            /* Pad block to 56 bytes */
            zeroByteArray(in, p, count - 8);
        }

        /* Append length in bits and transform */
        // Could use a PUT_64BIT... func here. This is a fairly
        // direct translation from the C code, where bits was an array
        // of two 32-bit ints.
        int lowbits = (int) bits;
        int highbits = (int) (bits >>> 32);
        PUT_32BIT_LSB_FIRST(in, 56, lowbits);
        PUT_32BIT_LSB_FIRST(in, 60, highbits);

        transform();
        PUT_32BIT_LSB_FIRST(digest, 0, buf[0]);
        PUT_32BIT_LSB_FIRST(digest, 4, buf[1]);
        PUT_32BIT_LSB_FIRST(digest, 8, buf[2]);
        PUT_32BIT_LSB_FIRST(digest, 12, buf[3]);

        /* zero sensitive data */
        /* notice this misses any sneaking out on the stack. The C
         * version uses registers in some spots, perhaps because
         * they care about this.
         */
        Arrays.fill( in, (byte) 0 );
        Arrays.fill( buf, 0 );
        bits = 0;
    }

    /////////////////////////////////////////////////////////////////////
    // Below here ye will only finde private functions                 //
    /////////////////////////////////////////////////////////////////////

    /**
     * Zeros the given portion of the array
     * @param a
     * Array
     * @param start
     * start offset
     * @param length
     * how many bytes to zero
     */
    private void zeroByteArray(
        byte[] a,
        int start,
        int length)
    {
        setByteArray(a, (byte) 0, start, length);
    }

    /**
     * Assigns a portion of the array to the given value
     * @param a
     * Array
     * @param val
     * value to assign
     * @param start
     * start offset
     * @param length
     * how many bytes to zero
     */
    private void setByteArray(
        byte[] a,
        byte val,
        int start,
        int length)
    {
        int i;
        int end = start + length;
        for (i = start; i < end; i++)
        {
            a[i] = val;
        }
    } 

    /**
     * MD5 main step
     * @param w
     * w
     * @param x
     * x
     * @param y
     * y
     * @param z
     * z
     * @param data
     * data
     * @param s
     * s
     * @return
     * step value
     */
    private int MD5STEP1(
        int w,
        int x,
        int y,
        int z,
        int data,
        int s )
    {
        w += data + (z ^ (x & (y ^ z)));
        return x + ((w << s) | (w >>> (32 - s)));
    }

    /**
     * MD5 main step
     * @param w
     * w
     * @param x
     * x
     * @param y
     * y
     * @param z
     * z
     * @param data
     * data
     * @param s
     * s
     * @return
     * step value
     */
    private int MD5STEP2(
        int w,
        int x,
        int y,
        int z,
        int data,
        int s )
    {
        w += data + (y ^ (z & (x ^ y)));
        return x + ((w << s) | (w >>> (32 - s)));
    }

    /**
     * MD5 main step
     * @param w
     * w
     * @param x
     * x
     * @param y
     * y
     * @param z
     * z
     * @param data
     * data
     * @param s
     * s
     * @return
     * step value
     */
    private int MD5STEP3(
        int w,
        int x,
        int y,
        int z,
        int data,
        int s )
    {
        w += data + (x ^ y ^ z);
        return x + ((w << s) | (w >>> (32 - s)));
    }

    /**
     * MD5 main step
     * @param w
     * w
     * @param x
     * x
     * @param y
     * y
     * @param z
     * z
     * @param data
     * data
     * @param s
     * s
     * @return
     * step value
     */
    private int MD5STEP4(
        int w,
        int x,
        int y,
        int z,
        int data,
        int s )
    {
        w += data + (y ^ (x | ~z));
        return x + ((w << s) | (w >>> (32 - s)));
    }

    /**
     * transforms the MD5 data
     */
    private void transform()
    {
        final int ii00 = ((in[ 0] & 0xff) | ((in[ 1] & 0xff) << 8) |  ((in[ 2] & 0xff) << 16) | ((in[ 3] & 0xff) << 24));
        final int ii01 = ((in[ 4] & 0xff) | ((in[ 5] & 0xff) << 8) |  ((in[ 6] & 0xff) << 16) | ((in[ 7] & 0xff) << 24));
        final int ii02 = ((in[ 8] & 0xff) | ((in[ 9] & 0xff) << 8) |  ((in[10] & 0xff) << 16) | ((in[11] & 0xff) << 24));
        final int ii03 = ((in[12] & 0xff) | ((in[13] & 0xff) << 8) |  ((in[14] & 0xff) << 16) | ((in[15] & 0xff) << 24));
        final int ii04 = ((in[16] & 0xff) | ((in[17] & 0xff) << 8) |  ((in[18] & 0xff) << 16) | ((in[19] & 0xff) << 24));
        final int ii05 = ((in[20] & 0xff) | ((in[21] & 0xff) << 8) |  ((in[22] & 0xff) << 16) | ((in[23] & 0xff) << 24));
        final int ii06 = ((in[24] & 0xff) | ((in[25] & 0xff) << 8) |  ((in[26] & 0xff) << 16) | ((in[27] & 0xff) << 24));
        final int ii07 = ((in[28] & 0xff) | ((in[29] & 0xff) << 8) |  ((in[30] & 0xff) << 16) | ((in[31] & 0xff) << 24));
        final int ii08 = ((in[32] & 0xff) | ((in[33] & 0xff) << 8) |  ((in[34] & 0xff) << 16) | ((in[35] & 0xff) << 24));
        final int ii09 = ((in[36] & 0xff) | ((in[37] & 0xff) << 8) |  ((in[38] & 0xff) << 16) | ((in[39] & 0xff) << 24));
        final int ii10 = ((in[40] & 0xff) | ((in[41] & 0xff) << 8) |  ((in[42] & 0xff) << 16) | ((in[43] & 0xff) << 24));
        final int ii11 = ((in[44] & 0xff) | ((in[45] & 0xff) << 8) |  ((in[46] & 0xff) << 16) | ((in[47] & 0xff) << 24));
        final int ii12 = ((in[48] & 0xff) | ((in[49] & 0xff) << 8) |  ((in[50] & 0xff) << 16) | ((in[51] & 0xff) << 24));
        final int ii13 = ((in[52] & 0xff) | ((in[53] & 0xff) << 8) |  ((in[54] & 0xff) << 16) | ((in[55] & 0xff) << 24));
        final int ii14 = ((in[56] & 0xff) | ((in[57] & 0xff) << 8) |  ((in[58] & 0xff) << 16) | ((in[59] & 0xff) << 24));
        final int ii15 = ((in[60] & 0xff) | ((in[61] & 0xff) << 8) |  ((in[62] & 0xff) << 16) | ((in[63] & 0xff) << 24));

        int a = this.buf[0];
        int b = this.buf[1];
        int c = this.buf[2];
        int d = this.buf[3];

        a = MD5STEP1( a, b, c, d, ii00 + 0xd76aa478,  7);
        d = MD5STEP1( d, a, b, c, ii01 + 0xe8c7b756, 12);
        c = MD5STEP1( c, d, a, b, ii02 + 0x242070db, 17);
        b = MD5STEP1( b, c, d, a, ii03 + 0xc1bdceee, 22);
        a = MD5STEP1( a, b, c, d, ii04 + 0xf57c0faf,  7);
        d = MD5STEP1( d, a, b, c, ii05 + 0x4787c62a, 12);
        c = MD5STEP1( c, d, a, b, ii06 + 0xa8304613, 17);
        b = MD5STEP1( b, c, d, a, ii07 + 0xfd469501, 22);
        a = MD5STEP1( a, b, c, d, ii08 + 0x698098d8,  7);
        d = MD5STEP1( d, a, b, c, ii09 + 0x8b44f7af, 12);
        c = MD5STEP1( c, d, a, b, ii10 + 0xffff5bb1, 17);
        b = MD5STEP1( b, c, d, a, ii11 + 0x895cd7be, 22);
        a = MD5STEP1( a, b, c, d, ii12 + 0x6b901122,  7);
        d = MD5STEP1( d, a, b, c, ii13 + 0xfd987193, 12);
        c = MD5STEP1( c, d, a, b, ii14 + 0xa679438e, 17);
        b = MD5STEP1( b, c, d, a, ii15 + 0x49b40821, 22);

        a = MD5STEP2( a, b, c, d, ii01 + 0xf61e2562,  5);
        d = MD5STEP2( d, a, b, c, ii06 + 0xc040b340,  9);
        c = MD5STEP2( c, d, a, b, ii11 + 0x265e5a51, 14);
        b = MD5STEP2( b, c, d, a, ii00 + 0xe9b6c7aa, 20);
        a = MD5STEP2( a, b, c, d, ii05 + 0xd62f105d,  5);
        d = MD5STEP2( d, a, b, c, ii10 + 0x02441453,  9);
        c = MD5STEP2( c, d, a, b, ii15 + 0xd8a1e681, 14);
        b = MD5STEP2( b, c, d, a, ii04 + 0xe7d3fbc8, 20);
        a = MD5STEP2( a, b, c, d, ii09 + 0x21e1cde6,  5);
        d = MD5STEP2( d, a, b, c, ii14 + 0xc33707d6,  9);
        c = MD5STEP2( c, d, a, b, ii03 + 0xf4d50d87, 14);
        b = MD5STEP2( b, c, d, a, ii08 + 0x455a14ed, 20);
        a = MD5STEP2( a, b, c, d, ii13 + 0xa9e3e905,  5);
        d = MD5STEP2( d, a, b, c, ii02 + 0xfcefa3f8,  9);
        c = MD5STEP2( c, d, a, b, ii07 + 0x676f02d9, 14);
        b = MD5STEP2( b, c, d, a, ii12 + 0x8d2a4c8a, 20);

        a = MD5STEP3( a, b, c, d, ii05 + 0xfffa3942,  4);
        d = MD5STEP3( d, a, b, c, ii08 + 0x8771f681, 11);
        c = MD5STEP3( c, d, a, b, ii11 + 0x6d9d6122, 16);
        b = MD5STEP3( b, c, d, a, ii14 + 0xfde5380c, 23);
        a = MD5STEP3( a, b, c, d, ii01 + 0xa4beea44,  4);
        d = MD5STEP3( d, a, b, c, ii04 + 0x4bdecfa9, 11);
        c = MD5STEP3( c, d, a, b, ii07 + 0xf6bb4b60, 16);
        b = MD5STEP3( b, c, d, a, ii10 + 0xbebfbc70, 23);
        a = MD5STEP3( a, b, c, d, ii13 + 0x289b7ec6,  4);
        d = MD5STEP3( d, a, b, c, ii00 + 0xeaa127fa, 11);
        c = MD5STEP3( c, d, a, b, ii03 + 0xd4ef3085, 16);
        b = MD5STEP3( b, c, d, a, ii06 + 0x04881d05, 23);
        a = MD5STEP3( a, b, c, d, ii09 + 0xd9d4d039,  4);
        d = MD5STEP3( d, a, b, c, ii12 + 0xe6db99e5, 11);
        c = MD5STEP3( c, d, a, b, ii15 + 0x1fa27cf8, 16);
        b = MD5STEP3( b, c, d, a, ii02 + 0xc4ac5665, 23);

        a = MD5STEP4( a, b, c, d, ii00 + 0xf4292244,  6);
        d = MD5STEP4( d, a, b, c, ii07 + 0x432aff97, 10);
        c = MD5STEP4( c, d, a, b, ii14 + 0xab9423a7, 15);
        b = MD5STEP4( b, c, d, a, ii05 + 0xfc93a039, 21);
        a = MD5STEP4( a, b, c, d, ii12 + 0x655b59c3,  6);
        d = MD5STEP4( d, a, b, c, ii03 + 0x8f0ccc92, 10);
        c = MD5STEP4( c, d, a, b, ii10 + 0xffeff47d, 15);
        b = MD5STEP4( b, c, d, a, ii01 + 0x85845dd1, 21);
        a = MD5STEP4( a, b, c, d, ii08 + 0x6fa87e4f,  6);
        d = MD5STEP4( d, a, b, c, ii15 + 0xfe2ce6e0, 10);
        c = MD5STEP4( c, d, a, b, ii06 + 0xa3014314, 15);
        b = MD5STEP4( b, c, d, a, ii13 + 0x4e0811a1, 21);
        a = MD5STEP4( a, b, c, d, ii04 + 0xf7537e82,  6);
        d = MD5STEP4( d, a, b, c, ii11 + 0xbd3af235, 10);
        c = MD5STEP4( c, d, a, b, ii02 + 0x2ad7d2bb, 15);
        b = MD5STEP4( b, c, d, a, ii09 + 0xeb86d391, 21);

        this.buf[0] += a;
        this.buf[1] += b;
        this.buf[2] += c;
        this.buf[3] += d;
    }

    /**
     * Sets the LSB fist
     * @param b
     * bytes
     * @param off
     * offset
     * @param value
     * LSB value
     */
    private void PUT_32BIT_LSB_FIRST(
        byte[] b,
        int off,
        int value)
    {
        b[off + 0] = (byte) (value);
        b[off + 1] = (byte) (value >>> 8);
        b[off + 2] = (byte) (value >>> 16);
        b[off + 3] = (byte) (value >>> 24);
    }

}
