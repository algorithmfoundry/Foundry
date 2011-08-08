/*
 * File:                RelaxedOnlineMaximumMarginAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 27, 2011, Sandia Corporation.
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

/**
 * An implementation of the Relaxed Online Maximum Margin Algorithm
 * (ROMMA). It is an online learner for a linear binary categorizer that
 * also has a kernel form.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
  title="Ultraconservative online algorithms for multiclass problems",
  author={"Koby Crammer", "Yoram Singer"},
  year=2003,
  type=PublicationType.Journal,
  publication="The Journal of Machine Learning Research",
  pages={951, 991},
  url="http://portal.acm.org/citation.cfm?id=944936")
public class RelaxedOnlineMaximumMarginAlgorithm
    extends AbstractKernelizableBinaryCategorizerOnlineLearner
{

    /**
     * Creates a new {@code RelaxedOnlineMaximumMarginAlgorithm}.
     */
    public RelaxedOnlineMaximumMarginAlgorithm()
    {
        this(VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code RelaxedOnlineMaximumMarginAlgorithm} with
     * the given vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public RelaxedOnlineMaximumMarginAlgorithm(
        final VectorFactory<?> vectorFactory)
    {
        super(vectorFactory);
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean label)
    {
       // Get the information about the example.
        final double actual = label ? +1.0 : -1.0;

        Vector weights = target.getWeights();
        if (weights == null)
        {
            // This is the first example, so initialize the weight vector.
            final double inputNorm = input.norm2Squared();
            weights = this.getVectorFactory().copyVector(input);
            weights.scaleEquals(actual / inputNorm);
            target.setWeights(weights);
        }
        else
        {
            // Predict the output as a double (negative values are false, positive
            // are true).
            final double prediction = target.evaluateAsDouble(input);

            if (actual * prediction <= 0.0)
            {
                final double inputNorm = input.norm2Squared();
                final double weightsNorm = weights.norm2Squared();

                final double denominator = inputNorm * weightsNorm - prediction * prediction;
                // Compute the update value.
                final double c = (inputNorm * weightsNorm - actual * prediction) / denominator;
                final double d = (weightsNorm * (actual - prediction)) / denominator;

                weights.scaleEquals(c);
                weights.plusEquals(input.scale(d));
            }
            // else - Passive when there is no loss.
        }

    }

    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
       // Get the information about the example.
        final double actual = label ? +1.0 : -1.0;

        if (target.getExamples().isEmpty())
        {
            // Initialize the target on the first update.
            final double inputNorm = target.getKernel().evaluate(input, input);

            if (inputNorm > 0.0)
            {
                target.add(input, actual / inputNorm);
            }
        }
        else
        {
            // Predict the output as a double (negative values are false, positive
            // are true).
            final double prediction = target.evaluateAsDouble(input);

            if (actual * prediction <= 0.0)
            {
                final double inputNorm = target.getKernel().evaluate(input, input);
                final double weightsNorm = KernelUtil.norm2Squared(target);

                final double denominator = inputNorm * weightsNorm - prediction * prediction;
                // Compute the update value.
                final double c = (inputNorm * weightsNorm - actual * prediction) / denominator;
                final double d = (weightsNorm * (actual - prediction)) / denominator;

                KernelUtil.scaleEquals(target, c);
                target.add(input, d);
            }
            // else - Passive when there is no loss.
        }
    }


}
