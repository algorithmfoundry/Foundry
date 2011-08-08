/*
 * File:                FunctionMinimizerPolakRibiere.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This is an implementation of the Polack-Ribiere conjugate gradient
 * minimization procedure.  This is an unconstrained nonlinear optimization
 * technique that uses first-order derivative (gradient) information to
 * determine the direction of exact line searches.  This algorithm is generally
 * considered to be inferior to BFGS, but does not store an NxN Hessian inverse.
 * Thus, if you have many inputs (N), then the conjugate gradient minimization
 * may be better than BFGS for your problem.  But try BFGS first.
 * <BR><BR>
 * The Polack-Ribiere CG variant used to be considered the best out there,
 * though I've run across a CG variant of Liu-Storey that appears to be
 * slightly better.  In my experience, they both perform about equally, with
 * Liu-Storey slightly better.  But try both before settling on one.
 * 
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="R. Fletcher",
            title="Practical Methods of Optimization, Second Edition",
            type=PublicationType.Book,
            year=1987,
            pages=83,
            notes="Equation 4.1.12"
        )
        ,
        @PublicationReference(
            author={
                "William H. Press",
                "Saul A. Teukolsky",
                "William T. Vetterling",
                "Brian P. Flannery"
            },
            title="Numerical Recipes in C, Second Edition",
            type=PublicationType.Book,
            year=1992,
            pages={423,424},
            notes="Section 10.6",
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
    }
)
public class FunctionMinimizerPolakRibiere 
    extends FunctionMinimizerConjugateGradient
{

    /** 
     * Creates a new instance of FunctionMinimizerPolakRibiere 
     */
    public FunctionMinimizerPolakRibiere()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }

    /**
     * Creates a new instance of FunctionMinimizerPolakRibiere
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerPolakRibiere(
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
    public FunctionMinimizerPolakRibiere(
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
        Vector deltaGradient = gradientCurrent.minus( gradientPrevious );
        double deltaTgradient = deltaGradient.dotProduct( gradientCurrent );
        double denom = gradientPrevious.norm2Squared();
        
        double beta = deltaTgradient / denom;
        return beta;
    }

}
