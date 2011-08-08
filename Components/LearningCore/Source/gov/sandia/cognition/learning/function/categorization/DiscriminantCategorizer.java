/*
 * File:                DiscriminantCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 * 
 * Copyright January 31, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.data.ValueDiscriminantPair;

/**
 * Interface for a {@code Categorizer} that can produce a value to discriminate
 * between how well different instances fit a given category. Thus, it can
 * produce a pair of the category value plus a discriminant for ordering within
 * that category.
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
public interface DiscriminantCategorizer<InputType, CategoryType, DiscriminantType extends Comparable<? super DiscriminantType>>
    extends Categorizer<InputType, CategoryType>
{

    /**
     * Evaluate the categorizer on the given input to produce the expected
     * category plus a discriminant for later producing an ordering of how well
     * items fit into that category.
     *
     * @param   input
     *      The input value to categorize with a discriminate
     * @return
     *      A pair containing the value and the discriminant value used for
     *      ordering results belonging to the same category.
     */
    public ValueDiscriminantPair<CategoryType, DiscriminantType> evaluateWithDiscriminant(
        final InputType input);
    
}
