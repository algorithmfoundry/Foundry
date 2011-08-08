/*
 * File:                LinearFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;

/**
 * This function simply acts as a pass-through, where evaluate(input)==input
 * for any input and the derivative is always equal to 1.0.
 * This is for those classes that expect an evaluator, but you don't want
 * to alter the value of the function, like a FeedforwardNeuralNetwork or
 * 
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Made clone() call super.clone().",
        "Otherwise, class looks fine."
    }
)
public class LinearFunction 
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /** 
     * Creates a new instance of LinearFunction 
     */
    public LinearFunction()
    {
        // Nothing to set
    }

    /**
     * Copy Constructor
     * @param other LinearFunction to copy
     */
    public LinearFunction(
        LinearFunction other )
    {
        // Nothing to copy
    }
    
    @Override
    public LinearFunction clone()
    {
        return (LinearFunction) super.clone();
    }

    public double evaluate(
        double input )
    {
        return input;
    }

    public double differentiate(
        double input )
    {
        return 1.0;
    }

}
