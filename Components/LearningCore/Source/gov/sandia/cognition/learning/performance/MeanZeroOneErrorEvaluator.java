/*
 * File:                MeanZeroOneErrorEvaluator.java
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
import gov.sandia.cognition.learning.function.cost.AbstractSupervisedCostFunction;
import java.util.Collection;

/**
 * The {@code MeanZeroOneErrorEvaluator} class implements a method for
 * computing the performance of a supervised learner by the mean number of
 * incorrect values between the target and estimated outputs. This can also be
 * referred to as the error rate. The term "mean zero-one error" comes from the
 * computation that the error is zero if the two values are equal and one if
 * the two values are not equal.
 *
 * This class can be used with any data type that has a valid equals method.
 *
 * @param   <InputType> The type of the input to the evaluator to compute the
 *          performance of.
 * @param   <DataType> The type of the target and estimate values.
 * @author  Justin Basilico
 * @since   2.0
 */
public class MeanZeroOneErrorEvaluator<InputType, DataType>
    extends AbstractSupervisedCostFunction<InputType, DataType>
{

    /**
     * Creates a new instance of MeanZeroOneErrorEvaluator.
     */
    public MeanZeroOneErrorEvaluator()
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
        final Collection<? extends TargetEstimatePair<? extends DataType, ? extends DataType>> data )
    {
        return MeanZeroOneErrorEvaluator.compute( data );
    }

    /**
     * Computes the mean zero-one loss for the given pairs of values. The error
     * is defined to be 0.0 if the two values are the same and 1.0 otherwise.
     * The mean of this error computed over all the data points is returned.
     *
     * @param   <DataType> The type of data to compute the estimate over.
     * @param   data The data to compute the error for.
     * @return  The mean zero-one error.
     */
    public static <DataType> double compute(
        final Collection<? extends TargetEstimatePair<? extends DataType, ? extends DataType>> data )
    {
        // Since we compute the mean we need to know how many items there are.
        final int count = data.size();

        if (count <= 0)
        {
            // There must be at least one item to compute a mean.
            return 0.0;
        }

        // Compute the error for each pair and add it to the sum.
        int errorSum = 0;
        for (TargetEstimatePair<? extends DataType, ? extends  DataType> pair
            : data)
        {
            final DataType target = pair.getTarget();
            final DataType estimate = pair.getEstimate();

            if ((target == null ^ estimate == null) || (target != null && !target.equals( estimate )))
            {
                // The two values are not equal so this is an error.
                errorSum += 1;
            }
        // else - This is not an error.
        }

        // Compute the mean of the error sum.
        return (double) errorSum / (double) count;
    }

}
