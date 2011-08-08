/*
 * File:                MeanL1CostFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * Cost function that evaluates the mean 1-norm error (absolute value of
 * difference) weighted by a sample "weight" that is embedded in each sample.
 * A derived class is used by the Daimler project.
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class MeanL1CostFunction
    extends AbstractSupervisedCostFunction<Vector, Vector>
{

    /**
     * Default constructor
     */
    public MeanL1CostFunction()
    {
        this( (Collection<? extends InputOutputPair<? extends Vector, Vector>>) null );
    }

    /**
     * Creates a new instance of MeanL1CostFunction
     * 
     * @param dataset 
     * Underlying set of data, with weights for each sample, that will be used
     * to evaluate the vectorFunction
     */
    public MeanL1CostFunction(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> dataset )
    {
        super( dataset );
    }

    @Override
    public MeanL1CostFunction clone()
    {
        return (MeanL1CostFunction) super.clone();
    }

    @Override
    public Double evaluatePerformance(
        Collection<? extends TargetEstimatePair<Vector, Vector>> data )
    {
        double denominator = 0.0;
        double sumL1 = 0.0;
        for (TargetEstimatePair<? extends Vector, ? extends Vector> pair : data)
        {
            Vector target = pair.getTarget();
            Vector estimate = pair.getEstimate();
            Vector error = target.minus( estimate );
            double weight = DatasetUtil.getWeight(pair);
            double errorL1 = weight * error.norm1();

            sumL1 += errorL1;
            denominator += weight;
        }

        double meanWeightedL1Error = 0.0;
        if (denominator != 0.0)
        {
            meanWeightedL1Error = sumL1 / denominator;
        }

        return meanWeightedL1Error;
    }

}
