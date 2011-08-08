/*
 * File:                DirectionalVectorToDifferentiableScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Creates a truly differentiable scalar function from a differentiable Vector
 * function, instead of using a forward-differences approximation to the
 * derivative like DirectionalVectorToScalarFunction does.
 * @author Kevin R. Dixon
 * @since 2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Made clone() call super.clone().",
        "Created test class.",
        "Class looks fine."
    }
)
public class DirectionalVectorToDifferentiableScalarFunction
    extends DirectionalVectorToScalarFunction
{
    
    /**
     * Last gradient information
     */
    private InputOutputPair<Vector,Vector> lastGradient;

    /** 
     * Creates a new instance of DirectionalVectorToDifferentiableScalarFunction 
     *
     * @param vectorScalarFunction
     * Function that maps a Vector onto a Double
     * @param vectorOffset offset vector from which to scale along
     *        direction to evaluate vectorFunction
     * @param direction Direction to optimize along
     */
    public DirectionalVectorToDifferentiableScalarFunction(
        DifferentiableEvaluator<? super Vector, ? extends Double, Vector> vectorScalarFunction,
        Vector vectorOffset,
        Vector direction )
    {
        super( vectorScalarFunction, vectorOffset, direction );
    }

    @Override
    public DirectionalVectorToDifferentiableScalarFunction clone()
    {
        DirectionalVectorToDifferentiableScalarFunction clone =
            (DirectionalVectorToDifferentiableScalarFunction) super.clone();
        clone.setLastGradient(null);
        return clone;
    }

    @SuppressWarnings("unchecked")
    @Override
    public double differentiate(
        double input )
    {
        Vector vectorInput = this.computeVector( input );
        Vector vectorDerivative =
            ((DifferentiableEvaluator<? super Vector, ? extends Double, Vector>) this.getVectorScalarFunction()).differentiate( vectorInput );
        double slope = this.getDirection().dotProduct( vectorDerivative );

        this.setLastGradient( new DefaultInputOutputPair<Vector, Vector>(
            vectorInput, vectorDerivative ) );
        return slope;
    }

    /**
     * Getter for lastGradient
     * @return
     * Last gradient information
     */
    public InputOutputPair<Vector, Vector> getLastGradient()
    {
        return this.lastGradient;
    }

    /**
     * Setter for lastGradient
     * @param lastGradient
     * Last gradient information
     */
    public void setLastGradient(
        InputOutputPair<Vector, Vector> lastGradient )
    {
        this.lastGradient = lastGradient;
    }
    
}
