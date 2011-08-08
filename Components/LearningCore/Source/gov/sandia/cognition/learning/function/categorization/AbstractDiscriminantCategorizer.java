/*
 * File:                AbstractDiscriminantCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 03, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.data.ValueDiscriminantPair;
import java.util.Set;

/**
 * An abstract implementation of the {@code DiscriminantCategorizer} interface.
 * It implements the evaluate method to call the evaluateWithDiscriminant
 * method.
 *
 * @param  <InputType>
 *      The type of the input the categorizer can use.
 * @param  <CategoryType>
 *      The type of category output by the categorizer.
 * @param <DiscriminantType>
 *      The type of discriminant that can be used to sort the output.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractDiscriminantCategorizer<InputType, CategoryType, DiscriminantType extends Comparable<? super DiscriminantType>>
    extends AbstractCategorizer<InputType, CategoryType>
    implements DiscriminantCategorizer<InputType, CategoryType, DiscriminantType>
{

    /**
     * Creates a new {@code AbstractDiscriminantCategorizer} with an empty
     * set of categories.
     */
    public AbstractDiscriminantCategorizer()
    {
        super();
    }

    /**
     * Creates a new {@code AbstractCategorizer} with the given category set.
     *
     * @param   categories
     *      The categories.
     */
    public AbstractDiscriminantCategorizer(
        final Set<CategoryType> categories)
    {
        super(categories);
    }

    @Override
    public CategoryType evaluate(
        final InputType input)
    {
        // Evaluate as a value-discriminant pair and then get the value.
        final ValueDiscriminantPair<CategoryType, ?> discriminant =
            this.evaluateWithDiscriminant(input);
        return discriminant == null ? null : discriminant.getValue();
    }

}
