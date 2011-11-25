/*
 * File:                ParallelizedCostFunctionContainer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.SequentialDataMultiPartitioner;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A cost function that automatically splits a ParallelizableCostFunction
 * across multiple cores/processors to speed up computation.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class ParallelizedCostFunctionContainer
    extends AbstractSupervisedCostFunction<Vector,Vector>
    implements DifferentiableCostFunction,
    ParallelAlgorithm
{
    
    /**
     * Cost function to parallelize
     */
    private ParallelizableCostFunction costFunction;

    /**
     * Collection of evaluation thread calls
     */
    private transient ArrayList<Callable<Object>> evaluationComponents;
    
    /**
     * Collection of evaluation gradient calls
     */
    private transient ArrayList<Callable<Object>> gradientComponents;
    
    /**
     * Thread pool used to parallelize the computation
     */
    private transient ThreadPoolExecutor threadPool;

    /**
     * Default constructor for ParallelizedCostFunctionContainer.
     */
    public ParallelizedCostFunctionContainer()
    {
        this( (ParallelizableCostFunction) null );
    }
    
    /**
     * Creates a new instance of ParallelizedCostFunctionContainer
     * @param costFunction
     * Cost function to parallelize
     */
    public ParallelizedCostFunctionContainer(
        ParallelizableCostFunction costFunction )
    {
        this( costFunction, ParallelUtil.createThreadPool() );
    }
    
    /**
     * Creates a new instance of ParallelizedCostFunctionContainer
     * @param threadPool 
     * Thread pool used to parallelize the computation
     * @param costFunction
     * Cost function to parallelize
     */
    public ParallelizedCostFunctionContainer(
        ParallelizableCostFunction costFunction,
        ThreadPoolExecutor threadPool )
    {
        this.setCostFunction( costFunction );
        this.setThreadPool( threadPool );
    }       
    
    @Override
    public ParallelizedCostFunctionContainer clone()
    {
        ParallelizedCostFunctionContainer clone =
            (ParallelizedCostFunctionContainer) super.clone();
        clone.setCostFunction( ObjectUtil.cloneSafe( this.getCostFunction() ) );
        clone.setThreadPool(
            ParallelUtil.createThreadPool( this.getNumThreads() ) );
        return clone;
    }    
    
    /**
     * Getter for costFunction
     * @return
     * Cost function to parallelize
     */
    public ParallelizableCostFunction getCostFunction()
    {
        return this.costFunction;
    }
    
    /**
     * Setter for costFunction
     * @param costFunction
     * Cost function to parallelize
     */
    public void setCostFunction(
        ParallelizableCostFunction costFunction )
    {
        this.costFunction = costFunction;
        this.evaluationComponents = null;
        this.gradientComponents = null;
    }
    
    /**
     * Splits the data across the numComponents cost functions
     */
    protected void createPartitions()
    {
        int numThreads = this.getNumThreads();
        ArrayList<ArrayList<InputOutputPair<? extends Vector, Vector>>> partitions =
            SequentialDataMultiPartitioner.create(
                this.getCostParameters(), numThreads );
        this.evaluationComponents = new ArrayList<Callable<Object>>( numThreads );
        this.gradientComponents = new ArrayList<Callable<Object>>( numThreads );
        for( int i = 0; i < numThreads; i++ )
        {
            ParallelizableCostFunction subcost =
                (ParallelizableCostFunction) this.getCostFunction().clone();
            subcost.setCostParameters( partitions.get(i) );
            this.evaluationComponents.add( new SubCostEvaluate( subcost, null ) );
            this.gradientComponents.add( new SubCostGradient( subcost, null ) );
        }
        
    }

    @Override
    public void setCostParameters(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> costParameters )
    {
        super.setCostParameters( costParameters );
        this.evaluationComponents = null;
        this.gradientComponents = null;
    }
    
    @Override
    public Double evaluate(
        Evaluator<? super Vector, ? extends Vector> evaluator )
    {
        
        if( this.evaluationComponents == null )
        {
            this.createPartitions();
        }
        
        // Set the subtasks
        for( Callable<Object> sce : this.evaluationComponents )
        {
            ((SubCostEvaluate) sce).evaluator = evaluator;
        }
        
        Collection<Object> partialResults = null;
        try
        {
            partialResults = ParallelUtil.executeInParallel(
                this.evaluationComponents, this.getThreadPool() );
        }
        catch (Exception ex)
        {
            Logger.getLogger( ParallelizedCostFunctionContainer.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        return this.getCostFunction().evaluateAmalgamate( partialResults );
        
    }
    
    
    @Override
    public Double evaluatePerformance(
        Collection<? extends TargetEstimatePair<? extends Vector, ? extends Vector>> data )
    {
        return this.getCostFunction().evaluatePerformance( data );
    }

    public Vector computeParameterGradient(
        GradientDescendable function )
    {
        
        if (this.gradientComponents == null)
        {
            this.createPartitions();
        }

        // Create the subtasks
        for (Callable<Object> eval : this.gradientComponents)
        {
            ((SubCostGradient) eval).evaluator = function;
        }

        Collection<Object> results = null;
        try
        {
            results = ParallelUtil.executeInParallel(
                this.gradientComponents, this.getThreadPool() );
        }
        catch (Exception ex)
        {
            Logger.getLogger( ParallelizedCostFunctionContainer.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        return this.getCostFunction().computeParameterGradientAmalgamate( results );
        
    }

    public ThreadPoolExecutor getThreadPool()
    {
        if( this.threadPool == null )
        {
            this.setThreadPool( ParallelUtil.createThreadPool() );
        }
        
        return this.threadPool;
    }

    public void setThreadPool(
        ThreadPoolExecutor threadPool )
    {
        this.threadPool = threadPool;
    }

    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads( this );
    }
    
    /**
     * Creates the thread pool using the Foundry's global thread pool.
     */
    protected void createThreadPool()
    {
        this.setThreadPool( ParallelUtil.createThreadPool() );
    }

    /**
     * Callable task for the evaluate() method.
     */
    protected static class SubCostEvaluate
        implements Callable<Object>
    {
        
        /**
         * Parallel cost function
         */
        private ParallelizableCostFunction costFunction;
        
        /**
         * Evaluator for which to compute the cost
         */
        private Evaluator<? super Vector, ? extends Vector> evaluator;
        
        /**
         * Creates a new instance of SubCostEvaluate
         * @param costFunction
         * Parallel cost function
         * @param evaluator
         * Evaluator for which to compute the cost
         */
        public SubCostEvaluate(
            ParallelizableCostFunction costFunction,
            Evaluator<? super Vector, ? extends Vector> evaluator )
        {
            this.costFunction = costFunction;
            this.evaluator = evaluator;
        }

        public Object call()
        {
            return this.costFunction.evaluatePartial( this.evaluator );
        }
        
    }
    
    /**
     * Callable task for the computeGradient() method
     */
    protected static class SubCostGradient
        implements Callable<Object>
    {
        
        /**
         * Parallel cost function
         */
        private ParallelizableCostFunction costFunction;
        
        /**
         * Function for which to compute the gradient
         */
        private GradientDescendable evaluator;
        
        /**
         * Creates a new instance of SubCostGradient
         * @param costFunction
         * Parallel cost function
         * @param evaluator
         * Function for which to compute the gradient
         */
        public SubCostGradient(
            ParallelizableCostFunction costFunction,
            GradientDescendable evaluator )
        {
            this.costFunction = costFunction;
            this.evaluator = evaluator;
        }

        public Object call()
        {
            return this.costFunction.computeParameterGradientPartial( this.evaluator );
        }
        
    }

}
