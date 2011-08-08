/*
 * File:                LineBracketInterpolatorParabola.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction.ClosedForm;

/**
 * Interpolates using a parabola based on three points without 
 * slope information.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class LineBracketInterpolatorParabola 
    extends AbstractLineBracketInterpolatorPolynomial<Evaluator<Double,Double>>
{

    /** 
     * Creates a new instance of LineBracketInterpolatorParabola 
     */
    public LineBracketInterpolatorParabola()
    {
        super( DEFAULT_TOLERANCE );
    }

    /**
     * Parabolic interpolation needs three noncollinear points to fit a
     * parabola to them.  So, we need the bounds and one other (noncollinear)
     * point.  This parabola does not use derivative information.
     * @param bracket
     * LineBracket to consider
     * @return
     * True if we have sufficient points, false otherwise
     */
    public boolean hasSufficientPoints(
        LineBracket bracket )
    {
        // I need the bounds and one other (noncollinear) point.
        // And they can't be infinite!
        return (bracket.getLowerBound() != null) &&
            (bracket.getUpperBound() != null) &&
            (bracket.getOtherPoint() != null) &&
            (!Double.isInfinite(bracket.getLowerBound().getOutput())) &&
            (!Double.isInfinite(bracket.getUpperBound().getOutput())) &&
            (!Double.isInfinite(bracket.getOtherPoint().getOutput()));
    }

    @Override
    public ClosedForm computePolynomial(
        LineBracket bracket,
        Evaluator<Double, Double> function )
    {
        InputOutputPair<Double,Double> p0 = bracket.getLowerBound();
        InputOutputPair<Double,Double> p1 = bracket.getOtherPoint();
        InputOutputPair<Double,Double> p2 = bracket.getUpperBound();
        
        return PolynomialFunction.Quadratic.fit( p0, p1, p2 );
    }

}
