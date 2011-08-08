/*
 * File:                AbstractInputOutputPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code InputOutputPair} interface. It
 * implements the toString, getFirst, and getSecond methods.
 *
 * @param  <InputType>
 *      The type for the input object in the pair.
 * @param  <OutputType>
 *      The type for the output object in the pair.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractInputOutputPair<InputType, OutputType>
    extends AbstractCloneableSerializable
    implements InputOutputPair<InputType, OutputType>
{

    /**
     * Creates a new {@code AbstractInputOutputPair}.
     */
    public AbstractInputOutputPair()
    {
        super();
    }

    @Override
    public String toString()
    {
        return "Input: " + this.getInput() + ", Output: " + this.getOutput();
    }

    /**
     * Gets the input, which is the first element in the pair.
     *
     * @return
     *      The input.
     */
    public InputType getFirst()
    {
        return this.getInput();
    }

    /**
     * Gets the output, which is the second element in the pair.
     *
     * @return
     *      The output.
     */
    public OutputType getSecond()
    {
        return this.getOutput();
    }

}
