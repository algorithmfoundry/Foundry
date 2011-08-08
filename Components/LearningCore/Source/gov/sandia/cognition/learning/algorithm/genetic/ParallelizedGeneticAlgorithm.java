/*
 * File:                ParallelizedGeneticAlgorithm.java
 * Authors:             Christina Warrender
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 08, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.algorithm.ParallelAlgorithm;
import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.Reproducer;
import gov.sandia.cognition.learning.data.SequentialDataMultiPartitioner;
import gov.sandia.cognition.learning.function.cost.CostFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a parallel implementation of the genetic algorithm.  The
 * default is to use n-1 available cores/hyperthreads on a machine and spread
 * the cost function evaluations across these computational units.  
 *
 * @param   <GenomeType> Type of genome used to represent a single element in 
 *          the genetic population. For example, a {@code Vector}.
 * @param   <CostParametersType> Type of parameters that the cost function 
 *          takes. For example, {@code Collection<InputOutputPairs>}.
 * @author cewarr
 */
public class ParallelizedGeneticAlgorithm <CostParametersType, GenomeType>
    extends GeneticAlgorithm<CostParametersType, GenomeType>
    implements ParallelAlgorithm
{
    
    /**
     * Parallel tasks that evaluate genome fitness
     */
    private ArrayList<Callable<ArrayList<EvaluatedGenome<GenomeType>>>> evaluateTasks;
    
    /**
     * Thread pool used to parallelize the computation
     */
    private transient ThreadPoolExecutor threadPool;    
      
    /** 
     * Default constructor 
     */
    public ParallelizedGeneticAlgorithm()
    {
        this( null, null, null, null );
    }
    
    /**
     * @param initialPopulation The initial population to start the algorithm
     * @param reproducer The reproduction method to use.
     * @param cost The cost function for genomes.
     * @param threadPool Thread pool to use for parallelization
     */
    public ParallelizedGeneticAlgorithm(
        Collection<GenomeType> initialPopulation,
        Reproducer<GenomeType> reproducer,
        CostFunction<? super GenomeType, ? super CostParametersType> cost,
        ThreadPoolExecutor threadPool)
    {
        super(initialPopulation, reproducer, cost);
        this.setThreadPool( threadPool );
    }    
    
    /**
     * @param initialPopulation The initial population to start the algorithm
     * @param reproducer The reproduction method to use.
     * @param cost The cost function for genomes.
     * @param threadPool Thread pool to use for parallelization
     * @param maxIterations The maximum number of iterations to run.
     * @param maxIterationsWithoutImprovement The maximum number of iterations
     * to go without improvement before stopping.
     */
    public ParallelizedGeneticAlgorithm(
        Collection<GenomeType> initialPopulation,
        Reproducer<GenomeType> reproducer,
        CostFunction<? super GenomeType, ? super CostParametersType> cost,
        ThreadPoolExecutor threadPool,
        int maxIterations,
        int maxIterationsWithoutImprovement)
    {
        super(initialPopulation, reproducer, cost, maxIterations, 
                maxIterationsWithoutImprovement);
        this.setThreadPool( threadPool );
    }    
    
    /**
     * Getter for threadPool
     * @return
     * Thread pool used to parallelize the computation
     */
    public ThreadPoolExecutor getThreadPool()
    {
        return this.threadPool;
    }

    /**
     * Setter for threadPool
     * @param threadPool
     * Thread pool used to parallelize the computation
     */
    public void setThreadPool(
        ThreadPoolExecutor threadPool )
    {
        this.threadPool = threadPool;
    }

    
    /**
     * Getter for #threads
     * @return
     * #threads 
     */
    public int getNumThreads()
    {
        return ParallelUtil.getNumThreads( this );
    }
    
    /**
     * Creates the evaluation tasks to execute in parallel.
     *
     * @param   population
     *      The population to create tasks for.
     */
    protected void createEvaluationTasks( Collection<GenomeType> population )
    {
        if( this.getThreadPool() == null )
        {
            this.setThreadPool( ParallelUtil.createThreadPool() );
        }
        
        int numThreads = this.getNumThreads();
                
        ArrayList<ArrayList<GenomeType>> partitions =
            SequentialDataMultiPartitioner.create( 
            population, numThreads );
        
        this.evaluateTasks = new 
                ArrayList<Callable<ArrayList<EvaluatedGenome<GenomeType>>>>( numThreads );
        for( int i = 0; i < numThreads; i++ )
        {
            this.evaluateTasks.add( new EvaluateGenome( partitions.get( i ) ) );
        }
        
    }

    /**
     * Converts a population of genomes into evaluated genomes.
     *
     * @param population The population of genomes to evaluate.
     * @return A population of evaluated genomes.
     */
    @Override
    protected ArrayList<EvaluatedGenome<GenomeType>> evaluatePopulation(
        Collection<GenomeType> population)
    {
        if (population == null)
            return null;
        
        // create tasks to evaluate population in parallel
        this.createEvaluationTasks( population );

        // Convert the Genome to an EvaluatedGenome.
        ArrayList<EvaluatedGenome<GenomeType>> evaluatedPopulation =
            new ArrayList<EvaluatedGenome<GenomeType>>(population.size());
        
        try
        {
            // Execute the evaluations in parallel
            List<Future<ArrayList<EvaluatedGenome<GenomeType>>>> results = 
                    this.getThreadPool().invokeAll( this.evaluateTasks );
            for( Future<ArrayList<EvaluatedGenome<GenomeType>>> result : results )
            {
                evaluatedPopulation.addAll( 0, result.get() );
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger( ParallelizedGeneticAlgorithm.class.getName() ).log( Level.SEVERE, null, ex );
        }
 

        return evaluatedPopulation;
    }



   
    /**
     * Callable task for the evaluate() method.
     */
    protected class EvaluateGenome
        implements Callable<ArrayList<EvaluatedGenome<GenomeType>>>
    {
        
        private Collection<GenomeType> population;
        /**
         * Creates a new instance of EvaluateGenome
         * @param population 
         * Population to evaluate
         */
        public EvaluateGenome(
            ArrayList<GenomeType> population )
        {
            this.population = population;
        }

        public ArrayList<EvaluatedGenome<GenomeType>> call()
        {
            return ParallelizedGeneticAlgorithm.super.evaluatePopulation(this.population);
        }
        
    }

}