/*
 * File:                FunctionMinimizerDirectionSetPowell.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToScalarFunction;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeFree;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the derivative-free unconstrained nonlinear direction-set
 * minimization algorithm called "Powell's Method" by Numerical Recipes.
 * The method was originally known as Smith's Direction-Set Method, to which
 * Powell made an ingenious improvement.  Powell's Method was later improved
 * upon by Brent.  This algorithm creates a basis set of search directions and
 * repeatedly searches along each direction until a local minimum is found
 * using only function evaluations, that is, no gradient information is needed.
 * This algorithm is amazingly good at finding a minimum, and is my method of
 * choice for derivative-free minimization.  However, be sure to check the
 * performance this algorithm's cousin, the Nelder-Mead downhill simplex
 * (FunctionMinimizerNelderMead) before deciding on a derivative-free
 * minimization algorithm.
 * <BR><BR>
 * That being said, it is sometimes more effective to use approximated
 * gradients and algorithms like BFGS (FunctionMinimizerBFGS) than
 * derivative-free minimization algorithms.  Thus, I would try them both.
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
            pages={87,90},
            notes="Section 4.2"
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
            pages={417,418},
            notes="Section 10.5",
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
    }
)
public class FunctionMinimizerDirectionSetPowell 
    extends AbstractAnytimeFunctionMinimizer<Vector, Double, Evaluator<? super Vector, Double>>
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
    private static final double TOLERANCE_DELTA_X = 1e-7;
    
    /**
     * Default line minimization algorithm, LineMinimizerDerivativeFree
     */
    public static final LineMinimizer<?> DEFAULT_LINE_MINIMIZER =
        new LineMinimizerDerivativeFree();

    /**
     * Work-horse algorithm that minimizes the function along a direction
     */
    private LineMinimizer<?> lineMinimizer;

    /**
     * Default constructor
     */
    public FunctionMinimizerDirectionSetPowell()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }

    /**
     * Creates a new instance of FunctionMinimizerDirectionSetPowell
     * @param lineMinimizer
     * Work-horse algorithm that minimizes the function along a direction
     */
    public FunctionMinimizerDirectionSetPowell(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );        
    }
    
    /**
     * Creates a new instance of FunctionMinimizerDirectionSetPowell
     * 
     * @param initialGuess Initial guess about the minimum of the method
     * @param tolerance Tolerance of the minimization algorithm, must be >= 0.0, typically ~1e-10
     * @param lineMinimizer 
     * Work-horse algorithm that minimizes the function along a direction
     * @param maxIterations Maximum number of iterations, must be >0, typically ~100
     */
    public FunctionMinimizerDirectionSetPowell(
        LineMinimizer<?> lineMinimizer,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( initialGuess, tolerance, maxIterations );
        this.setLineMinimizer( lineMinimizer );
    }

    /**
     * Matrix where the columns indicate the directions of minimization
     */
    private List<Vector> directionSet;    

    /**
     * Function that maps a Evaluator<Vector,Double> onto a 
     * Evaluator<Double,Double> using a set point, direction and scale factor
     */
    private DirectionalVectorToScalarFunction lineFunction;    
    
    @Override
    protected boolean initializeAlgorithm()
    {
        int numDimensions = this.initialGuess.getDimensionality();
        this.directionSet = new LinkedList<Vector>();
        for (int j = 0; j < numDimensions; j++)
        {
            Vector dj = VectorFactory.getDefault().createVector( numDimensions );
            dj.setElement( j, 1.0 );
            this.directionSet.add( dj );
        }
        
        double fx = this.data.evaluate( this.initialGuess );
        
        this.result = new DefaultInputOutputPair<Vector, Double>( this.initialGuess, fx );
        this.lineFunction =
            new DirectionalVectorToScalarFunction( this.data, null, null );
        
        return true;
    }

    @Override
    protected boolean step()
    {
        
        int bestIndex = -1;
        double bestDecrease = Double.POSITIVE_INFINITY;
        
        Vector xoriginal = this.result.getInput();
        double foriginal = this.result.getOutput();

        WeightedInputOutputPair<Vector,Double> lineResult;
        double scale;

        // In each iteration, loop over all direction in the set and
        // find the minimum along each direction
        // The "this.getKeepGoing()" part is if somebody calls "stop()" from
        // another thread while we're looping
        int index = 0;
        for( Vector direction : this.directionSet )
        {
            if( !this.getKeepGoing() )
            {
                return false;
            }
            Vector xold = this.result.getInput();
            double fold = this.result.getOutput();
            
            this.lineFunction.setVectorOffset( xold );
            this.lineFunction.setDirection( direction );
            lineResult = this.lineMinimizer.minimizeAlongDirection(
                this.lineFunction, fold, null);
            scale = lineResult.getWeight();
            this.result = lineResult;
            
            // Each "deltaj" should be <= 0.0 since the line search should
            // give us an fnew that is less than fold
            double fnew = this.result.getOutput();
            double deltaj = fnew - fold;
            if( bestDecrease > deltaj )
            {
                bestDecrease = deltaj;
                bestIndex = index;
            }
            
            // Let's scale the direction by the line-search distance
            if( scale != 0.0 )
            {
                direction.scaleEquals( scale );
            }
            index++;
        }

        Vector xnew = this.result.getInput();
        double fnew = this.result.getOutput();

        // This is the termination criterion
        if ((2.0 * Math.abs( foriginal - fnew )) <=
            (this.tolerance * (Math.abs( foriginal ) + Math.abs( fnew ))))
        {
            return false;
        }

        // Here's the new conjugate direction
        Vector direction = xnew.minus( xoriginal );

        // Fletcher says that we should perform a line search along the new
        // direction.  I find that this helps when appended the new direction
        // to the end.  If we're simply replacing the best direction with
        // the new direction, then this line search ends up costing us more
        // function evaluations.
        this.lineFunction.setVectorOffset( xnew );
        this.lineFunction.setDirection( direction );
        lineResult = this.lineMinimizer.minimizeAlongDirection(
            this.lineFunction, fnew, null);
        this.result = lineResult;
        scale = lineResult.getWeight();

        // See if we've converged on zero slope
        double maximumDelta = 0.0;
        Vector delta = this.result.getInput().minus( xoriginal );
        for( int i = 0; i < delta.getDimensionality(); i++ )
        {
            // Normalizing coefficient: max(|xi|, 1.0), so that we're always
            // reducing the values
            double normalizedX = Math.max( Math.abs(this.result.getInput().getElement(i)), 1.0 );
            double deltaX = Math.abs(delta.getElement(i)) / normalizedX;
            if( maximumDelta < deltaX )
            {
                maximumDelta = deltaX;
            }
        }
        
        if( maximumDelta < TOLERANCE_DELTA_X )
        {
            return false;
        }
        
        // Now we're going to replace the best direction with the conjugate
        // direction to ensure that we don't have increasing linear dependence        
        if( scale != 0.0 )
        {
            direction.scaleEquals( scale );
            this.directionSet.remove( bestIndex );
            this.directionSet.add( direction );
        }
        
        return true;
    }

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
