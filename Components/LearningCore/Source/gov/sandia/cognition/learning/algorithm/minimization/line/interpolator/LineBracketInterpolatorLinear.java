/*
 * File:                LineBracketInterpolatorLinear.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 18, 2008, Sandia Corporation.
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

/**
 * Interpolates using a linear (stright-line) curve between two
 * points, neither of which need slope information.
 * @author Kevin R. Dixon
 * @since 2.2
 */
public class LineBracketInterpolatorLinear 
    extends AbstractLineBracketInterpolatorPolynomial<Evaluator<Double,Double>>
{

    /** 
     * Creates a new instance of LineBracketInterpolatorLinear 
     */
    public LineBracketInterpolatorLinear()
    {
        super( DEFAULT_TOLERANCE );
    }

    @Override
    public PolynomialFunction.Linear computePolynomial(
        LineBracket bracket,
        Evaluator<Double, Double> function )
    {
        InputOutputPair<Double,Double> p0 = bracket.getLowerBound();
        InputOutputPair<Double,Double> p1 = bracket.getUpperBound();
        return PolynomialFunction.Linear.fit( p0, p1 );
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
