/*
 * File:                FunctionMinimizerQuasiNewton.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 20, 2008, Sandia Corporation.
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
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToDifferentiableScalarFunction;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeBased;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * This is an abstract implementation of the Quasi-Newton minimization method,
 * sometimes called "Variable-Metric methods."
 * This family of minimization algorithms uses first-order gradient information
 * to find a locally minimum to a scalar function.  Quasi-Newton methods
 * perform this minimization by creating an estimate of the inverse
 * of the Hessian to compute a new search direction.  A line search yields
 * the next point from which to estimate the function curvature (that is,
 * the estimate of the Hessian inverse).  Please note: Quasi-Newton algorithms
 * estimate the INVERSE of the Hessian and never actually invert/solve for
 * a matrix, making them extremely fast.
 * <BR><BR>
 * It is generally agreed that Quasi-Newton methods are the fastest
 * minimization algorithms that use one first-order gradient information.  In
 * particular, the BFGS update formula to the Quasi-Newton algorithm
 * (FunctionMinimizerBFGS) is generally regarded as the fastest unconstrained
 * optimizer out there, and is my method of choice.  However, all Quasi-Newton
 * methods require storage of the inverse Hessian, which can become quite
 * large.  If you cannot store the inverse Hessian estimate in memory, then
 * I would recommend using the Liu-Storey Conjugate Gradient algorithm instead
 * (FunctionMinimizerLiuStorey).
 * <BR><BR>
 * Generally speaking, Quasi-Newton methods require first-order gradient
 * information.  If you do not have access to gradients, then I would recommend
 * using automatic finite-difference approximations over the derivative-free
 * minimization algorithms.
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
            pages={49, 57},
            notes="Section 3.2"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Quasi-Newton method",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Quasi-Newton_methods"
        ),
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
            pages={425,430},
            notes="Section 10.7",
            url="http://www.nrbook.com/a/bookcpdf.php"
       )
    }
)
public abstract class FunctionMinimizerQuasiNewton 
    extends AbstractAnytimeFunctionMinimizer<Vector, Double, DifferentiableEvaluator<? super Vector, Double, Vector>>
{
    
    /**
     * Default maximum number of iterations before stopping, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;

    /**
     * Default tolerance, {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-5;

    /**
     * Default line minimization algorithm, LineMinimizerDerivativeBased
     */
    public static final LineMinimizer<?> DEFAULT_LINE_MINIMIZER =
//        new LineMinimizerBacktracking();
        new LineMinimizerDerivativeBased();
//        new LineMinimizerDerivativeFree();

    /**
     * Work-horse algorithm that minimizes the function along a direction
     */
    private LineMinimizer<?> lineMinimizer;    
    
    /**
     * Creates a new instance of FunctionMinimizerBFGS
     * 
     * @param initialGuess Initial guess about the minimum of the method
     * @param tolerance Tolerance of the minimization algorithm, must be >= 0.0, typically ~1e-10
     * @param lineMinimizer 
     * Work-horse algorithm that minimizes the function along a direction
     * @param maxIterations Maximum number of iterations, must be >0, typically ~100
     */
    public FunctionMinimizerQuasiNewton(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( initialGuess, tolerance, maxIterations );
        this.setLineMinimizer( lineMinimizer );
    }
    
    /**
     * Function that maps a Evaluator<Vector,Double> onto a 
     * Evaluator<Double,Double> using a set point, direction and scale factor
     */
    private DirectionalVectorToDifferentiableScalarFunction lineFunction;

    /**
     * Estimated inverse of the Hessian (second derivative)
     */
    private Matrix hessianInverse;

    /**
     * Gradient at the current guess
     */
    private Vector gradient;

    /**
     * The dimensionality of the input space
     */
    private int dimensionality;    
    
    @Override
    protected boolean initializeAlgorithm()
    {
        // Due to bizarre inheretance, this.data is the function to minimize...
        DifferentiableEvaluator<? super Vector, Double, Vector> f = this.data;
        
        this.result = new DefaultInputOutputPair<Vector, Double>(
            this.initialGuess.clone(), f.evaluate( this.initialGuess ) );

        this.gradient = f.differentiate( this.initialGuess );

        // Load up the line function with the current direction and
        // the search direction, which is the negative gradient, in other words
        // the direction of steepest descent
        this.lineFunction = new DirectionalVectorToDifferentiableScalarFunction(
            f, this.result.getInput(), this.gradient.scale( -1.0 ) );
        
        this.dimensionality = this.initialGuess.getDimensionality();
        this.hessianInverse = MatrixFactory.getDefault().createIdentity(
            this.dimensionality, this.dimensionality )
            .scale( 0.5 )
//            .scale( Math.sqrt(this.getTolerance()) )
            ;

        return true;
    }

    @Override
    protected boolean step()
    {
        // Search along the gradient for the minimum value
        Vector xold = this.result.getInput();
        
        this.result = this.lineMinimizer.minimizeAlongDirection(
            this.lineFunction, this.result.getOutput(), this.gradient );

        Vector xnew = this.result.getInput();
        double fnew = this.result.getOutput();

        this.lineFunction.setVectorOffset( xnew );

        // Let's cache some values for speed
        Vector gradientOld = this.gradient;
        
        // See if I've already computed the gradient information
        // NOTE: It's possible that there's still an inefficiency here.
        // For example, we could have computed the gradient for "xnew"
        // previous to the last evaluation.  But this would require a
        // tremendous amount of bookkeeping and memory.
        if( (this.lineFunction.getLastGradient() != null) &&
            (this.lineFunction.getLastGradient().getInput().equals( xnew )) )
        {
            this.gradient = this.lineFunction.getLastGradient().getOutput();
        }
        else
        {
            this.gradient = this.data.differentiate( xnew );
        }

        // Start caching vectors and dot products for the BFGS update...
        // this notation is taken from Wikipedia
        Vector gamma = this.gradient.minus( gradientOld );
        Vector delta = xnew.minus( xold );
       
        // If we've converged on zero slope, then we're done!
        if( MinimizationStoppingCriterion.convergence(
            xnew, fnew, this.gradient, delta, this.getTolerance() ) )
        {
            return false;
        }
        
        // Call the particular Quasi-Newton update rule
        this.updateHessianInverse( this.hessianInverse, delta, gamma );
        this.lineFunction.setDirection(
            this.hessianInverse.times( this.gradient ).scale( -1.0 ) );

        return true;
    }

    /**
     * The step that makes BFGS/DFP/SR1 different from each other.  This
     * embodies the specific Quasi-Newton update step
     * @param hessianInverse 
     * Current estimate of the Hessian inverse.  Must be modified!
     * @param delta
     * Change in the search points (xnew-xold)
     * @param gamma
     * Change in the gradients (gnew-gold)
     * @return
     * True if Hessian was modified, false otherwise (due to numerical 
     * instability, etc.)
     */
    protected abstract boolean updateHessianInverse(
        Matrix hessianInverse,
        Vector delta,
        Vector gamma );
    
    @Override
    protected void cleanupAlgorithm()
    {
    }
    
    /**
     * Getter for lineMinimizer
     * @return
     * Work-horse algorithm that minimizes the function along a direction
     */
    public LineMinimizer<?> getLineMinimizer()
    {
        return this.lineMinimizer;
    }

    /**
     * Setter for lineMinimizer
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public void setLineMinimizer(
        LineMinimizer<?> lineMinimizer )
    {
        this.lineMinimizer = lineMinimizer;
    }

}
