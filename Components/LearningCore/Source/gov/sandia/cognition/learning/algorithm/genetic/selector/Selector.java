/*
 * File:                Selector.java
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

package gov.sandia.cognition.learning.algorithm.genetic.selector;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.learning.algorithm.genetic.EvaluatedGenome;
import gov.sandia.cognition.learning.algorithm.genetic.reproducer.Reproducer;
import java.util.Collection;

/**
 * The Selector interface defines a type of reproducer that can select a portion
 * of a population for reproduction.
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example
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
                "Moved previous code review as CodeReview annotation",
                "Looks fine."
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
public interface Selector<GenomeType>
    extends Reproducer<GenomeType>
{
   /**
    * Selects and returns a portion of the given population.
    *
    * @param genomes The population to select from.
    * @return A portion of the given population.
    */
   public Collection<EvaluatedGenome<GenomeType>> select(
       Collection<EvaluatedGenome<GenomeType>> genomes);
}
