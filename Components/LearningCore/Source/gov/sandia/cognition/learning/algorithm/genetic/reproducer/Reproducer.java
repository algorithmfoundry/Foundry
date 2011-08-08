/*
 * File:                Reproducer.java
 * Authors:             Justin Basilico and Kevin R. Dixon
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

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import java.io.Serializable;
import java.util.Collection;

/**
 * The Reproducer interface defines the functionality of a reproduction 
 * algorithm in a genetic algorithm. Such an algorithm takes a collection of
 * genomes and their associated costs and then returns the new generation of
 * genomes based on that.
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments={
                "Moved previous code review to CodeReview annotation.",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-04",
            changesNeeded=false,
            comments="Interface looks fine."
        )
    }
)
public interface Reproducer<GenomeType>
    extends Serializable
{
    /**
     * Applies a reproduction algorithm to the given collection of genomes and
     * their associated score from the cost function. It returns a new 
     * population of genomes.
     *
     * @param genomes The current population of genomes along with their 
     *        associated costs.
     * @return A new population of genomes.
     */
    public Collection<GenomeType> reproduce(
        Collection<EvaluatedGenome<GenomeType>> genomes);
}
