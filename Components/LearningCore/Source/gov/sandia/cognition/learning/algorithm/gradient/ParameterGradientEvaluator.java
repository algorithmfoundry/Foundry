/*
 * File:                ParameterGradientEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.gradient;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.evaluator.Evaluator;

/**
 * Interface for computing the derivative of the output with respect to the
 * parameters for a given input.
 *
 * @param <InputOutputType> Input and Output classes of the {@code Evaluator},
 * such as {@code Vector}, for example
 * @param <GradientType> Class type of the gradient, such as {@code Matrix},
 * for example
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Minor change to javadoc.",
        "Looks fine."
    }
)
public interface ParameterGradientEvaluator<InputOutputType,GradientType>
    extends Evaluator<InputOutputType,InputOutputType>,
    Vectorizable
{
    
    /**
     * Computes the derivative of the output with respect to the parameters for
     * a particular input.
     * @param input 
     * Input about which to compute the parameter gradient
     * @return 
     * Change of the parameters with respect to the output
     */
    public GradientType computeParameterGradient(
        InputOutputType input );
    
}
