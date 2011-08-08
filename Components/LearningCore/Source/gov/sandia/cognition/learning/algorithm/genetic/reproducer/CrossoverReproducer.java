/*
 * File:                CrossoverReproducer.java
 * Authors:             Justin Basilico and Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.learning.algorithm.genetic.selector.Selector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The CrossoverReproducer takes a population of genomes, and applies the 
 * supplied CrossoverFunction to produce a new population.
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
                "This class still has open task from last code review, but I suspect it's due to lack of interest.",
                "Moved previous code review to CodeReview annotation",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-04",
            changesNeeded=false,
            comments={
                "Class looks fine.",
                "It may want to include some randomness in case the selector that it calls is deterministic and returns the same population twice."
            }
        )
    }
)
public class CrossoverReproducer<GenomeType>
    extends Object
    implements Reproducer<GenomeType>
{
    /** The selector to use to select the population. */
    private Selector<GenomeType> selector;
    
    /** The crossover function to use. */
    private CrossoverFunction<GenomeType> crossoverFunction;
    
    /**
     * Creates a new instance of CrossoverReproducer
     *
     * @param  selector The selector for the population to use.
     * @param  crossoverFunction The crossover function to use.
     */
    public CrossoverReproducer(
        Selector<GenomeType> selector,
        CrossoverFunction<GenomeType> crossoverFunction)
    {
        super();
        
        this.setSelector(selector);
        this.setCrossoverFunction(crossoverFunction);
    }
    
    /**
     * Produces a new population of genomes from the supplied population using
     * crossover. It works by using the selector to select two sets of genomes
     * and then crosses them over.
     *
     * @param genomes The population to reproduce.
     * @return The new population.
     */
    public ArrayList<GenomeType> reproduce(
        Collection<EvaluatedGenome<GenomeType>> genomes)
    {
// TO DO: Deal with the problem of having a deterministic selector by using
// permutations to do the actual matching up.
        // Select two sets of genomes.
        Collection<EvaluatedGenome<GenomeType>> selectedGenomes1 = 
            this.getSelector().select(genomes);
        Collection<EvaluatedGenome<GenomeType>> selectedGenomes2 = 
            this.getSelector().select(genomes);
        
        // Create the ArrayList of results.
        int numGenomes = 
            Math.min(selectedGenomes1.size(), selectedGenomes2.size());
        ArrayList<GenomeType> newGenomes = 
            new ArrayList<GenomeType>(numGenomes);
        
        // Iterate over both sets of selected genomes.
        Iterator<EvaluatedGenome<GenomeType>> it1 = selectedGenomes1.iterator();
        Iterator<EvaluatedGenome<GenomeType>> it2 = selectedGenomes2.iterator();
        while ( it1.hasNext() && it2.hasNext() )
        {
            // Perform the crossover between the two genomes.
            GenomeType genome1 = it1.next().getGenome();
            GenomeType genome2 = it2.next().getGenome();
            GenomeType newGenome = 
                this.getCrossoverFunction().crossover(genome1, genome2);
            newGenomes.add(newGenome);
        }
        
        return newGenomes;
    }
    
    /**
     * Gets the selector.
     *
     * @return The selector.
     */
    public Selector<GenomeType> getSelector()
    {
        return this.selector;
    }
    
    /**
     * Gets the CrossoverFunction.
     *
     * @return The CrossoverFunction.
     */
    public CrossoverFunction<GenomeType> getCrossoverFunction()
    {
        return this.crossoverFunction;
    }
    
    /**
     * Sets the selector.
     *
     * @param selector The new selector.
     */
    public void setSelector(
        Selector<GenomeType> selector)
    {
        this.selector = selector;
    }
    
    /**
     * Sets the CrossoverFunction.
     *
     * @param crossoverFunction The new CrossoverFunction.
     */
    public void setCrossoverFunction(
            CrossoverFunction<GenomeType> crossoverFunction)
    {
        this.crossoverFunction = crossoverFunction;
    }
}

