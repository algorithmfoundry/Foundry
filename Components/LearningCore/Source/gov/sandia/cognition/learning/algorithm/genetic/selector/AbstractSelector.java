/*
 * File:                AbstractSelector.java
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>AbstractSelector</code> class provides some common functionality 
 * for implementations of <code>Selectors</code>.
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
            date="2006-10-05",
            changesNeeded=false,
            comments="Class looks fine."
        )
    }
)
public abstract class AbstractSelector<GenomeType>
    implements Selector<GenomeType>
{
    /**
     * Creates a new instance of AbstractSelector.
     */
    public AbstractSelector()
    {
        super();
    }
    
    /**
     * Applies the selection algorithm to the given collection of genomes and
     * their associated score from the cost function. It returns a new 
     * population of genomes.
     *
     * @param genomes The current population of genomes along with their 
     *        associated costs.
     * @return A new population of genomes.
     */
    public Collection<GenomeType> reproduce(
            Collection<EvaluatedGenome<GenomeType>> genomes) 
    {
        // Call the selection algorithm.
        Collection<EvaluatedGenome<GenomeType>> selectedGenomes =
                this.select(genomes);
        
        // Convert the EvaluatedGenome list to just a list of Genomes.
        ArrayList<GenomeType> newGenomes =
                new ArrayList<GenomeType>(selectedGenomes.size());
        for (EvaluatedGenome<GenomeType> evaluatedGenome : selectedGenomes) 
        {
            newGenomes.add(evaluatedGenome.getGenome());
        }
        
        return newGenomes;
    }
}
