/*
 * File:                AbstractBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright December 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * The {@code AbstractBinaryCategorizer} implements the commonality of
 * the {@code BinaryCategorizer}, holding the collection of possible
 * values.
 *
 * @param  <InputType> The categorizer input type.
 * @author Justin Basilico
 * @since  2.0
 */
public abstract class AbstractBinaryCategorizer<InputType>
    extends AbstractCloneableSerializable
    implements BinaryCategorizer<InputType>
{
    /** The possible categories for a binary categorizer. */
    public static final Set<Boolean> BINARY_CATEGORIES =
        Collections.unmodifiableSet(new TreeSet<Boolean>(
            Arrays.asList(Boolean.TRUE, Boolean.FALSE)));
    
    /**
     * Creates a new {@code AbstractBinaryCategorizer}.
     */
    public AbstractBinaryCategorizer()
    {
        super();
    }
    
    public Set<Boolean> getCategories()
    {
        return BINARY_CATEGORIES;
    }
}
