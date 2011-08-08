/*
 * File:                MutationReproducer.java
 * Authors:             Jonathan McClain and Justin Basilico
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
import gov.sandia.cognition.learning.algorithm.annealing.Perturber;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.learning.algorithm.genetic.selector.Selector;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The MutationReproducer class implements a Reproducer that applies a 
 * {@code Perturber} to the supplied population to produce a new population.
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example
 * @author Jonathan McClain
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments={
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
                "Minor changes made.",
                "Looks good."
            }
        )
    }
)
public class MutationReproducer<GenomeType>
    extends Object
    implements Reproducer<GenomeType>
{
    /** The perturber to use for mutation. */
    private Perturber<GenomeType> perturber;
    
    /** The selector to use to select the population. */
    private Selector<GenomeType> selector;
    
    /**
     * Creates a new instance of MutationReproducer
     *
     * @param perturber The Perturber to use for mutating.
     * @param selector The Selector to use for selecting genomes to mutate.
     */
    public MutationReproducer(
        Perturber<GenomeType> perturber,
        Selector<GenomeType> selector)
    {
        super();
        this.setPerturber(perturber);
        this.setSelector(selector);
    }
    
    /**
     * Produces a new mutated population based on the supplied population.
     *
     * @param genomes The population to mutate.
     * @return The new population.
     */
    public Collection<GenomeType> reproduce(
        Collection<EvaluatedGenome<GenomeType>> genomes)
    {
        Collection<EvaluatedGenome<GenomeType>> selectedGenomes =
                this.getSelector().select(genomes);
        ArrayList<GenomeType> newGenomes = 
                new ArrayList<GenomeType>(selectedGenomes.size());
        for(EvaluatedGenome<GenomeType> genome : selectedGenomes)
        {
            newGenomes.add(this.getPerturber().perturb(genome.getGenome()));
        }
        return newGenomes;
    }
    
    /**
     * Gets the perturber used for mutation.
     *
     * @return The perturber used for mutation.
     */
    public Perturber<GenomeType> getPerturber()
    {
        return this.perturber;
    }
    
    /**
     * Gets the selector used to select the population.
     *
     * @return The selector.
     */
    public Selector<GenomeType> getSelector()
    {
        return this.selector;
    }
    
    /**
     * Sets the perturber used for mutation.
     *
     * @param perturber The new perturber.
     */
    public void setPerturber(Perturber<GenomeType> perturber)
    {
        this.perturber = perturber;
    }
    
    /**
     * Sets the selector used to select the population.
     *
     * @param selector The new selector.
     */
    public void setSelector(Selector<GenomeType> selector)
    {
        this.selector = selector;
    }
}

