/*
 * File:                LevenbergMarquardtEstimation.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 13, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Implementation of the nonlinear regression algorithm, known as
 * Levenberg-Marquardt Estimation (or LMA).  In its pure form, this algorithm
 * computes the least-squares solution to the parameters of a functional form
 * given a (weighted) set of input-output Vector pairs.  While the algorithm
 * is specified in terms of parameter gradients, it has been proven that a
 * forward difference Jacobian approximation does not hurt the convergence
 * properties of LMA.  (Thus, you can use GradientDescendableApproximator with
 * impunity.)  LMA requires the storage of the explicit Jacobian, which may
 * not be possible for large problems.
 * <BR>
 * When gradients are available, LMA appears competitive with BFGS for function
 * evaluations, but BFGS is ~2 times faster than LMA.
 * <BR>
 * When gradients are not available, LMA slows down by a factor of ~3 and 
 * requires "M" more function evaluations, where "M" is the number of 
 * parameters in the function.  However, LMA with approximated
 * parameter Jacobian is ~4 times FASTER than Powell's minimization (and
 * Powell's method requires about ~8 times more function evaluations).
 * <BR>
 * Take home message: Use ParameterDifferentiableCostMinimizer with BFGS when
 * possible, or LMA with approximated Jacobian when not gradient information
 * is not available.
 * <BR>
 * Loosely based on Numerical Recipes in C, p.685-687
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReferences(
    references={
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
            pages={685,687},
            notes="Section 15.5",
            url="http://www.nrbook.com/a/bookcpdf.php"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Levenberg-Marquardt algorithm",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Levenberg-Marquardt_algorithm"
        )
    }
)
public class LevenbergMarquardtEstimation 
    extends LeastSquaresEstimator
{

    /**
     * Default initial value of the damping factor {@value}
     */
    public static final double DEFAULT_DAMPING = 1.0;

    /**
     * Divisor of the damping factor on unsuccessful iteration, dividing 
     * damping factor on cost-reducing iteration {@value}
     */
    public static final double DEFAULT_DAMPING_DIVISOR = 10.0;

    /**
     * Default maximum number of iterations without improvement before stopping {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS_WITHOUT_IMPROVEMENT = 4;
    
    /**
     * Number of sequential unsuccessful iterations without a cost-reducing step
     */
    private int iterationsWithoutImprovement;

    /**
     * Maximum number of iterations without improvement before stopping
     */
    private int maxIterationsWithoutImprovement;

    /**
     * Current damping factor for the ridge regression
     */
    private double dampingFactor;
    
    /**
     * Divisor of the damping factor on a successful iteration, must be greater
     * then 1.0, typically ~10.0
     */
    private double dampingFactorDivisor;
    
    /** 
     * Creates a new instance of LevenbergMarquardtEstimation 
     */
    public LevenbergMarquardtEstimation()
    {
        this( DEFAULT_DAMPING, DEFAULT_DAMPING_DIVISOR, 
            DEFAULT_MAX_ITERATIONS, DEFAULT_MAX_ITERATIONS_WITHOUT_IMPROVEMENT,
            DEFAULT_TOLERANCE );
    }
    
    /**
     * Creates a new instance of LevenbergMarquardtEstimation
     * @param dampingFactor
     * Current damping factor for the ridge regression
     * @param dampingFactorDivisor
     * Divisor of the damping factor on a successful iteration, must be greater
     * then 1.0, typically ~10.0
     * @param maxIterations
     * Maximum iterations before stopping
     * @param maxIterationsWithoutImprovement
     * Number of sequential unsuccessful iterations without a cost-reducing step
     * @param tolerance
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    public LevenbergMarquardtEstimation(
        double dampingFactor,
        double dampingFactorDivisor,
        int maxIterations,
        int maxIterationsWithoutImprovement,
        double tolerance )
    {
        super( maxIterations, tolerance );
        
        this.setDampingFactor(dampingFactor);
        this.setDampingFactorDivisor(dampingFactorDivisor);
        this.setMaxIterationsWithoutImprovement(maxIterationsWithoutImprovement);
    }
    
    /**
     * Parameters used to generate the lastCost
     */
    private Vector bestParameters;
    
    /**
     * Cost associated with lastParameters
     */
    private SumSquaredErrorCostFunction.Cache bestParametersCost;
        
    @Override
    protected boolean initializeAlgorithm()
    {
        this.setResult( this.getObjectToOptimize().clone() );
        this.setIterationsWithoutImprovement(0);
        this.bestParameters = this.getResult().convertToVector();
        this.bestParametersCost = SumSquaredErrorCostFunction.Cache.compute(
            this.getResult(), this.getData() );
        this.setResultCost( this.bestParametersCost.parameterCost );
        
        return true;
    }

    @Override
    protected boolean step()
    {
                
        // These negatives are to compensate because the derivative is the
        // negative of the error.  Since we're computing the derivative, but
        // the descent direction is the negative of the gradient, we need to
        // scale the matrices by -1.0
        Matrix JtJpI = this.bestParametersCost.JtJ.scale( -1.0 );
        Vector Jte = this.bestParametersCost.Jte;
        
        int M = JtJpI.getNumRows();
        for( int i = 0; i < M; i++ )
        {
            // Again, the damping factor is subtracted to compensate for the
            // negative between gradient and direction
            double v = JtJpI.getElement(i, i);
            JtJpI.setElement(i, i, v - this.getDampingFactor() );
        }
        
        // This is the ridge-regression (Tikhonov regularization) step to solve
        // for the parameter change
        Vector trialParameters = JtJpI.solve(Jte);
        trialParameters.plusEquals(this.bestParameters);
        
        // If the trial parameters reduce the cost, then accept them
        this.getResult().convertFromVector( trialParameters );
        SumSquaredErrorCostFunction.Cache trialCost = 
            SumSquaredErrorCostFunction.Cache.compute( this.getResult(), this.getData() ); 
        
        if( trialCost.parameterCost < this.bestParametersCost.parameterCost )
        {
            this.iterationsWithoutImprovement = 0;
            this.dampingFactor /= this.dampingFactorDivisor;
            this.bestParameters = trialParameters;
            
            // Test to see if we're done
            double delta = trialParameters.norm2() *
                (this.bestParametersCost.parameterCost - trialCost.parameterCost);
                
            this.bestParameters = trialParameters;
            this.bestParametersCost = trialCost;
            
            this.setResultCost( this.bestParametersCost.parameterCost );
            
            if( delta < this.getTolerance() )
            {
                return false;
            }
            
        }
        
        // The trial parameters aren't better than the best parameters,
        // so increase the regularization damping factor
        else
        {
            this.dampingFactor *= this.dampingFactorDivisor;
            this.iterationsWithoutImprovement++;
        }
        
        if( this.iterationsWithoutImprovement > this.getMaxIterationsWithoutImprovement() )
        {
            return false;
        }
        
        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        // Make sure we've uploaded the best parameters into the function
        // We won't have necessarily have the best parameters uploaded if
        // we didn't finish on the "delta" convergence condition
        this.getResult().convertFromVector( this.bestParameters );
    }

    /**
     * Getter for iterationsWithoutImprovement
     * @return
     * Number of sequential unsuccessful iterations without a cost-reducing step
     */
    public int getIterationsWithoutImprovement()
    {
        return this.iterationsWithoutImprovement;
    }

    /**
     * Setter for iterationsWithoutImprovement
     * @param iterationsWithoutImprovement
     * Number of sequential unsuccessful iterations without a cost-reducing step
     */
    public void setIterationsWithoutImprovement(
        int iterationsWithoutImprovement )
    {
        this.iterationsWithoutImprovement = iterationsWithoutImprovement;
    }

    /**
     * Getter for maxIterationsWithoutImprovement
     * @return
     * Maximum number of iterations without improvement before stopping
     */
    public int getMaxIterationsWithoutImprovement()
    {
        return this.maxIterationsWithoutImprovement;
    }

    /**
     * Setter for maxIterationsWithoutImprovement
     * @param maxIterationsWithoutImprovement
     * Maximum number of iterations without improvement before stopping
     */
    public void setMaxIterationsWithoutImprovement(
        int maxIterationsWithoutImprovement )
    {
        this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
    }

    /**
     * Getter for dampingFactor
     * @return
     * Current damping factor for the ridge regression
     */
    public double getDampingFactor()
    {
        return this.dampingFactor;
    }

    /**
     * Setter for dampingFactor
     * @param dampingFactor
     * Current damping factor for the ridge regression
     */
    public void setDampingFactor(
        double dampingFactor )
    {
        this.dampingFactor = dampingFactor;
    }

    /**
     * Getter for dampingFactorDivisor
     * @return
     * Divisor of the damping factor on a successful iteration, must be greater
     * then 1.0, typically ~10.0
     */
    public double getDampingFactorDivisor()
    {
        return dampingFactorDivisor;
    }

    /**
     * Setter for dampingFactorDivisor
     * @param dampingFactorDivisor
     * Divisor of the damping factor on a successful iteration, must be greater
     * then 1.0, typically ~10.0
     */
    public void setDampingFactorDivisor(
        double dampingFactorDivisor )
    {
        this.dampingFactorDivisor = dampingFactorDivisor;
    }

}
