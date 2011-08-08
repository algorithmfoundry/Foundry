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
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
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
 *      w_{t+1} = argmin_{w \in R^n} 0.5 ||w - w_t||^2
 *      such that
 *      l(w; (x_t, y_t)) = 0
 * where l(w; (x_t, y_t)) is the hinge loss (0 if y(w * x) >= 1) and
 * 1 - y(w * x) otherwise.
 *
 * <br/><br/>
 * The actual update step of the PA algorithm is simple:
 *      w_{t+1} = w_t + \tau_t * y_t * x_t
 * where
 *      \tau_t = l_t / ||x_t||^2
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
    url="http://portal.acm.org/citation.cfm?id=1248566")
public class OnlinePassiveAggressivePerceptron
    extends AbstractOnlineLinearBinaryCategorizerLearner
{

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
        super(vectorFactory);
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final InputOutputPair<? extends Vectorizable, Boolean> example)
    {
       // Get the information about the example.
        final Vector input = example.getInput().convertToVector();
        final boolean label = example.getOutput();

        Vector weights = target.getWeights();
        if (weights == null)
        {
            // This is the first example, so initialize the weight vector.
            weights = this.getVectorFactory().createVector(
                input.getDimensionality());
            target.setWeights(weights);
        }
        // else - Use the existing weights.

        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;
        final double loss = 1.0 - actual * prediction;

        if (loss > 0.0)
        {
            // Update methods use ||x||^2.
            final double inputNorm2Squared = input.norm2Squared();

            // Compute the update value (tau).
            final double update = this.computeUpdate(
                input, actual, prediction, loss, inputNorm2Squared);

            // Do w += y * tau * x
            weights.plusEquals(input.scale(actual * update));
        }
        // else - Passive when there is no loss.
        
    }

    /**
     * Compute the update value (tau) for the algorithm. Other variants of
     * the algorithm should override this method.
     *
     * @param   input
     *      The input vector.
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
        final Vector input,
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

        // Compute the update value: l / ||x||^2
        return loss / inputNorm2Squared;
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
            final Vector input,
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
            final Vector input,
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
