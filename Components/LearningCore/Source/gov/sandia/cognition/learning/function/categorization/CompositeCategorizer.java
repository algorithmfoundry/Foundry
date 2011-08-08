/*
 * File:                CompositeCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 06, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Set;

/**
 * Composes a preprocessor function with a categorizer. It takes an input
 * value and passes it to the preprocessor to get an intermediate value then it
 * passes the intermediate value to the categorizer to produce the output.
 *
 * @param   <InputType>
 *      The type of input to the categorizer. It is the type of input to the
 *      preprocessor.
 * @param   <IntermediateType>
 *      The type of the intermediate values for the categorizer. It is the type
 *      of the output of the preprocessor and the input of the internal
 *      categorizer.
 * @param   <CategoryType>
 *      The type of the output of the categorizer. It is the type of the output
 *      of the internal categorizer as well.
 * @author  Justin Basilico
 * @since   3.0
 */
public class CompositeCategorizer<InputType, IntermediateType, CategoryType>
    extends AbstractCloneableSerializable
    implements Categorizer<InputType, CategoryType>
{

    /** The preprocessor for the input data. */
    protected Evaluator<? super InputType, ? extends IntermediateType> preprocessor;

    /** The categorizer. */
    protected Categorizer<? super IntermediateType, ? extends CategoryType> categorizer;

    /**
     * Creates a new CompositeCategorizer with the preprocessor and categorizer
     * set to null.
     */
    public CompositeCategorizer()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code CompositeCategorizer}.
     *
     * @param   preprocessor
     *      The preprocessing evaluator that is run on the input.
     * @param   categorizer
     *      The categorizer that takes the output of the preprocessor and
     *      categorizes it.
     */
    public CompositeCategorizer(
        final Evaluator<? super InputType, ? extends IntermediateType> preprocessor,
        final Categorizer<? super IntermediateType, ? extends CategoryType> categorizer)
    {
        super();

        this.setPreprocessor(preprocessor);
        this.setCategorizer(categorizer);
    }

    @Override
    public CompositeCategorizer<InputType, IntermediateType, CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        final CompositeCategorizer<InputType, IntermediateType, CategoryType>
            clone = (CompositeCategorizer<InputType, IntermediateType, CategoryType>)
                super.clone();
        clone.preprocessor = ObjectUtil.cloneSmart(this.preprocessor);
        clone.categorizer = ObjectUtil.cloneSmart(this.categorizer);

        return clone;
    }

    public CategoryType evaluate(
        final InputType input)
    {
        // Run the preprocessor.
        final IntermediateType intermediate = this.preprocessor.evaluate(input);

        // Run the categorizer.
        return this.categorizer.evaluate(intermediate);
    }

    /**
     * Gets the set of categories, which is just the set returned by the
     * categorizer. If there is no categorizer, the set is null.
     *
     * @return
     *      The set of categories.
     */
    public Set<? extends CategoryType> getCategories()
    {
        if (this.categorizer == null)
        {
            return null;
        }
        else
        {
            return this.categorizer.getCategories();
        }
    }

    /**
     * Gets the preprocessor, which takes the input and produces an intermediate
     * value that is then passed to the categorizer.
     *
     * @return
     *      The preprocessor.
     */
    public Evaluator<? super InputType, ? extends IntermediateType> getPreprocessor()
    {
        return this.preprocessor;
    }

    /**
     * Sets the preprocessor, which takes the input and produces an intermediate
     * value that is then passed to the categorizer.
     *
     * @param   preprocessor
     *      The preprocessor.
     */
    public void setPreprocessor(
        final Evaluator<? super InputType, ? extends IntermediateType> preprocessor)
    {
        this.preprocessor = preprocessor;
    }

    /**
     * Gets the categorizer, which takes the output of the preprocessor and
     * categorizes it.
     *
     * @return
     *      The categorizer.
     */
    public Categorizer<? super IntermediateType, ? extends CategoryType> getCategorizer()
    {
        return this.categorizer;
    }

    /**
     * Sets the categorizer, which takes the output of the preprocessor and
     * categorizes it.
     *
     * @param   categorizer
     *      The categorizer.
     */
    public void setCategorizer(
        final Categorizer<? super IntermediateType, ? extends CategoryType> categorizer)
    {
        this.categorizer = categorizer;
    }

}
