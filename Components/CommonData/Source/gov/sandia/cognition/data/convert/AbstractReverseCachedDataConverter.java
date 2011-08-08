/*
 * File:                AbstractReverseCachedDataConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert;

/**
 * Abstract implementation of  {@code ReversibleDataConverter} that caches the 
 * reverse converter.
 * 
 * @param   <InputType>
 *      The input type to convert from.
 * @param   <OutputType>
 *      The output type to convert to.
 * @param   <ReverseConverterType>
 *      The type of the reverse converter.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractReverseCachedDataConverter<InputType, OutputType, ReverseConverterType extends DataConverter<? super OutputType, ? extends InputType>>
    extends AbstractReversibleDataConverter<InputType, OutputType>
{

    /** A cached value of the reverse converter. */
    protected transient ReverseConverterType reverse;

    /**
     * Creates a new {@code AbstractReverseCachedDataConverter}.
     */
    public AbstractReverseCachedDataConverter()
    {
        super();

        // The cached reverse starts out as null.
        this.reverse = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    public ReverseConverterType reverse()
    {
        if (this.reverse == null)
        {
            // We have not cached the reverse converter, so cache it.
            this.reverse = this.createReverse();
        }

        return this.reverse;
    }

    /**
     * Creates a new reverse converter. Should only be called when there is a
     * cache miss for this converter.
     * 
     * @return  The reverse converter.
     */
    protected abstract ReverseConverterType createReverse();

}
