/*
 * File:                OnlineShiftingPerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2011, Sandia Corporation.
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
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Shifting Perceptron algorithm. It is like the
 * Perceptron except that it adds a decay to the existing weights, which is
 * parameterized by lambda.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Giovanni Cavallanti", "Nicolo Cesa-Bianchi", "Claudio Gentile"},
    title="Tracking the best hyperplane with a simple budget Perceptron",
    year=2007,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={143, 167},
    url="http://www.springerlink.com/index/H40NV525LX161227.pdf")
public class OnlineShiftingPerceptron
    extends AbstractLinearCombinationOnlineLearner
{
    
    /** Algorithm does not update the bias by default. */
    public static final boolean DEFAULT_UPDATE_BIAS = false;

    /** The default value of lambda is 0.001. */
    public static final double DEFAULT_LAMBDA = 0.001;

    /** The lambda parameter for controlling how much shifting occurs. */
    protected double lambda;

    /**
     * Creates a new {@code OnlineShiftingPerceptron} with default parameters.
     */
    public OnlineShiftingPerceptron()
    {
        this(DEFAULT_LAMBDA);
    }

    /**
     * Creates a new {@code OnlineShiftingPerceptron} with the given parameters.
     *
     * @param   lambda
     *      The lambda parameter to control the amount of shift. Must be
     *      positive.
     */
    public OnlineShiftingPerceptron(
        final double lambda)
    {
        this(lambda, VectorFactory.getDefault());
    }
    /**
     * Creates a new {@code OnlineShiftingPerceptron} with the given parameters.
     *
     * @param   lambda
     *      The lambda parameter to control the amount of shift. Must be
     *      positive.
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public OnlineShiftingPerceptron(
        final double lambda,
        final VectorFactory<?> vectorFactory)
    {
        super(DEFAULT_UPDATE_BIAS, vectorFactory);

        this.setLambda(lambda);
    }
    
    @Override
    public LinearBinaryCategorizer createInitialLearnedObject()
    {
        // This needs a special result class to count the number of errors.
        return new LinearResult();
    }

    @Override
    public double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean label,
        final double predicted)
    {
        // Basic update step is the perceptron.
        return OnlinePerceptron.computeUpdate(label, predicted);
    }

    @Override
    public <InputType> double computeUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label,
        final double predicted)
    {
        // Basic update step is the perceptron.
        return OnlinePerceptron.computeUpdate(label, predicted);
    }

    @Override
    protected double computeDecay(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted,
        final double update)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        final double margin = actual * predicted;
        
        if (margin > 0.0)
        {
            return 1.0;
        }
        else
        {
            // Compute the decay by using lambda plus the error count.
            final LinearResult result = (LinearResult) target;
            final long errorCount = result.errorCount;
            final double lambdaK = this.lambda / (this.lambda + errorCount);
            result.errorCount += 1;
            return (1.0 - lambdaK);
        }
    }

    @Override
    protected <InputType> double computeDecay(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted,
        final double update)
    {
        final double actual = actualCategory ? +1.0 : -1.0;
        final double margin = actual * predicted;

        if (margin > 0.0)
        {
            return 1.0;
        }
        else
        {
            // Compute the decay by using lambda plus the error count.
            final long errorCount = target.getExamples().size();
            final double lambdaK = this.lambda / (this.lambda + errorCount);
            return (1.0 - lambdaK);
        }
    }

    /**
     * Gets the lambda parameter, which controls how much shifting and decay
     * there is in the weight vector.
     *
     * @return
     *      The lambda parameter. Must be positive.
     */
    public double getLambda()
    {
        return this.lambda;
    }

    /**
     * Sets the lambda parameter, which controls how much shifting and decay
     * there is in the weight vector.
     *
     * @param   lambda
     *      The lambda parameter. Must be positive.
     */
    public void setLambda(
        final double lambda)
    {
        ArgumentChecker.assertIsPositive("lambda", lambda);
        this.lambda = lambda;
    }

    /**
     * This is the result learned by the shifting perceptron.
     */
    public static class LinearResult
        extends LinearBinaryCategorizer
    {
// TODO: It may be good to have a more general version of the LinearBinaryCategorizer
// that keeps the error count and update count on it.
// -- jdbasil (2011-04-28)

        /** The number of errors made by the categorizer so far. */
        protected long errorCount;

        /**
         * Creates a new, empty {@code LinearResult}.
         */
        public LinearResult()
        {
            super();

            this.setErrorCount(0);
        }

        /**
         * Gets the error count.
         *
         * @return
         *      The error count.
         */
        public long getErrorCount()
        {
            return errorCount;
        }

        /**
         * Sets the error count.
         *
         * @param   errorCount
         *      The error count.
         */
        public void setErrorCount(
            final long errorCount)
        {
            this.errorCount = errorCount;
        }


    }
}
