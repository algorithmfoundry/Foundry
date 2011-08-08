/*
 * File:                CrossoverFunction.java
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
 */

package gov.sandia.cognition.learning.algorithm.genetic.reproducer;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import java.io.Serializable;

/**
 * The CrossoverFunction interface implements standard functionality for 
 * implementing crossover for genetic algorithms.
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example.
 * @author Jonathan McClain
 * @since 1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=false,
            comments="Looks fine."
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
public interface CrossoverFunction<GenomeType>
    extends Serializable
{
    /**
     * Crosses over the provided genomes to produce a new genome.
     *
     * @param genome1 The first genome to crossover.
     * @param genome2 The second genome to crossover.
     * @return The result of the crossover.
     */
    public GenomeType crossover(
        GenomeType genome1,
        GenomeType genome2);
}
