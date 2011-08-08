/*
 * File:                KNearestNeighborExhaustive.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.Summarizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * A generic k-nearest-neighbor classifier.  This classifier simply looks at
 * the nearest "k" neighbors to a point and returns the average of them.  Thus,
 * the learner is trivial, but the lookups (evaluations) are expensive.
 * For example, with k=1, then k-nearest-neighbor simply returns the nearest
 * data point to a given input.  And so forth.
 *
 * @param <InputType> Type of data upon which the KNearestNeighborExhaustive operates,
 * something like Vector, Double, or String
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="k-nearest neighbor algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/K-nearest_neighbor_algorithm"
)
public class KNearestNeighborExhaustive<InputType, OutputType>
    extends AbstractKNearestNeighbor<InputType,OutputType>
{

    /**
     * Underlying data for the classifier
     */
    private Collection<InputOutputPair<? extends InputType, OutputType>> data;
    
    /**
     * Creates a new instance of {@code KNearestNeighborExhaustive}.
     */
    public KNearestNeighborExhaustive()
    {
        this(DEFAULT_K, null, null, null);
    }

    /**
     * Creates a new instance of KNearestNeighborExhaustive
     * 
     * @param k
     * Number of neighbors to consider, must be greater than zero
     * @param data 
     * Underlying data for the classifier
     * @param divergenceFunction Divergence function that determines how "far" two objects are apart
     * @param averager Creates a single object from a collection of data
     */
    public KNearestNeighborExhaustive(
        int k,
        Collection<? extends InputOutputPair<? extends InputType,OutputType>> data,
        DivergenceFunction<? super InputType, ? super InputType> divergenceFunction,
        Summarizer<? super OutputType,? extends OutputType> averager )
    {
        super( k, divergenceFunction, averager );
        this.setData(new LinkedList<InputOutputPair<? extends InputType, OutputType>>());
        if (data != null)
        {
            this.getData().addAll(data);
        }
    }

    @Override
    public KNearestNeighborExhaustive<InputType, OutputType> clone()
    {
        KNearestNeighborExhaustive<InputType, OutputType> clone =
            (KNearestNeighborExhaustive<InputType, OutputType>) super.clone();

        // We'll just clone the pointer, not the elements of the Collection
        clone.setData(new LinkedList<InputOutputPair<? extends InputType,OutputType>>( this.getData() ) );
        return clone;
    }

    
    @Override
    protected Collection<OutputType> computeNeighborhood(
        InputType key)
    {
        // We fill a priority queue of (up to) k nearest neighbors.
        final int maxNumNeighbors = this.getK();
        final PriorityQueue<Neighbor> neighbors = new PriorityQueue<Neighbor>();
        for (Pair<? extends InputType, ? extends OutputType> example 
            : this.getData())
        {
            // Get the divergence from the input to the value.
            final double divergence = this.getDivergenceFunction().evaluate(
                key, example.getFirst());
            
            if (neighbors.size() < maxNumNeighbors)
            {
                // The queue isn't even full yet, so add the neighbor.
                neighbors.add(new Neighbor(example.getSecond(), divergence));
            }
            // The queue of k neighbors is now full. We check first to see if
            // this divergence is any better than the furthest neighbor in the
            // queue to avoid having to do an unnessecary add and remove
            else if (divergence < neighbors.peek().divergence)
            {
                // Kick out the furthest neighbor.
                neighbors.remove();
                
                // Add the new neighbor. This increases the queue from having
                // k to having k+1 neighbors.
                neighbors.add(new Neighbor(example.getSecond(), divergence));
                
            }
        }

        // We used meta-data to sort the queue, so create a list containing
        // just the output values associated with the k nearest neighbors.
        final ArrayList<OutputType> nearest = 
            new ArrayList<OutputType>(neighbors.size());
        for (Neighbor neighbor : neighbors)
        {
            nearest.add(neighbor.value);
        }

        return nearest;

    }

    public Collection<InputOutputPair<? extends InputType,OutputType>> getData()
    {
        return this.data;
    }

    /**
     * Setter for data
     * @param data 
     * Underlying data for the classifier
     */
    public void setData(
        Collection<InputOutputPair<? extends InputType, OutputType>> data )
    {
        this.data = data;
    }

    /**
     * Holds neighbor information used during the evaluate method and is put
     * into a priority queue. 
     */
    protected class Neighbor
        extends AbstractCloneableSerializable
        implements Comparable<Neighbor>
    {
        // Note: This class does not follow the get/set pattern in order to
        // make it as fast as possible, because it is used within the evaluate
        // method. Also, its a private internal class, so no one else should
        // use it.
        /**
         * Output value.
         */
        private OutputType value;

        /**
         * Divergence associated with this value.
         */
        private double divergence;
        
        /**
         * Creates a new neighbor.
         * 
         * @param   value
         *      The value associated with the neighbor.
         * @param divergence
         */
        public Neighbor(
            final OutputType value,
            final double divergence)
        {
            super();
            
            this.value = value;
            this.divergence = divergence;
        }
        
        public int compareTo(
            final Neighbor other)
        {
            // We reverse the comparison so that the item at the head of the
            // priority queue is the furthest neighbor
            return -Double.compare(this.divergence, other.divergence);
        }
        
    }

    /**
     * This is a BatchLearner interface for creating a new KNearestNeighborExhaustive
     * from a given dataset, simply a pass-through to the constructor of
     * KNearestNeighborExhaustive
     * @param <InputType> Type of data upon which the KNearestNeighborExhaustive operates,
     * something like Vector, Double, or String
     * @param <OutputType> Output of the evaluator, like Matrix, Double, String
     */
    public static class Learner<InputType, OutputType>
        extends KNearestNeighborExhaustive<InputType,OutputType>
        implements SupervisedBatchLearner<InputType,OutputType,KNearestNeighborExhaustive<InputType, OutputType>>
    {

        /**
         * Default constructor.
         */
        public Learner()
        {
            this( DEFAULT_K, null, null );
        }

        /**
         * Creates a new instance of Learner
         * @param k
         * Number of neighbors to consider, must be greater than zero
         * @param divergenceFunction
         * Divergence function that determines how "far" two objects are apart,
         * where lower values indicate two objects are more similar
         * @param averager
         * Creates a single object from a collection of data
         */
        public Learner(
            int k,
            DivergenceFunction<? super InputType, ? super InputType> divergenceFunction,
            Summarizer<? super OutputType,OutputType> averager )
        {
            super( k, null, divergenceFunction, averager );
        }

        /**
         * Creates a new KNearestNeighborExhaustive from a Collection of InputType.
         * This is simply a pass-through to the constructor, and there is
         * no computation time associated with this function call.
         * @param data Dataset from which to create a new KNearestNeighborExhaustive
         * @return
         * KNearestNeighborExhaustive based on the given dataset.
         */
        public KNearestNeighborExhaustive<InputType, OutputType> learn(
            Collection<? extends InputOutputPair<? extends InputType,OutputType>> data )
        {
            ArrayList<InputOutputPair<? extends InputType,OutputType>> list =
                new ArrayList<InputOutputPair<? extends InputType, OutputType>>( data );

            return new KNearestNeighborExhaustive<InputType, OutputType>(
                this.getK(), list, this.getDivergenceFunction(), this.getAverager() );
        }

    }

}
