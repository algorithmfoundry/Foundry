/*
 * File:                DifferentiableCostFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 26, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * The <code>DifferentiableCostFunction</code> is a cost function that can 
 * be differentiated. This requires that it operate as a cost function for
 * <code>VectorFunction</code> objects and it has a separate method for
 * doing the differentiation of a given 
 * <code>DifferentiableVectorFunction</code> with respect to the cost function.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface DifferentiableCostFunction
    extends SupervisedCostFunction<Vector,Vector>
{
    /**
     * Differentiates function with respect to its parameters.
     *
     * @param function The object to differentiate.
     * @return Derivatives of the object with respect to the cost function.
     */
    public Vector computeParameterGradient(
        GradientDescendable function);
}
