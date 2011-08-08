/*
 * File:                LineBracketInterpolatorHermiteCubic.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction.ClosedForm;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;

/**
 * Interpolates using a cubic with two points, both of which must have
 * slope information.  If slope information is not present, then the
 * interpolator will call the differentiate() method on the Evaluator.
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class LineBracketInterpolatorHermiteCubic 
    extends AbstractLineBracketInterpolatorPolynomial<DifferentiableUnivariateScalarFunction>
{

    /** 
     * Creates a new instance of LineBracketInterpolatorHermiteCubic 
     */
    public LineBracketInterpolatorHermiteCubic()
    {
        super( DEFAULT_TOLERANCE );
    }

    @Override
    public ClosedForm computePolynomial(
        LineBracket bracket,
        DifferentiableUnivariateScalarFunction function )
    {
        InputOutputSlopeTriplet p0 = bracket.getLowerBound();
        InputOutputSlopeTriplet p1 = bracket.getUpperBound();
                
        // Compute the gradient information, if necessary
        if( p0.getSlope() == null )
        {
            p0.setSlope( function.differentiate( p0.getInput() ) );
        }
        if( p1.getSlope() == null )
        {
            p1.setSlope( function.differentiate( p1.getInput() ) );
        }
        
        return PolynomialFunction.Cubic.fit( p0, p1 );
    }

    public boolean hasSufficientPoints(
        LineBracket bracket )
    {
        return (bracket.getLowerBound() != null) &&
            (bracket.getUpperBound() != null) &&
            (!Double.isInfinite(bracket.getLowerBound().getOutput())) &&
            (!Double.isInfinite(bracket.getUpperBound().getOutput()));
    }

}
