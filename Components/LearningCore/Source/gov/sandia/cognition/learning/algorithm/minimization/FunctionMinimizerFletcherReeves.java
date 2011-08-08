/*
 * File:                FunctionMinimizerFletcherReeves.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 21, 2008, Sandia Corporation.
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
 * This is an implementation of the Fletcher-Reeves conjugate gradient
 * minimization procedure.  This is an unconstrained nonlinear optimization
 * technique that uses first-order derivative (gradient) information to
 * determine the direction of exact line searches.  This algorithm is the
 * original variant of conjugate gradient minimization, and is generally
 * regarded as inferior to other variants, such as Polack-Ribiere and
 * Liu-Storey.  Furthermore, CG is generally regarded as inferior to
 * quasi-Newton methods such as BFGS, but goes not store an NxN Hessian inverse.
 * Thus, if you have many inputs (N), then the conjugate gradient minimization
 * may be better than BFGS for your problem.  But even if you decide to go with
 * CG, then try the other CG variants before Fletcher-Reeves.
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReference(
    author="R. Fletcher",
    title="Practical Methods of Optimization, Second Edition",
    type=PublicationType.Book,
    year=1987,
    pages={80,83},
    notes="Equation 4.1.4"
)
public class FunctionMinimizerFletcherReeves 
    extends FunctionMinimizerConjugateGradient
{

    /** 
     * Creates a new instance of FunctionMinimizerPolakRibiere 
     */
    public FunctionMinimizerFletcherReeves()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }
    
    /**
     * Creates a new instance of FunctionMinimizerPolakRibiere 
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerFletcherReeves(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }

    /**
     * Creates a new instance of FunctionMinimizerConjugateGradient
     * 
     * @param initialGuess Initial guess about the minimum of the method
     * @param tolerance
     * Tolerance of the minimization algorithm, must be >= 0.0, typically ~1e-10
     * @param lineMinimizer 
     * Work-horse algorithm that minimizes the function along a direction
     * @param maxIterations
     * Maximum number of iterations, must be >0, typically ~100
     */
    public FunctionMinimizerFletcherReeves(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( lineMinimizer, initialGuess, tolerance, maxIterations );
    }
    
    protected double computeScaleFactor(
        Vector gradientCurrent,
        Vector gradientPrevious )
    {
        double gradientTgradient = gradientCurrent.norm2Squared();
        double denom = gradientPrevious.norm2Squared();
        
        double beta = gradientTgradient / denom;
        return beta;
    }

}
