/*
 * File:                Winnow.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright December 02, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;

/**
 * An implementation of the Winnow incremental learning algorithm. It uses a
 * multiplicative weight update rule to learn a binary categorization from
 * binary features and produces a linear categorizer.
 *
 * It supports being used as either the Winnow1 or Winnow2 variants, which vary
 * in how the demotion step is performed. Winnow1 demotes weights to zero while
 * Winnow2 divides by the weight update value (alpha).
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReference(
    author="Nick Littlestone",
    title="Learning Quickly When Irrelevant Attributes Abound: A New Linear-threshold Algorithm",
    year=1988,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={285, 318})
public class Winnow
    extends AbstractOnlineLinearBinaryCategorizerLearner
{

    /** The default value of the weight update is {@value}. */
    public static final double DEFAULT_WEIGHT_UPDATE = 2.0;

    /** The default value of demoteToZero is {@value}. */
    public static final boolean DEFAULT_DEMOTE_TO_ZERO = false;

    /** The amount of the weight update (alpha). Must be greater than 1. */
    protected double weightUpdate;

    /** An option to demote to zero. */
    protected boolean demoteToZero;

    /** The cached value of the inverse of weight update (commonly alpha or
     *  1 + epsilon). */
    protected double weightUpdateInverse;

    /**
     * Creates a new {@code Winnow} with default parameters.
     */
    public Winnow()
    {
        this(DEFAULT_WEIGHT_UPDATE, DEFAULT_DEMOTE_TO_ZERO);
    }

    /**
     * Creates a new {@code Winnow} with the given weight update and the
     * default demote to zero (false).
     *
     * @param   weightUpdate
     *      The multiplicative factor to update the weights. Must be greater
     *      than one.
     */
    public Winnow(
        final double weightUpdate)
    {
        this(weightUpdate, DEFAULT_DEMOTE_TO_ZERO);
    }

    /**
     * Creates a new {@code Winnow} with the given parameters.
     *
     * @param   weightUpdate
     *      The multiplicative factor to update the weights. Must be greater
     *      than one.
     * @param   demoteToZero
     *      True to demote to zero (Winnow1) and false to demote by dividing by
     *      the weight (Winnow2).
     */
    public Winnow(
        final double weightUpdate,
        final boolean demoteToZero)
    {
        super();

        this.setWeightUpdate(weightUpdate);
        this.setDemoteToZero(demoteToZero);
    }

    @Override
    public Winnow clone()
    {
        return (Winnow) super.clone();
    }

    @Override
    public LinearBinaryCategorizer createInitialLearnedObject()
    {
        return new LinearBinaryCategorizer();
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actual)
    {
        Vector weights = target.getWeights();
        if (weights == null)
        {
            // This is the first example, so initialize the weight vector to
            // be all ones.
            final int dimensionality = input.getDimensionality();
            weights = this.getVectorFactory().copyVector(input);
            for (int i = 0; i < dimensionality; i++)
            {
                weights.setElement(i, 1.0);
            }

            target.setWeights(weights);
            target.setBias((double) -input.getDimensionality() / 2.0);
        }
        // else - Use the existing weights.

        // Predict the output then see if it matches the actual output.
        final boolean prediction = target.evaluate(input);
        final boolean error = actual != prediction;

        // Make an update if there was an error.
        if (error)
        {
            double update;
            if (actual)
            {
                // Promotion step.
                update = this.weightUpdate;
            }
            else
            {
                // Demotion step.
                if (this.demoteToZero)
                {
                    update = 0.0;
                }
                else
                {
                    update = this.weightUpdateInverse;
                }
            }

            // Use the weight update on all active elements.
            for (VectorEntry entry : input)
            {
                if (entry.getValue() > 0.0)
                {
                    final int i = entry.getIndex();
                    weights.setElement(i, update * weights.getElement(i));
                }
            }
        }
        // else - There was no error made, so nothing to update.
    }

    /**
     * Gets the multiplicative weight update term.
     *
     * @return
     *      The multiplicative factor to update the weights. Must be greater
     *      than one.
     */
    public double getWeightUpdate()
    {
        return this.weightUpdate;
    }

    /**
     * Sets the multiplicative weight update term.
     *
     * @param   weightUpdate
     *      The multiplicative factor to update the weights. Must be greater
     *      than one.
     */
    public void setWeightUpdate(
        final double weightUpdate)
    {
        if (weightUpdate <= 1.0)
        {
            throw new IllegalArgumentException(
                "weightUpdate must be greater than 1.0.");
        }
        
        this.weightUpdate = weightUpdate;
        this.weightUpdateInverse = 1.0 / weightUpdate;
    }

    /**
     * Gets whether or not the algorithm will demote features involved in an
     * incorrect categorization to zero (Winnow1). If false, it is demoted using
     * the inverse of the promotion weight (Winnow2).
     *
     * @return
     *      True to demote to zero (Winnow1) and false to demote by dividing by
     *      the weight.
     */
    public boolean isDemoteToZero()
    {
        return this.demoteToZero;
    }

    /**
     * Sets whether or not the algorithm will demote features involved in an
     * incorrect categorization to zero (Winnow1). If false, it is demoted using
     * the inverse of the promotion weight (Winnow2).
     *
     * @param   demoteToZero
     *      True to demote to zero (Winnow1) and false to demote by dividing by
     *      the weight (Winnow2).
     */
    public void setDemoteToZero(
        final boolean demoteToZero)
    {
        this.demoteToZero = demoteToZero;
    }

}
