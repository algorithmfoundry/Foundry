/*
 * File:                AbstractHashFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.hash;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Partial implementation of HashFunction
 * @author Kevin R. Dixon
 * @since  3.4.2
 */
public abstract class AbstractHashFunction
    extends AbstractCloneableSerializable
    implements HashFunction
{

    /** 
     * Creates a new instance of AbstractHashFunction 
     */
    public AbstractHashFunction()
    {
        super();
    }

    @Override
    public AbstractHashFunction clone()
    {
        AbstractHashFunction clone = (AbstractHashFunction) super.clone();
        return clone;
    }

    @Override
    public byte[] evaluate(
        byte[] input)
    {
        return this.evaluate(input, this.getDefaultSeed() );
    }

    @Override
    public byte[] evaluate(
        byte[] input,
        byte[] seed)
    {
        byte[] output = new byte[ this.length() ];
        this.evaluateInto(input, output, seed);
        return output;
    }

    @Override
    public void evaluateInto(
        byte[] input,
        byte[] output)
    {
        this.evaluateInto(input, output, this.getDefaultSeed());
    }    

}
