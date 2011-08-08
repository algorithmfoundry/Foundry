/*
 * File:                FunctionMinimizerDFP.java
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
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of the Davidon-Fletcher-Powell (DFP) formula for a
 * Quasi-Newton minimization update.  The DFP formula is generally recognized
 * as inferior to the BFGS update formula (FunctionMinimizerBFGS), and I would
 * recommend using that first.  However, there are a few problems that DFP
 * may be more efficient than BFGS, so here it is.
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="R. Fletcher",
            title="Practical Methods of Optimization, Second Edition",
            type=PublicationType.Book,
            year=1987,
            pages=54,
            notes="Section 3.2, Equation 3.2.11"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Davidon-Fletcher-Powell formula",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Davidon-Fletcher-Powell_formula"
        )
    }
)
public class FunctionMinimizerDFP 
    extends FunctionMinimizerQuasiNewton
{

    /** 
     * Creates a new instance of FunctionMinimizerBFGS 
     */
    public FunctionMinimizerDFP()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }
    
    /**
     * Creates a new instance of FunctionMinimizerBFGS 
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerDFP(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );        
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
    public FunctionMinimizerDFP(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( lineMinimizer, initialGuess, tolerance, maxIterations );
    }

    @PublicationReference(
        author="R. Fletcher",
        title="Practical Methods of Optimization, Second Edition",
        type=PublicationType.Book,
        year=1987,
        pages=54,
        notes="Section 3.2, Equation 3.2.11"
    )
    @Override
    protected boolean updateHessianInverse(
        Matrix hessianInverse,
        Vector delta,
        Vector gamma )
    {
        
        // Solving for Fletcher's "H"
        int M = hessianInverse.getNumRows();
        
        // This is formula 3.2.12 on p.55 in PMOO
        // Solving for Fletcher's "H"
        Vector Higamma = hessianInverse.times( gamma );
        double deltaTgamma = delta.dotProduct( gamma );
        double gammaTHigamma = gamma.dotProduct( Higamma );
        
        // If we're close to singular, then skip the Hessian update.
        // Some people add some clever resetting code here.
        // I'll just skip for now.
        double singularThreshold = Math.sqrt( 
            this.getTolerance() * delta.norm2Squared() * gamma.norm2Squared() );
        
        if( (singularThreshold >= Math.abs(deltaTgamma)) ||
            (singularThreshold >= Math.abs(gammaTHigamma)) )
        {
            return false;
        }
        
        for( int i = 0; i < M; i++ )
        {
            double deltai = delta.getElement( i );
            double Higammai = Higamma.getElement( i );
            
            // Since the Hessian inverse is symmetric, we can just iterate
            // through the bottom half of the matrix and mirror the values.
            for( int j = 0; j <= i; j++ )
            {
                double oldValue = hessianInverse.getElement( i, j );
                double change = deltai*delta.getElement(j) / deltaTgamma;
                change -= Higammai*Higamma.getElement(j) / gammaTHigamma;
                
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
