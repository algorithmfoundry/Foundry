/*
 * File:                MeanAbsoluteError.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.data.TargetEstimatePair;
import java.util.Collection;

/**
 * The {@code MeanAbsoluteError} class implements a method for computing the
 * performance of a supervised learner for a scalar function by the mean
 * absolute value between the target and estimated outputs. This can also be
 * referred to as the mean L1 error.
 *
 * @param   <InputType> The type of the input to the evaluator to compute the
 *          performance of.
 * @author  Justin Basilico
 * @since   2.0
 */
public class MeanAbsoluteErrorEvaluator<InputType>
    extends AbstractSupervisedPerformanceEvaluator<InputType, Double, Double, Double>
{

    /**
     * Creates a new instance of MeanAbsoluteError
     */
    public MeanAbsoluteErrorEvaluator()
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
        return MeanAbsoluteErrorEvaluator.compute( data );
    }

    /**
     * Computes the mean absolute error for the given pairs of values. The
     * absolute value of the difference between the two values in each pair is 
     * computed and then the mean over all the values is returned.
     *
     * @param  data The data to compute the mean absolute error over.
     * @return The mean absolute error.
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

            // The error is the absolute value of the difference.
            final double error = Math.abs( difference );
            errorSum += error;
        }

        // Compute the mean of the error sum.
        return errorSum / (double) count;
    }

}
