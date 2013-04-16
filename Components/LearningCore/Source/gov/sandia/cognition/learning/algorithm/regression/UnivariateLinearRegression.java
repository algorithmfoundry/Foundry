/*
 * File:            UnivariateLinearRegression.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * An implementation of simple univariate linear regression. It fits a function
 * of the form f(x) = mx + b to the given data. It supports learning from
 * weighted examples.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class UnivariateLinearRegression
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Double, Double, LinearFunction>
{

    /**
     * Creates a new {@code UnivariateLinearRegression}.
     */
    public UnivariateLinearRegression()
    {
        super();
    }

    @Override
    public LinearFunction learn(
        final Collection<? extends InputOutputPair<? extends Double, Double>> data)
    {
        // First we want the weighted means.
        double weightSum = 0.0;
        double xMean = 0.0;
        double yMean = 0.0;

        for (InputOutputPair<? extends Double, Double> example : data)
        {
            final double weight = Math.abs(DatasetUtil.getWeight(example));
            xMean += weight * example.getInput();
            yMean += weight * example.getOutput();
            weightSum += weight;
        }

        if (weightSum == 0.0)
        {
            // Be nice in handling zeros.
            xMean = 0.0;
            yMean = 0.0;
        }
        else
        {
            xMean /= weightSum;
            yMean /= weightSum;
        }

        // Compute m = sum_i (x_i - xMean) (y_i - yMean) / sum_i (x_i - xMean)^2
        // but with weights.
        double numerator = 0.0;
        double denominator = 0.0;
        for (InputOutputPair<? extends Double, Double> example : data)
        {
            final double weight = Math.abs(DatasetUtil.getWeight(example));

            final double x = example.getInput();
            final double y = example.getOutput();

            final double xDiff = x - xMean;
            final double yDiff = y - yMean;
            numerator += weight * xDiff * yDiff;
            denominator += weight * xDiff * xDiff;
        }

        // Compute b = yMean - m * xMean.
        final double slope = denominator == 0.0 ? 0.0 : numerator / denominator;
        final double offset = yMean - slope * xMean;

        // Return the result.
        return new LinearFunction(slope, offset);
    }
}
