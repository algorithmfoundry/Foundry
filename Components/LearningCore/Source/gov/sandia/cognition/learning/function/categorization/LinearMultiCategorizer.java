/*
 * File:                LinearMultiCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A multi-category version of the LinearBinaryCategorizer that keeps a separate
 * LinearBinaryCategorizer for each category. Each of these linear categorizers
 * are called prototypes. Categorization is done by choosing the prototype
 * (w_c, b_c) such that w_c * x + b_c is highest.
 * 
 * @param   <CategoryType>
 *      The type of categories that the categorizer can output.
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class LinearMultiCategorizer<CategoryType>
    extends AbstractCloneableSerializable
    implements DiscriminantCategorizer<Vectorizable, CategoryType, Double>,
        VectorInputEvaluator<Vectorizable, CategoryType>
{

    /** A map of each category to its prototype categorizer. */
    protected Map<CategoryType, LinearBinaryCategorizer> prototypes;

    /**
     * Creates a new, empty {@code LinearMultiCategorizer}.
     */
    public LinearMultiCategorizer()
    {
        this(new LinkedHashMap<CategoryType, LinearBinaryCategorizer>());
    }

    /**
     * Creates a new {@code LinearMultiCategorizer} with the given prototypes.
     *
     * @param   prototypes
     *      The mapping of categories to prototypes.
     */
    public LinearMultiCategorizer(
        final Map<CategoryType, LinearBinaryCategorizer> prototypes)
    {
        super();

        this.setPrototypes(prototypes);
    }

    @Override
    public LinearMultiCategorizer<CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        final LinearMultiCategorizer<CategoryType> clone = 
            (LinearMultiCategorizer<CategoryType>) super.clone();

        // Clone the prototypes.
        if (this.prototypes != null)
        {
            clone.prototypes =
                new LinkedHashMap<CategoryType, LinearBinaryCategorizer>(
                    this.prototypes.size());

            for (CategoryType category : this.prototypes.keySet())
            {
                clone.prototypes.put(category,
                    this.prototypes.get(category).clone());
            }
        }

        return clone;
    }

    @Override
    public CategoryType evaluate(
        final Vectorizable input)
    {
        return this.evaluateWithDiscriminant(input).getValue();
    }

    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        final Vectorizable input)
    {
        // Convert the input to a vector.
        final Vector inputVector = input.convertToVector();

        // Find the category that has the highest match.
        double bestScore = 0.0;
        CategoryType bestCategory = null;
        for (CategoryType category : this.getCategories())
        {
            final double score = this.evaluateAsDouble(inputVector, category);

            if (bestCategory == null || score > bestScore)
            {
                bestScore = score;
                bestCategory = category;
            }
        }

        // Return the discriminant for the category.
        return new DefaultWeightedValueDiscriminant<CategoryType>(
            bestCategory, bestScore);
    }

    /**
     * Evaluates how much the given input matches the prototype for the given
     * category.
     *
     * @param   input
     *      The input.
     * @param   category
     *      The category to match the input against.
     * @return
     *      A real value indicating how much the input matches the category.
     *      A larger value indicates a better match.
     */
    public double evaluateAsDouble(
        final Vectorizable input,
        final CategoryType category)
    {
        return this.evaluateAsDouble(input.convertToVector(), category);
    }
    
    /**
     * Evaluates how much the given input matches the prototype for the given
     * category.
     *
     * @param   input
     *      The input.
     * @param   category
     *      The category to match the input against.
     * @return
     *      A real value indicating how much the input matches the category.
     *      A larger value indicates a better match.
     */
    public double evaluateAsDouble(
        final Vector input,
        final CategoryType category)
    {
        final LinearBinaryCategorizer prototype = this.prototypes.get(category);

        if (prototype == null)
        {
            // Bad prototype.
            return 0.0;
        }
        else
        {
            // Evaluate the input with the prototype.
            return prototype.evaluateAsDouble(input);
        }
    }

    @Override
    public Set<? extends CategoryType> getCategories()
    {
        return this.prototypes.keySet();
    }

    @Override
    public int getInputDimensionality()
    {
        final LinearBinaryCategorizer firstPrototype =
            CollectionUtil.getFirst(this.prototypes.values());
        if (firstPrototype == null)
        {
            return -1;
        }
        else
        {
            return firstPrototype.getInputDimensionality();
        }
    }

    /**
     * Gets the mapping of categories to prototypes.
     *
     * @return
     *      The mapping of categories to prototypes.
     */
    public Map<CategoryType, LinearBinaryCategorizer> getPrototypes()
    {
        return prototypes;
    }

    /**
     * Sets the mapping of categories to prototypes.
     *
     * @param   prototypes
     *      The mapping of categories to prototypes.
     */
    public void setPrototypes(
        final Map<CategoryType, LinearBinaryCategorizer> prototypes)
    {
        this.prototypes = prototypes;
    }

}
