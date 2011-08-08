/*
 * File:                AbstractTermWeightNormalizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.normalize;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code TermWeightNormalizer} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractTermWeightNormalizer
    extends AbstractCloneableSerializable
    implements TermWeightNormalizer
{

    /**
     * Creates a new {@code AbstractTermWeightNormalizer}.
     */
    public AbstractTermWeightNormalizer()
    {
        super();
    }

}
