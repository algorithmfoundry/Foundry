/*
 * File:                SimulatedAnnealer.java
 * Authors:             Jonathan McClain, Justin Basilico, and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchCostMinimizationLearner;
import gov.sandia.cognition.learning.function.cost.CostFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * The SimulatedAnnealer class implements the simulated annealing algorithm
 * using the provided cost function and perturbation function.
 * <BR><BR>
 * Simulated annealing is attempts to find the minimum-cost parameters of
 * a function using a stochastic hill climbing (descent, actually).  A 
 * lower-cost parameter tweak is always taken, but a higher-cost tweak is
 * taken with a probability dictated by an "annealing" schedule.  This 
 * stochastic step toward badness is an attempt to find global minima, instead
 * of your vanilla-flavored local minima.  Thus, SA only relies on function
 * evaluations, not needed the gradient.
 * <BR><BR>
 * Here's my opinion on simulated annealing: it is a method of absolute last
 * resort, and I have trouble thinking of a more general, brain-dead approach.
 * Use SA only when you are stranded on a desolate glacier, one arm 
 * trapped under a boulder, and your pocket knife is out of reach.
 * <BR><BR>
 * If you are still reading this, then I assume you still think you need SA 
 * because you can only evaluate a function against a cost function and,
 * oh my goodness, you have a huge search space.  At least, you should be using 
 * Genetic Algorithms.  Even better, try Powell's method, which is a powerful 
 * minimization technique that only relies on function evaluations.  Think of it
 * as SA, but smart.  Generally better than Powell's method is Conjugate 
 * Gradient with automated gradient approximation, which only relies on 
 * function evaluations and automatically estimates the gradient for you.
 * If you can store the (approximated) Jacobian in memory, then the best 
 * technique is usually BFGS with automated gradient approximation (sometimes
 * Levenberg-Marquardt Estimation is as good as BFGS, but usually not).
 * <BR><BR>
 * If you're still going to use SA, then may the optimization gods have mercy
 * on your soul.
 * 
 * @param <AnnealedType> Class returned from the {@code learn()} method, such as a 
 * {@code FeedforwardNeuralNetwork}, for example
 * @param <CostParametersType> Cost parameters given to the {@code learn()} method, such
 * as {@code Collection<InputOutputPair>}, for example
 * @author Jonathan McClain
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-22",
            changesNeeded=false,
            comments={
                "Moved previous code review to annotation.",
                "Added HTML tags to javadoc.",
                "Fixed a few typos in javadoc.",
                "Code looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-02",
            changesNeeded=false,
            comments={
                "Did some reformatting of the code.",
                "Added missing documentation.", 
                "Cleaned up the use of default parameter values."
            }
        )
    }
)
public class SimulatedAnnealer<CostParametersType, AnnealedType>
    extends AbstractAnytimeBatchLearner<CostParametersType, AnnealedType>
    implements BatchCostMinimizationLearner<CostParametersType, AnnealedType>,
        MeasurablePerformanceAlgorithm
{

    /** The default starting temperature for the algorithm, {@value}. */
    public static final double DEFAULT_STARTING_TEMPERATURE = 1.0;

    /** The default cooling factor for learning, {@value}. */
    public static final double DEFAULT_COOLING_FACTOR = 0.1;

    /** The default number of maximum iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;
    /** The cost function to minimize. */
    private CostFunction<? super AnnealedType, ? super CostParametersType> cost;

    /** The perturbing function to use to perturb the objects. */
    private Perturber<AnnealedType> perturber;

    /** The current temperature. */
    private double temperature;

    /** The maximum number of iterations to go without improvement before 
     *  stopping. */
    private int maxIterationsWithoutImprovement;

    /** The number of iterations since the last improvement. */
    private int iterationsWithoutImprovement;

    /** The cooling factor applied at each step. */
    private double coolingFactor;

    /** The random number generator to use. */
    private Random random;

    /** The best state found so far. */
    private AnnealedType bestSoFar;

    /** The score for the best state found so far. */
    private double bestSoFarScore;

    /** The current state. */
    private AnnealedType current;

    /** The score of the current state. */
    private double currentScore;

    /**
     * Creates a new instance of SimulatedAnnealer.
     * 
     * @param initial Initial candidate to consider
     * @param perturber The perturbing function to use.
     * @param cost The cost function to minimize.
     */
    public SimulatedAnnealer(
        AnnealedType initial,
        Perturber<AnnealedType> perturber,
        CostFunction<? super AnnealedType, ? super CostParametersType> cost )
    {
        this( initial, perturber, cost, DEFAULT_MAX_ITERATIONS );
    }

    /**
     * Creates a new instance of SimulatedAnnealer.
     * 
     * @param initial Initial candidate to consider
     * @param perturber The perturbing function to use.
     * @param cost The cost function to minimize.
     * @param maxIterations The maximum number of iterations to perform.
     */
    public SimulatedAnnealer(
        AnnealedType initial,
        Perturber<AnnealedType> perturber,
        CostFunction<? super AnnealedType, ? super CostParametersType> cost,
        int maxIterations )
    {
        this( initial, perturber, cost, maxIterations, 1 + maxIterations / 10 );
    }

    /**
     * Creates a new instance of SimulatedAnnealer.
     * 
     * @param initial Initial candidate to consider
     * @param perturber The perturbing function to use.
     * @param cost The cost function to minimize.
     * @param maxIterations The maximum number of iterations to perform.
     * @param maxIterationsWithoutImprovement The maximum number of iterations
     * to go without improvement before stopping.
     */
    public SimulatedAnnealer(
        AnnealedType initial,
        Perturber<AnnealedType> perturber,
        CostFunction<? super AnnealedType, ? super CostParametersType> cost,
        int maxIterations,
        int maxIterationsWithoutImprovement )
    {
        super( maxIterations );

        this.setCostFunction( cost );
        this.setPerturber( perturber );
        this.setTemperature( DEFAULT_STARTING_TEMPERATURE );
        this.setMaxIterationsWithoutImprovement(
            maxIterationsWithoutImprovement );
        this.setIterationsWithoutImprovement( 0 );
        this.setCoolingFactor( DEFAULT_COOLING_FACTOR );
        this.setRandom( new Random() );
        this.setBestSoFar( null );
        this.setBestSoFarScore( 0.0 );
        this.setCurrent( initial );
        this.setCurrentScore( 0.0 );
    }
    
    @Override
    public SimulatedAnnealer<CostParametersType, AnnealedType> clone()
    {
        @SuppressWarnings("unchecked")
        final SimulatedAnnealer<CostParametersType, AnnealedType> result =
            (SimulatedAnnealer<CostParametersType, AnnealedType>) super.clone();
        
        result.cost = ObjectUtil.cloneSafe(this.cost);
        result.perturber = ObjectUtil.cloneSmart(this.perturber);
        result.random = ObjectUtil.deepCopy(this.random);
        
        result.bestSoFar = null;
        result.bestSoFarScore = 0.0;
        result.current = null;
        result.currentScore = 0.0;
        
        return result;
    }

    protected boolean initializeAlgorithm()
    {
        this.setIteration( 0 );
        this.setIterationsWithoutImprovement( 0 );
        this.setCurrentScore(
            this.getCostFunction().evaluate( this.getCurrent() ) );
        this.setBestSoFar( this.getCurrent() );
        this.setBestSoFarScore( this.getCurrentScore() );

        return true;
    }

    /**
     * Takes one step in the Simulated Annealing process.
     *
     * @return Boolean indicating whether the SA process should continue (i.e.
     * no stopping conditions have been met).
     */
    protected boolean step()
    {

        // Perturb the current value
        AnnealedType next = this.getPerturber().perturb( this.getCurrent() );

        // Score the perturbed value
        double nextScore = this.getCostFunction().evaluate( next );

        // Check to see if this is the best so far
        if (nextScore < this.getBestSoFarScore())
        {
            this.setBestSoFar( next );
            this.setBestSoFarScore( nextScore );

            // We have improved, so reset.
            this.setIterationsWithoutImprovement( 0 );
        }
        else
        {
            this.setIterationsWithoutImprovement(
                this.getIterationsWithoutImprovement() + 1 );
        }

        // Compute the difference in scores
        double scoreDiff = nextScore - currentScore;

        if ((scoreDiff <= 0) ||
            (this.getRandom().nextDouble() < Math.exp( -scoreDiff / this.getTemperature() )))
        {
            // Use the perturbed value.
            this.setCurrent( next );
            this.setCurrentScore( nextScore );
        }
        // Else keep the old value.

        // Decrease the temperature.
        this.setTemperature( this.getCoolingFactor() * this.getTemperature() );

        return (this.getIterationsWithoutImprovement() <=
            this.getMaxIterationsWithoutImprovement());
    }

    public CostFunction<? super AnnealedType, ? super CostParametersType> getCostFunction()
    {
        return this.cost;
    }

    /**
     * Gets the perturber.
     *
     * @return The perturber.
     */
    public Perturber<AnnealedType> getPerturber()
    {
        return this.perturber;
    }

    /**
     * Gets the current temperature of the system.
     *
     * @return The current temperature.
     */
    protected double getTemperature()
    {
        return this.temperature;
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
    protected int getIterationsWithoutImprovement()
    {
        return this.iterationsWithoutImprovement;
    }

    /**
     * Gets the cooling factor.
     *
     * @return The cooling factor.
     */
    public double getCoolingFactor()
    {
        return this.coolingFactor;
    }

    /**
     * Gets the random number generator.
     *
     * @return The random number generator.
     */
    public Random getRandom()
    {
        return this.random;
    }

    /**
     * Gets the best state found so far.
     *
     * @return The best state.
     */
    protected AnnealedType getBestSoFar()
    {
        return this.bestSoFar;
    }

    /**
     * Gets the score for the best state found so far.
     *
     * @return The score.
     */
    protected double getBestSoFarScore()
    {
        return this.bestSoFarScore;
    }

    /**
     * Gets the current state of the system.
     *
     * @return The current state.
     */
    protected AnnealedType getCurrent()
    {
        return this.current;
    }

    /**
     * Gets the score of the current state.
     *
     * @return The score.
     */
    protected double getCurrentScore()
    {
        return this.currentScore;
    }

    /**
     * Sets the cost function.
     *
     * @param cost The new cost function.
     */
    public void setCostFunction(
        CostFunction<? super AnnealedType, ? super CostParametersType> cost )
    {
        this.cost = cost;
    }

    /**
     * Sets the perturber.
     *
     * @param perturber The new perturber.
     */
    public void setPerturber(
        Perturber<AnnealedType> perturber )
    {
        this.perturber = perturber;
    }

    /**
     * Sets the current temperature of the system.
     *
     * @param temperature The new temperature.
     */
    protected void setTemperature(
        double temperature )
    {
        this.temperature = temperature;
    }

    /**
     * Sets the maximum number of iterations to go without improvement before 
     * stopping.
     * 
     * 
     * @param maxIterationsWithoutImprovement The new maximum.
     */
    public void setMaxIterationsWithoutImprovement(
        int maxIterationsWithoutImprovement )
    {
        this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
    }

    /**
     * Sets the current number of iterations without improvement.
     * 
     * @param iterationsWithoutImprovement The new iteration.
     */
    protected void setIterationsWithoutImprovement(
        int iterationsWithoutImprovement )
    {
        this.iterationsWithoutImprovement = iterationsWithoutImprovement;
    }

    /**
     * Sets the cooling factor.
     *
     * @param coolingFactor The new cooling factor.
     */
    public void setCoolingFactor(
        double coolingFactor )
    {
        if (coolingFactor <= 0.0 || coolingFactor > 1.0)
        {
            throw new IllegalArgumentException( "The cooling factor must be" + "greater than zero and less than or equal to one." );
        }

        this.coolingFactor = coolingFactor;
    }

    /**
     * Sets the random number generator.
     *
     * @param random The new random number generator.
     */
    public void setRandom(
        Random random )
    {
        this.random = random;
    }

    /**
     * Sets the best state found so far.
     *
     * @param bestSoFar The new best state.
     */
    protected void setBestSoFar(
        AnnealedType bestSoFar )
    {
        this.bestSoFar = bestSoFar;
    }

    /**
     * Sets the score for the best state found so far.
     *
     * @param bestSoFarScore The new score.
     */
    protected void setBestSoFarScore(
        double bestSoFarScore )
    {
        this.bestSoFarScore = bestSoFarScore;
    }

    /**
     * Sets the current state of the system.
     *
     * @param current The new current state.
     */
    protected void setCurrent(
        AnnealedType current )
    {
        this.current = current;
    }

    /**
     * Sets the score of the current state.
     *
     * @param currentScore The new score.
     */
    protected void setCurrentScore(
        double currentScore )
    {
        this.currentScore = currentScore;
    }

    protected void cleanupAlgorithm()
    {
    }

    public AnnealedType getResult()
    {
        return this.getBestSoFar();
    }
    
    /**
     * Gets the performance, which is the best score so far.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>("score", this.getBestSoFarScore());
    }

}
