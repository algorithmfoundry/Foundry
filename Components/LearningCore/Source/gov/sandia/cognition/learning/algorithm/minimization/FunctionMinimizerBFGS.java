/*
 * File:                FunctionMinimizerBFGS.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 7, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States 
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of the Broyden-Fletcher-Goldfarb-Shanno (BFGS) Quasi-Newton
 * nonlinear minimization algorithm.  This algorithm is generally considered
 * to be the most effective/efficient unconstrained nonlinear optimization 
 * algorithm out there.  It does, however, require the derivative of the
 * function to optimize.  Furthermore, it requires the storage of an
 * approximation of the Hessian inverse (BFGS does not invert any matrix,
 * it merely approximates the inverse explicitly... clever, eh?), which is an
 * N-by-N matrix, where N is the size of the input space.
 * <BR><BR>
 * In practice, one can use approximated Jacobians with BFGS with good
 * convergence results, so one can use, for example, 
 * {@code GradientDescendableApproximator} (when used for parameter cost
 * minimization) when exact gradients are not available.  Using approximated
 * Jacobian tends, to slow down BFGS by a factor of about ~3, but it appears
 * to generally outperform derivative-free minimization techniques, like 
 * Powell's direction-set method.  Also, BFGS appears to outperform
 * Leveberg-Marquardt estimation for parameter estimation, in my experience.
 * <BR><BR>
 * Again, just to recap: BFGS appears to be the method of choice when 
 * minimizing against a cost function (using exact or approximated Jacobians).
 *<BR><BR>
 * Note that there is a reduced-memory implementation of BFGS, called L-BFGS,
 * that reduces the memory needed to store the Hessian inverse.  We have not
 * yet implemented this.
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
            pages=55,
            notes="Section 3.2, Equation 3.2.12"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="BFGS method",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/BFGS_method"
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
            pages={428,429},
            notes="Section 10.7",
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
    }
)
public class FunctionMinimizerBFGS 
    extends FunctionMinimizerQuasiNewton
{

    /** 
     * Creates a new instance of FunctionMinimizerBFGS 
     */
    public FunctionMinimizerBFGS()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }

    /**
     * Creates a new instance of FunctionMinimizerBFGS 
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerBFGS(
        LineMinimizer<?> lineMinimizer )
    {
        super( lineMinimizer, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }    
    
    /**
     * Creates a new instance of FunctionMinimizerBFGS
     * 
     * @param initialGuess Initial guess about the minimum of the method
     * @param tolerance Tolerance of the minimization algorithm, must be >= 0.0, typically ~1e-10
     * @param lineMinimizer 
     * Work-horse algorithm that minimizes the function along a direction
     * @param maxIterations Maximum number of iterations, must be >0, typically ~100
     */
    public FunctionMinimizerBFGS(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( lineMinimizer, initialGuess, tolerance, maxIterations );
    }
    
    public boolean updateHessianInverse(
        Matrix hessianInverse,
        Vector delta,
        Vector gamma )
    {
        return FunctionMinimizerBFGS.BFGSupdateRule(
            hessianInverse, delta, gamma, this.getTolerance() );
    }
    
    /**
     * BFGS Quasi-Newton update rule
     * @param hessianInverse
     * Current Hessian inverse estimate, will be modified
     * @param delta
     * Change in x-axis
     * @param gamma
     * Change in gradient
     * @param tolerance 
     * Tolerance of the algorithm
     * @return
     * true if update, false otherwise
     */
    @PublicationReference(
        author="R. Fletcher",
        title="Practical Methods of Optimization, Second Edition",
        type=PublicationType.Book,
        year=1987,
        pages=55,
        notes="Section 3.2, Equation 3.2.12"
    )
    public static boolean BFGSupdateRule(
        Matrix hessianInverse,
        Vector delta,
        Vector gamma,
        double tolerance )
    {
        
        int M = hessianInverse.getNumRows();
        
        // This is formula 3.2.12 on p.55 in PMOO
        // Solving for Fletcher's "H"
        Vector Higamma = hessianInverse.times( gamma );
        double deltaTgamma = delta.dotProduct( gamma );
                
        // If we're close to singular, then skip the Hessian update.
        // Some people add some clever resetting code here.
        // I'll just skip for now.
        if( Math.sqrt( tolerance * delta.norm2Squared() * gamma.norm2Squared() )
            >= Math.abs(deltaTgamma) )
        {
            return false;
        }
        
        double term1 = 1.0 + (gamma.dotProduct( Higamma ) / deltaTgamma);
        
        for( int i = 0; i < M; i++ )
        {
            double deltai = delta.getElement( i );
            double Higammai = Higamma.getElement( i );
            
            // Since the Hessian inverse is symmetric, we can just iterate
            // through the bottom half of the matrix and mirror the values.
            for( int j = 0; j <= i; j++ )
            {
                double oldValue = hessianInverse.getElement( i, j );
                double change = term1*deltai*delta.getElement( j );
                change -= deltai*Higamma.getElement(j) + Higammai*delta.getElement(j);
                change /= deltaTgamma;
                
                double newValue = oldValue + change;
                
                // Since the Hessian inverse is symmetric, we can just iterate
                // through the bottom half of the matrix and mirror the values.
                // But make sure we don't double update the diagonal.
                hessianInverse.setElement( i, j, newValue );
                if( i != j )
                {
                    hessianInverse.setElement( j, i, newValue );
                }
            }
            
        }

        return true;        
        
    }    
    
}
