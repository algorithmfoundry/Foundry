/*
 * File:                ScalarFunctionToBinaryCategorizer.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Adapts a scalar function to be a categorizer using a threshold.
 * 
 * @param <InputType> Input to the discriminant function.
 * @author  Justin Basilico
 * @since   3.0
 */
public class ScalarFunctionToBinaryCategorizerAdapter<InputType>
    extends AbstractThresholdBinaryCategorizer<InputType>
{

    /** The scalar evaluator. */
    protected Evaluator<? super InputType, Double> evaluator;

    /**
     * Creates a new {@code ScalarFunctionToBinaryCategorizerAdapter}.
     */
    public ScalarFunctionToBinaryCategorizerAdapter()
    {
        this(null);
    }

    /**
     * Creates a new {@code ScalarFunctionToBinaryCategorizerAdapter} with the
     * given evaluator and a default threshold of 0.0.
     *
     * @param   evaluator
     *      The scalar function to adapt.
     */
    public ScalarFunctionToBinaryCategorizerAdapter(
        final Evaluator<? super InputType, Double> evaluator)
    {
        this(evaluator, DEFAULT_THRESHOLD);
    }

    /**
     * Creates a new {@code ScalarFunctionToBinaryCategorizerAdapter} with the
     * given evaluator and threshold.
     *
     * @param   evaluator
     *      The scalar function to adapt.
     * @param   threshold
     *      The threshold to use.
     */
    public ScalarFunctionToBinaryCategorizerAdapter(
        final Evaluator<? super InputType, Double> evaluator,
        final double threshold)
    {
        super(threshold);

        this.setEvaluator(evaluator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ScalarFunctionToBinaryCategorizerAdapter<InputType> clone()
    {
        ScalarFunctionToBinaryCategorizerAdapter<InputType> clone =
            (ScalarFunctionToBinaryCategorizerAdapter<InputType>) super.clone();

        clone.setEvaluator( ObjectUtil.cloneSmart(this.getEvaluator()) );
        return clone;
    }

    @Override
    protected double evaluateWithoutThreshold(
        InputType input)
    {
        return this.getEvaluator().evaluate(input);
    }

    /**
     * Gets the scalar function that the adapter wraps.
     *
     * @return
     *      The scalar function.
     */
    public Evaluator<? super InputType, Double> getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * Sets the scalar function that the adapter wraps.
     *
     * @param   evaluator
     *      The scalar function.
     */
    public void setEvaluator(
        final Evaluator<? super InputType, Double> evaluator)
    {
        this.evaluator = evaluator;
    }
    
}
