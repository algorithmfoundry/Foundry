/*
 * File:                MultiReproducer.java
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * The MultiReproducer class implements a Reproducer that takes multiple 
 * Reproducers and applies them to a population.
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
                "This class still has an open task... I suspect this is due to a lack of interest.",
                "Moved previous code review as CodeReview annotation.",
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
                "Looks fine otherwise."
            }
        )
    }
)
public class MultiReproducer<GenomeType>
    extends Object
    implements Reproducer<GenomeType>
{
    /** The reproducers to use for reproducing. */
    private Collection<Reproducer<GenomeType>> reproducers;
    
    /**
     * Creates a new instance of MultiReproducer.
     *
     * @param  reproducers The reproducers to use.
     */
    public MultiReproducer(
        Collection<Reproducer<GenomeType>> reproducers)
    {
        super();
        
        this.setReproducers(reproducers);
    }
    
    /**
     * Applies the supplied reproducers to the population of genomes.
     *
     * @param genomes The current population of genomes along with their 
     *        associated costs.
     * @return A new population of genomes.
     */
    public ArrayList<GenomeType> reproduce(
        Collection<EvaluatedGenome<GenomeType>> genomes)
    {
// TODO: I do not like that EACH reproducer calls "addAll()" to the next
// generation.  This means the size of newGenomes will be a function of
// however many reproducers are in the MultiReproducer, and will lead to
// exponential explosion of genomes as generations progress.  
// I think that each reproducer should simply reproduce the entire collection
// each iteration.  However,  I don't see a way to fix this, other than
// "faking" the evaluated scores from the "genome" EvaluatedGenome scores and
// applying them to the genomes returned by each reproducer.
//      -- krdixon, 2006-11-03
        
        
        
        // Assume the new population will be about the same size as the given
        // one.
        ArrayList<GenomeType> newGenomes = 
            new ArrayList<GenomeType>(genomes.size());
        
        for ( Reproducer<GenomeType> reproducer : this.getReproducers() )
        {
            newGenomes.addAll(reproducer.reproduce(genomes));
        }
        
        return newGenomes;
    }
    
    /**
     * Gets the reproducers to use for reproducing.
     *
     * @return The reproducers.
     */
    public Collection<Reproducer<GenomeType>> getReproducers()
    {
        return this.reproducers;
    }
    
    /**
     * Sets the reproducers to use for reproducing.
     *
     * @param reproducers The new reproducers.
     */
    public void setReproducers(
        Collection<Reproducer<GenomeType>> reproducers)
    {
        this.reproducers = reproducers;
    }
}

