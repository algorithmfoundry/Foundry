/*
 * File:                TournamentSelector.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 4, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.selector;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The TournamentSelector class implements a Selector that uses tournament 
 * selection to create a new population.
 * <BR><BR>
 * The tournament selector selects each member of the population by having
 * a tournament of the given tournament size. The winner of that tournament
 * (the genome with the lowest cost) is then entered into the selected 
 * population. This allows 
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example
 * @author Justin Basilico
 * @author Jonathan McClain
 * @since 1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments={
                "Now implements Randomized.",
                "Moved previous code review as CodeReview annotation",
                "Looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-04",
            changesNeeded=false,
            comments={
                "I optimized the code to make it faster and use less memory.",
                "I also made the Random object a parameter of the class, not generated each time select is called."
            }
        )
    }
)
public class TournamentSelector<GenomeType>
    extends AbstractSelector<GenomeType>
    implements Randomized
{
    /** The percent of the population to select. */
    private double percent = 0.0;
    
    /** The size of the tournament. */
    private int tournamentSize = 0;
    
    /** The random number generator to use. */
    private Random random = null;
    
    /**
     * Creates a new instance of TournamentSelector.
     *
     * @param percent The percent of the population to select.
     * @param tournamentSize The size of each tournament when selecting.
     */
    public TournamentSelector(
        double percent,
        int tournamentSize)
    {
        this(percent, tournamentSize, new Random());
    }
    
    /**
     * Creates a new instance of TournamentSelector.
     *
     * @param percent The percent of the population to select.
     * @param tournamentSize The size of each tournament when selecting.
     * @param random The random number generator to use.
     */
    public TournamentSelector(
        double percent,
        int tournamentSize,
        Random random)
    {
        super();
        
        this.setPercent(percent);
        this.setTournamentSize(tournamentSize);
        this.setRandom(random);
    }

    /**
     * Uses tournament selection to create a new population.
     *
     * @param genomes The population to select from.
     * @return The selected population.
     */
    public Collection<EvaluatedGenome<GenomeType>> select(
        Collection<EvaluatedGenome<GenomeType>> genomes) 
    {
        // Convert the population to an array list to make it fast.
        ArrayList<EvaluatedGenome<GenomeType>> population;
        
        if( genomes instanceof ArrayList )
        {
            population = (ArrayList<EvaluatedGenome<GenomeType>>) genomes;
        }
        else
        {
            population = new ArrayList<EvaluatedGenome<GenomeType>>(genomes);
        }
        
        int numToCreate = (int) Math.round(
            (double) population.size() * this.getPercent());
        ArrayList<EvaluatedGenome<GenomeType>> selectedPopulation =
             new ArrayList<EvaluatedGenome<GenomeType>>(numToCreate);
        
        int tournySize = this.getTournamentSize();
        int numGenomes = genomes.size();
        Random rng = this.getRandom();
        
        // Loop through the number to create and create that many tournaments.
        for (int i = 0; i < numToCreate; i++)
        {
            // Conduct the tournament to select the next member of the 
            // population.
            EvaluatedGenome<GenomeType> winner = null;
            
            for (int j = 0; j < tournySize; j++)
            {
                // Select a random genome.
                int randomIndex = rng.nextInt(numGenomes);
                EvaluatedGenome<GenomeType> genome = 
                    population.get(randomIndex);
                
                if ( winner == null || genome.getCost() < winner.getCost() )
                {
                    // This is the best seen so far.
                    winner = genome;
                }
            }
            
            // Add the winner of the tournament.
            selectedPopulation.add(winner);
        }
        
        // Return the selected population.
        return selectedPopulation;
    }
    
    /**
     * Gets the percent of the population to select.
     *
     * @return The percent.
     */
    public double getPercent()
    {
        return this.percent;
    }
    
    /**
     * Gets the size for tournaments.
     *
     * @return The size.
     */
    public int getTournamentSize()
    {
        return this.tournamentSize;
    }

    /**
     * Gets the random number generator being used.
     *
     * @return The random number generator being used.
     */
    public Random getRandom()
    {
        return this.random;
    }
    
    /**
     * Sets the percent of the population to select.
     *
     * @param percent The new percent.
     */
    public void setPercent(
        double percent)
    {
        if ( percent <= 0.0 )
        {
            throw new IllegalArgumentException(
                "The percentage must be positive.");
        }
        
        this.percent = percent;
    }
    
    /**
     * Sets the size for tournaments.
     *
     * @param tournamentSize The new size.
     */
    public void setTournamentSize(
        int tournamentSize)
    {
        if ( tournamentSize <= 0 )
        {
            throw new IllegalArgumentException(
                "The tournament size must be positive.");
        }
        
        this.tournamentSize = tournamentSize;
    }
    
    /**
     * Sets the random number generator to use.
     *
     * @param  random The new random number generator.
     */
    public void setRandom(
        Random random)
    {
        this.random = random;
    }
}
