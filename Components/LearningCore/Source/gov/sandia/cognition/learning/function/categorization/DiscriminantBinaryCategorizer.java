/*
 * File:                DiscriminantBinaryCategorizer.java
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

/**
 * Interface for a linear discriminant categorizer in the binary categorization
 * domain. It allows the output to be evaluated as a double.
 *
 * @param  <InputType>
 *      The type of the input the categorizer can use.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface DiscriminantBinaryCategorizer<InputType>
    extends BinaryCategorizer<InputType>, DiscriminantCategorizer<InputType, Boolean, Double>
{

    /**
     * Categorizes the given input vector as a double where values greater than
     * zero are in the true category and less than zero are in the false
     * category. Zero can be treated as unknown or as the true category,
     * depending on the categorizer.
     *
     * @param  input
     *      The input value to categorize.
     * @return
     *      The categorization of the input value where the sign is the
     *      categorization.
     */
    public double evaluateAsDouble(
        final InputType input);
    
}
