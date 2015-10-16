/*
 * File:                HashFunction.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * A hash function maps an arbitrarily large input and maps it onto a
 * finite-length output.
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
public interface HashFunction
    extends CloneableSerializable,
    Evaluator<byte[],byte[]>
{
    
    /**
     * Evaluates the input byte array with the given seed.
     * @param input
     * Input array to hash into the output
     * @param seed
     * Seed to offset the input
     * @return
     * Hash code of the input with the given seed
     */
    public byte[] evaluate(
        byte[] input,
        byte[] seed );

    /**
     * Evaluates the input into the given output
     * @param input
     * Input to compute the hash-function value of
     * @param output
     * Output to put the hash-function into
     */
    public void evaluateInto(
        byte[] input,
        byte[] output );

    /**
     * Evaluates the input into the given output
     * @param input
     * Input to compute the hash-function value of
     * @param output
     * Output to put the hash-function into
     * @param seed
     * Seed to offset the hash code.
     */
    public void evaluateInto(
        byte[] input,
        byte[] output,
        byte[] seed );

    /**
     * Returns the number of bytes in the output hash code.
     * @return
     * Number of bytes in the output hash code.
     */
    public int length();

    /**
     * Gets the default seed for the hash function
     * @return
     * Default seed for the hash algorithm
     */
    public byte[] getDefaultSeed();

}
