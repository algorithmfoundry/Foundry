/*
 * File:                MeanSquaredErrorCostFunction.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * The MeanSquaredErrorCostFunction implements a cost function for functions
 * that take as input a vector and return a vector.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=false,
    comments="Minor documentaMtion changes."
)
public class MeanSquaredErrorCostFunction
    extends AbstractSupervisedCostFunction<Vector, Vector>
    implements DifferentiableCostFunction
{

    /**
     * Creates a new instance of MeanSquaredErrorCostFunction with no initial
     * dataset.
     */
    public MeanSquaredErrorCostFunction()
    {
        this( (Collection<? extends InputOutputPair<? extends Vector, Vector>>) null );
    }

    /** 
     * Creates a new instance of MeanSquaredErrorCostFunction
     *
     * @param dataset The dataset of examples to use to compute the error.
     */
    public MeanSquaredErrorCostFunction(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> dataset )
    {
        super( dataset );
    }

    @Override
    public MeanSquaredErrorCostFunction clone()
    {
        return (MeanSquaredErrorCostFunction) super.clone();
    }

    @Override
    public Double evaluatePerformance(
        Collection<? extends TargetEstimatePair<Vector, Vector>> data )
    {

        double sumSquaredError = 0.0;
        double denominator = 0.0;

        for (TargetEstimatePair<? extends Vector, ? extends Vector> pair : data)
        {
            // Compute the error vector.
            Vector target = pair.getTarget();
            Vector estimate = pair.getEstimate();
            double errorSquared = target.euclideanDistanceSquared( estimate );
            double weight = DatasetUtil.getWeight(pair);

            sumSquaredError += weight * errorSquared;
            denominator += weight;
        }

        double meanSquaredError = 0.0;
        if (denominator != 0.0)
        {
            meanSquaredError = sumSquaredError / denominator;
        }

        return meanSquaredError;

    }

    public Vector computeParameterGradient(
        GradientDescendable function )
    {

        RingAccumulator<Vector> parameterDelta =
            new RingAccumulator<Vector>();

        double denominator = 0.0;

        for (InputOutputPair<? extends Vector, ? extends Vector> pair : this.getCostParameters())
        {
            Vector input = pair.getInput();
            Vector target = pair.getOutput();

            Vector negativeError = function.evaluate( input );
            negativeError.minusEquals( target );

            double weight = DatasetUtil.getWeight(pair);

            if (weight != 1.0)
            {
                negativeError.scaleEquals( weight );
            }

            denominator += weight;

            Matrix gradient = function.computeParameterGradient( input );
            Vector parameterUpdate = negativeError.times( gradient );
            parameterDelta.accumulate( parameterUpdate );
        }

        Vector negativeSum = parameterDelta.getSum();
        if (denominator != 0.0)
        {
            negativeSum.scaleEquals( 1.0 / denominator );
        }

        return negativeSum;

    }

}

