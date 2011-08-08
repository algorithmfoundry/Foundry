/*
 * File:                EvaluatedGenome.java
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

package gov.sandia.cognition.learning.algorithm.genetic;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.Pair;
import java.io.Serializable;

/**
 * The EvaluatedGenome class wraps together a Genome and its cost score.
 *
 * @param <GenomeType> Type of genome used to represent a single element in the
 * genetic population, such as a Vector, for example.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-23",
            changesNeeded=true,
            comments={
                "I think this class should be replaced by InputOutputPair<GenomeType,Double>",
                "It doesn't really serve a useful purpose anymore.",
                "Replaced previous code review with CodeReview annotation."
            },
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2008-09-15",
                moreChangesNeeded=false,
                comments={
                    "InputOutputPair is meant for supervised learning",
                    "I added the Pair interface to the class, since it is a pair.",
                    "I prefer having a some semantically meaningful class name and properties rather than using the vague Pair or InputOutputPair."    
                }       
            )
            
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-02",
            changesNeeded=false,
            comments="This simple container class looks fine."
        )
    }
)
public class EvaluatedGenome<GenomeType>
    extends java.lang.Object
    implements Pair<GenomeType, Double>, Serializable
{
    /** The genome that was evaluated. */
    private GenomeType genome = null;
    
    /** The cost associated with a Genome. */
    private double cost = 0.0;
    
    /**
     * Creates a new instance of EvaluatedGenome.
     *
     * @param genome The genome.
     * @param cost The cost associated with the genome.
     */
    public EvaluatedGenome(
        double cost,
        GenomeType genome)
    {
        super();
        
        this.setGenome(genome);
        this.setCost(cost);
    }
    
    /**
     * Gets the genome.
     *
     * @return The genome.
     */
    public GenomeType getGenome()
    {
        return this.genome;
    }
    
    /**
     * Gets the cost of the genome.
     *
     * @return The cost.
     */
    public double getCost()
    {
        return this.cost;
    }

    /**
     * Sets the genome.
     *
     * @param genome The new genome.
     */
    public void setGenome(
        GenomeType genome)
    {
        this.genome = genome;
    }
    
    /**
     * Sets the cost of the genome.
     *
     * @param cost The new cost.
     */
    public void setCost(
        double cost)
    {
        this.cost = cost;
    }

    /**
     * The first entry in the pair is the genome.
     * 
     * @return The genome.
     */
    public GenomeType getFirst()
    {
        return this.getGenome();
    }

    /**
     * The second entry in the pair is the cost.
     * 
     * @return The cost.
     */
    public Double getSecond()
    {
        return this.getCost();
    }
}
