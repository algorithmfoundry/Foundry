/*
 * File:                GeneticAlgorithm.java
 * Authors:             Jonathan McClain, Justin Basilico, and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.Reproducer;
import gov.sandia.cognition.learning.algorithm.BatchCostMinimizationLearner;
import gov.sandia.cognition.learning.function.cost.CostFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The GeneticAlgorithm class implements a generic genetic algorithm
 * that uses a given cost function to minimize and a given reproduction 
 * function for generating the population.
 * <BR><BR>
 * A GA maintains a population of potential solutions to the minimization
 * problem, each with an associated cost-function score.  The members of the
 * population are called "genomes".  The genomes are allowed to "reproduce"
 * based on their cost-function scores to produce the next "generation" of 
 * genomes.  We always carry over the all-time lowest-cost genome to the next
 * generation.  Notice that GAs have gone slightly overboard with the
 * evolutionary metaphor?  Me too.
 * <BR><BR>
 * GAs can be a very effective approach to finding the minimum-cost parameter
 * set of a function for a cost function.  However, GAs are generally regarded
 * as a method of last resort, being superior to only Simulated Annealing
 * (please read my opinion/polemic/diatribe on Simulated Annealing).
 * <BR><BR>
 * Here's a recap of that comment:
 * Instead of GAs, try Powell's method, which is a powerful minimization 
 * technique that only relies on function evaluations.  Generally better than 
 * Powell's method is Conjugate Gradient with automated gradient approximation, 
 * which only relies on function evaluations and automatically estimates the 
 * gradient for you.
 * <BR><BR>
 * If you can store the (approximated) Jacobian in memory, then the best 
 * technique is usually BFGS with automated gradient approximation (sometimes
 * Levenberg-Marquardt Estimation is as good as BFGS, but usually not).
 *
 * @param   <GenomeType> Type of genome used to represent a single element in 
 *          the genetic population. For example, a {@code Vector}.
 * @param   <CostParametersType> Type of parameters that the cost function 
 *          takes. For example, {@code Collection<InputOutputPairs>}.
 * @author  Jonathan McClain
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   1.0
 * @see     gov.sandia.cognition.learning.algorithm.annealing.SimulatedAnnealer
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments={
                "I don't much like the constructors for this class, but it's probably not worth changing at this point.",
                "Made some cosmetic changes to the code.",
                "Added previous code review as CodeReview annotation",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-05",
            changesNeeded=false,
            comments={
                "Cleaned up the code a little.",
                "Made the constructor initialize the variables."
            }
        )
    }
)
public class GeneticAlgorithm<CostParametersType, GenomeType>
    extends AbstractAnytimeBatchLearner<CostParametersType, GenomeType>
    implements BatchCostMinimizationLearner<CostParametersType, GenomeType>,
        MeasurablePerformanceAlgorithm
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;

    /** The cost function for genomes. */
    private CostFunction<? super GenomeType, ? super CostParametersType> 
        costFunction;

    /** The reproduction function for genomes. */
    private Reproducer<GenomeType> reproducer;

    /** The best genome found so far. */
    private EvaluatedGenome<GenomeType> bestSoFar;

    /** The maximum number of iterations to go without improvement before 
     *  stopping */
    private int maxIterationsWithoutImprovement;

    /** The number of iterations since the last improvement. */
    private int iterationsWithoutImprovement;

    /** The population of genomes. */
    private Collection<EvaluatedGenome<GenomeType>> population;

    /** The initial population of genomes */
    private Collection<GenomeType> initialPopulation;

    // TODO: Make these constructors easier to use, perhaps giving a simple
    // default constructor -- krdixon, 2008-07-23
    
    /**
     * Creates a new instance of GeneticAlgorithm.
     * 
     * @param cost The cost function for genomes.
     * @param initialPopulation The initial population to start the algorithm
     * @param reproducer The reproduction method to use.
     */
    public GeneticAlgorithm(
        Collection<GenomeType> initialPopulation,
        Reproducer<GenomeType> reproducer,
        CostFunction<? super GenomeType, ? super CostParametersType> cost)
    {
        this(initialPopulation, reproducer, cost, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of GeneticAlgorithm.
     * 
     * @param cost The cost function for genomes.
     * @param initialPopulation The initial population to start the algorithm
     * @param reproducer The reproduction method to use.
     * @param maxIterations The maximum number of iterations to run.
     */
    public GeneticAlgorithm(
        Collection<GenomeType> initialPopulation,
        Reproducer<GenomeType> reproducer,
        CostFunction<? super GenomeType, ? super CostParametersType> cost,
        int maxIterations)
    {
        this(initialPopulation, reproducer, cost, maxIterations,
            1 + maxIterations / 10);
    }

    /**
     * Creates a new instance of GeneticAlgorithm.
     * 
     * @param cost The cost function for genomes.
     * @param initialPopulation The initial population to start the algorithm
     * @param reproducer The reproduction method to use.
     * @param maxIterations The maximum number of iterations to run.
     * @param maxIterationsWithoutImprovement The maximum number of iterations
     * to go without improvement before stopping.
     */
    public GeneticAlgorithm(
        Collection<GenomeType> initialPopulation,
        Reproducer<GenomeType> reproducer,
        CostFunction<? super GenomeType, ? super CostParametersType> cost,
        int maxIterations,
        int maxIterationsWithoutImprovement)
    {
        super(maxIterations);

        this.setCostFunction(cost);
        this.setReproducer(reproducer);
        this.setBestSoFar(null);
        this.setMaxIterationsWithoutImprovement(maxIterationsWithoutImprovement);
        this.setIterationsWithoutImprovement(0);

        this.setPopulation(null);
        this.setInitialPopulation(initialPopulation);
    }

    protected boolean initializeAlgorithm()
    {
        // Set the initial population.
        if (this.getPopulation() == null)
        {
            this.setPopulation(
                this.evaluatePopulation(this.getInitialPopulation()));
        }

        // Set the best found so far.
        this.setBestSoFar(
            this.searchForBetter(this.getBestSoFar(), this.getPopulation()));

        // Reset the iteration counters.
        this.setIteration(0);
        this.setIterationsWithoutImprovement(0);

        return true;
    }

    protected boolean step()
    {

        // Produce the new population.
        this.setPopulation(this.evaluatePopulation(
            this.getReproducer().reproduce(this.getPopulation())));

        // Look for better genomes in this population.
        EvaluatedGenome<GenomeType> best =
            this.searchForBetter(this.getBestSoFar(), this.getPopulation());

        // Check to see if we have improved.
        if (best.getCost() < this.getBestSoFar().getCost())
        {
            // We have improved, so reset.
            this.setBestSoFar(best);
            this.setIterationsWithoutImprovement(0);
        }
        else
        {
            this.setIterationsWithoutImprovement(
                this.getIterationsWithoutImprovement() + 1);
        }

        return (this.getIterationsWithoutImprovement() <=
            this.getMaxIterationsWithoutImprovement());

    }

    protected void cleanupAlgorithm()
    {
    }

    public GenomeType getResult()
    {
        return (this.getBestSoFar() != null) ? this.getBestSoFar().getGenome() : null;
    }    
    
    /**
     * Searches the provided population of genomes for one whose cost is lower
     * than the provided best so far genome.
     *
     * @param bestSoFar The genome to compare to.
     * @param population The population to search.
     * @return The best genome that was found. Returns the original if no better
     * were found.
     */
    protected EvaluatedGenome<GenomeType> searchForBetter(
        EvaluatedGenome<GenomeType> bestSoFar,
        Collection<EvaluatedGenome<GenomeType>> population)
    {
        // Find the best genome so far.
        EvaluatedGenome<GenomeType> currentBest = bestSoFar;
        for (EvaluatedGenome<GenomeType> evaluatedGenome : population)
        {
            if (currentBest == null ||
                evaluatedGenome.getCost() < currentBest.getCost())
            {
                currentBest = evaluatedGenome;
            }
        }
        return currentBest;
    }

    /**
     * Converts a population of genomes into evaluated genomes.
     *
     * @param population The population of genomes to evaluate.
     * @return A population of evaluated genomes.
     */
    protected ArrayList<EvaluatedGenome<GenomeType>> evaluatePopulation(
        Collection<GenomeType> population)
    {
        // Conver the Genome to an EvaluatedGenome.
        ArrayList<EvaluatedGenome<GenomeType>> evaluatedPopulation =
            new ArrayList<EvaluatedGenome<GenomeType>>(population.size());

        for (GenomeType genome : population)
        {
            // Evaluate the cost of this genome.
            double cost = this.getCostFunction().evaluate(genome);

            // Add it to the population.
            evaluatedPopulation.add(
                new EvaluatedGenome<GenomeType>(cost, genome));
        }

        return evaluatedPopulation;
    }

    public CostFunction<? super GenomeType, ? super CostParametersType> getCostFunction()
    {
        return this.costFunction;
    }

    /**
     * Gets the reproducer.
     *
     * @return The reproducer.
     */
    public Reproducer<GenomeType> getReproducer()
    {
        return this.reproducer;
    }

    /**
     * Gets the best genome found so far.
     *
     * @return The best genome found so far.
     */
    public EvaluatedGenome<GenomeType> getBestSoFar()
    {
        return this.bestSoFar;
    }

    /**
     * Gets the maximum number of iterations to go without improvement before 
     * stopping.
     * 
     * @return The current maximum.
     */
    public int getMaxIterationsWithoutImprovement()
    {
        return this.maxIterationsWithoutImprovement;
    }

    /**
     * Gets the current number of iterations without improvement.
     * 
     * @return The current iteration.
     */
    public int getIterationsWithoutImprovement()
    {
        return this.iterationsWithoutImprovement;
    }

    /**
     * Gets the population of genomes.
     *
     * @return The population of genomes.
     */
    public Collection<EvaluatedGenome<GenomeType>> getPopulation()
    {
        return this.population;
    }

    /**
     * Sets the cost function.
     *
     * @param cost The new cost function.
     */
    public void setCostFunction(
        CostFunction<? super GenomeType, ? super CostParametersType> cost)
    {
        this.costFunction = cost;
    }

    /**
     * Sets the reproducer.
     *
     * @param reproducer The new reproducer.
     */
    public void setReproducer(
        Reproducer<GenomeType> reproducer)
    {
        this.reproducer = reproducer;
    }

    /**
     * Sets the best genome found so far.
     *
     * @param bestSoFar The new best genome.
     */
    public void setBestSoFar(
        EvaluatedGenome<GenomeType> bestSoFar)
    {
        this.bestSoFar = bestSoFar;
    }

    /**
     * Sets the maximum number of iterations to go without improvement before 
     * stopping.
     * 
     * @param maxIterationsWithoutImprovement The new maximum.
     */
    public void setMaxIterationsWithoutImprovement(
        int maxIterationsWithoutImprovement)
    {
        this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
    }

    /**
     * Sets the current number of iterations without improvement.
     * 
     * @param iterationsWithoutImprovement The new iteration.
     */
    public void setIterationsWithoutImprovement(
        int iterationsWithoutImprovement)
    {
        this.iterationsWithoutImprovement = iterationsWithoutImprovement;
    }

    /**
     * Sets the population of genomes.
     *
     * @param population The new population.
     */
    public void setPopulation(
        Collection<EvaluatedGenome<GenomeType>> population)
    {
        this.population = population;
    }

    /**
     * Getter for initialPopulation.
     *
     * @return The initial population of genomes.
     */
    public Collection<GenomeType> getInitialPopulation()
    {
        return this.initialPopulation;
    }

    /**
     * Setter for initialPopulation.
     *
     * @param initialPopulation The initial population of genomes.
     */
    public void setInitialPopulation(
        Collection<GenomeType> initialPopulation)
    {
        this.initialPopulation = initialPopulation;
    }
    
    /**
     * Gets the performance, which is the cost of the best genome.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>("cost", this.getBestSoFar().getCost());
    }
}
