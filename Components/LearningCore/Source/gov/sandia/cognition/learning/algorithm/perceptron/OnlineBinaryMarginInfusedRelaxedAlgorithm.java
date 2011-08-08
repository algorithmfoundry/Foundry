/*
 * File:                OnlineBinaryMarginInfusedRelaxedAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.KernelUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the binary MIRA algorithm.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Koby Crammer", "Yoram Singer"},
    title="Ultraconservative Online Algorithms for Multiclass Problems",
    year=2003,
    type=PublicationType.Journal,
    publication="Journal of Machine Learning Research",
    pages={951, 991},
    url="http://portal.acm.org/citation.cfm?id=944936")
public class OnlineBinaryMarginInfusedRelaxedAlgorithm
    extends AbstractLinearCombinationOnlineLearner
{

    /** MIRA does not use a bias by default. */
    public static final boolean DEFAULT_UPDATE_BIAS = false;

    /** The default minimum margin is {@value}. */
    public static final double DEFAULT_MIN_MARGIN = 0.0;

    /** The minimum margin to enforce. Must be non-negative. */
    protected double minMargin;

    /**
     * Creates a new {@code OnlineBinaryMarginInfusedRelaxedAlgorithm} with
     * default parameters.
     */
    public OnlineBinaryMarginInfusedRelaxedAlgorithm()
    {
        this(DEFAULT_MIN_MARGIN);
    }

    /**
     * Creates a new {@code OnlineBinaryMarginInfusedRelaxedAlgorithm} with
     * the given minimum margin.
     *
     * @param   minMargin
     *      The minimum margin to enforce. Must be non-negative.
     */
    public OnlineBinaryMarginInfusedRelaxedAlgorithm(
        final double minMargin)
    {
        this(minMargin, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code OnlineBinaryMarginInfusedRelaxedAlgorithm} with
     * the new minimum margin.
     *
     * @param   minMargin
     *      The minimum margin to enforce. Must be non-negative.
     * @param   vectorFactory
     *      The factory to use to create vectors.
     */
    public OnlineBinaryMarginInfusedRelaxedAlgorithm(
        final double minMargin,
        final VectorFactory<?> vectorFactory)
    {
        super(DEFAULT_UPDATE_BIAS, vectorFactory);

        this.setMinMargin(minMargin);
    }
    
    /**
     * Gets the minimum margin to enforce. Any value less than or equal to
     * this is considered an error for the algorithm.
     *
     * @return
     *      The minimum margin. Cannot be negative.
     */
    public double getMinMargin()
    {
        return this.minMargin;
    }

    /**
     * Gets the minimum margin to enforce. Any value less than or equal to
     * this is considered an error for the algorithm.
     *
     * @param   minMargin
     *      The minimum margin. Cannot be negative.
     */
    public void setMinMargin(
        final double minMargin)
    {
        ArgumentChecker.assertIsNonNegative("minMargin", minMargin);
        this.minMargin = minMargin;
    }

    @Override
    protected void initialize(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory)
    {
        final double norm = input.norm2();
        if (norm != 0.0)
        {
            final Vector weights = this.getVectorFactory().copyVector(input);
            final double actual = actualCategory ? +1.0 : -1.0;
            weights.scaleEquals(actual / input.norm2());
            target.setWeights(weights);
        }
    }

    @Override
    protected double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted)
    {
        // Get the actual category.
        final double actual = actualCategory ? +1.0 : -1.0;

        // Compute the margin.
        final double margin = actual * predicted;

            final double norm = input.norm2Squared();

        return computeUpdate(margin, norm);
    }

    private double computeUpdate(
        final double margin,
        final double norm)
    {
        if (norm == 0.0)
        {
            // Avoid divide-by-zero.
            return 0.0;
        }

        
        if (margin <= this.minMargin)
        {
            // Compute the update value.
            if (norm != 0.0)
            {
                return Math.min(-margin / norm, 1.0);
            }
        }
        return 0.0;
    }

    @Override
    protected <InputType> void initialize(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory)
    {
        final double norm = KernelUtil.norm2(input, target.getKernel());

        if (norm != 0.0)
        {
            final double actual = actualCategory ? +1.0 : -1.0;
            target.add(input, actual / norm);
        }
    }

    @Override
    protected <InputType> double computeUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted)
    {
        // Get the actual category.
        final double actual = actualCategory ? +1.0 : -1.0;

        // Compute the margin.
        final double margin = actual * predicted;

        if (margin <= this.minMargin)
        {
            // Compute the update value.
            final double norm = KernelUtil.norm2Squared(input,
                target.getKernel());

            if (norm != 0.0)
            {
                return Math.min(-margin / norm, 1.0);
            }
        }

        return 0.0;
    }

}
