/*
 * File:                AbstractParallelizableCostFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 23, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Arrays;
import java.util.Collection;

/**
 * Partial implementation of the ParallelizableCostFunction
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class AbstractParallelizableCostFunction 
    extends AbstractSupervisedCostFunction<Vector,Vector>
    implements ParallelizableCostFunction
{

    /** 
     * Creates a new instance of AbstractParallelizableCostFunction 
     * @param costParameters 
     * Dataset to use
     */
    public AbstractParallelizableCostFunction(
        Collection<? extends InputOutputPair<? extends Vector,Vector>> costParameters )
    {
        super( costParameters );
    }

    @Override
    public Double evaluate(
        Evaluator<? super Vector, ? extends Vector> evaluator )
    {
        Object result = this.evaluatePartial( evaluator );
        return this.evaluateAmalgamate( Arrays.asList( result ) );
    }
    
    public Vector computeParameterGradient(
        GradientDescendable function )
    {
        Object result = this.computeParameterGradientPartial( function );
        return this.computeParameterGradientAmalgamate( Arrays.asList( result ) );
    }
    
}
