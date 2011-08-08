/*
 * File:                FunctionMinimizerConjugateGradient.java
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
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeBased;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToDifferentiableScalarFunction;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Conjugate gradient method is a class of algorithms for finding the
 * unconstrained local minimum of a nonlinear function.  CG algorithms find
 * the local minimum by using a line search algorithm along a "conjugate
 * gradient" direction using first-order (gradient) information. The particular 
 * approaches vary only slightly according to how the direction is updated.
 * However, in my experience, the Liu-Storey CG variant
 * (FunctionMinimizerLiuStorey) performs the best.
 * <BR><BR>
 * All CG variants tend to require more function/gradient evaluations than
 * their Quasi-Newton cousins.  However, CG methods only require O(N) storage,
 * whereas Quasi-Newton algorithms (FunctionMinimizerQuasiNewton) require
 * O(N*N) storage, where N is the size of the input space.  So, if your input
 * space is large, then CG algorithms may be the method of choice.  In any 
 * case, the Liu-Storey CG variant tends to perform fairly well compared to the
 * best Quasi-Newton algorithms, BFGS in particular.
 * 
 * @see FunctionMinimizerQuasiNewton
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
            pages={80,87},
            notes="Section 4.1"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Nonlinear conjugate gradient method",
            type=PublicationType.WebPage,
            url="http://en.wikipedia.org/wiki/Nonlinear_conjugate_gradient_method",
            year=2008
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
            pages={423,424},
            notes="Section 10.6",
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
    }
)
public abstract class FunctionMinimizerConjugateGradient 
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
     * Test for convergence on change in x, {@value}
     */
    private static final double TOLERANCE_DELTA_Y = 1e-10;
    
    /**
     * Default line minimization algorithm, LineMinimizerDerivativeBased
     */
    public static final LineMinimizer<?> DEFAULT_LINE_MINIMIZER =
        new LineMinimizerDerivativeBased();
//        new LineMinimizerDerivativeFree();

    /**
     * Work-horse algorithm that minimizes the function along a direction
     */
    private LineMinimizer<?> lineMinimizer;    
    
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
    public FunctionMinimizerConjugateGradient(
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
    protected DirectionalVectorToDifferentiableScalarFunction lineFunction;

    /**
     * Gradient at the current guess
     */
    private Vector gradient;

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

    @Override
    protected boolean initializeAlgorithm()
    {
        this.result = new DefaultInputOutputPair<Vector, Double>(
            this.initialGuess, this.data.evaluate( this.initialGuess ) );

        this.gradient = this.data.differentiate( this.initialGuess );
        
        this.lineFunction = new DirectionalVectorToDifferentiableScalarFunction(
            this.data, this.initialGuess, this.gradient.scale(-1.0) );
        
        return true;
    }

    @Override
    protected boolean step()
    {

        // Rename the function "this.data" to "f" to minimize confusion.
        DifferentiableEvaluator<? super Vector, ? extends Double, Vector> f = this.data;
        
        Vector xold = this.result.getInput();
        
        // Find the minimum along this search direction
        this.result = this.lineMinimizer.minimizeAlongDirection(
            this.lineFunction, this.result.getOutput(), this.gradient );        

        Vector xnew = this.result.getInput();
        double fnew = this.result.getOutput();
        
        // Save off the previous gradient
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
            this.gradient = f.differentiate( xnew );
        }
        
        // Test for almost zero-gradient convergence
        if( MinimizationStoppingCriterion.convergence( 
            xnew, fnew, this.gradient, xnew.minus( xold ), this.getTolerance() ) )
        {
            return false;
        }
        
        double beta;

        // This is how often we will reset the search direction and 
        // re-initialize it as the direction of steepest descent
        int resetPeriod = this.gradient.getDimensionality()*2;
        if( ((this.getIteration()+1) % resetPeriod) == 0 )
        {
            beta = 0.0;
        }
        else
        {
            beta = this.computeScaleFactor( this.gradient, gradientOld );
        }
        
        Vector newDirection = 
            this.lineFunction.getDirection().scale( beta ).minus( this.gradient );
        this.lineFunction.setDirection( newDirection );
        this.lineFunction.setVectorOffset( xnew );
        
        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
    }    
    
    /**
     * Computes the conjugate gradient parameter for the particular update
     * scheme.
     * @param gradientCurrent
     * Gradient at the current evaluation point
     * @param gradientPrevious
     * Gradient at the previous evaluation point
     * @return
     * "beta" scale factor
     */
    protected abstract double computeScaleFactor(
        Vector gradientCurrent,
        Vector gradientPrevious );
    
}
