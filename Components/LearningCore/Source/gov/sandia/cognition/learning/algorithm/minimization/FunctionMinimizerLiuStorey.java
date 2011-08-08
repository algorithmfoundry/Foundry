/*
 * File:                FunctionMinimizerLiuStorey.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This is an implementation of the Liu-Storey conjugate gradient
 * minimization procedure.  This is an unconstrained nonlinear optimization
 * technique that uses first-order derivative (gradient) information to
 * determine the direction of exact line searches.  This algorithm is generally
 * considered to be inferior to BFGS, but does not store an NxN Hessian inverse.
 * Thus, if you have many inputs (N), then the conjugate gradient minimization
 * may be better than BFGS for your problem.  But try BFGS first.
 * <BR><BR>
 * The Liu-Storey CG variant is considered often superior to the CG variant of 
 * Polack-Ribiere.  In my experience, they both perform about equally, with
 * Liu-Storey slightly better.  But try both before settling on one.
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReference(
    author={
        "Y. Liu",
        "C. Storey"
    },
    title="Efficient generalized conjugate gradient algorithms, Part 1: theory",
    type=PublicationType.Journal,
    publication="Journal of Optimization Theory and Applications",
    pages={129,137},
    year=1991,
    notes={
        "I've seen independent analyses that indicate that this is the most efficient CG algorithm out there.",
        "For example, http://www.ici.ro/camo/neculai/cg.ppt"
    }
)
public class FunctionMinimizerLiuStorey 
    extends FunctionMinimizerConjugateGradient
{

    /** 
     * Creates a new instance of FunctionMinimizerLiuStorey 
     */
    public FunctionMinimizerLiuStorey()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }

    /**
     * Creates a new instance of FunctionMinimizerLiuStorey
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerLiuStorey(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );        
    }

    /**
     * Creates a new instance of FunctionMinimizerLiuStorey
     * 
     * @param initialGuess Initial guess about the minimum of the method
     * @param tolerance
     * Tolerance of the minimization algorithm, must be >= 0.0, typically ~1e-10
     * @param lineMinimizer 
     * Work-horse algorithm that minimizes the function along a direction
     * @param maxIterations
     * Maximum number of iterations, must be >0, typically ~100
     */
    public FunctionMinimizerLiuStorey(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( lineMinimizer, initialGuess, tolerance, maxIterations );
    }

    @Override
    protected double computeScaleFactor(
        Vector gradientCurrent,
        Vector gradientPrevious )
    {
        Vector direction = this.lineFunction.getDirection();
        
        Vector deltaGradient = gradientCurrent.minus( gradientPrevious );
        double deltaTgradient = deltaGradient.dotProduct( gradientCurrent );
        double denom = gradientPrevious.dotProduct( direction );
        double beta = -deltaTgradient / denom;
        return beta;
    }

}
