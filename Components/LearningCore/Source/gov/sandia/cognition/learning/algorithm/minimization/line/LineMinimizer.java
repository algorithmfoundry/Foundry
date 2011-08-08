/*
 * File:                LineMinimizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 1, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolator;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Defines the functionality of a line-minimization algorithm, often called a
 * "line search" algorithm.  These algorithms find the minimum of a scalar
 * function near an "initial guess".  Generally, these algorithms operate in
 * two phases.  The first phase is known as the "bracketing phase", where the
 * algorithm attempts to find an interval along the x-axis that contains a
 * known minimum.  Once the bracketing phase is complete, and a minimum is
 * bracketing, the second phase begins.  The second phase is known as the
 * "sectioning phase", where the size of the bracket is repeatedly reduced,
 * while keeping the minimum between the bracket bounds, until a desired
 * accuracy is reached.
 * <BR>
 * Again, just to recap: line minimizers operate in two phases.  First, rope
 * off x-axis territory until you can demonstrate you've got a minimum in your
 * bracket.  Then, squeeze the bracket together until you get sufficiently
 * close to the minimum.
 * <BR>
 * Some LineMinimizers do not require derivative information in either the
 * bracketing or sectioning, such as Brent's method
 * (LineMinimizerDerivativeFree).  Some LineMinimizers make use of derivative
 * information both during bracketing and sectioning, such as Fletcher's line
 * search (LineMinimizerDerivativeBased).
 * <BR>
 * In my experience, derivative-free methods are as efficient as
 * derivative-based line minimizations.  Because I do not see large
 * computational or real-world time savings, I tend to just use derivative-free
 * line minimizers using Brent's interpolator (which is very clever and
 * extremely robust).  But it's always worth trying out derivative-based line
 * minimizers using Hermite polynomial interpolators (you can also use Brent's
 * interpolator with derivative-based methods, but it doesn't make use of the
 * gradients that are being calculated anyway, so it tends to slow things
 * down).
 * 
 * @param <EvaluatorType> Type of Evaluator to use.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface LineMinimizer<EvaluatorType extends Evaluator<Double,Double>>
    extends FunctionMinimizer<Double, Double, EvaluatorType>
{

    /**
     * Gets the interpolator used to fit data points and derive an
     * interpolated (hypothesized) minimum to try next.
     * @return
     * LineBracketInterpolator used in this optimization
     */
    public LineBracketInterpolator<? super EvaluatorType> getInterpolator();
    
    /**
     * Gets the LineBracket used to bound the search
     * @return
     * LineBracket used to bound the search
     */
    public LineBracket getBracket();
    
    /**
     * Returns true if the algorithm has found a valid bracket on a minimum,
     * false if the algorithm needs to continue the bracketing phase
     * @return
     * True if a valid bracket on a minimum has been found, false otherwise
     */
    public boolean isValidBracket();
    
    /**
     * Continues the bracketing phase of the algorithm, which attempts to
     * place a bracket around a known minimum.
     * @return
     * True if a valid bracket has been found, false to continue the bracketing
     * phase of the algorithm.
     */
    public boolean bracketingStep();
    
    /**
     * Continues the sectioning phase of the algorihtm.  This phase occurs
     * after the bracketing phase and attempts to shrink the size of the
     * bracket (using a LineBracketInterpolator) until sufficient accuracy is
     * attained.
     * @return
     * True that a valid minimum has been found, false to continue sectioning
     * the bracket.
     */
    public boolean sectioningStep();
        
    /**
     * Minimizes a Vector function along the direction given by the
     * DirectionalVectorToScalarFunction.  The offset in {@code function} is
     * taken to be the initialGuess.
     * @param function
     * Defines the direction to search along, and the initial guess.  The
     * direction is scaled by the line-search solution
     * @param functionValue 
     * Value of function at initialGuess, may be null
     * @param gradient
     * Derivative of the output with respect to the input of {@code function}
     * at the initial guess.  Gradient may be null if it's not being computer.
     * So, gradient is not required for all line-search methods, but
     * will throw an exception if it's expected but not available.
     * @return
     * Location of the minimum-value Vector solution to function and it's
     * corresponding output value.  The weight is the length of the optimal
     * line search along "direction".
     */
    WeightedInputOutputPair<Vector, Double> minimizeAlongDirection(
        DirectionalVectorToScalarFunction function,
        Double functionValue,
        Vector gradient );
    
}
