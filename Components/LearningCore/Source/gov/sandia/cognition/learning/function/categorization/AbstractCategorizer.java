/*
 * File:                AbstractCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 07, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An abstract implementation of the {@code Categorizer} interface. Keeps track
 * of the set of categories.
 *
 * @param  <InputType> The type of the input the categorizer can use.
 * @param  <CategoryType> The type of category output by the categorizer.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractCategorizer<InputType, CategoryType>
    extends AbstractCloneableSerializable
    implements Categorizer<InputType, CategoryType>
{
    /** The set of categories that are the possible output values of the
     *  categorizer. */
    protected Set<CategoryType> categories;

    /**
     * Creates a new {@code AbstractCategorizer} with an empty category set.
     */
    public AbstractCategorizer()
    {
        this(new LinkedHashSet<CategoryType>());
    }

    /**
     * Creates a new {@code AbstractCategorizer} with the given category set.
     *
     * @param   categories
     *      The categories.
     */
    public AbstractCategorizer(
        final Set<CategoryType> categories)
    {
        super();

        this.setCategories(categories);
    }

    @Override
    public AbstractCategorizer<InputType, CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractCategorizer<InputType, CategoryType> clone =
            (AbstractCategorizer<InputType, CategoryType>) super.clone();
        clone.setCategories( new LinkedHashSet<CategoryType>( this.getCategories() ) );
        return clone;
    }

    public Set<CategoryType> getCategories()
    {
        return this.categories;
    }

    /**
     * Sets the Set of possible categories, which are the output values.
     *
     * @param   categories The list of possible output categories.
     */
    protected void setCategories(
        final Set<CategoryType> categories)
    {
        this.categories = categories;
    }
}
