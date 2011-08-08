/*
 * File:                RootMeanSquaredErrorEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 18, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 * 
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.data.TargetEstimatePair;
import java.util.Collection;

/**
 * The {@code RootMeanSquaredErrorEvaluator} class implements a method for 
 * computing the performance of a supervised learner for a scalar function by 
 * the root mean squared error (RMSE or RSE) between the target and estimated 
 * outputs.
 * 
 * @param   <InputType> The type of the input to the evaluator to compute the
 *          performance of.
 * @author  Justin Basilico
 * @since   2.0
 */
public class RootMeanSquaredErrorEvaluator<InputType>
    extends AbstractSupervisedPerformanceEvaluator<InputType, Double, Double, Double>
{

    /**
     * Creates a new {@code RootMeanSquaredErrorEvaluator}.
     */
    public RootMeanSquaredErrorEvaluator()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param  data {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Double evaluatePerformance(
        final Collection<? extends TargetEstimatePair<Double, Double>> data )
    {
        return RootMeanSquaredErrorEvaluator.compute( data );
    }

    /**
     * Computes the mean squared error for the given pairs of values. The
     * squared difference between the two values in each pair is computed and
     * then the mean over all the values is returned.
     *
     * @param  data The data to compute the mean squared error over.
     * @return The mean squared error.
     */
    public static double compute(
        final Collection<? extends TargetEstimatePair<Double, Double>> data )
    {
        // Since we compute the mean we need to know how many items there are.
        final int count = data.size();

        if (count <= 0)
        {
            // There must be at least one item to compute a mean.
            return 0.0;
        }

        // Compute the error for each pair and add it to the sum.
        double errorSum = 0.0;
        for (TargetEstimatePair<Double, Double> pair : data)
        {
            final double target = pair.getTarget();
            final double estimate = pair.getEstimate();
            final double difference = target - estimate;

            // The error is the squared difference.
            final double error = difference * difference;
            errorSum += error;
        }

        // Compute the mean of the error sum.
        return Math.sqrt( errorSum / (double) count );
    }

}
