/*
 * File:                OnlinePerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright October 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * An online version of the classic Perceptron algorithm.
 * 
 * @author  Justin Basilico
 * @since   3.1
 * @see     Perceptron
 */
@PublicationReference(
    author="Wikipedia",
    title="Perceptron Learning algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Perceptron#Learning_algorithm"
)
public class OnlinePerceptron
    extends AbstractLinearCombinationOnlineLearner
{
    /** By default the bias is updated. */
    public static final boolean DEFAULT_UPDATE_BIAS = true;

    /**
     * Creates a new {@code OnlinePerceptron}.
     */
    public OnlinePerceptron()
    {
        this(VectorFactory.getDenseDefault());
    }

    /**
     * Creates a new {@code OnlinePerceptron} with the given vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use to create the weight vectors.
     */
    public OnlinePerceptron(
        final VectorFactory<?> vectorFactory)
    {
        super(DEFAULT_UPDATE_BIAS, vectorFactory);
    }

    @Override
    public double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        boolean label,
        double predicted)
    {
        return computeUpdate(label, predicted);
    }

    @Override
    public <InputType> double computeUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted)
    {
        return computeUpdate(actualCategory, predicted);
    }

    /**
     * Computes the update weight for the given actual category and predicted
     * value according to the Perceptron update rule.
     *
     * @param   actualCategory
     *      The actual category.
     * @param   predicted
     *      The predicted category.
     * @return
     *      The update value, which is either 0.0 or 1.0.
     */
    public static double computeUpdate(
        final boolean actualCategory,
        final double predicted)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        if (predicted * actual <= 0.0)
        {
            // An error of 1.0.
            return 1.0;
        }
        else
        {
            // Not an error.
            return 0.0;
        }
    }
    
}
