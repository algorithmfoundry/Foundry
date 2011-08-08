/*
 * File:                AbstractDiscriminantBinaryCategorizer.java
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
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;

/**
 * An abstract implementation of the {@code DiscriminantBinaryCategorizer}
 * interface. Provides implementations of the evaluate and
 * evaluateWithDiscriminant that call evaluateAsDouble and uses the sign as
 * the category and the magnitude as the discriminant.
 *
 * @param  <InputType>
 *      The type of the input the categorizer can use.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractDiscriminantBinaryCategorizer<InputType>
    extends AbstractBinaryCategorizer<InputType>
    implements DiscriminantBinaryCategorizer<InputType>
{

    /**
     * Creates a new {@code AbstractDiscriminantBinaryCategorizer}.
     */
    public AbstractDiscriminantBinaryCategorizer()
    {
        super();
    }

    @Override
    public Boolean evaluate(
        final InputType input)
    {
        // The sign of the double evaluation is the category.
        return this.evaluateAsDouble(input) >= 0.0;
    }

    @Override
    public ValueDiscriminantPair<Boolean, Double> evaluateWithDiscriminant(
        final InputType input)
    {
        // Evaluate the input as a double whose sign is the category.
        final double value = this.evaluateAsDouble(input);
        final boolean category = value >= 0.0;

        // The discriminant needs to be larger for a better match, even in
        // the case of the false category, which requires doing an absolute
        // value to the sign.
        final double discriminant = category ? value : -value;
        return DefaultWeightedValueDiscriminant.create(category, discriminant);
    }

}
