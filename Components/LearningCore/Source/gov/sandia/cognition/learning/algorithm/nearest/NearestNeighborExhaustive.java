/*
 * File:                NearestNeighborExhaustive.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 24, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The {@code NearestNeighborExhaustive} class implements a simple evaluator
 * that looks up a given input object in a collection of input-output pair
 * examples and returns the output associated with the most similar input.
 * Similarity is judged using the given divergence function. This is an
 * implementation of the standard nearest-neighbor algorithm, which is a corner
 * case of the k-nearest neighbor algorithm, implemented in the
 * {@code KNearestNeighbor} class.
 * 
 * @param   <InputType> The input type for nearest neighbor.
 * @param   <OutputType> The output type for nearest neighbor.
 * @author  Justin Basilico
 * @since   2.1
 * @see     KNearestNeighbor
 */
@PublicationReference(
    author="Wikipedia",
    title="k-nearest neighbor algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/K-nearest_neighbor_algorithm"
)
public class NearestNeighborExhaustive<InputType, OutputType>
    extends AbstractNearestNeighbor<InputType,OutputType>
{
    /** The data that nearest-neighbor is performed over. */
    protected LinkedList<InputOutputPair<? extends InputType, OutputType>> data;
    
    /**
     * Creates a new instance of {@code NearestNeighborExhaustive}.
     */
    public NearestNeighborExhaustive()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of {@code NearestNeighborExhaustive}.
     * 
     * @param   divergenceFunction The divergence function to use.
     */
    public NearestNeighborExhaustive(
        final DivergenceFunction<? super InputType, ? super InputType> divergenceFunction)
    {
        this(divergenceFunction, null);
    }
    
    /**
     * Creates a new instance of {@code NearestNeighborExhaustive}.
     * 
     * @param   divergenceFunction The divergence function to use.
     * @param   data The data to perform nearest neighbor over.
     */
    public NearestNeighborExhaustive(
        final DivergenceFunction<? super InputType, ? super InputType> divergenceFunction,
        final Collection<? extends InputOutputPair<? extends InputType, OutputType>> data)
    {
        super(divergenceFunction);
        
        this.setData(new LinkedList<InputOutputPair<? extends InputType, OutputType>>());
        if (data != null)
        {
            this.getData().addAll(data);
        }
    }

    @Override
    public NearestNeighborExhaustive<InputType, OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        NearestNeighborExhaustive<InputType,OutputType> clone =
            (NearestNeighborExhaustive<InputType,OutputType>) super.clone();
        clone.setData( new LinkedList<InputOutputPair<? extends InputType, OutputType>>() );
        if( this.getData() != null )
        {
            clone.getData().addAll( this.getData() );
        }

        return clone;
        
    }

    /**
     * Evaluates the object to do nearest-neighbor lookup for the given input.
     * 
     * @param   input The input to evaluate.
     * @return  The output associated with the input that is the most similar
     *      (least divergent) to the input.
     */
    public OutputType evaluate(
        final InputType input)
    {
        // We need to find the best example and best divergence in the data.
        InputOutputPair<? extends InputType, OutputType> bestExample = null;
        double bestDivergence = Double.MAX_VALUE;
        
        // Go through all the data.
        for( InputOutputPair<? extends InputType, OutputType> example
            : getData() )
        {
            // Calculate the divergence.
            final double divergence = 
                this.divergenceFunction.evaluate(input, example.getInput());
            
            if (bestExample == null || divergence < bestDivergence)
            {
                bestExample = example;
                bestDivergence = divergence;
            }
            // else - Not the best.
        }
        
        if (bestExample != null)
        {
            // Return the output associated with the best example.
            return bestExample.getOutput();
        }
        else
        {
            // No output.
            return null;
        }
    }

    public LinkedList<InputOutputPair<? extends InputType, OutputType>> getData()
    {
        return this.data;
    }
    
    /**
     * Sets the data that the object performs nearest-neighbor lookup on.
     * 
     * @param   data The data to perform nearest-neighbor lookup on.
     */
    public void setData(
        final LinkedList<InputOutputPair<? extends InputType, OutputType>> data)
    {
        this.data = data;
    }

    /**
     * The {@code NearestNeighborExhaustive.Learner} class implements a batch learner for
     * the {@code NearestNeighborExhaustive} class.
     * 
     * @param   <InputType> The input type for nearest neighbor.
     * @param   <OutputType> The output type for nearest neighbor.
     * @author  Justin Basilico
     * @since   2.1
     */
    public static class Learner<InputType, OutputType>
        extends DefaultDivergenceFunctionContainer<InputType, InputType>
        implements SupervisedBatchLearner<InputType,OutputType,NearestNeighborExhaustive<InputType, OutputType>>
    {
        /**
         * Creates a new instance of {@code NearestNeighborExhaustive.Learner}.
         */
        public Learner()
        {
            this(null);
        }
    
        /**
         * Creates a new instance of {@code NearestNeighborExhaustive.Learner}.
         * 
         * @param   divergenceFunction The divergence function to use.
         */
        public Learner(
            final DivergenceFunction<? super InputType, ? super InputType>
                divergenceFunction)
        {
            super(divergenceFunction);
        }
        
        public NearestNeighborExhaustive<InputType, OutputType> learn(
            Collection<? extends InputOutputPair<? extends InputType, OutputType>> data )
        {
            // Just pass the divergence function and the data to the nearest
            // neighbor object.
            return new NearestNeighborExhaustive<InputType, OutputType>(
                this.getDivergenceFunction(), data );
        }
        
    }
    
}
