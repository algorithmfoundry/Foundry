/*
 * File:                VectorizableCrossoverFunction.java
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
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.Random;

/**
 * The VectorizableCrossoverFunction class is a {@code CrossoverFunction} that
 * takes two {@code Vectorizable}.
 *
 * @author Justin Basilico
 * @author Kevin Dixon
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
                "Now extends AbstractRandomized.",
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
                "Restructured the code to get rid of some extra copying.",
                "Added the Random object as part of the constructor instead of creating it each time inside the reproduction loop."
            }
        )
    }
)
public class VectorizableCrossoverFunction
    extends AbstractRandomized
    implements CrossoverFunction<Vectorizable>
{
    /**
     * Probability that an element in the child will come from vector2, and
     * with probability (1-probabilityCrossover) the element will come from
     * vector1.  Thus, probabilityCrossover==0.0 means that all elements will
     * come from vector1 and probabilityCrossover==1.0 means that all elements
     * will come from vector2, probabilityCrossover==0.5 has maximum entropy
     * in terms of where the elements of the child vector came from
     */
    private double probabilityCrossover;

    /**
     * Default probability of cross over, {@value}.
     */
    public static final double DEFAULT_PROBABILITY = 0.5;

    /**
     * Creates a new instance of VectorizableCrossoverFunction with an crossover
     * probability of 0.5.
     */
    public VectorizableCrossoverFunction()
    {
        this(DEFAULT_PROBABILITY);
    }
    
    /**
     * Creates a new instance of VectorizableCrossoverFunction.
     *
     * @param  probabilityCrossover Probability that an element in the child 
     *         will come from vector2
     */
    public VectorizableCrossoverFunction(
        double probabilityCrossover)
    {
        this(probabilityCrossover, new Random());
    }
    
    /**
     * Creates a new instance of VectorizableCrossoverFunction.
     *
     * @param  probabilityCrossover Probability that an element in the child 
     *         will come from vector2
     * @param  random The random number generator to use.
     */
    public VectorizableCrossoverFunction(
        double probabilityCrossover,
        Random random)
    {
        super( random );
        
        this.setProbabilityCrossover(probabilityCrossover);
    }
    
    /**
     * Crosses over each element of the parent vectors. Uses a random boolean 
     * to decide which parent contributes an element to the child.
     *
     * @param parent1 The first parent to crossover.
     * @param parent2 The second parent to crossover.
     * @return The result of the crossover.
     */
    public Vectorizable crossover(
        Vectorizable parent1,
        Vectorizable parent2)
    {
        // Convert to a vector.
        Vector vector1 = parent1.convertToVector();
        Vector vector2 = parent2.convertToVector();
        
        // Create the child by cloning the first parent and then copying the
        // second parent's values in when the probability is correct.
        Vector child = vector1.clone();
        for (int i = 0; i < child.getDimensionality(); i++)
        {
            if ( this.random.nextDouble() <= this.getProbabilityCrossover() )
            {
                child.setElement(i, vector2.getElement(i));
            }
        }
        
        // Create the result of the crossover.
        Vectorizable result = parent1.clone();
        result.convertFromVector(child);
        return result;
    }

    /**
     * Getter for probabilityCrossover.
     *
     * @return Probability that an element in the child will come from vector2
     */
    public double getProbabilityCrossover()
    {
        return this.probabilityCrossover;
    }

    /**
     * Setter for probabilityCrossover.
     *
     * @param probabilityCrossover Probability that an element in the child 
     *        will come from vector2
     */
    public void setProbabilityCrossover(
        double probabilityCrossover)
    {
        this.probabilityCrossover = probabilityCrossover;
    }
    
}
