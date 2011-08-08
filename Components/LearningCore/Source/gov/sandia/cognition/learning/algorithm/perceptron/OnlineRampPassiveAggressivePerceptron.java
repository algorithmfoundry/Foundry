/*
 * File:                OnlineRampPassiveAggressivePerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 24, 2011, Sandia Corporation.
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
 * An implementation of the Ramp Loss Passive Aggressive Perceptron (PA^R) from
 * the referenced paper. It is an extension of the quadratic soft margin version
 * of PA (PA-II) that replaces the hinge loss with a ramp loss. This ends up
 * only changing the determination of when an update is made but does not change
 * how the update is made.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    title="Online Passive-Aggressive Algorithms on a Budget",
    author={"Zhuang Wang", "Slobodan Vucetic"},
    year=2010,
    type=PublicationType.Conference,
    publication="Proceedings of the 13th International Conference on Artificial Intelligence and Statistics (AISTATS)",
    url="http://jmlr.csail.mit.edu/proceedings/papers/v9/wang10b/wang10b.pdf",
    notes="This presents the PA-I-R algorithm. This implementation is non-budgeted form. We this also implements PA-R and PA-II-R")
public class OnlineRampPassiveAggressivePerceptron
    extends OnlinePassiveAggressivePerceptron.QuadraticSoftMargin
{

    /**
     * Creates a new {@code OnlineRampPassiveAggressivePerceptron} with default parameters.
     */
    public OnlineRampPassiveAggressivePerceptron()
    {
        this(DEFAULT_AGGRESSIVENESS);
    }

    /**
     * Creates a new {@code OnlineRampPassiveAggressivePerceptron} with the given
     * aggressiveness.
     *
     * @param   aggressiveness
     *      The aggressiveness. Must be positive.
     */
    public OnlineRampPassiveAggressivePerceptron(
        final double aggressiveness)
    {
        this(aggressiveness, VectorFactory.getDefault());
    }


    /**
     * Creates a new {@code OnlineRampPassiveAggressivePerceptron} with the given parameters.
     *
     * @param   aggressiveness
     *      The aggressiveness. Must be positive.
     * @param   vectorFactory
     *      The factory to use to create new weight vectors.
     */
    public OnlineRampPassiveAggressivePerceptron(
        final double aggressiveness,
        final VectorFactory<?> vectorFactory)
    {
        super(aggressiveness, vectorFactory);
    }

    @Override
    public double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        final double margin = actual * predicted;
        final double hingeLoss = 1.0 - margin;

        if (Math.abs(margin) > 1.0)
        {
            // Passive when there is no loss.
            return 0.0;
        }
        else
        {
            // Update methods use ||x||^2.
            final double inputNorm2Squared = input.norm2Squared();

            // Compute the update value (tau).
            return this.computeUpdate(
                actual, predicted, hingeLoss, inputNorm2Squared);
        }
    }

    @Override
    public <InputType> double computeUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        final double margin = actual * predicted;
        final double hingeLoss = 1.0 - margin;

        if (Math.abs(margin) > 1.0)
        {
            // Passive when there is no loss.
            return 0.0;
        }
        else
        {
            // Update methods use ||x||^2 = k(x, x).
            final double norm = target.getKernel().evaluate(input, input);

            // Compute the update value (tau).
            return this.computeUpdate(actual, predicted, hingeLoss, norm);
        }
    }

}
