/*
 * File:                FletcherXuHybridEstimation.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 4, 2008, Sandia Corporation.
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
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerBFGS;
import gov.sandia.cognition.learning.algorithm.minimization.MinimizationStoppingCriterion;
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToDifferentiableScalarFunction;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeFree;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The Fletcher-Xu hybrid estimation for solving the nonlinear least-squares
 * parameters. The FX method is a hybrid between the BFGS Quasi-Newton method
 * and the Gauss-Newton minimization procedure.  We have slightly modified the
 * original algorithm to choose between BFGS and Levenberg-Marquardt (Tikhonov
 * regularization or ridge regression) update formulae, as the LMA update is
 * more stable than Gauss-Newton and produces better results on my test battery.
 * <BR><BR>
 * The motivation behind FX hybrid is that Gauss-Newton (and
 * Levenberg-Marquardt) has quadratic convergence properties, but has linear
 * convergence when the parameters are far from optimal.  BFGS always
 * demonstrates superlinear convergence, even on nonoptimal parameter sets.
 * The FX hybrid attempts to use BFGS when the solutions are far from optimal,
 * but switches to Gauss-Newton (Levenberg-Marquardt in our implementation)
 * when its quadratic convergence properties can be brought to bear.
 * <BR><BR>
 * Generally speaking, FX hybrid is the most efficient and effective
 * parameter-estimation procedure I know of.  However, FX requires the storage
 * of an estimated Hessian inverse, which is O(N*N) space, where "N" is the
 * number of parameters to estimate.
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
            pages={116,117},
            notes="Section 6.1 motivates the algorithm w.r.t. Gauss-Newton, BFGS, and Levenberg-Marquardt"
        )
        ,
        @PublicationReference(
            author={
                "R. Fletcher",
                "C. Xu"
            },
            title="Hybrid Methods for Nonlinear Least Squares",
            type=PublicationType.Journal,
            year=1987,
            pages={371,389},
            publication="Institute of Mathematics and its Applications Journal of Numerical Analysis"
        )
    }
)    
public class FletcherXuHybridEstimation 
    extends LeastSquaresEstimator
{
    
    /**
     * Reduction test for Equation 6.1.16 in Fletcher PMOO, 0.2 given on page 117.
     * Lower values result in more Gauss-Newton steps, larger values mean more
     * BFGS steps (1.0), default is {@value}
     * In my test battery, 0.2 also performs the best.
     */
    public static final double DEFAULT_REDUCTION_TEST = 0.2;
    
    /**
     * Divisor of the damping factor on unsuccessful iteration, dividing 
     * damping factor on cost-reducing iteration {@value}
     */
    public static final double DEFAULT_DAMPING_DIVISOR = 2.0;    

    /**
     * Default line minimization algorithm, LineMinimizerDerivativeFree
     */
    public static final LineMinimizer<?> DEFAULT_LINE_MINIMIZER =
        new LineMinimizerDerivativeFree();    
    
    /**
     * Workhorse algorithm that finds the minimum along a particular direction
     */
    private LineMinimizer<?> lineMinimizer;    
    
    /**
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     */
    private double reductionTest;    
    
    /**
     * Amount to modify the damping factor, typically 2.0 or 10.0
     */
    private double dampingFactorDivisor;    
    
    /** 
     * Creates a new instance of FletcherXuHybridEstimation 
     */
    public FletcherXuHybridEstimation()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_LINE_MINIMIZER ) );
    }
    
    /**
     * Creates a new instance of FletcherXuHybridEstimation
     * @param lineMinimizer
     */
    public FletcherXuHybridEstimation(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, DEFAULT_REDUCTION_TEST );
    }
    
    /**
     * Creates a new instance of FletcherXuHybridEstimation
     * 
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     * @param reductionTest
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     */
    public FletcherXuHybridEstimation(
        LineMinimizer<?> lineMinimizer,
        double reductionTest )
    {
        this( lineMinimizer, reductionTest, DEFAULT_DAMPING_DIVISOR );
    }
    
    /**
     * Creates a new instance of FletcherXuHybridEstimation
     * 
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     * @param reductionTest
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     * @param dampingFactorDivisor
     * Amount to modify the damping factor, typically 2.0 or 10.0
     */
    public FletcherXuHybridEstimation(
        LineMinimizer<?> lineMinimizer,
        double reductionTest,
        double dampingFactorDivisor )
    {
        this( lineMinimizer, reductionTest, dampingFactorDivisor,
            DEFAULT_MAX_ITERATIONS, DEFAULT_TOLERANCE );
    }
    
    /**
     * Creates a new instance of FletcherXuHybridEstimation
     * 
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     * @param reductionTest
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     * @param dampingFactorDivisor
     * Amount to modify the damping factor, typically 2.0 or 10.0
     * @param maxIterations
     * Maximum number of iterations before stopping
     * @param tolerance
     * Tolerance of the algorithm.
     */
    public FletcherXuHybridEstimation(
        LineMinimizer<?> lineMinimizer,
        double reductionTest,
        double dampingFactorDivisor,
        int maxIterations,
        double tolerance )
    {
        super( maxIterations, tolerance );
        this.setLineMinimizer( lineMinimizer );
        this.setReductionTest( reductionTest );
        this.setDampingFactorDivisor( dampingFactorDivisor );
    }
        

    /**
     * Last value of the parameter cost
     */
    private SumSquaredErrorCostFunction.Cache lastCost;
    
    /**
     * Function that maps a Evaluator<Vector,Double> onto a 
     * Evaluator<Double,Double> using a set point, direction and scale factor
     */
    private DirectionalVectorToDifferentiableScalarFunction lineFunction;    
    
    /**
     * Estimated inverse of the Hessian (second derivative)
     */
    private Matrix hessianInverse;
    
    @Override
    protected boolean initializeAlgorithm()
    {
        this.setResult( this.getObjectToOptimize().clone() );
        this.getCostFunction().setCostParameters( this.getData() );
        this.dampingFactor = 1.0;
        
        this.lastCost = SumSquaredErrorCostFunction.Cache.compute( 
            this.getResult(), this.getData() );
        
        ParameterDifferentiableCostMinimizer.ParameterCostEvaluatorDerivativeBased f =
            new ParameterDifferentiableCostMinimizer.ParameterCostEvaluatorDerivativeBased(
                this.getResult(), this.getCostFunction() );
        
        // Load up the line function with the current direction and
        // the search direction, which is the negative gradient, in other words
        // the direction of steepest descent
        Vector parameters = this.getResult().convertToVector();
        int M = parameters.getDimensionality();        
        this.lineFunction = new DirectionalVectorToDifferentiableScalarFunction(
            f, parameters, this.lastCost.Jte );
        
        this.hessianInverse = 
            MatrixFactory.getDefault().createIdentity( M, M ).scale( 0.5 );
        
        return true;
    }

    /**
     * Damping factor for the Levenberg-Marquardt ridge regression
     */
    private double dampingFactor;
    
    @Override
    protected boolean step()
    {
        InputOutputPair<Vector,Double> result = this.getLineMinimizer().minimizeAlongDirection(
            this.lineFunction, this.lastCost.parameterCost, this.lastCost.Jte );
     
        Vector lastParameters = this.lineFunction.getVectorOffset();
        
        Vector delta = result.getInput().minus( lastParameters );
        this.lineFunction.setVectorOffset( result.getInput() );
        this.getResult().convertFromVector( result.getInput() );
            
        // If the trial parameters reduce the cost, then accept them
        SumSquaredErrorCostFunction.Cache cache = 
            SumSquaredErrorCostFunction.Cache.compute(
                this.getResult(), this.getData() );

        this.setResultCost( cache.parameterCost );
        
        // Equation 6.1.16 in Fletcher PMOO
        if( this.getReductionTest()*this.lastCost.parameterCost <=
            (this.lastCost.parameterCost - cache.parameterCost) )
        {
            // On my test battery, taking a Levenberg-Marquardt step performs
            // better here than a Gauss-Newton step.
            // I've also tried pretty much every variant on these parameters,
            // and this is what came out the best.  I've also tried:
            // Gauss-Newton: direction = cache.JtJ.solve(Jte);
            // Steepest descent: direction = Jte;
            Matrix JtJpI = cache.JtJ.scale(-1.0);
            Vector Jte = cache.Jte;
            
            int M = JtJpI.getNumRows();
            for( int i = 0; i < M; i++ )
            {
                // Again, the damping factor is subtracted to compensate for the
                // negative between gradient and direction
                double v = JtJpI.getElement(i, i);
                JtJpI.setElement(i, i, v - this.dampingFactor );
            }
            
            // This is the ridge-regression (Tikhonov regularization) step to solve
            // for the parameter change
            Vector direction = JtJpI.solve(Jte);
            this.dampingFactor /= this.getDampingFactorDivisor();
            
            double directionNorm = direction.norm2();
            if( directionNorm > GaussNewtonAlgorithm.STEP_MAX )
            {
                direction.scaleEquals( GaussNewtonAlgorithm.STEP_MAX / directionNorm );
            }
            
            // Take a Levenber-Marquardt step
            this.lineFunction.setDirection( direction );
        }
        else
        {
            // Suggested by Fletcher, PMOO p.117, Equation 6.1.15
            // I've modified it slightly, the last term sould be:
            // cache.J.transpose().minus( lastCost.J ).times( cache.error )
            // However, I don't cache the errors individually, so this is
            // as close as I can get... It seems to be very effective.
            Vector gamma = cache.JtJ.times( this.lineFunction.getDirection().scale( 2.0 ) ).plus(
                cache.Jte.minus( lastCost.Jte ) );
            FunctionMinimizerBFGS.BFGSupdateRule( 
                this.hessianInverse, delta, gamma, this.getTolerance() );        
            
            // Take a BFGS step
            Vector direction = this.hessianInverse.times( cache.Jte );
            this.lineFunction.setDirection( direction );
            
            this.dampingFactor *= this.getDampingFactorDivisor();
        }
        
        this.lastCost = cache;
        
        return !MinimizationStoppingCriterion.convergence( 
            result.getInput(), result.getOutput(), delta, cache.Jte, this.getTolerance() );
    }

    @Override
    protected void cleanupAlgorithm()
    {
    }

    /**
     * Getter for reduction test.
     * @return
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     */
    public double getReductionTest()
    {
        return this.reductionTest;
    }

    /**
     * Setter for reduction test.
     * @param reductionTest
     * Reduction test for switching between BFGS and Levenberg-Marquardt, must
     * be [0,1].  Lower values result in more Levenberg-Marquardt steps,
     * larger values result in more BFGS steps.
     */
    public void setReductionTest(
        double reductionTest )
    {
        if( (reductionTest < 0.0) ||
            (reductionTest > 1.0) )
        {
            throw new IllegalArgumentException( "reductionTest must be [0,1]" );
        }
        this.reductionTest = reductionTest;
    }

    /**
     * Getter for dampingFactorDivisor
     * @return
     * Amount to modify the damping factor, typically 2.0 or 10.0
     */
    public double getDampingFactorDivisor()
    {
        return this.dampingFactorDivisor;
    }
    
    /**
     * Setter for dampingFactorDivisor
     * @param dampingFactorDivisor
     * Amount to modify the damping factor, typically 2.0 or 10.0
     */
    public void setDampingFactorDivisor(
        double dampingFactorDivisor )
    {
        this.dampingFactorDivisor = dampingFactorDivisor;
    }

    /**
     * Getter for lineMinimizer
     * @return
     * Workhorse algorithm that finds the minimum along a particular direction
     */
    public LineMinimizer<?> getLineMinimizer()
    {
        return this.lineMinimizer;
    }

    /**
     * Setter for lineMinimizer
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     */
    public void setLineMinimizer(
        LineMinimizer<?> lineMinimizer )
    {
        this.lineMinimizer = lineMinimizer;
    }

}
