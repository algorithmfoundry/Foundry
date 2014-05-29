/*
 * File:                OnlinePassiveAggressivePerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Passive-Aggressive algorithm for learning a linear
 * binary categorizer. The Passive-Aggressive algorithm is similar to the
 * Perceptron algorithm, except that it attempt to enforce a unit margin
 * and also aggressively updates errors so that if given the same example as
 * the next input, it will get it correct. This is an implementation of the PA
 * algorithm that is designed for linearly separable cases (hard margin). Two
 * internal classes, {@code LinearSoftMargin} and {@code QuadraticSoftMargin}
 * implement the soft-margin variants (PA-I and PA-II).
 * <br/><br/>
 * Solves the optimization problem: <br/>
 * <BR>     w_{t+1} = argmin_{w \in R^n} 0.5 ||w - w_t||^2
 * <BR>     such that
 * <BR>     l(w; (x_t, y_t)) = 0
 * <BR> where l(w; (x_t, y_t)) is the hinge loss (0 if y(w * x) >= 1) and
 * 1 - y(w * x) otherwise.
 *
 * <br/><br/>
 * The actual update step of the PA algorithm is simple:
 * <BR>     w_{t+1} = w_t + \tau_t * y_t * x_t
 * <BR>where
 * <BR>     \tau_t = l_t / ||x_t||^2
 *
 * <br/><br/>
 *
 * Note: This algorithm does not modify the bias of the linear categorizer.
 * Thus, it is recommended that a bias term be added to the inputs prior to
 * use.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
@PublicationReference(
    title="Online Passive-Aggressive Algorithms",
    author={"Koby Crammer", "Ofer Dekel", "Joseph Keshet", "Shai Shalev-Shwartz", "Yoram Singer"},
    year=2006,
    type=PublicationType.Journal,
    publication="Journal of Machine Learning Research",
    pages={551, 585},
    url="http://jmlr.org/papers/volume7/crammer06a/crammer06a.pdf")
public class OnlinePassiveAggressivePerceptron
    extends AbstractLinearCombinationOnlineLearner
{
    
    /** By default the Passive-Aggressive Perceptron does not use a bias. */
    public static final boolean DEFAULT_UPDATE_BIAS = false;

    /**
     * Creates a new {@code OnlinePassiveAggressivePerceptron}.
     */
    public OnlinePassiveAggressivePerceptron()
    {
        this(VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code OnlinePassiveAggressivePerceptron} with the given
     * vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public OnlinePassiveAggressivePerceptron(
        final VectorFactory<?> vectorFactory)
    {
        super(DEFAULT_UPDATE_BIAS, vectorFactory);
    }

    /**
     * Compute the update value (tau) for the algorithm. Other variants of
     * the algorithm should override this method.
     *
     * @param   actual
     *      The actual label represented as a double (-1 or +1).
     * @param   predicted
     *      The value predicted by the current categorizer (w * x + b).
     * @param   loss
     *      The loss function (1 - predicted).
     * @param   inputNorm2Squared
     *      The squared 2-norm of the input (||x||^2).
     * @return
     *      The update value to scale the input by. Should be > 0.0. May be 0.0
     *      in degenerate cases such as a zero-vector input.
     */
    protected double computeUpdate(
        final double actual,
        final double predicted,
        final double loss,
        final double inputNorm2Squared)
    {
        if (inputNorm2Squared == 0.0)
        {
            // Avoid divide-by-zero.
            return 0.0;
        }

        return loss / inputNorm2Squared;
    }

    @Override
    public double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        final double loss = 1.0 - actual * predicted;

        if (loss <= 0.0)
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
                actual, predicted, loss, inputNorm2Squared);
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
        final double loss = 1.0 - actual * predicted;

        if (loss <= 0.0)
        {
            // Passive when there is no loss.
            return 0.0;
        }
        else
        {
            // Update methods use ||x||^2 = k(x, x).
            final double norm = target.getKernel().evaluate(input, input);

            // Compute the update value (tau).
            return this.computeUpdate(actual, predicted, loss, norm);
        }
    }

    /**
     * An abstract class for soft-margin versions of the Passive-Aggressive
     * algorithm. Stores the aggressiveness parameter (C).
     */
    public static abstract class AbstractSoftMargin
        extends OnlinePassiveAggressivePerceptron
    {

        /** The default aggressiveness is {@value}. */
        public static final double DEFAULT_AGGRESSIVENESS = 0.001;

        /** The aggressiveness parameter (C), which is the trade-off between
         *  aggressive updating to meet an incorrect example and keeping
         *  history around. */
        protected double aggressiveness;

        /**
         * Creates a new {@code AbstractSoftMargin} with default parameters.
         */
        public AbstractSoftMargin()
        {
            this(DEFAULT_AGGRESSIVENESS);
        }

        /**
         * Creates a new {@code AbstractSoftMargin} with the given
         * aggressiveness.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         */
        public AbstractSoftMargin(
            final double aggressiveness)
        {
            this(aggressiveness, VectorFactory.getDefault());
        }

        /**
         * Creates a new {@code AbstractSoftMargin} with the given parameters.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         * @param   vectorFactory
         *      The factory to use to create new weight vectors.
         */
        public AbstractSoftMargin(
            final double aggressiveness,
            final VectorFactory<?> vectorFactory)
        {
            super(vectorFactory);

            this.setAggressiveness(aggressiveness);
        }

        /**
         * Gets the aggressiveness parameter (C), which is the trade-off between
         * aggressive updating to meet an incorrect example and keeping
         * history around.
         *
         * @return
         *      The aggressiveness. Must be positive.
         */
        public double getAggressiveness()
        {
            return this.aggressiveness;
        }

        /**
         * Sets the aggressiveness parameter (C), which is the trade-off between
         * aggressive updating to meet an incorrect example and keeping
         * history around.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         */
        public void setAggressiveness(
            final double aggressiveness)
        {
            ArgumentChecker.assertIsPositive("aggressiveness", aggressiveness);
            this.aggressiveness = aggressiveness;
        }

    }

    /**
     * An implementation of the linear soft-margin variant of the Passive-
     * Aggressive algorithm (PA-I). It trades-off quickly responding to errors
     * with keeping around history in the case of non-linearly separable data.
     * <br/><br/>
     *
     * Solves the optimization problem: <br/>
     *      w_{t+1} = argmin_{w \in R^n} 0.5 ||w - w_t||^2 + C * \epsilon
     *      such that
     *      l(w; (x_t, y_t)) <= \epslion
     *      \epsilon >= 0
     *
     * where l(w; (x_t, y_t)) is the hinge loss (0 if y(w * x) >= 1) and
     * 1 - y(w * x) otherwise.
     *
     * <br/><br/>
     * The actual update step of the PA algorithm is simple:
     *      w_{t+1} = w_t + \tau_t * y_t * x_t
     * where
     *      \tau_t = min(C, l_t / ||x_t||^2)
     *
     * <br/><br/>
     *
     * Note: This algorithm does not modify the bias of the linear categorizer.
     * Thus, it is recommended that a bias term be added to the inputs prior to
     * use.
     */
    public static class LinearSoftMargin
        extends AbstractSoftMargin
    {

        /**
         * Creates a new {@code LinearSoftMargin} with default parameters.
         */
        public LinearSoftMargin()
        {
            this(DEFAULT_AGGRESSIVENESS);
        }

        /**
         * Creates a new {@code LinearSoftMargin} with the given
         * aggressiveness.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         */
        public LinearSoftMargin(
            final double aggressiveness)
        {
            this(aggressiveness, VectorFactory.getDefault());
        }

        /**
         * Creates a new {@code LinearSoftMargin} with the given parameters.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         * @param   vectorFactory
         *      The factory to use to create new weight vectors.
         */
        public LinearSoftMargin(
            final double aggressiveness,
            final VectorFactory<?> vectorFactory)
        {
            super(aggressiveness, vectorFactory);
        }

        @Override
        protected double computeUpdate(
            final double actual,
            final double predicted,
            final double loss,
            final double inputNorm2Squared)
        {
            if (inputNorm2Squared == 0.0)
            {
                // Avoid divide-by-zero.
                return 0.0;
            }

            // Compute the update: min(C, l / ||x||^2)
            return Math.min(this.aggressiveness, loss / inputNorm2Squared);
        }

    }

    /**
     * An implementation of the quadratic soft-margin variant of the Passive-
     * Aggressive algorithm (PA-II). It trades-off quickly responding to errors
     * with keeping around history in the case of non-linearly separable data.
     * <br/><br/>
     *
     * Solves the optimization problem: <br/>
     *      w_{t+1} = argmin_{w \in R^n} 0.5 ||w - w_t||^2 + C * \epsilon^2
     *      such that
     *      l(w; (x_t, y_t)) <= \epslion
     *
     * where l(w; (x_t, y_t)) is the hinge loss (0 if y(w * x) >= 1) and
     * 1 - y(w * x) otherwise.
     *
     * <br/><br/>
     * The actual update step of the PA algorithm is simple:
     *      w_{t+1} = w_t + \tau_t * y_t * x_t
     * where
     *      \tau_t = l_t / (||x_t||^2 + 0.5 * C)
     *
     * <br/><br/>
     *
     * Note: This algorithm does not modify the bias of the linear categorizer.
     * Thus, it is recommended that a bias term be added to the inputs prior to
     * use.
     */
    public static class QuadraticSoftMargin
        extends AbstractSoftMargin
    {
        /**
         * Creates a new {@code QuadraticSoftMargin} with default parameters.
         */
        public QuadraticSoftMargin()
        {
            this(DEFAULT_AGGRESSIVENESS);
        }

        /**
         * Creates a new {@code QuadraticSoftMargin} with the given
         * aggressiveness.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         */
        public QuadraticSoftMargin(
            final double aggressiveness)
        {
            this(aggressiveness, VectorFactory.getDefault());
        }

        /**
         * Creates a new {@code QuadraticSoftMargin} with the given parameters.
         *
         * @param   aggressiveness
         *      The aggressiveness. Must be positive.
         * @param   vectorFactory
         *      The factory to use to create new weight vectors.
         */
        public QuadraticSoftMargin(
            final double aggressiveness,
            final VectorFactory<?> vectorFactory)
        {
            super(aggressiveness, vectorFactory);
        }

        @Override
        protected double computeUpdate(
            final double actual,
            final double predicted,
            final double loss,
            final double inputNorm2Squared)
        {
            // tau = l / (||x||^2 + 1 / (2C))
            return loss
                / (inputNorm2Squared + 1.0 / (2.0 * this.aggressiveness));
        }

    }

}
