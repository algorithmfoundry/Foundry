/*
 * File:                LineBracketInterpolatorHermiteParabola.java
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
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;

/**
 * Interpolates using a parabola with two points, at least one of which must
 * have slope information.  If slope information is not present, then the
 * interpolator will call the differentiate() method on the Evaluator.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class LineBracketInterpolatorHermiteParabola 
    extends AbstractLineBracketInterpolatorPolynomial<DifferentiableUnivariateScalarFunction>
{

    /** 
     * Creates a new instance of LineBracketInterpolatorHermiteParabola 
     */
    public LineBracketInterpolatorHermiteParabola()
    {
        super( DEFAULT_TOLERANCE );
    }

    public boolean hasSufficientPoints(
        LineBracket bracket )
    {
        return (bracket.getLowerBound() != null) &&
            (bracket.getUpperBound() != null) &&
            (!Double.isInfinite(bracket.getLowerBound().getOutput())) &&
            (!Double.isInfinite(bracket.getUpperBound().getOutput()));
    }

    @Override
    public PolynomialFunction.ClosedForm computePolynomial(
        LineBracket bracket,
        DifferentiableUnivariateScalarFunction function )
    {
        InputOutputSlopeTriplet a = bracket.getLowerBound();
        InputOutputSlopeTriplet b = bracket.getUpperBound();
        
        InputOutputSlopeTriplet p0;
        InputOutputPair<Double,Double> p1;
        
        // One of these points needs slope information
        if( a.getSlope() != null )
        {
            p0 = a;
            p1 = b;
        }
        else if( b.getSlope() != null )
        {
            p0 = b;
            p1 = a;
        }
        
        // If nobody has slope information, then we'll need to compute it once
        else
        {
            Double slope = function.differentiate( a.getInput() );
            a.setSlope( slope );
            p0 = a;
            p1 = b;
        }
        
        return PolynomialFunction.Quadratic.fit( p0, p1 );
        
    }


}
