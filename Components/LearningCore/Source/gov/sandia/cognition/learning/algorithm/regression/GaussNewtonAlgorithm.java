/*
 * File:                GaussNewtonAlgorithm.java
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
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.MinimizationStoppingCriterion;
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToDifferentiableScalarFunction;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeFree;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of the Gauss-Newton parameter-estimation procedure.
 * Please do not use this method, as it is extremely unstable, and really only
 * for demonstration purposes only.
 * <BR><BR>
 * Instead, use the Fletcher-Xu hybrid method.
 * @see FletcherXuHybridEstimation
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReference(
    author="Wikipedia",
    title="Gauss-Newton algorithm",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Gauss%E2%80%93Newton_algorithm"
)
public class GaussNewtonAlgorithm 
    extends LeastSquaresEstimator
{

    /**
     * Default line minimizer, LineMinimizerDerivativeBased.
     */
    public static final LineMinimizer<?> DEFAULT_LINE_MINIMIZER =
        new LineMinimizerDerivativeFree();
    
    /**
     * Workhorse algorithm that finds the minimum along a particular direction
     */
    private LineMinimizer<?> lineMinimizer;

    /** 
     * Creates a new instance of GaussNewtonAlgorithm 
     */
    public GaussNewtonAlgorithm()
    {
        this( ObjectUtil.cloneSafe(DEFAULT_LINE_MINIMIZER) );
    }

    /**
     * Creates a new instance of GaussNewtonAlgorithm
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     */
    public GaussNewtonAlgorithm(
        LineMinimizer<?> lineMinimizer )
    {
        this( lineMinimizer, 2*DEFAULT_MAX_ITERATIONS, 1e-2*DEFAULT_TOLERANCE );
    }

    /**
     * Creates a new instance of GaussNewtonAlgorithm
     * @param lineMinimizer
     * Workhorse algorithm that finds the minimum along a particular direction
     * @param maxIterations
     * Maximum number of iterations
     * @param tolerance
     * Tolerance before stopping.
     */
    public GaussNewtonAlgorithm(
        LineMinimizer<?> lineMinimizer,
        int maxIterations,
        double tolerance )
    {
        super( maxIterations, tolerance );
        this.setLineMinimizer(lineMinimizer);
    }

    /**
     * Function that maps a Evaluator<Vector,Double> onto a 
     * Evaluator<Double,Double> using a set point, direction and scale factor
     */
    private DirectionalVectorToDifferentiableScalarFunction lineFunction;

    @Override
    protected boolean initializeAlgorithm()
    {
        this.setResult( this.getObjectToOptimize().clone() );
        
        this.getCostFunction().setCostParameters( this.getData() );
        
        Vector parameters = this.getResult().convertToVector();
        
        SumSquaredErrorCostFunction.Cache cost =
            SumSquaredErrorCostFunction.Cache.compute(this.getResult(), this.getData() );

        ParameterDifferentiableCostMinimizer.ParameterCostEvaluatorDerivativeBased f =
            new ParameterDifferentiableCostMinimizer.ParameterCostEvaluatorDerivativeBased(
                this.getResult(), this.getCostFunction() );

        // Load up the line function with the current direction and
        // the search direction, which is the negative gradient, in other words
        // the direction of steepest descent
        this.lineFunction = new DirectionalVectorToDifferentiableScalarFunction(
            f, parameters, cost.Jte );
        
        return true;
    }

    /**
     * Maximum step norm allowed under a Gauss-Newton step, {@value}
     */
    public static final double STEP_MAX = 100.0;
    
    @Override
    protected boolean step()
    {
        SumSquaredErrorCostFunction.Cache cost = 
            SumSquaredErrorCostFunction.Cache.compute( this.getResult(), this.getData() );
        
        Vector lastParameters = this.lineFunction.getVectorOffset();
        Vector direction = cost.JtJ.solve(cost.Jte);
        double directionNorm = direction.norm2();
        if( directionNorm > STEP_MAX )
        {
            direction.scaleEquals( STEP_MAX / directionNorm );
        }
        
        this.lineFunction.setDirection( direction );
        InputOutputPair<Vector,Double> result = this.getLineMinimizer().minimizeAlongDirection(
            this.lineFunction, cost.parameterCost, cost.Jte );
        this.lineFunction.setVectorOffset( result.getInput() );
        
        this.setResultCost( result.getOutput() );
        
        Vector delta = result.getInput().minus( lastParameters );
        
        this.getResult().convertFromVector( result.getInput() );

        return !MinimizationStoppingCriterion.convergence( 
            result.getInput(), result.getOutput(), cost.Jte, delta, this.getTolerance() );
    }

    @Override
    protected void cleanupAlgorithm()
    {
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
        LineMinimizer<?> lineMinimizer)
    {
        this.lineMinimizer = lineMinimizer;
    }

}
